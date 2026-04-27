package com.elyric.plugin.nav_plugin.scan

import com.elyric.nav_api.NavData
import com.elyric.nav_api.NavType
import com.elyric.plugin.nav_plugin.model.NavDestinationStore
import com.elyric.plugin.nav_plugin.model.PluginInfo
import com.elyric.plugin.nav_plugin.model.PluginInfo.KEY_AS_START
import com.elyric.plugin.nav_plugin.model.PluginInfo.KEY_ROUTE
import com.elyric.plugin.nav_plugin.model.PluginInfo.NAV_API_PACKAGE_NAME
import com.elyric.plugin.nav_plugin.model.PluginInfo.NAV_DESTINATION_TYPE
import com.elyric.plugin.nav_plugin.model.PluginInfo.NAV_DESTINATION_DESCRIPTOR
import com.elyric.plugin.nav_plugin.model.PluginInfo.NAV_REGISTRY_NAME
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
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import java.io.File

class NavClassVisitor(
    api: Int,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(api, nextClassVisitor) {
    private var internalClassName: String = ""
    private var route: String? = null
    private var navType: NavType? = null
    private var asStart:Boolean = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        internalClassName = name.orEmpty()
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        val parentVisitor = super.visitAnnotation(descriptor, visible)
        println(descriptor)
        if (descriptor != NAV_DESTINATION_DESCRIPTOR) {
            return parentVisitor
        }
        return object : AnnotationVisitor(api, parentVisitor) {
            override fun visit(name: String?, value: Any?) {
                when (name) {
                    KEY_ROUTE -> route = value as? String
                    KEY_AS_START -> asStart = value as Boolean
                }
                super.visit(name, value)
            }

            override fun visitEnum(name: String?, descriptor: String?, value: String?) {
                if (descriptor == NAV_DESTINATION_TYPE && value != null) {
                    navType = runCatching {
                        NavType.valueOf(value)
                    }.getOrElse { NavType.NONE }
                }
                super.visitEnum(name, descriptor, value)
            }
        }
    }

    override fun visitEnd() {
        val route = route
        val navType = navType
        val asStart = asStart
        if (!route.isNullOrBlank() && navType != null) {
            val navData = NavData(
                route = route,
                className = internalClassName.replace('/', '.'),
                navType = navType,
                asStart = asStart
            )
            println("NavPlugin found destination: $navData")
            NavDestinationStore.add(navData)
        }
        super.visitEnd()
    }
    fun generateNavRegistry(outputDir: File, destinations: List<NavData>) {
        val navDataClassName = ClassName(NAV_API_PACKAGE_NAME, "NavData")
        val navTypeClassName = ClassName(NAV_API_PACKAGE_NAME, "NavType")
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
        val typeSpec = TypeSpec.objectBuilder(NAV_REGISTRY_NAME)
            .addProperty(destinationsProperty)
            .addInitializerBlock(initBlock)
            .addFunction(getFunction)
            .build()
        FileSpec.builder(NAV_API_PACKAGE_NAME, NAV_REGISTRY_NAME)
            .addType(typeSpec)
            .build()
            .writeTo(outputDir)
    }

    private companion object {

    }

}
