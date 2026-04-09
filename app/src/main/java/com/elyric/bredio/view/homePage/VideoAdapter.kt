package com.elyric.bredio.view.homePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elyric.bredio.R
import com.elyric.bredio.databinding.ItemHomeBinding
import com.elyric.domain.model.Video

class VideoAdapter: RecyclerView.Adapter<VideoHolder>() {
    private val videos = mutableListOf<Video>()

    fun updateVideos(newVideos: List<Video>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = videos.size

            override fun getNewListSize(): Int = newVideos.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return videos[oldItemPosition].id == newVideos[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return videos[oldItemPosition] == newVideos[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        videos.clear()
        videos.addAll(newVideos)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoHolder(binding)
    }

    override fun onBindViewHolder(
        holder: VideoHolder,
        position: Int
    ) {
        val video = videos[position]
        holder.binding.apply {
            videoTitle.text = video.title
            Glide.with(coverImage)
                .load(video.coverUrl)
                .placeholder(R.drawable.loading)
                .into(coverImage)
        }
    }

    override fun getItemCount(): Int = videos.size
}
