package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser

enum class XMAS { X, M, A, S }

data class WorkFlowStart(val start: WorkFlow, val accepted: WorkFlow, val rejected: WorkFlow)

data class WorkFlow(val name: String, val rules: MutableList<Rule> = mutableListOf())

class Rule(val value: XMAS, val range: IntRange, val target: WorkFlow)

data class Part(val x: Int, val m: Int, val a: Int, val s: Int)

object WorkFlowParser : InputParser<Pair<WorkFlowStart, List<Part>>> {
    override fun parse(input: String): Pair<WorkFlowStart, List<Part>> {
        val (workflows, parts) = input.split("\n\n")
        val workflowMap = mutableMapOf("A" to WorkFlow("A"), "R" to WorkFlow("R"))

        workflows.split("\n").forEach { workflow ->
            val name = workflow.split("{")[0]
            val rules = workflow.split("{")[1].split("}")[0].split(",")
                .map {
                    if (":" !in it) {
                        Rule(XMAS.X, 1..4000, workflowMap.getOrPut(it) { WorkFlow(it) })
                    } else {
                        val number = it.split(":").first().substring(2).toInt()
                        Rule(
                            value = XMAS.valueOf("${it[0]}".uppercase()),
                            range = if (it.contains("<")) 1 until number else number + 1..4000,
                            target = workflowMap.getOrPut(it.split(":").last()) { WorkFlow(it.split(":").last()) })
                    }
                }
            workflowMap.getOrPut(name) { WorkFlow(name) }.rules.addAll(rules)
        }

        val parsedParts = parts.split("\n").map { part ->
            "(\\d+)".toRegex().findAll(part).map { it.groupValues[1] }.toList().let {
                Part(it[0].toInt(), it[1].toInt(), it[2].toInt(), it[3].toInt())
            }
        }
        return WorkFlowStart(workflowMap["in"]!!, workflowMap["A"]!!, workflowMap["R"]!!) to parsedParts
    }

}

fun main() {
    aoc(WorkFlowParser) {
        puzzle { 2023 day 19 }

        part1 { (workflow, parts) ->
            parts.sumOf { part ->
                var w = workflow.start
                while (w.name !in listOf("A", "R")) {
                    w = w.rules.first { rule ->
                        when (rule.value) {
                            XMAS.X -> part.x in rule.range
                            XMAS.M -> part.m in rule.range
                            XMAS.A -> part.a in rule.range
                            XMAS.S -> part.s in rule.range
                        }
                    }.target
                }
                if (w.name == "A") part.x.toLong() + part.m + part.a + part.s else 0L
            }
        }

        part2(submit = true) { (workflow, _) ->
            data class RangedXmas(
                val x: IntRange = 1..4000,
                val m: IntRange = 1..4000,
                val a: IntRange = 1..4000,
                val s: IntRange = 1..4000,
            )

            val queue = ArrayDeque<Pair<RangedXmas, WorkFlow>>()
            queue.add(RangedXmas() to workflow.start)
            val acceptedRanges = mutableListOf<RangedXmas>()
            val rejectedRanges = mutableListOf<RangedXmas>()

            while (queue.isNotEmpty()) {
                val (range, w) = queue.removeFirst()
                if (w.name == "A") {
                    acceptedRanges.add(range)
                    continue
                } else if (w.name == "R") {
                    rejectedRanges.add(range)
                    continue
                }

                var remainingRange = range
                w.rules.forEach { rule ->
                    var split = if (rule.range.first == 1) rule.range.last else rule.range.first
                    split += (if (rule.range.last == 4000) 0 else 1)
                    var a: RangedXmas? = null
                    var b: RangedXmas? = null
                    when (rule.value) {
                        XMAS.X -> {
                            if (split != 4000 && split in remainingRange.x) {
                                a = remainingRange.copy(x = remainingRange.x.first until split)
                                b = remainingRange.copy(x = split..remainingRange.x.last)
                            }
                        }

                        XMAS.M -> {
                            if (split != 4000 && split in remainingRange.m) {
                                a = remainingRange.copy(m = remainingRange.m.first until split)
                                b = remainingRange.copy(m = split..remainingRange.m.last)
                            }
                        }

                        XMAS.A -> {
                            if (split != 4000 && split in remainingRange.a) {
                                a = remainingRange.copy(a = remainingRange.a.first until split)
                                b = remainingRange.copy(a = split..remainingRange.a.last)
                            }
                        }

                        XMAS.S -> {
                            if (split != 4000 && split in remainingRange.s) {
                                a = remainingRange.copy(s = remainingRange.s.first until split)
                                b = remainingRange.copy(s = split..remainingRange.s.last)
                            }
                        }
                    }
                    if (a != null && rule.range.first == 1) {
                        queue.add(a to rule.target)
                        remainingRange = b!!
                    } else if (b != null) {
                        queue.add(b to rule.target)
                        remainingRange = a!!
                    } else {
                        queue.add(remainingRange to rule.target)
                    }
                }
            }

            val result = acceptedRanges.sumOf {
                it.x.count().toLong() * it.m.count().toLong() * it.a.count().toLong() * it.s.count().toLong()
            }
            result
        }
    }
}


