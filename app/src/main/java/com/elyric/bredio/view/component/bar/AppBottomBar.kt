package com.elyric.bredio.view.component.bar

import android.content.res.ColorStateList
import android.graphics.Color
import android.content.Context
import android.util.AttributeSet
import com.elyric.bredio.ConfigLoader
import com.elyric.bredio.BottomNavigationTabConfig
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppBottomBar(
    context: Context,
    attrs: AttributeSet?,
): BottomNavigationView(context,attrs){
    init {
        val config = ConfigLoader.loadBottomNavigationConfig(context)
        val tabs = config.tabs
        val selectTab = config.selectTab

        itemIconTintList = createItemColorStateList(config.activeColor, config.inActiveColor)
        itemTextColor = createItemColorStateList(config.activeColor, config.inActiveColor)

        menu.clear()
        tabs.sortedBy { it.index }
            .filter { it.enable }
            .forEach { tab ->
                val itemId = tab.route.hashCode()
                val item = menu.add(0, itemId, tab.index, tab.title)
                val iconResId = resolveIconResId(tab)
                if (iconResId != 0) {
                    item.setIcon(iconResId)
                }
                if (tab.route.isBlank()) {
                    item.isEnabled = false
                }
            }

        val selectedTab = tabs.firstOrNull { it.index == selectTab && it.enable && it.route.isNotBlank() }
        if (selectedTab != null) {
            selectedItemId = selectedTab.route.hashCode()
        }
    }

    private fun createItemColorStateList(activeColor: String, inactiveColor: String): ColorStateList {
        val checkedState = intArrayOf(android.R.attr.state_checked)
        val defaultState = intArrayOf()
        return ColorStateList(
            arrayOf(checkedState, defaultState),
            intArrayOf(Color.parseColor(activeColor), Color.parseColor(inactiveColor))
        )
    }

    private fun resolveIconResId(tab: BottomNavigationTabConfig): Int {
        if (tab.icon.isBlank()) {
            return 0
        }
        return resources.getIdentifier(tab.icon, "drawable", context.packageName)
    }
}
