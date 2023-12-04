package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.AsListOfStrings
import java.math.BigDecimal
import java.math.BigInteger

fun main() {
    aoc(AsListOfStrings) {
        puzzle { 2023 day 4 }

        val whitespace = "\\s+".toRegex()

        fun String.splitToNumbers() = trim().split(whitespace).map { num -> num.toInt() }

        fun String.parseNumbers() = split("|").let {
            it.first().splitToNumbers() to it.last().splitToNumbers()
        }

        fun matchesForCard(winningNumbers: List<Int>, myNumbers: List<Int>): Int = myNumbers.map {
            if (it in winningNumbers) 1 else 0
        }.sum()

        fun pointsForCard(winningNumbers: List<Int>, myNumbers: List<Int>): Int {
            val matches = matchesForCard(winningNumbers, myNumbers)
            return if (matches > 0) 1 shl (matches - 1) else 0
        }

        part1 { input ->
            input.sumOf { card ->
                val (winningNumbers, myNumbers) = card.split(":").last().parseNumbers()
                pointsForCard(winningNumbers, myNumbers)
            }
        }

        part2 { input ->
            val pointsPerCard = input.map { card ->
                val (winningNumbers, myNumbers) = card.split(":").last().parseNumbers()
                matchesForCard(winningNumbers, myNumbers)
            }

            val totalCards = MutableList(input.size) { 1 }
            for (i in totalCards.indices) {
                if (pointsPerCard[i] > 0) {
                    for (p in 0 until pointsPerCard[i]) {
                        if (i + p + 1 < totalCards.size) totalCards[i + p + 1] += totalCards[i]
                    }
                }
            }

            totalCards.sum()
        }
    }
}


