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

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    fun emit(s: String, indent: String) {
        file += ("$indent$s\n")
    }

    private val fileKt =
        codeGenerator.createNewFile(Dependencies(false), "", "ProcessedFiles", "kt")


    // zwei listen
    //eine Fragmenten Liste
    //eine Model Liste
    // aus den jewaligen listen dann das passende paar raussuchen

    // next = kontrolle ob die Datein im gleichen packet sind


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

        val files = resolver.getAllFiles()
        emit("TestProcessor: process()", "")

        for (file in files) {
            emit("TestProcessor: processing ${file.fileName}", "")
        }

        val javaFile = codeGenerator.createNewFile(Dependencies(false), "", "Generated", "java")
        javaFile += ("class Generated {}")


        fileKt += ("public class checkAllProcessedFiles{\n")
        fileKt += ("val  allProcessedList: MutableList<String> = mutableListOf() \n")
        fileKt += ("fun allTheProcessedFiles() : MutableList<String> { return allProcessedList }\n")
        fileKt += ("}")

        getAllProcessedFiles(resolver)

        // regex setup

        //Konnte nicht verarbeitet werden
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

        val sortStringFragment = "Fragment"
        val sortStringViewModel = "ViewModel"

        val allFragmentFileNames: MutableList<String> = mutableListOf()
        val allViewModelFileNames: MutableList<String> = mutableListOf()

        for ((counter) in allKSFileNames.withIndex()) {

            if (allKSFileNames[counter].contains(sortStringFragment)) {
                allFragmentFileNames.add(allKSFileNames[counter])
            } else if (allKSFileNames[counter].contains(sortStringViewModel)) {
                allViewModelFileNames.add(allKSFileNames[counter])
            }
        }
        checkForTowOfAKind(allFragmentFileNames, allViewModelFileNames)
    }

    private fun checkForTowOfAKind(
        allFragmentFileNames: MutableList<String>,
        allViewModelFileNames: MutableList<String>
    ) {




        if(allFragmentFileNames[1].contains(allViewModelFileNames[1]))
        for (String in allFragmentFileNames) {
            fileKt += ("$String ")
        }


    }

    /*

    // codeschnipsel zum Testen
    for (String in allFragmentFileNames) {
          fileKt += ("$String ")
      }*/


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