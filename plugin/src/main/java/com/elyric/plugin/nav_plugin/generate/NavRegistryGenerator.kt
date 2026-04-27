package com.elyric.plugin.nav_plugin.generate

import com.elyric.plugin.nav_plugin.model.NavDestinationStore
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
import java.io.File
import kotlin.collections.forEach

class NavRegistryGenerator {
    fun generateNavRegistry(outputDir: File) {
        val navDataClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavData")
        val navTypeClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavType")
        val destinationsType = MUTABLE_LIST.parameterizedBy(navDataClassName)
        val returnType = LIST.parameterizedBy(navDataClassName)
        val initBlock = CodeBlock.builder().apply {
            NavDestinationStore.navDatas.forEach { destination ->
                addStatement(
                    "destinations.add(%T(route = %S,\n className = %S,\n navType = %T.%L,\n asStart = %L))",
                    navDataClassName,
                    destination.route,
                    destination.className,
                    navTypeClassName,
                    destination.navType.name,
                    destination.asStart
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

        FileSpec.builder(PluginInfo.NAV_API_PACKAGE_NAME, PluginInfo.NAV_REGISTRY_NAME)
            .addType(typeSpec)
            .build()
            .writeTo(outputDir)
        NavDestinationStore.clear()
    }
}
