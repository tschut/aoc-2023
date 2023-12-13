package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import javax.swing.Spring

const val OPERATIONAL = '.'
const val DAMAGED = '#'
const val UNKNOWN = '?'

val cache = mutableMapOf<String, Long>()

data class SpringRow(
    val springs: List<Char>,
    val configuration: List<Int>,
) {
    override fun toString() = "${springs.joinToString("")} ${configuration.joinToString(",")}"

    private fun key() = springs.joinToString("") + configuration.joinToString(",") { "$it" }

    fun countPossibleArrangements(prefix: String = ""): Long {
        cache[key()]?.let { return it }

        if (configuration.isEmpty()) {
            return if (springs.count { it == DAMAGED } > 0) {
                0
            } else {
                1
            }
        }

        if (configuration.sum() > springs.count { it != OPERATIONAL }) return 0

        val minSpaceNeeded = configuration.size + configuration.sum() - 1
        if (springs.size < minSpaceNeeded) return 0

        return when (springs.first()) {
            OPERATIONAL -> SpringRow(springs.drop(1), configuration).countPossibleArrangements(prefix + springs[0])
            DAMAGED -> {
                if (hasPotentiallyDamagedGroupAtStart(configuration.first())) {
                    SpringRow(springs.drop(configuration.first() + 1), configuration.drop(1))
                        .countPossibleArrangements(prefix + "#".repeat(configuration.first()) + ".")
                } else {
                    0
                }
            }

            else -> {
                SpringRow(springs.toMutableList().also { it[0] = OPERATIONAL }, configuration)
                    .countPossibleArrangements(prefix) +
                        SpringRow(springs.toMutableList().also { it[0] = DAMAGED }, configuration)
                            .countPossibleArrangements(prefix)
            }
        }.also {
            cache[key()] = it
        }
    }

    fun expanded() = SpringRow(
        springs = springs + '?' + springs + '?' + springs + '?' + springs + '?' + springs,
        configuration = configuration + configuration + configuration + configuration + configuration
    )

    private fun hasPotentiallyDamagedGroupAtStart(size: Int): Boolean {
        return (springs.take(size).all { it != OPERATIONAL })
                && (springs.size == size || springs[size] != DAMAGED)
    }
}

fun String.toSpringRow() = SpringRow(
    springs = split(" ")[0].toCharArray().toList(),
    configuration = split(" ")[1].split(",").map { it.toInt() },
)


object SpringRowParser : InputParser<List<SpringRow>> {
    override fun parse(input: String): List<SpringRow> = input.split("\n").map(String::toSpringRow)
}

fun main() {
    aoc(SpringRowParser) {
        puzzle { 2023 day 12 }

        part1 { input ->
            input.sumOf { it.countPossibleArrangements() }
        }

        part2 { input ->
            input.map(SpringRow::expanded).sumOf { it.countPossibleArrangements() }
        }
    }
}
