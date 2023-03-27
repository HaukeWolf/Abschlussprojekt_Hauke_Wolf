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

    //Gibt es da einen Besseren Weg? Habe bis jetzt keinen anderen weg gefunden
    private val regexFragment = "(.+)Fragment".toRegex()
    private val regexViewModel = "(.+)ViewModel".toRegex()

    val regexFragmentSplit = "Fragment".toRegex()
    val regexViewModelSplit = "ViewModel".toRegex()

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

        //Überhaupt nicht hübsch I know =/ wusste mir nicht anders zu helfen //
        //Update: auch super unnötig hab ich nur noch zu testzwecken drin damit alle Itmes in der map "Gegenüber" liegen
        var listPositionsCounter = 0

        for ((counter) in allKSFileNames.withIndex()) {

            regexFragment.find(allKSFileNames[counter])
                ?.let {
                    allFragmentFileNames.add(listPositionsCounter, it.value)

                    listPositionsCounter++
                }

            regexViewModel.find(allKSFileNames[counter])
                ?.let {
                    allViewModelFileNames.add(listPositionsCounter - 1, it.value)
                }
        }
        checkForTowOfAKind(allFragmentFileNames, allViewModelFileNames)
    }

    private fun checkForTowOfAKind(
        allFragmentFileNames: MutableList<String>,
        allViewModelFileNames: MutableList<String>
    ) {

        for ((counter, string) in allFragmentFileNames.withIndex()) {

            fileKt += if (regexFragmentSplit.split(string) == regexViewModelSplit.split(
                    allViewModelFileNames[counter]
                )
            ) {
                " heureka "
            } else {
                " nahhBro "
            }
        }

        for ((counter, string) in allViewModelFileNames.withIndex()) {

            fileKt += if (regexViewModelSplit.split(string) == regexFragmentSplit.split(
                    allFragmentFileNames[counter]
                )
            ) {
                " heureka "
            } else {
                " nahhBro "
            }
        }

        //Test Code:

        for (String in allFragmentFileNames) {
            fileKt += ("$String ")
        }

        for (String in allViewModelFileNames) {
            fileKt += ("$String ")
        }
    }


// ! "VordererTeil" + "Fragment" als map machen um abzugleichen ob der "Vordere Teil" auch in der anderen map verfügbar ist!!!

//nicht nach Upper sondern nach Regex Splitten den ich selber rein gebe
//wegen z.B. MainTripFragment -> Main Trip Fragment

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

