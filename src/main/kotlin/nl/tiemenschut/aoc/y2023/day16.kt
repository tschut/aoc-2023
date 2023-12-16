package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.util.Direction
import nl.tiemenschut.aoc.lib.util.Direction.*
import nl.tiemenschut.aoc.lib.util.grid.CharGridParser
import nl.tiemenschut.aoc.lib.util.grid.Grid
import nl.tiemenschut.aoc.lib.util.points.Point
import nl.tiemenschut.aoc.lib.util.points.by

typealias Beam = Pair<Point<Int>, Direction>

fun Collection<Beam>.energized(): Int = map { it.first }.toSet().count()

fun Beam.reflectForwardSlash(): Beam = when (second) {
    UP -> Beam(first.right(), RIGHT)
    DOWN -> Beam(first.left(), LEFT)
    LEFT -> Beam(first.down(), DOWN)
    RIGHT -> Beam(first.up(), UP)
}

fun Beam.reflectBackwardSlash(): Beam = when (second) {
    UP -> Beam(first.left(), LEFT)
    DOWN -> Beam(first.right(), RIGHT)
    LEFT -> Beam(first.up(), UP)
    RIGHT -> Beam(first.down(), DOWN)
}

fun Beam.forward() = Beam(first.moved(second), second)

fun Beam.split() = when (second) {
    in listOf(RIGHT, LEFT) -> setOf(copy(second = UP).forward(), copy(second = DOWN).forward())
    else -> setOf(copy(second = LEFT).forward(), copy(second = RIGHT).forward())
}

fun Beam.next(c: Char): Set<Beam> = when (c) {
    '.' -> setOf(forward())
    '/' -> setOf(reflectForwardSlash())
    '\\' -> setOf(reflectBackwardSlash())
    '-' -> if (second in listOf(RIGHT, LEFT)) setOf(forward()) else split()
    '|' -> if (second in listOf(UP, DOWN)) setOf(forward()) else split()
    else -> throw RuntimeException()
}

operator fun <T> Grid<T>.contains(p: Point<Int>) = (p.x in 0 until width() && p.y in 0 until height())

fun main() {
    aoc(CharGridParser) {
        puzzle { 2023 day 16 }

        fun propagateBeams(beams: Set<Beam>, visited: Set<Beam>, grid: Grid<Char>): Set<Beam> {
            val next = beams
                .flatMap { it.next(grid[it.first]) }
                .filter { it.first in grid }
                .toSet() - visited

            if (visited.containsAll(next)) return visited

            return propagateBeams(next, visited + next, grid)
        }

        part1 { input ->
            val startingBeams = setOf(Beam(0 by 0, RIGHT))
            propagateBeams(startingBeams, startingBeams, input).energized()
        }

        part2 { input ->
            val startingBeams = buildSet {
                addAll((0 until input.width()).map { Beam(it by 0, DOWN) })
                addAll((0 until input.width()).map { Beam(it by input.height() - 1, UP) })
                addAll((0 until input.height()).map { Beam(0 by it, RIGHT) })
                addAll((0 until input.height()).map { Beam(input.height() - 1 by it, LEFT) })
            }
            startingBeams.maxOf { beam ->
                propagateBeams(setOf(beam), setOf(beam), input).energized()
            }
        }
    }
}
