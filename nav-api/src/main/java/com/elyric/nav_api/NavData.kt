package com.elyric.nav_api

data class NavData(
    val route: String,
    val className: String,
    val navType: NavType,
    val asStart: Boolean = false
)
