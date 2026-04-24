package com.elyric.nav_api

object NavRegistry {
    private val destinations = mutableListOf<NavData>()
    init {

    }
    fun get(): List<NavData> {
        val list = destinations.toList()
        return list
    }
}