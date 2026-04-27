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
        val destinations = NavRegistry.get()
        var startDestinationId: Int? = null

        destinations.forEach { navData ->
            val destinationId = navData.route.hashCode()
            when (navData.navType) {
                NavType.ACTIVITY -> {
                    val navigator = provider.get<ActivityNavigator>("activity")
                    val destination = navigator.createDestination()
                    destination.id = destinationId
                    destination.setComponentName(ComponentName(context, navData.className))
                    navGraph.addDestination(destination)
                }
                NavType.FRAGMENT -> {
                    val navigator = provider.get<FragmentNavigator>("fragment")
                    val destination = navigator.createDestination()
                    destination.id = destinationId
                    destination.setClassName(navData.className)
                    navGraph.addDestination(destination)
                }

                NavType.DIALOG -> {
                    val navigator = provider.get<DialogFragmentNavigator>("dialog")
                    val destination = navigator.createDestination()
                    destination.id = destinationId
                    destination.setClassName(navData.className)
                    navGraph.addDestination(destination)
                }
                else -> {
                    throw IllegalArgumentException("NavigatorProvider:NavType not found")
                }
            }
            if (navData.asStart && startDestinationId == null) {
                startDestinationId = destinationId
            }
        }
        val rootStartDestination = startDestinationId
            ?: destinations.firstOrNull()?.route?.hashCode()
            ?: throw IllegalStateException("NavGraphBuilder: no destinations found in NavRegistry")
        navGraph.setStartDestination(rootStartDestination)
        controller.setGraph(navGraph,null)
    }
}
