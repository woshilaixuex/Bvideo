package com.elyric.bredio.view.component.navigation

import android.content.ComponentName
import android.content.Context
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.get
import com.elyric.nav_api.NavRegistry
import com.elyric.nav_api.NavType

object NavGraphBuilder {
    fun build(controller: NavController,context: Context) {
        val provider = controller.navigatorProvider
        val navigator = provider.getNavigator<NavGraphNavigator>("navigation")
        val navGraph = navigator.createDestination()

        val iterator = NavRegistry.get().iterator()
        while (iterator.hasNext()) {
            val navData = iterator.next()
            when (navData.navType) {
                NavType.ACTIVITY -> {
                    val navigator = provider.get<ActivityNavigator>("activity")
                    val destination = navigator.createDestination()
                    destination.id = navData.route.hashCode()
                    destination.setComponentName(ComponentName(context, navData.className))
                    navGraph.addDestination(destination)
                }
                NavType.FRAGMENT -> {
                    val navigator = provider.get<FragmentNavigator>("fragment")
                    val destination = navigator.createDestination()
                    destination.id = navData.route.hashCode()
                    destination.setClassName(navData.className)
                    navGraph.addDestination(destination)
                }

                NavType.DIALOG -> {
                    val navigator = provider.get<DialogFragmentNavigator>("dialog")
                    val destination = navigator.createDestination()
                    destination.id = navData.route.hashCode()
                    destination.setClassName(navData.className)
                    navGraph.addDestination(destination)
                }
                else -> {
                    throw IllegalArgumentException("NavigatorProvider:NavType not found")
                }
            }
            navGraph.setStartDestination(navData.route.hashCode())
        }
        controller.graph = navGraph
    }


}