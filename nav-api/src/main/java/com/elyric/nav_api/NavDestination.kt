package com.elyric.nav_api

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class NavDestination(
    val route: String,
    val type: NavType
){

}
