package com.elyric.plugin.nav_plugin.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class NavKspProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return NavKspProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}

internal class NavKspProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private var processed = false

    override fun process(resolver: com.google.devtools.ksp.processing.Resolver): List<com.google.devtools.ksp.symbol.KSAnnotated> {
        if (processed) {
            logger.warn("NavKspProcessor: skip because already processed")
            return emptyList()
        }

        val annotatedSymbols = resolver.getSymbolsWithAnnotation(
            "com.elyric.nav_api.NavDestination"
        )
        val destinations = mutableListOf<com.google.devtools.ksp.symbol.KSClassDeclaration>()

        annotatedSymbols.forEach { symbol ->
            val declaration = symbol as? com.google.devtools.ksp.symbol.KSClassDeclaration
            if (declaration == null) {
                logger.warn("NavKspProcessor: skip non-class symbol ${symbol::class.qualifiedName}")
                return@forEach
            }
            destinations += declaration
        }

        logger.warn("NavKspProcessor: collected ${destinations.size} destination(s)")
        NavRegistryKspGenerator(codeGenerator, logger).generate(destinations)
        logger.warn("NavKspProcessor: registry generated")
        processed = true
        return emptyList()
    }
}
