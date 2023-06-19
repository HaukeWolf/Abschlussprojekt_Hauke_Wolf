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

    private lateinit var file: OutputStream
    private var invoked = false

    private val regexFragment = "(.+)Fragment".toRegex()
    private val regexViewModel = "(.+)ViewModel".toRegex()


    private val regexFragmentSplit = "Fragment".toRegex()
    private val regexViewModelSplit = "ViewModel".toRegex()

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    private fun emit(s: String, indent: String) {
        file += ("$indent$s\n")
    }

    private val fileKt = codeGenerator.createNewFile(Dependencies(false), "", "ProcessedFiles", "kt")
    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (invoked) {
            return emptyList()
        }

        val symbols = resolver.getSymbolsWithAnnotation("com.cgi.kspAnnotations.ClassAnnotationKSP")
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        logger.info(options.entries.toString())

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

        for ((counter, string) in allFragmentFileNames.withIndex()) {
            if (regexFragmentSplit.split(string) == regexViewModelSplit.split(allViewModelFileNames[counter])
            ) {
                logger.warn("Success Found a match")
            } else {

                logger.warn("Error! found no match!")
            }
        }

        for ((counter, string) in allViewModelFileNames.withIndex()) {

            if (regexFragment.split(string) == regexViewModel.split(allFragmentFileNames[counter])
            ) {
                logger.warn("Success for processing ViewModels")
            } else {

                logger.warn("Error from processing ViewModels")
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

