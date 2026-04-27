package com.elyric.bredio.view.userPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elyric.bredio.R
import com.elyric.nav_api.NavDestination
import com.elyric.nav_api.NavType

@NavDestination(route = "user_fragment", type = NavType.FRAGMENT)
class UserFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}