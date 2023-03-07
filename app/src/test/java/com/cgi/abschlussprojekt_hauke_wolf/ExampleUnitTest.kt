package com.cgi.abschlussprojekt_hauke_wolf

import com.cgi.libksp.KSP_Processor
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.snaky.ksp.processor.models.ProcessedFunction
import com.snaky.ksp.processor.models.kspPackage
import org.junit.Assert.assertEquals

class FileContentGeneratorTest {
    private fun createGenerator() = KSP_Processor()


    private val visitorTransform: (KSFunctionDeclaration) -> ProcessedFunction? = {
        (it as KSFunctionStub).processedFunction
    }

    @Test
    fun `empty list generates nothing`() {
        val generator = createGenerator()
        val content = generator.generateContent(mapOf(), "function") {
            ProcessedFunction(listOf("import"),
                "text")
        }
        assertEquals("package ${kspPackage()}", content)
    }