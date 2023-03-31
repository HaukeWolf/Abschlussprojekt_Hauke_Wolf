package com.cgi.libksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

class KSPProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    lateinit var file: OutputStream
    var invoked = false

    private val regexFragment = "(.+)Fragment".toRegex()
    private val regexViewModel = "(.+)ViewModel".toRegex()

    private val regexFragmentSplit = "Fragment".toRegex()
    private val regexViewModelSplit = "ViewModel".toRegex()

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    fun emit(s: String, indent: String) {
        file += ("$indent$s\n")
    }

    private val fileKt =
        codeGenerator.createNewFile(Dependencies(false), "", "ProcessedFiles", "kt")


    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.cgi.kspAnnotations.FunctionTemp")
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        if (invoked) {
            return emptyList()
        }

        logger.info(options.entries.toString())

        file = codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor", "log")
        emit("TestProcessor: init($options)", "")

        val files = resolver.getAllFiles()
        emit("TestProcessor: process()", "")

        for (file in files) {
            emit("TestProcessor: processing ${file.fileName}", "")
        }

        val javaFile = codeGenerator.createNewFile(Dependencies(false), "", "Generated", "java")
        javaFile += ("class Generated {}")

        getAllProcessedFiles(resolver)

        invoked = true
        return symbols.filterNot { it.validate() }.toList()
    }

    private fun getAllProcessedFiles(resolver: Resolver) {

        val allKSFiles = resolver.getAllFiles()
        val allKSFileNames: MutableList<String> = mutableListOf()

        for (file in allKSFiles) {
            allKSFileNames.add(file.fileName)
        }
        sortFilesByFunction(allKSFileNames)
    }

    private fun sortFilesByFunction(allKSFileNames: MutableList<String>) {

        val allFragmentFileNames: MutableList<String> = mutableListOf()
        val allViewModelFileNames: MutableList<String> = mutableListOf()

        for ((counter) in allKSFileNames.withIndex()) {

            regexFragment.find(allKSFileNames[counter])
                ?.let {
                    allFragmentFileNames.add(it.value)
                }

            regexViewModel.find(allKSFileNames[counter])
                ?.let {
                    allViewModelFileNames.add(it.value)
                }
        }
        checkForTowOfAKind(allFragmentFileNames, allViewModelFileNames)
    }

    // Hallo Paul
    // Hab hier nicht all zu viel verändert. Deinen Vorschlag eine Liste zu verwenden hab ich angenommen.
    // So funktioniert es erstmal auf stumpfe weise mit dem vergleichen ^^
    // hab mich auch mit dem Logger auseinandergesetzt und logge mir schonmal die ergebnisse raus
    //
    // demnächst arbeite ich dann an der Dynamik des vergleiches
    // auch hab ich mich mit der "Nächsten" regel auseinandergesetzt -> Prüfen ob die Processed datein in dem "richtigen" Packet liegen


    private fun checkForTowOfAKind(
        allFragmentFileNames: MutableList<String>,
        allViewModelFileNames: MutableList<String>
    ) {

        // Der Vergeich funktioniert soweit ist aber noch nicht hübsch
        for ((counter, string) in allFragmentFileNames.withIndex()) {

            if (regexFragmentSplit.split(string) == regexViewModelSplit.split(
                    allViewModelFileNames[counter]
                )
            ) {
                logger.warn("Success")
            } else {

                logger.warn("Error from up")
            }
        }


        logger.warn("Hello from MyClass")

        for ((counter, string) in allViewModelFileNames.withIndex()) {

            if (regexViewModelSplit.split(string) == regexFragmentSplit.split(
                    allFragmentFileNames[counter]
                )
            ) {
                logger.warn("Success")
            } else {

                logger.warn("Error from down")
            }
        }

        //Test Code:

/*        for (String in allFragmentFileNames) {
            fileKt += ("$String ")
        }

        for (String in allViewModelFileNames) {
            fileKt += ("$String ")
        }*/
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

