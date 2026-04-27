package com.elyric.bredio.view.searchPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elyric.bredio.R
import com.elyric.bredio.view.component.fragment.BaseFragment
import com.elyric.nav_api.NavDestination
import com.elyric.nav_api.NavType

@NavDestination(route = "search_fragment", type = NavType.FRAGMENT)
class SearchFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
