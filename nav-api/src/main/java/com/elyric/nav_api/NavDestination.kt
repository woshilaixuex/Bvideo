package com.elyric.nav_api

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class NavDestination(
    val route: String,
    val type: NavType = NavType.NONE,
    val asStart: Boolean = false
)
