package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import kotlin.math.*
import kotlin.math.ceil

data class Race(val time: Long, val distance: Long)

fun main() {
    aoc {
        puzzle { 2023 day 6 }

        fun Race.lowestWinningHold() = ceil((-time + sqrt(((time * time) - (4 * distance)).toDouble())) / -2).roundToInt()
        fun Race.highestWinningHold() = floor((-time - sqrt(((time * time) - (4 * distance)).toDouble())) / -2).roundToInt()
        fun Race.totalWinningHolds() = (highestWinningHold() - lowestWinningHold()) + 1

        part1 {
            listOf(
                Race(56, 499),
                Race(97, 2210),
                Race(77, 1097),
                Race(93, 1440)
            ).map { it.totalWinningHolds() }.reduce { acc, i -> acc * i }
        }

        part2 {
            Race(56977793, 499221010971440).totalWinningHolds()
        }
    }
}


