package com.elyric.plugin.nav_plugin.runtime

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.elyric.plugin.nav_plugin.trace.TraceTimeClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * KSP is still responsible for navigation registry generation.
 * ASM is used only for opt-in method tracing through @TraceTime.
 */
class NavPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("com.android.application") {
            registerTraceTransform(target)
        }
        target.plugins.withId("com.android.library") {
            registerTraceTransform(target)
        }
        target.logger.lifecycle("NavPlugin: KSP handles navigation registry; ASM TraceTime transform is ready when this plugin is applied.")
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerTraceTransform(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            as AndroidComponentsExtension<*, *, *>
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                TraceTimeClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) {}
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}
