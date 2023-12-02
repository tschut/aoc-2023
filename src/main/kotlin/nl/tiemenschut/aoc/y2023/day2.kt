package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.AsListOfStrings

fun main() {
    aoc(AsListOfStrings) {
        puzzle { 2023 day 2 }

        data class Turn(val red: Int, val green: Int, val blue: Int)
        data class Game(val id: Int, val turns: List<Turn>)

        val gameIdRegex = "Game (.*):".toRegex()
        val redRegex = "(\\d*) red".toRegex()
        val greenRegex = "(\\d*) green".toRegex()
        val blueRegex = "(\\d*) blue".toRegex()

        fun String.toGame(): Game {
            return Game(
                id = gameIdRegex.find(this)!!.groupValues[1].toInt(),
                turns = split(":")[1].split(";").map { turn ->
                    Turn(
                        red = redRegex.find(turn)?.groupValues?.last()?.toInt() ?: 0,
                        green = greenRegex.find(turn)?.groupValues?.last()?.toInt() ?: 0,
                        blue = blueRegex.find(turn)?.groupValues?.last()?.toInt() ?: 0,
                    )
                })
        }

        part1 { input ->
            input
                .map { it.toGame() }
                .filter {
                    it.turns.all { turn ->
                        turn.red <= 12 && turn.green <= 13 && turn.blue <= 14
                    }
                }
                .sumOf { it.id }
        }

        part2 { input ->
            input
                .map { it.toGame() }
                .sumOf { game ->
                    game.turns.maxOf { it.red } * game.turns.maxOf { it.green } * game.turns.maxOf { it.blue }
                }
        }
    }
}



