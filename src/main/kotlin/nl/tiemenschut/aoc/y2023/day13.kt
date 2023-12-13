package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.lib.util.grid.CharGridParser
import nl.tiemenschut.aoc.lib.util.grid.Grid
import nl.tiemenschut.aoc.lib.util.points.by
import nl.tiemenschut.aoc.y2023.Type.COL
import nl.tiemenschut.aoc.y2023.Type.ROW
import kotlin.math.min

object MirrorGridParser : InputParser<List<Grid<Char>>> {
    override fun parse(input: String) = input.split("\n\n").map {
        CharGridParser.parse(it.trim())
    }
}

enum class Type { ROW, COL }

data class Reflection(val type: Type, val index: Int)

fun Grid<Char>.getCol(x: Int): String = buildString {
    for (y in 0 until height()) append(this@getCol[x by y])
}

fun Grid<Char>.getRow(y: Int): String = buildString {
    for (x in 0 until width()) append(this@getRow[x by y])
}

fun Grid<Char>.findReflection(): Reflection {
    outer@ for (x in 1 until width()) {
        for (offset in 1..min(width() - x, x - 0)) {
            if (getCol(x - offset) != getCol(x + offset - 1)) continue@outer
        }
        return Reflection(COL, x)
    }

    outer@ for (y in 1 until height()) {
        for (offset in 1..min(height() - y, y - 0)) {
            if (getRow(y - offset) != getRow(y + offset - 1)) continue@outer
        }
        return Reflection(ROW, y)
    }
    throw RuntimeException("wtf")
}

fun Grid<Char>.findReflectionWithSmudge(): Reflection {
    outer@ for (x in 1 until width()) {
        var differences = 0
        for (offset in 1..min(width() - x, x - 0)) {
            val a = getCol(x - offset)
            val b = getCol(x + offset - 1)
            differences += a.mapIndexed { index, c -> if (c == b[index]) 0 else 1 }.sum()
        }
        if (differences == 1) return Reflection(COL, x)
    }

    outer@ for (y in 1 until height()) {
        var differences = 0
        for (offset in 1..min(height() - y, y - 0)) {
            val a = getRow(y - offset)
            val b = getRow(y + offset - 1)
            differences += a.mapIndexed { index, c -> if (c == b[index]) 0 else 1 }.sum()
        }
        if (differences == 1) return Reflection(ROW, y)
    }
    throw RuntimeException("wtf")
}

fun main() {
    aoc(MirrorGridParser) {
        puzzle { 2023 day 13 }

        part1 { input ->
            input.map { it.findReflection() }.sumOf { if (it.type == COL) it.index else 100 * (it.index) }
        }

        part2 { input ->
            input.map { it.findReflectionWithSmudge() }.sumOf { if (it.type == COL) it.index else 100 * (it.index) }
        }
    }
}


