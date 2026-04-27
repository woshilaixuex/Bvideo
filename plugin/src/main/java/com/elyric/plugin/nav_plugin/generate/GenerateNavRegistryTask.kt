package com.elyric.plugin.nav_plugin.generate

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateNavRegistryTask(): DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty
    @TaskAction
    fun generate() {
        NavRegistryGenerator().generateNavRegistry(outputDir.get().asFile)
    }
}
