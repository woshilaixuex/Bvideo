package com.elyric.bredio

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object ConfigLoader {
    private const val DEFAULT_BOTTOM_NAV_ASSET = "nav_bottom_item.json"

    fun loadBottomNavigationConfig(
        context: Context,
        assetName: String = DEFAULT_BOTTOM_NAV_ASSET
    ): BottomNavigationConfig {
        val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
        return parseBottomNavigationConfig(json)
    }

    fun parseBottomNavigationConfig(json: String): BottomNavigationConfig {
        val root = JSONObject(json)
        return BottomNavigationConfig(
            activeColor = root.optString("activeColor"),
            inActiveColor = root.optString("inActiveColor"),
            selectTab = root.optInt("selectTab", 0),
            tabs = root.optJSONArray("tabs").toBottomNavigationTabs()
        )
    }

    private fun JSONArray?.toBottomNavigationTabs(): List<BottomNavigationTabConfig> {
        if (this == null) {
            return emptyList()
        }
        return buildList(length()) {
            for (index in 0 until length()) {
                val item = optJSONObject(index) ?: continue
                add(
                    BottomNavigationTabConfig(
                        size = item.optInt("size", 24),
                        enable = item.optBoolean("enable", true),
                        index = item.optInt("index", index),
                        route = item.optString("route"),
                        title = item.optString("title"),
                        icon = item.optString("icon")
                    )
                )
            }
        }
    }
}

data class BottomNavigationConfig(
    val activeColor: String,
    val inActiveColor: String,
    val selectTab: Int,
    val tabs: List<BottomNavigationTabConfig>
)

data class BottomNavigationTabConfig(
    val size: Int,
    val enable: Boolean,
    val index: Int,
    val route: String,
    val title: String,
    val icon: String = ""
)
