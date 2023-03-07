package com.cgi.libksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

class KSP_Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {
    constructor() : this()

    lateinit var file: OutputStream
    var invoked = false

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    fun emit(s: String, indent: String) {
        file += ("$indent$s\n")
    }


    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.cgi.kspAnnotations.FunctionTemp")
            .filterIsInstance<KSClassDeclaration>()

        //tempchanges // aus den Symbols kann man sich strings usw vom AnotationAufruf Holen
        if (!symbols.iterator().hasNext()) return emptyList()

        if (invoked) {
            return emptyList()
        }


        //mit UnitTests abholen
        logger.info(options.entries.toString())

        file = codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor", "log")
        emit("TestProcessor: init($options)", "")

        val javaFile = codeGenerator.createNewFile(Dependencies(false), "", "Generated", "java")
        javaFile += ("class Generated {}")

        val fileKt = codeGenerator.createNewFile(Dependencies(false), "", "HELLO", "java")
        fileKt += ("public class HELLO{\n")
        fileKt += ("public int foo() { return 1234; }\n")
        fileKt += ("}")

        val files = resolver.getAllFiles()
        emit("TestProcessor: process()", "")

        for (file in files) {
            emit("TestProcessor: processing ${file.fileName}", "")

        }

/*


        var counterForTest = 1

        val file: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.cgi.libKSPGenCode",
            fileName = "GeneratedFunctionsSelf" + counterForTest++.toString()
        )

        file += "package com.cgi.gen003\n"


        symbols.forEach { it.accept(Visitor(file), Unit) }

        file.close()
*/

        //Konnte nicht verarbeitet werden
        invoked = true
        return symbols.filterNot { it.validate() }.toList()
    }
//private val file: OutputStream // in Visitor
    inner class Visitor() : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        }
    }
}