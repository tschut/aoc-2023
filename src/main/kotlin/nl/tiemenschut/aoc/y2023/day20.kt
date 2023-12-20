package nl.tiemenschut.aoc.y2023

import nl.tiemenschut.aoc.lib.dsl.aoc
import nl.tiemenschut.aoc.lib.dsl.day
import nl.tiemenschut.aoc.lib.dsl.parser.InputParser
import nl.tiemenschut.aoc.y2023.PulseValue.HIGH
import nl.tiemenschut.aoc.y2023.PulseValue.LOW

sealed class Module(open val name: String, open val connections: MutableList<Module> = mutableListOf()) {
    abstract fun handlePulse(pulse: Pulse): List<Pulse>
}

class Broadcast : Module("broadcaster") {
    override fun handlePulse(pulse: Pulse) = connections.map { Pulse(this, it, LOW) }
}

data class FlipFlop(override val name: String) : Module(name) {
    private var state: PulseValue = LOW

    override fun handlePulse(pulse: Pulse): List<Pulse> {
        if (pulse.value == LOW) {
            state = if (state == LOW) HIGH else LOW
            return connections.map { Pulse(this, it, state) }
        }
        return emptyList()
    }
}

data class Conjunction(override val name: String) : Module(name) {
    val memory: MutableMap<String, PulseValue> = mutableMapOf()

    override fun handlePulse(pulse: Pulse): List<Pulse> {
        memory[pulse.source.name] = pulse.value
        val outgoingPulse = if (memory.values.all { it == HIGH }) LOW else HIGH
        return connections.map { Pulse(this, it, outgoingPulse) }
    }
}

data class EndPoint(override val name: String) : Module(name) {
    override fun handlePulse(pulse: Pulse): List<Pulse> = emptyList()
}

data object Button : Module("button") {
    override fun handlePulse(pulse: Pulse): List<Pulse> {
        TODO("Not yet implemented")
    }
}

enum class PulseValue { LOW, HIGH }

data class Pulse(val source: Module, val destination: Module?, val value: PulseValue)

object ModuleConfigurationParser : InputParser<Map<String, Module>> {
    override fun parse(input: String): Map<String, Module> {
        val moduleConnections = mutableMapOf<String, List<String>>()
        val modules = input.split("\n").associate { line ->
            val (left, right) = line.split(" -> ")
            val module = when {
                "%" in left -> FlipFlop(left.substring(1..2))
                "&" in left -> Conjunction(left.substring(1..2))
                else -> Broadcast()
            }
            moduleConnections[module.name] = right.split(",").map { it.trim() }
            module.name to module
        }
        moduleConnections.forEach { (moduleName, connectionName) ->
            modules[moduleName]?.connections?.addAll(connectionName.map { modules[it] ?: EndPoint(it) })
        }
        modules.forEach { (name, module) ->
            module.connections.forEach { connection ->
                val connected = modules[connection.name]
                if (connected is Conjunction) connected.memory[name] = LOW
            }
        }
        return modules
    }
}

fun main() {
    aoc(ModuleConfigurationParser) {
        puzzle { 2023 day 20 }

        fun Map<String, Module>.pushTheButton(): Pair<Long, Long> {
            var countLow = 1L
            var countHigh = 0L
            val pulseQueue = ArrayDeque(listOf(Pulse(Button, get("broadcaster")!!, LOW)))
            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                val outgoingPulses = pulse.destination!!.handlePulse(pulse)
                countLow += outgoingPulses.count { it.value == LOW }
                countHigh += outgoingPulses.count { it.value == HIGH }

                pulseQueue.addAll(outgoingPulses.filter { p -> p.destination != null })
            }

            return countLow to countHigh
        }

        part1 { input ->
            (0 until 1000).fold(0L to 0L) { acc, _ ->
                input.pushTheButton().let { acc.first + it.first to acc.second + it.second }
            }.let { it.first * it.second }
        }

        val vdPulses = mutableSetOf<String>()
        fun Map<String, Module>.pushTheButtonUntilRxReceivesLow(): Boolean {
            var result = false
            val pulseQueue = ArrayDeque(listOf(Pulse(Button, get("broadcaster"), LOW)))
            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                val outgoingPulses = pulse.destination!!.handlePulse(pulse)
                outgoingPulses.find { it.destination?.name == "vd" && it.value == HIGH && vdPulses.add(it.source.name) }
                    ?.let {
                        println("#### ${it.source.name}")
                        result = true
                    }
                pulseQueue.addAll(outgoingPulses.filter { p -> p.destination != null })
            }
            return result
        }

        part2(submit = false) { input ->
            var count = 1
            while (true) {
                if (input.pushTheButtonUntilRxReceivesLow()) {
                    println("found: $count")
                }
                count++
            }
            // manually submit lcm (= product, all are prime) of the found values.
            // Brute forcing a general solution is too slow.
        }
    }
}


