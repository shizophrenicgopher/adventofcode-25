package fyi.pauli.aoc.days
import fyi.pauli.aoc.day

data class Machine(
    val target: List<Boolean>,
    val buttons: List<List<Int>>,
    val joltage: List<Int>,
)

fun parseMachine(line: String): Machine =
    line.split("]")
        .let { parts ->
            Machine(
                target = parts[0].substring(1).map { it == '#' },
                buttons = Regex("""\(([0-9,]+)\)""")
                    .findAll(parts[1])
                    .map { it.groupValues[1].split(",").map(String::toInt) }
                    .toList(),
                joltage = Regex("""\{([0-9,]+)\}""")
                    .find(parts[1])
                    ?.groupValues?.get(1)
                    ?.split(",")
                    ?.map(String::toInt)
                    ?: emptyList()
            )
        }

fun solveMinPresses(machine: Machine): Int =
    (0 until (1 shl machine.buttons.size)).minOfOrNull { mask ->
        machine.buttons.flatMapIndexed { idx, button ->
            if (mask and (1 shl idx) != 0) button else emptyList()
        }
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value % 2 == 1 }
            .let { toggles ->
                machine.target.mapIndexed { idx, target ->
                    (toggles[idx] ?: false) == target
                }.all { it }
            }
            .let { if (it) mask.countOneBits() else Int.MAX_VALUE }
    } ?: Int.MAX_VALUE

fun solveMinJoltage(machine: Machine): Int {
    val n = machine.buttons.size
    val m = machine.joltage.size

    val remaining = machine.joltage.toIntArray()
    val presses = IntArray(n)

    val counterToButtons = Array(m) { c ->
        machine.buttons.indices.filter { c in machine.buttons[it] }.toIntArray()
    }

    for (counter in remaining.indices.sortedBy { remaining[it] }) {
        val need = remaining[counter]
        if (need <= 0) continue

        val buttons = counterToButtons[counter]
        if (buttons.isEmpty()) continue

        val bestBtn = buttons.minByOrNull { btn ->
            val affected = machine.buttons[btn]
            affected.count { remaining[it] <= 0 } * 1000 + affected.size
        } ?: continue

        presses[bestBtn] += need
        machine.buttons[bestBtn].forEach { remaining[it] -= need }
    }

    if (remaining.all { it == 0 }) return presses.sum()

    return solveDFS(machine)
}

fun solveDFS(machine: Machine): Int {
    val n = machine.buttons.size
    val m = machine.joltage.size
    val presses = IntArray(n)
    val current = IntArray(m)
    var best = Int.MAX_VALUE

    fun dfs(btn: Int, sum: Int) {
        if (sum >= best) return

        if (btn == n) {
            if ((0 until m).all { current[it] == machine.joltage[it] }) {
                best = sum
            }
            return
        }

        val deficit = (0 until m).sumOf { maxOf(0, machine.joltage[it] - current[it]) }
        if (deficit == 0) {
            if ((0 until m).all { current[it] == machine.joltage[it] }) {
                best = minOf(best, sum)
            }
            return
        }

        val maxNeeded = machine.buttons[btn].maxOfOrNull { maxOf(0, machine.joltage[it] - current[it]) } ?: 0
        val limit = minOf(maxNeeded, best - sum - 1)

        for (count in 0..limit) {
            presses[btn] = count
            machine.buttons[btn].forEach { current[it] += count }

            val overshot = (0 until m).any { current[it] > machine.joltage[it] }

            if (!overshot) {
                dfs(btn + 1, sum + count)
            }

            machine.buttons[btn].forEach { current[it] -= count }
        }
    }

    dfs(0, 0)
    return best
}

val day10 = day(10) {
    val mapped = inputLines
        .filter(String::isNotBlank)
        .map(::parseMachine)

    first = {
        mapped.sumOf(::solveMinPresses)
    }

    second = {
        mapped.sumOf(::solveMinJoltage)
    }
}