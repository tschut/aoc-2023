package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.AsListOfStrings

fun main() {
    aoc(AsListOfStrings) {
        puzzle { 2023 day 1 }

        part1 { input ->
            input.sumOf { line ->
                val numbers = line.filter { it.isDigit() }
                "${numbers.first()}${numbers.last()}".toInt()
            }
        }

        val digits = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        part2 { input ->
            input.sumOf { line ->
                val numbers = line.windowed(size = 5, partialWindows = true).mapNotNull {
                    if (it.first().isDigit()) it.first().digitToInt()
                    else digits[digits.keys.firstOrNull { digit -> it.startsWith(digit) }]
                }
                "${numbers.first()}${numbers.last()}".toInt()
            }
        }
    }
}


