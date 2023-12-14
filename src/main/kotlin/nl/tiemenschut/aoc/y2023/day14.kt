package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.util.grid.CharGridParser
import nl.tiemenschut.aoc.lib.util.grid.Grid
import nl.tiemenschut.aoc.lib.util.points.by

fun main() {
    aoc(CharGridParser) {
        puzzle { 2023 day 14 }

        fun Grid<Char>.setCol(x: Int, value: String) {
            (0..<height()).forEach { y -> this[x by y] = value[y] }
        }

        fun Grid<Char>.setRow(y: Int, value: String) {
            (0..<width()).forEach { x -> this[x by y] = value[x] }
        }

        val cache = mutableMapOf<String, String>()

        fun String.tilt(): String {
            cache[this]?.let { return it }

            var targetPos = -1
            val result = this.toMutableList()
            indices.forEach { i ->
                when (result[i]) {
                    '.' -> if (targetPos == -1) targetPos = i
                    'O' -> {
                        if (targetPos != -1) {
                            result[targetPos] = 'O'
                            result[i] = '.'
                            targetPos++
                        }
                    }

                    else -> targetPos = -1
                }
            }
            return result.joinToString("").also { cache[this] = it }
        }

        fun Grid<Char>.totalLoad(): Int = (0..<height()).sumOf { y ->
            (height() - y) * getRow(y).count { it == 'O' }
        }

        fun Grid<Char>.tiltNorth(): Grid<Char> = this.also { repeat(width()) { x -> setCol(x, getCol(x).tilt()) } }

        fun Grid<Char>.tiltWest(): Grid<Char> = this.also { repeat(height()) { y -> setRow(y, getRow(y).tilt()) } }

        fun Grid<Char>.tiltSouth(): Grid<Char> =
            this.also { repeat(width()) { x -> setCol(x, getCol(x).reversed().tilt().reversed()) } }

        fun Grid<Char>.tiltEast(): Grid<Char> =
            this.also { repeat(height()) { y -> setRow(y, getRow(y).reversed().tilt().reversed()) } }

        fun Grid<Char>.spin(): Grid<Char> {
            return this.tiltNorth().tiltWest().tiltSouth().tiltEast()
        }

        part1 { input ->
            input.tiltNorth().totalLoad()
        }

        fun Grid<Char>.spin(times: Int): Grid<Char> {
            val loopDetection = mutableListOf<Int>()
            var x = 0
            while (x < times) {
                val hashcode = (0..<height()).joinToString { y -> getRow(y) }.hashCode()
                if (loopDetection.contains(hashcode)) {
                    val loopSize = x - loopDetection.indexOf(hashcode)
                    x += loopSize * ((times - x) / loopSize)
                } else {
                    loopDetection.add(hashcode)
                }

                this.spin()
                x++
            }
            return this
        }

        part2 { input ->
            input.spin(1_000_000_000).totalLoad()
        }
    }
}
