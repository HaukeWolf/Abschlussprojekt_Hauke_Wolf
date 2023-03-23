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

        val allFragmentFileNames: MutableMap<Int, String> = mutableMapOf()
        val allViewModelFileNames: MutableMap<Int, String> = mutableMapOf()

        //Überhaupt nicht hübsch I know =/ wusste mir nicht anders zu helfen //
        //Update: auch super unnötig hab ich nur noch zu testzwecken drin damit alle Itmes in der map "Gegenüber" liegen
        var mapPositionsCounter = 1

        for ((counter) in allKSFileNames.withIndex()) {

            regexFragment.find(allKSFileNames[counter])
                ?.let {
                    allFragmentFileNames[mapPositionsCounter] = it.value

                    mapPositionsCounter++
                }

            regexViewModel.find(allKSFileNames[counter])
                ?.let {
                    allViewModelFileNames[mapPositionsCounter - 1] = it.value
                }
        }

    var counter = 1
    /*  for (element in allFragmentFileNames) {

          allFragmentFileNames[counter] = regexFragmentSplit.split(allFragmentFileNames[counter].toString()).toString()
          counter++
      }*/

    // fileKt += (allFragmentFileNames[1]?.let { regexFragmentSplit.split(it) }.toString())


    checkForTowOfAKind(allFragmentFileNames, allViewModelFileNames)
}

private fun checkForTowOfAKind(
    allFragmentFileNames: MutableMap<Int, String>,
    allViewModelFileNames: MutableMap<Int, String>
) {


    var counter = 1

    if (regexFragmentSplit.split(allFragmentFileNames[1].toString()).contains(
            regexViewModelSplit.split(allViewModelFileNames[1].toString()).toString()
        )) {
        fileKt += (allFragmentFileNames[1].toString())

        fileKt+= "heureka"
    }else{
        fileKt+= "mad"
    }



    if (allFragmentFileNames[counter]!!.contains(allViewModelFileNames[counter].toString())) {
           fileKt += (allFragmentFileNames[1].toString())
       }



    //  fileKt+= (allFragmentFileNames[1].toString())
    /*  if (allFragmentFileNames[1]?.contains(allViewModelFileNames[1].toString()) == true){
           fileKt+= "heureka"
      }*/

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

