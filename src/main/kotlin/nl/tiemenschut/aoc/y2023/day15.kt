package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day

typealias Box = LinkedHashMap<String, Int>

fun main() {
    aoc {
        puzzle { 2023 day 15 }

        fun String.christmasHash() = this.fold(0) { acc, c -> (17 * (acc + c.code)).and(0b11111111) }

        part1 { input ->
            input.split(",").sumOf { it.christmasHash() }
        }

        part2 { input ->
            val boxes: List<Box> = List(256) { Box() }
            val operationRegex = "([a-z]*)([-|=])([1-9]?)".toRegex()

            input.split(",").forEach { it ->
                val (_, label, operator, f) = operationRegex.find(it)!!.groupValues

                val box = boxes[label.christmasHash()]
                when (operator) {
                    "-" -> box.remove(label)
                    else -> if (label in box) box[label] = f.toInt() else box.putLast(label, f.toInt())
                }

            }
            boxes.mapIndexed { index, box ->
                var power = 0
                var slot = 1
                box.forEach { (_, f) ->
                    power += (1 + index) * slot++ * f
                }
                power
            }.sum()
        }
    }
}


