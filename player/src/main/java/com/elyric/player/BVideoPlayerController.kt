package com.elyric.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

class BVideoPlayerController(context: Context) {
    lateinit private var playerDataSource: BPlayerDataSource
    val player = ExoPlayer.Builder(context).build()
    fun setSource(name:String,url:String){
        playerDataSource.setSource(name,url)
    }
    fun prepare(){

    }
    fun start(){

    }
    fun pause(){

    }
    fun stop(){

    }
    fun release(){

    }
}