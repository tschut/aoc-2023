package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.lib.util.Direction
import nl.tiemenschut.aoc.lib.util.Direction.*
import nl.tiemenschut.aoc.lib.util.points.Point
import nl.tiemenschut.aoc.lib.util.points.by
import kotlin.math.abs

data class DigPlanRow(val direction: Direction, val steps: Int)

typealias DigPlan = List<DigPlanRow>

object DigPlanParser : InputParser<DigPlan> {
    private fun String.toDirection() =
        if (this == "U") UP else if (this == "D") DOWN else if (this == "R") RIGHT else LEFT

    override fun parse(input: String) = "([RDLU]) (\\d+).*".toRegex().findAll(input).map {
        it.groupValues.let { (_, d, s) -> DigPlanRow(d.toDirection(), s.toInt()) }
    }.toList()
}

object DigPlanPart2Parser : InputParser<DigPlan> {
    private fun String.toDirection() =
        if (this == "3") UP else if (this == "1") DOWN else if (this == "0") RIGHT else LEFT

    override fun parse(input: String) = ".*\\(#(.{5})(.)\\)".toRegex().findAll(input).map {
        it.groupValues.let { (_, s, d) -> DigPlanRow(d.toDirection(), s.toInt(16)) }
    }.toList()
}

fun main() {
    aoc {
        puzzle { 2023 day 18 }

        fun Point<Int>.moved(d: Direction, count: Int): Point<Int> = when (d) {
            UP -> x by y - count
            Direction.DOWN -> x by y + count
            LEFT -> x - count by y
            Direction.RIGHT -> x + count by y
        }

        fun List<Point<Int>>.surface() = abs(this.windowed(2).sumOf { (p, q) ->
            (p.x + q.x).toLong() * (q.y - p.y).toLong()
        } / 2)

        fun List<Point<Int>>.length(): Long =
            this.windowed(2, step = 1).sumOf { (p, q) -> p.manhattanDistance(q).toLong() }

        part1 { input ->
            DigPlanParser.parse(input).runningFold((0 by 0) as Point<Int>) { acc, digPlanRow ->
                acc.moved(digPlanRow.direction, digPlanRow.steps)
            }.let {
                it.surface() + it.length() / 2 + 1
            }
        }

        part2 { input ->
            DigPlanPart2Parser.parse(input).runningFold((0 by 0) as Point<Int>) { acc, digPlanRow ->
                acc.moved(digPlanRow.direction, digPlanRow.steps)
            }.let {
                it.surface() + it.length() / 2 + 1
            }
        }
    }
}


