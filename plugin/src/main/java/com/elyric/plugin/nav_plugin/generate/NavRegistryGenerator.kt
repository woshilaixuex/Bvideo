package com.elyric.plugin.nav_plugin.generate

import com.elyric.nav_api.NavData
import com.elyric.nav_api.NavType
import com.elyric.plugin.nav_plugin.model.PluginInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MUTABLE_LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import kotlin.collections.forEach

class NavRegistryGenerator(private val project: Project){
    fun generateNavRegistry(destinations: List<NavData>) {
        val navDataClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavData")
        val navTypeClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavType")
        val destinationsType = MUTABLE_LIST.parameterizedBy(navDataClassName)
        val returnType = LIST.parameterizedBy(navDataClassName)
        val initBlock = CodeBlock.builder().apply {
            destinations.forEach { destination ->
                addStatement(
                    "destinations.add(%T(route = %S, className = %S, navType = %T.%L))",
                    navDataClassName,
                    destination.route,
                    destination.className,
                    navTypeClassName,
                    destination.navType.name
                )
            }
        }.build()

        val destinationsProperty = PropertySpec.builder(
            "destinations",
            destinationsType,
            KModifier.PRIVATE
        ).initializer("mutableListOf()").build()

        val getFunction = FunSpec.builder("get")
            .returns(returnType)
            .addStatement("return destinations.toList()")
            .build()

        val typeSpec = TypeSpec.objectBuilder(PluginInfo.NAV_REGISTRY_NAME)
            .addProperty(destinationsProperty)
            .addInitializerBlock(initBlock)
            .addFunction(getFunction)
            .build()

        val fileSpec =
            FileSpec.builder(PluginInfo.NAV_API_PACKAGE_NAME, PluginInfo.NAV_REGISTRY_NAME)
                .addType(typeSpec)
                .build()
        val runtimeProject = project.rootProject.findProject(PluginInfo.NAV_RUNTIME_MODULE_NAME)
            ?: throw GradleException("NavRegistryGenerator: navRuntime project not found ${PluginInfo.NAV_RUNTIME_MODULE_NAME}")

        val sourceSet = runtimeProject.extensions.findByName("sourceSets") as SourceSetContainer
        val outputFileDir = sourceSet.first().java.srcDirs.first().absoluteFile
        println("NavRegistryGenerator outputFileDir:${outputFileDir.absolutePath}")
        fileSpec.writeTo(outputFileDir)
    }
}