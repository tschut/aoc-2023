package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.lib.util.points.Point
import nl.tiemenschut.aoc.lib.util.points.PointInt
import nl.tiemenschut.aoc.lib.util.points.by

class TubeMap(
    val grid: List<List<Char>>,
    val start: PointInt
) {
    fun findConnections(p: Point<Int>): List<Point<Int>> = buildList {
        if (grid[p].connectsUp() && grid[p.up()].connectsDown()) add(p.up())
        if (grid[p].connectsDown() && grid[p.down()].connectsUp()) add(p.down())
        if (grid[p].connectsLeft() && grid[p.left()].connectsRight()) add(p.left())
        if (grid[p].connectsRight() && grid[p.right()].connectsLeft()) add(p.right())
    }

    operator fun List<List<Char>>.get(p: Point<Int>): Char = this[p.x][p.y]

    private fun Char.connectsUp() = this in listOf('S', '|', 'L', 'J')
    private fun Char.connectsDown() = this in listOf('S', '|', 'F', '7')
    private fun Char.connectsLeft() = this in listOf('S', '-', 'J', '7')
    private fun Char.connectsRight() = this in listOf('S', '-', 'F', 'L')
}

object CharGridParser : InputParser<TubeMap> {
    override fun parse(input: String): TubeMap {
        val lines = input.split("\n")

        var start = 0 by 0
        val grid = List(lines[0].length) { MutableList(lines.size) { '.' } }.also { grid ->
            for (y in lines.indices) {
                for (x in lines[y].indices) {
                    grid[x][y] = lines[y][x]
                    if (grid[x][y] == 'S') start = x by y
                }
            }
        }

        return TubeMap(grid, start)
    }
}

fun main() {
    aoc(CharGridParser) {
        puzzle { 2023 day 10 }

        part1 { input ->
            tailrec fun findDistance(c1: Point<Int>, c2: Point<Int>, seen: Set<Point<Int>>, distance: Int): Int {
                val connections = input.findConnections(c1) + input.findConnections(c2)
                val newConnections = connections.filter { it !in seen }.toSet()
                return if (newConnections.size == 1) {
                    distance
                }
                else {
                    findDistance(newConnections.first(), newConnections.last(), seen + newConnections, distance + 1)
                }
            }

            findDistance(input.start, input.start, mutableSetOf(input.start), 1)
        }

        part2(submit = false) { input ->
        }
    }
}


