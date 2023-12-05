package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import java.lang.RuntimeException

data class CategoryMap(val source: LongRange, val dest: LongRange)

data class Almanac(
    val seeds: List<Long>,
    val maps: List<List<CategoryMap>>,
)

data class AlmanacPart2(
    val seeds: List<LongRange>,
    val maps: List<List<CategoryMap>>,
)

private fun parseCategoryMappings(input: String) = input.split("\n.*map:".toRegex()).drop(1)
    .map { map ->
        map.trim().split("\n").map { range ->
            range.split(" ")
                .map { it.toLong() }
                .let {
                    CategoryMap(
                        dest = LongRange(it[0], it[0] + it[2] - 1),
                        source = LongRange(it[1], it[1] + it[2] - 1),
                    )
                }
        }.sortedBy { it.source.first }
    }

object AlmanacParser : InputParser<Almanac> {
    override fun parse(input: String) = Almanac(
        seeds = "seeds: (.*)".toRegex().find(input)!!.groupValues[1].split(" ").map { it.toLong() },
        maps = parseCategoryMappings(input)
    )
}

object AlmanacParserPart2 : InputParser<AlmanacPart2> {
    override fun parse(input: String) = AlmanacPart2(
        seeds = "seeds: (.*)".toRegex().find(input)!!.groupValues[1]
            .split(" ")
            .map { it.toLong() }
            .windowed(2, step = 2).map { LongRange(it[0], it[0] + (it[1] - 1)) },
        maps = parseCategoryMappings(input)
    )
}

fun main() {
    aoc {
        puzzle { 2023 day 5 }

        fun Almanac.locationForSeed(seed: Long): Long {
            var current = seed
            maps.forEach { map ->
                val transform = map
                    .firstOrNull { it.source.contains(current) }
                    ?: CategoryMap(seed..seed, seed..seed)

                current = (current - transform.source.first) + transform.dest.first
            }
            return current
        }

        part1 { input ->
            val almanac = AlmanacParser.parse(input)
            almanac.seeds.minOf { almanac.locationForSeed(it) }
        }

        fun LongRange.splitLeft(splitpoint: Long): List<LongRange> {
            return if (splitpoint - 1 in this) {
                listOf(
                    LongRange(first, splitpoint - 1),
                    LongRange(splitpoint, last)
                )
            } else throw RuntimeException()
        }

        fun LongRange.splitRight(splitpoint: Long): List<LongRange> {
            return if (splitpoint + 1 in this) {
                listOf(
                    LongRange(first, splitpoint),
                    LongRange(splitpoint + 1, last)
                )
            } else throw RuntimeException()
        }

        fun nextLevel(input: List<LongRange>, maps: List<List<CategoryMap>>, level: Int): List<LongRange> {
            if (level == maps.size) return input

            val currentMaps = maps[level]

            val leftSplit = input.flatMap { range ->
                currentMaps.find { map -> map.source.first > range.first && map.source.first <= range.last }
                    ?.let { match -> range.splitLeft(match.source.first) }
                    ?: listOf(range)
            }.toSet()

            val rightSplit = leftSplit.flatMap { range ->
                currentMaps.find { map -> map.source.last >= range.first && map.source.last < range.last }
                    ?.let { match ->
                        range.splitRight(match.source.last)
                    }
                    ?: listOf(range)
            }.toSet()

            val output = rightSplit.map {
                currentMaps.firstOrNull { map -> map.source.contains(it.first) }?.let { match ->
                    val offset = match.dest.first - match.source.first
                    LongRange(it.first + offset, it.last + offset)
                } ?: it
            }

            return nextLevel(output, maps, level + 1)
        }

        part2 { input ->
            val almanac = AlmanacParserPart2.parse(input)

            nextLevel(almanac.seeds, almanac.maps, 0).minOf { it.first }
        }
    }
}
