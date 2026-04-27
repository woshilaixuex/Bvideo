package com.elyric.nav_api

object NavRegistry {
    private const val GENERATED_REGISTRY_CLASS = "com.elyric.nav_api.NavRegistryEntries"
    private const val GENERATED_REGISTRY_METHOD = "get"

    fun get(): List<NavData> {
        return try {
            val clazz = Class.forName(GENERATED_REGISTRY_CLASS)
            val instance = clazz.getField("INSTANCE").get(null)
            val method = clazz.getMethod(GENERATED_REGISTRY_METHOD)
            @Suppress("UNCHECKED_CAST")
            method.invoke(instance) as? List<NavData>
                ?: throw IllegalStateException("NavRegistry: generated registry returned null")
        } catch (error: Throwable) {
            throw IllegalStateException(
                "NavRegistry: generated registry not found. Ensure KSP ran for the app module.",
                error
            )
        }
    }
}
