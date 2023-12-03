package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser

const val SYMBOL = -1
const val NOTHING = -2
const val GEAR = -3

object EngineSchematicParser : InputParser<List<MutableList<Int>>> {
    override fun parse(input: String) = input.split("\n").map {
        it.map { c ->
            when {
                c.isDigit() -> c.digitToInt()
                c == '.' -> NOTHING
                c == '*' -> GEAR
                else -> SYMBOL
            }
        }.toMutableList()
    }
}

fun main() {
    aoc(EngineSchematicParser) {
        puzzle { 2023 day 3 }

        fun Int.isSymbol() = this in listOf(SYMBOL, GEAR)

        fun MutableList<Int>.partNumber(previousLine: List<Int>, startIndex: Int, nextLine: List<Int>): Int {
            var endIndex = startIndex

            // find value
            var value = ""
            while (endIndex < size && get(endIndex) >= 0) {
                value = "$value${get(endIndex)}"
                this[endIndex] = NOTHING
                endIndex++
            }

            // determine if it is a partnumber or not
            val p1 = (startIndex - 1).coerceAtLeast(0)
            val p2 = (endIndex).coerceAtMost(size - 1)

            return if (previousLine.slice(p1..p2).any { it.isSymbol() }
                || slice(p1..p2).any { it.isSymbol() }
                || nextLine.slice(p1..p2).any { it.isSymbol() }) {
                value.toInt()
            } else {
                0
            }
        }

        part1 { input ->
            val emptyLine = List(input.size) { NOTHING }

            input.mapIndexed { y, line ->
                line.mapIndexed { x, i ->
                    if (i > 0) {
                        line.partNumber(
                            previousLine = if (y == 0) emptyLine else input[y - 1],
                            startIndex = x,
                            nextLine = if (y == input.size - 1) emptyLine else input[y + 1]
                        )
                    } else 0
                }.sum()
            }.sum()
        }

        fun List<Int>.adjacentNumbers(startIndex: Int): List<Int> {
            return ((startIndex - 1).coerceAtLeast(0) .. (startIndex + 1).coerceAtMost(size - 1)).mapNotNull {
                if (get(it) >= 0) {
                    var range: IntRange = it..it
                    while (range.first > 0 && get(range.first - 1) >= 0) range = range.first - 1..range.last
                    while (range.last < size - 1 && get(range.last + 1) >= 0) range = range.first..range.last + 1
                    range
                } else null
            }
                .toSet()
                .map { slice(it).joinToString("").toInt() }
        }

        part2 { input ->
            input.mapIndexed { y, line ->
                line.mapIndexed { x, i ->
                    if (i == GEAR) {
                        buildList {
                            addAll(line.adjacentNumbers(x))
                            if (y > 0) addAll(input[y - 1].adjacentNumbers(x))
                            if (y < input.size - 1) addAll(input[y + 1].adjacentNumbers(x))
                        }
                            .takeIf { it.size == 2 }
                            ?.let { it[0] * it[1] } ?: 0
                    } else 0
                }.sum()
            }.sum()
        }
    }
}
