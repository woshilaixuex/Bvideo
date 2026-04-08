package com.elyric.bredio.view.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.elyric.bredio.R
import com.elyric.bredio.view.component.fragment.BaseFragment
import com.elyric.player.BPlayerView
import com.elyric.player.BVideoPlayerController


class HomeFragment : BaseFragment() {
//    private lateinit var playerView: BPlayerView
//    private lateinit var controller: BVideoPlayerController
//    private lateinit var searchBarView: View

    override fun initViews() {
        super.initViews()
//        playerView = requireView().findViewById(R.id.playerView)
//        searchBarView = requireView().findViewById(R.id.searchBarView)
//        controller = BVideoPlayerController(requireContext())
//        controller.attach(playerView)
//        controller.play("https://www.w3schools.com/html/mov_bbb.mp4")
    }

    override fun initListeners() {
        super.initListeners()
//        playerView.getInnerView().setOnClickListener {
//
//        }
//        searchBarView.setOnClickListener {
//            findNavController().navigate(R.id.searchFragment)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onDestroyView() {
//        controller.release()
        super.onDestroyView()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}
