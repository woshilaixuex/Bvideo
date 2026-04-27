package com.elyric.bredio.view.videoPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.media3.common.util.UnstableApi
import com.elyric.bredio.databinding.FragmentVideoBinding
import com.elyric.domain.model.Video
import com.elyric.nav_api.NavDestination
import com.elyric.nav_api.NavType
import com.elyric.player.controller.BVideoPlayerController

@OptIn(UnstableApi::class)
@NavDestination(route = VideoFragment.ROUTE, type = NavType.FRAGMENT)
class VideoFragment : Fragment() {
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private var playerController: BVideoPlayerController? = null
    private lateinit var currentVideo: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentVideo = Video(
            id = requireArguments().getString(ARG_VIDEO_ID).orEmpty(),
            title = requireArguments().getString(ARG_VIDEO_TITLE).orEmpty(),
            description = requireArguments().getString(ARG_VIDEO_DESCRIPTION).orEmpty(),
            coverUrl = "",
            playUrl = requireArguments().getString(ARG_VIDEO_PLAY_URL).orEmpty().ifBlank { DEFAULT_PLAY_URL }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvVideoTitle.text = currentVideo.title
        binding.tvVideoDescription.text = currentVideo.description

        playerController = BVideoPlayerController(requireContext()).also { controller ->
            controller.attach(binding.videoPlayer)
            controller.play(currentVideo.playUrl)
        }

        binding.videoPlayer.setOnBackClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStop() {
        playerController?.pause()
        super.onStop()
    }

    override fun onDestroyView() {
        playerController?.release()
        playerController = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ROUTE = "video_fragment"
        const val ARG_VIDEO_ID = "arg_video_id"
        const val ARG_VIDEO_TITLE = "arg_video_title"
        const val ARG_VIDEO_DESCRIPTION = "arg_video_description"
        const val ARG_VIDEO_PLAY_URL = "arg_video_play_url"
        const val DEFAULT_PLAY_URL = "https://www.w3schools.com/html/mov_bbb.mp4"

        fun createArgs(video: Video): Bundle {
            return Bundle().apply {
                putString(ARG_VIDEO_ID, video.id)
                putString(ARG_VIDEO_TITLE, video.title)
                putString(ARG_VIDEO_DESCRIPTION, video.description)
                putString(ARG_VIDEO_PLAY_URL, video.playUrl.ifBlank { DEFAULT_PLAY_URL })
            }
        }
    }
}
