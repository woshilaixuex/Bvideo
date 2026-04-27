package com.elyric.plugin.nav_plugin.runtime

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ASM path is intentionally disabled while the project uses KSP-based registry generation.
 */
class NavPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.logger.lifecycle("NavPlugin: ASM processing is disabled. Use KSP dependency `ksp(project(\":plugin\"))` in the app module.")
    }
}
