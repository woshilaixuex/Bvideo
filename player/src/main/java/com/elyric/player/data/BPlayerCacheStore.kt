package com.elyric.player.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import android.net.Uri
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@UnstableApi
object BPlayerCacheStore {
    private const val CACHE_DIR_NAME = "b_player_media_cache"
    private const val MAX_CACHE_BYTES = 256L * 1024L * 1024L
    private const val DEFAULT_PRELOAD_BITRATE_KBPS = 2_000
    private const val MIN_PRELOAD_BYTES = 512L * 1024L

    @Volatile
    private var simpleCache: SimpleCache? = null
    private val preloadExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun createMediaSource(context: Context, url: String): MediaSource {
        return ProgressiveMediaSource.Factory(createCacheDataSourceFactory(context))
            .createMediaSource(MediaItem.fromUri(url))
    }

    fun preloadSeconds(
        context: Context,
        url: String,
        seconds: Int,
        estimatedBitrateKbps: Int = DEFAULT_PRELOAD_BITRATE_KBPS,
        onProgress: ((cachedBytes: Long, targetBytes: Long) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): BPlayerPreloadTask {
        val targetBytes = estimatePreloadBytes(seconds, estimatedBitrateKbps)
        return preloadBytes(context, url, targetBytes, onProgress, onError)
    }

    fun preloadBytes(
        context: Context,
        url: String,
        bytes: Long,
        onProgress: ((cachedBytes: Long, targetBytes: Long) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): BPlayerPreloadTask {
        val targetBytes = bytes.coerceAtLeast(MIN_PRELOAD_BYTES)
        val cancelled = AtomicBoolean(false)
        var cacheWriter: CacheWriter? = null
        val task = BPlayerPreloadTask {
            cancelled.set(true)
            cacheWriter?.cancel()
        }

        preloadExecutor.execute {
            try {
                val dataSpec = DataSpec.Builder()
                    .setUri(Uri.parse(url))
                    .setPosition(0L)
                    .setLength(targetBytes)
                    .build()
                cacheWriter = CacheWriter(
                    createCacheDataSourceFactory(context).createDataSource(),
                    dataSpec,
                    null
                ) { _, bytesCached, _ ->
                    if (!cancelled.get()) {
                        onProgress?.invoke(bytesCached.coerceAtMost(targetBytes), targetBytes)
                    }
                }
                cacheWriter?.cache()
            } catch (throwable: Throwable) {
                if (!cancelled.get()) {
                    onError?.invoke(throwable)
                }
            }
        }

        return task
    }

    private fun createCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val appContext = context.applicationContext
        val cache = getCache(appContext)
        val upstreamFactory = DefaultDataSource.Factory(
            appContext,
            DefaultHttpDataSource.Factory()
        )
        val cacheDataSinkFactory = CacheDataSink.Factory()
            .setCache(cache)
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(cacheDataSinkFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun estimatePreloadBytes(seconds: Int, estimatedBitrateKbps: Int): Long {
        val safeSeconds = seconds.coerceAtLeast(1)
        val safeBitrateKbps = estimatedBitrateKbps.coerceAtLeast(1)
        return safeSeconds * safeBitrateKbps * 1024L / 8L
    }

    private fun getCache(context: Context): SimpleCache {
        return simpleCache ?: synchronized(this) {
            simpleCache ?: SimpleCache(
                File(context.cacheDir, CACHE_DIR_NAME),
                LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES),
                StandaloneDatabaseProvider(context)
            ).also {
                simpleCache = it
            }
        }
    }
}

class BPlayerPreloadTask internal constructor(
    private val cancelAction: () -> Unit
) {
    fun cancel() {
        cancelAction()
    }
}
