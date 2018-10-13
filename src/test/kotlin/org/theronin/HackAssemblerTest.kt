package org.theronin

import org.junit.Assert.assertEquals
import org.junit.Test

class HackAssemblerTest {

    @Test
    fun testStrip() {
        val assembly =
                """|//This is a comment on its own line
                   |
                   |D=A-M
                   |@34 //some comment inline
                   |
                   |//another comment
                   |A=32
                   |
                   |//Notice the space before this line!
                   | @variable
                   |""".trimMargin()

        val expected = listOf(
                "D=A-M",
                "@34",
                "A=32",
                "@variable"
        )

        assertEquals(expected, HackAssembler.Companion.strip(assembly))
    }

    @Test
    fun testFindLabelAddresses() {
        val input = listOf(
                "(START)",
                "D=A-M",
                "(LOOP)",
                "@34",
                "A=32",
                "(END)",
                "@variable"
        )

        val expected = mapOf(
                "START" to 0,
                "LOOP" to 1,
                "END" to 3
        )

        assertEquals(expected, HackAssembler.Companion.findLabelAddresses(input))
    }

    @Test
    fun removeLabels() {
        val input = listOf(
                "(START)",
                "D=A-M",
                "(LOOP)",
                "@34",
                "A=32",
                "(END)",
                "@variable"
        )

        val expected = listOf(
                "D=A-M",
                "@34",
                "A=32",
                "@variable"
        )

        assertEquals(expected, HackAssembler.Companion.removeLabels(input))
    }

    @Test
    fun tokeniseComputationInstructionAllValidCommands() {
        val compPrefix = "111"
        val compParts = mapOf(
                "0"     to "0101010",
                "1"     to "0111111",
                "-1"    to "0111010",
                "D"     to "0001100",
                "A"     to "0111000",
                "M"     to "1111000",
                "!D"    to "0001101",
                "!A"    to "0110001",
                "!M"    to "1110001",
                "-D"    to "0001111",
                "-A"    to "0110011",
                "-M"    to "1110011",
                "D+1"   to "0011111",
                "A+1"   to "0110111",
                "M+1"   to "1110111",
                "D-1"   to "0001110",
                "A-1"   to "0110010",
                "M-1"   to "1110010",
                "D+A"   to "0000010",
                "D+M"   to "1000010",
                "D-A"   to "0010011",
                "D-M"   to "1010011",
                "A-D"   to "0000111",
                "M-D"   to "1000111",
                "D&A"   to "0000000",
                "D&M"   to "1000000",
                "D|A"   to "0010101",
                "D|M"   to "1010101"
        )

        val destParts = mapOf(
                ""     to "000",
                "M="   to "001",
                "D="   to "010",
                "A="   to "100",
                "MD="  to "011",
                "AM="  to "101",
                "AD="  to "110",
                "AMD=" to "111"
        )

        val jumpParts = mapOf(
                ""     to "000",
                ";JGT" to "001",
                ";JEQ" to "010",
                ";JLT" to "100",
                ";JNE" to "101",
                ";JGE" to "011",
                ";JLE" to "110",
                ";JMP" to "111"
        )

        val allCommands = mutableMapOf<String, String>()

        for (comp in compParts) {
            for (dest in destParts) {
                for (jump in jumpParts) {
                    allCommands.put(dest.key + comp.key + jump.key, compPrefix + comp.value + dest.value + jump.value)
                }
            }
        }

        allCommands.forEach { e ->
            assertEquals(e.value, HackAssembler.Companion.tokeniseComputationInstruction(e.key))
        }
    }

    @Test
    fun testAssemble() {
        val input  = HackAssemblerTest::class.java.getResource("/fill.asm").readText()
        val output = HackAssemblerTest::class.java.getResource("/fill.hack").readText()
        println(HackAssembler().assemble(input))
        assertEquals(output, HackAssembler().assemble(input))
    }
}