package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.util.grid.CharGridParser
import nl.tiemenschut.aoc.lib.util.grid.Grid
import nl.tiemenschut.aoc.lib.util.points.by
import kotlin.math.max
import kotlin.math.min

fun main() {
    aoc(CharGridParser) {
        puzzle { 2023 day 11 }

        fun Grid<Char>.findEmptyRowsAndColumns() {
            for (x in 0 until width()) {
                val col = (0 until height()).map { x by it }
                if (col.all { get(it) in listOf('.', '2') }) col.forEach { set(it, '2') }
            }
            for (y in 0 until height()) {
                val row = (0 until width()).map { it by y }
                if (row.all { get(it) in listOf('.', '2') }) row.forEach { set(it, '2') }
            }
        }

        fun calculateAllDistances(input: Grid<Char>, scale: Int): Long {
            val galaxies = input.allIndexOff('#')
            var totalDistance = 0L
            galaxies.mapIndexed { fromIndex, from ->
                for (toIndex in fromIndex + 1..<galaxies.size) {
                    val to = galaxies[toIndex]
                    var distance = 0L

                    for (x in min(from.x, to.x)..<max(from.x, to.x)) {
                        distance += if (input[x by from.y] == '2') scale else 1
                    }
                    for (y in min(from.y, to.y)..<max(from.y, to.y)) {
                        distance += if (input[to.x by y] == '2') scale else 1
                    }

                    totalDistance += distance
                }
            }
            return totalDistance
        }

        part1 { input ->
            input.findEmptyRowsAndColumns()

            calculateAllDistances(input, 2)
        }

        part2 { input ->
            input.findEmptyRowsAndColumns()

            calculateAllDistances(input, 1_000_000)

        }
    }
}

