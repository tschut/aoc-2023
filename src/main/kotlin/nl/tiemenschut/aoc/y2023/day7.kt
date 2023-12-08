package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import kotlin.math.pow

fun Char.toCardValue(jValue: Int): Int {
    return when (this) {
        'T' -> 10
        'J' -> jValue
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> this.digitToInt()
    }
}

data class Hand(val cards: List<Int>) {
    fun score(): Int = ("${typeScore()}" + cards.joinToString("") { it.toString(16) }).toInt(16)

    fun scoreWithJs(): Int {
        if (cards.none { it == 1 }) return score()

        val options = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14)
        val current = MutableList(cards.count { it == 1 }) { 0 }
        current[current.size - 1] = -1
        var max = typeScore()
        do {
            fun addOne(index: Int) {
                if (index == -1) return
                if (current[index] == options.size - 1) {
                    current[index] = 0
                    addOne(index - 1)
                }
                current[index] ++
            }
            addOne(current.size - 1)

            val newCards = cards.toMutableList()
            for (i in current.indices) {
                newCards[newCards.indexOfFirst { it == 1 }] = options[current[i]]
            }
            max = max.coerceAtLeast(Hand(newCards).typeScore())
        } while (current.any { it != options.size - 1 })

        return ("$max" + cards.joinToString("") { it.toString(16) }).toInt(16)
    }

    private fun typeScore() = if (grouped.size == 1) 6
    else if (grouped.maxBy { it.value }.value == 4) 5
    else if (grouped.size == 2 && grouped.values.sorted() == listOf(2, 3)) 4
    else if (grouped.size == 3 && grouped.values.sorted() == listOf(1, 1, 3)) 3
    else if (grouped.size == 3 && grouped.values.sorted() == listOf(1, 2, 2)) 2
    else if (grouped.size == 4) 1
    else 0

    private val grouped: Map<Int, Int> by lazy { cards.groupingBy { it }.eachCount() }
}

class HandsParser(private val jValue: Int) : InputParser<List<Pair<Hand, Int>>> {
    override fun parse(input: String): List<Pair<Hand, Int>> = input.split("\n").map { line ->
        val (hand, bid) = line.split(" ")
        Hand(hand.map { it.toCardValue(jValue) }) to bid.toInt()
    }
}

fun main() {
    aoc {
        puzzle { 2023 day 7 }

        part1 { input ->
            HandsParser(11).parse(input)
                .sortedBy { it.first.score() }
                .foldIndexed(0) { index, acc, pair ->
                    acc + (index + 1) * pair.second
                }
        }

        part2 { input ->
            HandsParser(1).parse(input)
                .sortedBy { it.first.scoreWithJs() }
                .foldIndexed(0) { index, acc, pair ->
                    acc + (index + 1) * pair.second
                }
        }
    }
}



