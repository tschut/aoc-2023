package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.util.Direction
import nl.tiemenschut.aoc.lib.util.Direction.DOWN
import nl.tiemenschut.aoc.lib.util.Direction.RIGHT
import nl.tiemenschut.aoc.lib.util.grid.CharGridParser
import nl.tiemenschut.aoc.lib.util.grid.Grid
import nl.tiemenschut.aoc.lib.util.points.Point
import nl.tiemenschut.aoc.lib.util.points.by
import java.util.PriorityQueue

fun main() {
    aoc(CharGridParser) {
        puzzle { 2023 day 17 }

        fun List<Direction>.nextValidDirections(): Set<Direction> {
            if (isEmpty()) return mutableSetOf(DOWN, RIGHT)
            val last = last()
            val result = mutableSetOf(last.rotateLeft(), last.rotateRight())
            if (size < 3 || takeLast(3).any { it != last }) result.add(last)
            return result
        }

        data class Day17Key(val p: Point<Int>, val lastD: Direction, val c: Int)

        fun key(p: Point<Int>, d: List<Direction>): Day17Key =
            Day17Key(p, d.last(), d.takeLastWhile { it == d.last() }.count())

        fun Grid<Char>.bestimate(): Int {
            var p: Point<Int> = 0 by 0
            var cost = 0
            while (p != width() - 1 by height() - 1) {
                p = p.moved(RIGHT)
                cost += get(p).digitToInt()
                p = p.moved(DOWN)
                cost += get(p).digitToInt()
            }
            return cost
        }

        part1 { input ->
            val bestPerLocation: MutableMap<Day17Key, Int> =
                mutableMapOf(Day17Key(0 by 0, RIGHT, 0) to 0)

            data class PathFindingPoint(val pos: Point<Int>, val directions: List<Direction>, val cost: Int)

            fun Grid<Char>.findCheapest(points: PriorityQueue<PathFindingPoint>): Int {
                var totalIter = 0
                var best = input.bestimate()
                while (points.isNotEmpty()) {
                    val p = points.poll()
                    if (p.cost >= best) continue
                    totalIter++

                    p.directions.nextValidDirections()
                        .map { d -> p.pos.moved(d) to d }
                        .filter { (n, _) -> n in this@findCheapest }
                        .map { (n, d) ->
                            PathFindingPoint(
                                n,
                                (p.directions + d).takeLast(3),
                                p.cost + this[n].digitToInt()
                            )
                        }
                        .filter { pfp ->
                            bestPerLocation.getOrDefault(key(pfp.pos, pfp.directions), Int.MAX_VALUE) > pfp.cost
                        }
                        .forEach { pfp ->
                            bestPerLocation[key(pfp.pos, pfp.directions)] = pfp.cost
                            if (pfp.pos == input.width() - 1 by input.height() - 1) {
                                best = best.coerceAtMost(pfp.cost)
                            } else if (pfp.cost < best) {
                                points.add(pfp)
                            }
                        }
                }
                println("iterations: $totalIter")
                return best
            }

            val start = PriorityQueue<PathFindingPoint>(1) { o1, o2 -> o1.cost.compareTo(o2.cost) }
            start.add(PathFindingPoint(0 by 0, emptyList(), 0))
            input.findCheapest(start)
        }

        fun Grid<Char>.bestimatePart2(): Int {
            var p: Point<Int> = 0 by 0
            var cost = 0
            while (p != width() - 1 by height() - 1) {
                repeat(4) {
                    p = p.moved(RIGHT)
                    if (p in this) cost += get(p).digitToInt()
                }
                repeat(4) {
                    p = p.moved(DOWN)
                    if (p in this) cost += get(p).digitToInt()
                }
            }
            return cost
        }

        fun Point<Int>.moved(d: Direction, count: Int): Point<Int> = when (d) {
            Direction.UP -> x by y - count
            Direction.DOWN -> x by y + count
            Direction.LEFT -> x - count by y
            Direction.RIGHT -> x + count by y
        }

        part2 { input ->
            val bestPerLocation: MutableMap<Day17Key, Int> = mutableMapOf(Day17Key(0 by 0, RIGHT, 0) to 0)

            data class PathFindingPoint(val pos: Point<Int>, val directions: List<Direction>, val cost: Int)

            fun Grid<Char>.findCheapest(points: PriorityQueue<PathFindingPoint>): Int {
                var totalIter = 0
                var best = input.bestimatePart2()
                while (points.isNotEmpty()) {
                    val p = points.poll()
                    if (p.cost >= best) continue
                    totalIter++

                    val nextDirections = if (p.directions.isEmpty()) setOf(RIGHT, DOWN)
                    else setOf(p.directions.last().rotateLeft(), p.directions.last().rotateRight())

                    nextDirections.flatMap { d ->
                        var runningCost = p.cost + (1 .. 3).sumOf {
                            val next = p.pos.moved(d, it)
                            if (next in this) this[next].digitToInt() else 0
                        }
                        val runningDirections = p.directions.toMutableList()
                        (4..10).mapNotNull {
                            val next = p.pos.moved(d, it)
                            if (next in this) {
                                runningCost += this[next].digitToInt()
                                runningDirections.add(d)
                                PathFindingPoint(next, runningDirections.takeLast(3), runningCost)
                            } else null
                        }
                    }
                        .filter { pfp ->
                            bestPerLocation.getOrDefault(key(pfp.pos, pfp.directions), Int.MAX_VALUE) > pfp.cost
                        }
                        .forEach { pfp ->
                            bestPerLocation[key(pfp.pos, pfp.directions)] = pfp.cost
                            if (pfp.pos == input.width() - 1 by input.height() - 1) {
                                best = best.coerceAtMost(pfp.cost)
                            } else if (pfp.cost < best) {
                                points.add(pfp)
                            }
                        }
                }
                println("iterations: $totalIter")
                return best
            }

            val start = PriorityQueue<PathFindingPoint>(1) { o1, o2 -> o1.cost.compareTo(o2.cost) }
            start.add(PathFindingPoint(0 by 0, emptyList(), 0))
            input.findCheapest(start)
        }
    }
}


