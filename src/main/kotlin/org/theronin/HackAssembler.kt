package org.theronin
import kotlin.collections.joinToString

class HackAssembler {

    private val symbolTable = mutableMapOf(
            "SCREEN"    to 16384,
            "KBD"       to 24576
    )

    init {
        for (i in 0..15) {
            symbolTable.put("R$i", i)
        }
    }

    private var nextVariableAddress = 16

    /**
     *
     */
    fun assemble(input: String): String {
        val code = strip(input)
        symbolTable += findLabelAddresses(code)
        return removeLabels(code).joinToString("\n", transform = this::tokenise)
    }

    /**
     * Takes a line of assembly code and returns the binary machine instruction
     */
    fun tokenise(line: String): String {
        return if (line.contains('@')) {
            val address = line.substringAfter('@')
            val addressInt =
                    if (address.toIntOrNull() != null) {
                        address.toInt()
                    } else {
                        val address = symbolTable.getOrPut(address, {
                            val ret = nextVariableAddress
                            nextVariableAddress += 1
                            ret
                        })
                        address
                    }
            "0${String.format("%15s", addressInt.toString(2)).replace(' ', '0')}"
        } else {
            tokeniseComputationInstruction(line)
        }
    }


    companion object {

        private val compPrefix = "111"
        private val compParts = mapOf(
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

        private val destParts = mapOf(
                ""     to "000",
                "M"   to "001",
                "D"   to "010",
                "A"   to "100",
                "MD"  to "011",
                "AM"  to "101",
                "AD"  to "110",
                "AMD" to "111"
        )

        private val jumpParts = mapOf(
                ""      to "000",
                "JGT"  to "001",
                "JEQ"  to "010",
                "JLT"  to "100",
                "JNE"  to "101",
                "JGE"  to "011",
                "JLE"  to "110",
                "JMP" to "111"
        )

        /**
         * Creates a List representation of the input Hack program that excludes all empty lines and comments
         */
        fun strip(input: String): List<String> {
            return input.split(
                    delimiters = *charArrayOf('\n'),
                    ignoreCase = true
            ).map { line ->
                line.substringBefore("//").trim()
            }.filterNot { line ->
                line.isBlank() || line.substringBefore("//").isEmpty()
            }
        }

        /**
         * For a stripped assembly code, extract all symbols and their respective addresses in the program
         */
        fun findLabelAddresses(input: List<String>): Map<String, Int> {
            var labelCount = 0
            return input.withIndex().filter { v ->
                v.value.contains("(")
            }.map { v ->
                labelCount += 1
                v.value.trim('(', ')') to (v.index - labelCount + 1)
            }.toMap()
        }

        fun removeLabels(input: List<String>): List<String> {
            return input.filterNot { line -> line.contains("(") }
        }

        fun tokeniseComputationInstruction(instruction: String): String {

            val destPart = instruction.substringBefore("=", "").trim()
            val compPart = instruction.substringBefore(";").substringAfter("=").trim()
            val jumpPart = instruction.substringAfter(";", "").trim()

            return compPrefix + compParts[compPart] + destParts[destPart] + jumpParts[jumpPart]
        }
    }




}