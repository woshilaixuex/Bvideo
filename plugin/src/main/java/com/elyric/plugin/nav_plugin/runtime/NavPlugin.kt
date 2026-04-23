package com.elyric.plugin.nav_plugin.runtime

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.elyric.plugin.nav_plugin.scan.NavClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 注册扫包
 */
class NavPlugin : Plugin<Project> {


    override fun apply(target: Project) {
        target.plugins.withId("com.android.application") {
            val androidComponents = target.extensions.getByType(
                ApplicationAndroidComponentsExtension::class.java
            )

            androidComponents.onVariants(androidComponents.selector().all()) { variant ->
                variant.instrumentation.transformClassesWith(
                    NavClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) {

                }

                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
            }
        }
    }
}