package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.AsListOfStrings

fun main() {
    aoc(AsListOfStrings) {
        puzzle { 2023 day 9 }

        fun List<Long>.findNext(): Long {
            return if (all { it == 0L }) 0L
            else windowed(2).map { it[1] - it[0] }.findNext() + last()
        }

        part1 { input ->
            input.sumOf { line -> line.split(" ").map { it.toLong() }.findNext() }
        }

        fun List<Long>.findPrevious(): Long {
            return if (all { it == 0L }) 0L
            else first() - windowed(2).map { it[1] - it[0] }.findPrevious()
        }

        part2 { input ->
            input.sumOf { line -> line.split(" ").map { it.toLong() }.findPrevious() }
        }
    }
}
