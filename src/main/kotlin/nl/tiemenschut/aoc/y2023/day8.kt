package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.lib.util.infiniterator

data class Node(val id: String) {
    var left: Node? = null
    var right: Node? = null
}


data class DesertMap(
    val directions: String,
    val start: Node?,
    val end: Node?,
    val nodes: List<Node>,
)

object MapParser : InputParser<DesertMap> {
    override fun parse(input: String): DesertMap {
        val nodeRegex = "(.{3}) = \\((.{3}), (.{3})\\)".toRegex()
        val (directions, nodesInput) = input.split("\n\n")

        val nodeMap = nodeRegex.findAll(nodesInput).associate { nodeInput: MatchResult ->
            val (_, root, left, right) = nodeInput.groupValues
            root to (left to right)
        }

        val nodes = nodeMap.map { Node(it.key) }
        nodes.forEach { node ->
            node.left = nodes.find { it.id == nodeMap[node.id]!!.first }
            node.right = nodes.find { it.id == nodeMap[node.id]!!.second }
        }

        return DesertMap(
            directions,
            nodes.find { it.id == "AAA" },
            nodes.find { it.id == "ZZZ" },
            nodes,
        )
    }

}

fun main() {
    aoc(MapParser) {
        puzzle { 2023 day 8 }

        part1 { input ->
            var steps = 0
            var current = input.start!!
            run loop@ {
                input.directions.toList().infiniterator().forEach {
                    current = if (it == 'L') current.left!! else current.right!!
                    steps ++
                    if (current === input.end) return@loop
                }
            }

            steps
        }

        part2(submit = true) { input ->
            val starts = input.nodes.filter { it.id.endsWith("A") }
            val progression: List<MutableList<Pair<Node, Int>>> = List(starts.size) { mutableListOf<Pair<Node, Int>>() }
            val loopStarts = mutableListOf<Int>()

            starts.forEachIndexed { iStart, start ->
                var index = 0
                var current = start
                while (true) {
                    val loopStart = progression[iStart].indexOf(current to index)
                    if (loopStart != -1) {
                        loopStarts.add(loopStart)
                        break
                    }

                    progression[iStart].add(current to index)
                    current = if (input.directions[index] == 'L') current.left!! else current.right!!
                    if (++index == input.directions.length) index = 0
                }
            }

            data class Loop(
                val offset: Long,
                val loop: Long,
                val indexOfEnd: Long,
            ) {
                fun isZ(index: Long) = ((index - offset) % loop) == indexOfEnd
            }

            val loops = progression.mapIndexed { i, p ->
                Loop(
                    loopStarts[i].toLong(),
                    (p.size - loopStarts[i]).toLong(),
                    p.indexOfLast { n -> n.first.id.endsWith('Z') }.toLong() - loopStarts[i]
                )
            }

            val smallestLoop = loops.minBy { it.loop }
            val firstZInSmallestLoop = (smallestLoop.offset .. smallestLoop.loop + smallestLoop.offset).first { smallestLoop.isZ(it) }
            var count = 0L
            var tiredCamel = firstZInSmallestLoop
            while (loops.any { !it.isZ(tiredCamel) }) {
                tiredCamel += smallestLoop.loop
                if (count++ % 1_000_000L == 0L) println("$tiredCamel million tiredCamels")
            }

            tiredCamel
        }
    }
}


