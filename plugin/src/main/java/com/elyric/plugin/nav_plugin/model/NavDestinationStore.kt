package com.elyric.plugin.nav_plugin.model

import com.elyric.nav_api.NavData
import java.util.Collections

object NavDestinationStore {
    val navDatas = Collections.synchronizedList(mutableListOf<NavData>())
    fun add(navData: NavData) = navDatas.add(navData)

    fun clear() = navDatas.clear()
}
