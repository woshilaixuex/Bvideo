package com.elyric.plugin.nav_plugin.ksp

import com.elyric.plugin.nav_plugin.model.PluginInfo
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
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

internal class NavRegistryKspGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) {
    fun generate(destinations: List<KSClassDeclaration>) {
        val entries = destinations.mapNotNull { it.toDestinationEntry(logger) }
            .sortedWith(compareBy<DestinationEntry> { !it.asStart }.thenBy { it.route }.thenBy { it.className })

        val navDataClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavData")
        val navTypeClassName = ClassName(PluginInfo.NAV_API_PACKAGE_NAME, "NavType")
        val destinationsType = MUTABLE_LIST.parameterizedBy(navDataClassName)
        val returnType = LIST.parameterizedBy(navDataClassName)
        val initBlock = CodeBlock.builder().apply {
            entries.forEach { entry ->
                addStatement(
                    "destinations.add(%T(route = %S, className = %S, navType = %T.%L, asStart = %L))",
                    navDataClassName,
                    entry.route,
                    entry.className,
                    navTypeClassName,
                    entry.navType,
                    entry.asStart
                )
            }
        }.build()

        val typeSpec = TypeSpec.objectBuilder(PluginInfo.NAV_REGISTRY_ENTRIES_NAME)
            .addProperty(
                PropertySpec.builder(
                    "destinations",
                    destinationsType,
                    KModifier.PRIVATE
                ).initializer("mutableListOf()").build()
            )
            .addInitializerBlock(initBlock)
            .addFunction(
                FunSpec.builder("get")
                    .returns(returnType)
                    .addStatement("return destinations.toList()")
                    .build()
            )
            .build()

        val fileSpec = FileSpec.builder(
            PluginInfo.NAV_API_PACKAGE_NAME,
            PluginInfo.NAV_REGISTRY_ENTRIES_NAME
        ).addType(typeSpec).build()

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                *destinations.mapNotNull { it.containingFile }.distinct().toTypedArray()
            ),
            packageName = PluginInfo.NAV_API_PACKAGE_NAME,
            fileName = PluginInfo.NAV_REGISTRY_ENTRIES_NAME
        )
        file.writer().use { writer ->
            fileSpec.writeTo(writer)
        }
    }
}

private data class DestinationEntry(
    val route: String,
    val className: String,
    val navType: String,
    val asStart: Boolean
)

private fun KSClassDeclaration.toDestinationEntry(logger: KSPLogger): DestinationEntry? {
    val annotation = annotations.firstOrNull {
        it.annotationType.resolve().declaration.qualifiedName?.asString() ==
            "${PluginInfo.NAV_API_PACKAGE_NAME}.NavDestination"
    } ?: return null

    val args = annotation.arguments.associateBy { it.name?.asString().orEmpty() }
    val route = args[PluginInfo.KEY_ROUTE]?.value as? String
    if (route.isNullOrBlank()) {
        logger.error("@NavDestination route must not be blank", this)
        return null
    }

    val className = qualifiedName?.asString()
    if (className.isNullOrBlank()) {
        logger.error("Failed to resolve qualified class name", this)
        return null
    }

    return DestinationEntry(
        route = route,
        className = className,
        navType = annotation.readNavTypeName(),
        asStart = args[PluginInfo.KEY_AS_START]?.value as? Boolean ?: false
    )
}

private fun KSAnnotation.readNavTypeName(): String {
    val typeArgument = arguments.firstOrNull { it.name?.asString() == PluginInfo.KEY_TYPE }?.value
    return when (typeArgument) {
        is KSClassDeclaration -> typeArgument.simpleName.asString()
        is KSType -> typeArgument.declaration.simpleName.asString()
        null -> "NONE"
        else -> typeArgument.toString().substringAfterLast('.')
    }
}
