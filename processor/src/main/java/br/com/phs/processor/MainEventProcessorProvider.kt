package br.com.phs.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class MainEventProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MainEventProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }

}