package br.com.phs.processor

import br.com.phs.annotations.MainEvent
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

class MainEventProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation(MainEvent::class.qualifiedName!!)
        val unableToProcess = symbols.filterNot { it.validate() }

        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(MainEventClassVisitor(dependencies), Unit) }

        return unableToProcess.toList()

    }

    private inner class MainEventClassVisitor(val dependencies: Dependencies): KSVisitorVoid() {

        private val packageName = "br.com.phs.helloksp"

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            if (classDeclaration.isAbstract()) {
                logger.error(
                    "||Class Annotated with MainEvent should kotlin data class",
                    classDeclaration
                )
            }

            if (classDeclaration.classKind != ClassKind.CLASS) {
                logger.error(
                    "||Class Annotated with Projections should kotlin data class",
                    classDeclaration
                )
            }

            val className = classDeclaration.simpleName.getShortName()
            val classPackage = classDeclaration.packageName.asString() + "." + className
            val classVariableNameInCamelCase = className.replaceFirst(className[0], className[0].lowercaseChar()) //need this for using in generated code

            logger.warn("package $classPackage")

            val properties = classDeclaration.primaryConstructor?.parameters ?: emptyList()
            if (properties.isEmpty()) {
                logger.error("No variables found in class", classDeclaration)
            }

            val hashmapEntries = StringBuilder()
            val bundleEntries = StringBuilder()
            for (prop in properties) {

                if (prop.isNotKotlinPrimitive()) {
                    logger.error("||Event params variables should be private", prop)
                }

                val propName = prop.name?.getShortName()?: ""
                logger.warn("||${prop.name?.getShortName()}")

                hashmapEntries.append(
                    """put("$propName", $classVariableNameInCamelCase.$propName);""".trimMargin()
                )

                val propPrimitiveTypeName = prop.getPrimitiveTypeName()

                bundleEntries.append(
                    """put$propPrimitiveTypeName("$propName", $classVariableNameInCamelCase.$propName);""".trimMargin()
                )

            }

            val toGenerateFileName = "${classDeclaration.simpleName.getShortName()}Event"

            val outputStream: OutputStream = codeGenerator.createNewFile(
                dependencies = dependencies,
                packageName = packageName,
                fileName = toGenerateFileName
            )

            outputStream.write(
                """
                    |package $packageName
                    
                    |import $classPackage
                    |import android.os.Bundle
                    |import $packageName.Event
                    
                    |class $toGenerateFileName(val $classVariableNameInCamelCase: $className): Event {
                    
                    |    override fun getHashMapOfParamsForCustomAnalytics(): HashMap<*, *>? {
                    |        val map = HashMap<String, Any>().apply {
                    |            $hashmapEntries
                    |        }
                    |        return map
                    |    }
                        
                    |    override fun getBundleOfParamsForFirebase(): Bundle {
                    |        val bundle = Bundle().apply {
                    |            $bundleEntries
                    |        }
                    |        return bundle
                    |    }
                        
                    |}
                """.trimMargin().toByteArray())

        }

    }

}
