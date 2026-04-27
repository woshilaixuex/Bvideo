package com.elyric.bredio.view.shortVideoPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elyric.bredio.R
import com.elyric.nav_api.NavDestination
import com.elyric.nav_api.NavType

@NavDestination(route = "short_video_fragment", type = NavType.FRAGMENT)
class ShortVideoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_short_video, container, false)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShortVideoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}