package com.elyric.nav_api

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class TraceTime(
    val tag: String = "BTraceTime",
    val name: String = "",
    val thresholdMs: Long = 0L
)
