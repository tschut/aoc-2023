package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.lib.util.points.Point
import nl.tiemenschut.aoc.lib.util.points.PointInt
import nl.tiemenschut.aoc.lib.util.points.by

data class TubeMap(
    val grid: List<MutableList<Char>>,
    val start: PointInt,
) {
    fun findConnections(p: Point<Int>): List<Point<Int>> = buildList {
        if (get(p).connectsUp() && get(p.up()).connectsDown()) add(p.up())
        if (get(p).connectsDown() && get(p.down()).connectsUp()) add(p.down())
        if (get(p).connectsLeft() && get(p.left()).connectsRight()) add(p.left())
        if (get(p).connectsRight() && get(p.right()).connectsLeft()) add(p.right())
    }

    fun get(p: Point<Int>): Char = grid[p.x][p.y]
}

fun Char.connectsUp() = this in listOf('S', '|', 'L', 'J')
fun Char.connectsDown() = this in listOf('S', '|', 'F', '7')
fun Char.connectsLeft() = this in listOf('S', '-', 'J', '7')
fun Char.connectsRight() = this in listOf('S', '-', 'F', 'L')

object TubeMapParser : InputParser<TubeMap> {
    override fun parse(input: String): TubeMap {
        val lines = input.split("\n")

        var start = 0 by 0
        val grid = List(lines[0].length + 2) { MutableList(lines.size + 2) { '.' } }.also { grid ->
            for (y in lines.indices) {
                for (x in lines[y].indices) {
                    grid[x + 1][y + 1] = lines[y][x]
                    if (grid[x + 1][y + 1] == 'S') start = x by y
                }
            }
        }

        return TubeMap(grid, start)
    }
}

fun main() {
    aoc(TubeMapParser) {
        puzzle { 2023 day 10 }

        part1 { input ->
            tailrec fun findDistance(c1: Point<Int>, c2: Point<Int>, seen: Set<Point<Int>>, distance: Int): Int {
                val connections = input.findConnections(c1) + input.findConnections(c2)
                val newConnections = connections.filter { it !in seen }.toSet()
                return if (newConnections.size == 1) {
                    distance
                } else {
                    findDistance(newConnections.first(), newConnections.last(), seen + newConnections, distance + 1)
                }
            }

            findDistance(input.start, input.start, mutableSetOf(input.start), 1)
        }

        part2 { input ->
            tailrec fun findLoop(c1: Point<Int>, c2: Point<Int>, loop: Set<Point<Int>>): Set<Point<Int>> {
                val connections = input.findConnections(c1) + input.findConnections(c2)
                val newConnections = connections.filter { it !in loop }.toSet()
                return if (newConnections.size == 1) {
                    newConnections + loop
                } else {
                    findLoop(newConnections.first(), newConnections.last(), loop + newConnections)
                }
            }

            val loop = findLoop(input.start, input.start, mutableSetOf(input.start))
            val gridWithLoop = input.copy(grid = input.grid.mapIndexed { x, line ->
                line.mapIndexed { y, c -> if (x by y in loop) c else '.' }.toMutableList()
            })

            val expandedGrid = gridWithLoop.copy(
                grid = List(gridWithLoop.grid.size * 2) {
                    MutableList(gridWithLoop.grid[0].size * 2) { '.' }
                }.also { grid ->
                    for (y in grid[0].indices) {
                        for (x in grid.indices) {
                            if (x % 2 == 0 && y % 2 == 0) {
                                grid[x][y] = gridWithLoop.grid[x / 2][y / 2]
                            } else {
                                if (y % 2 == 0 && gridWithLoop.grid[x / 2][y / 2].connectsRight() && gridWithLoop.grid[x / 2 + 1][y / 2].connectsLeft()) {
                                    grid[x][y] = '-'
                                } else if (x % 2 == 0 && gridWithLoop.grid[x / 2][y / 2].connectsDown() && gridWithLoop.grid[x / 2][y / 2 + 1].connectsUp()) {
                                    grid[x][y] = '|'
                                }
                            }
                        }
                    }
                })

            tailrec fun floodGrid(q: ArrayDeque<Point<Int>>, map: TubeMap) {
                if (q.isEmpty()) return

                q.removeFirst().surrounding()
                    .filter { it.x >= 0 && it.y >= 0 && it.x < map.grid.size && it.y < map.grid[0].size }
                    .filter { map.get(it) == '.' }.forEach {
                        map.grid[it.x][it.y] = 'O'
                        q.add(it)
                    }
                floodGrid(q, map)
            }
            floodGrid(ArrayDeque<Point<Int>>().also { it.add(0 by 0) }, expandedGrid)

            var sum = 0
            expandedGrid.grid.forEachIndexed { x, line ->
                line.forEachIndexed { y, c ->
                    if (x % 2 == 0 && y % 2 == 0 && c == '.') sum++
                }
            }

            sum
        }
    }
}


