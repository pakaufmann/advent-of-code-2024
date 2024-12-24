import java.io.File

fun main() {
    val gateRegex = "(.+) (.+) (.+) -> (.+)".toRegex()

    val lines = File("inputs/day24.txt").readLines()
    val wires = lines.takeWhile { it != "" }.map {
        val wire = it.split(": ")
        wire[0] to (wire[1].toInt() as Int?)
    }.toMap()

    val gates = lines.dropWhile { it != "" }.drop(1).map {
        val values = gateRegex.find(it)!!.groupValues
        Gate(values[2], values[1], values[3], values[4])
    }

    val allWires = gates.associate { it.output to (null as Int?) } + wires

    println(part1(allWires, gates))
    println(part2(allWires, gates))
}

private fun part1(start: Map<String, Int?>, gates: List<Gate>): Long =
    calculateResult(gates, start)

private fun part2(start: Map<String, Int?>, gates: List<Gate>): String {
    val wrongOutputs = gates.filter { it.output.first() == 'z' && it.output != "z45" && it.type != "XOR" }
    val wrongXors =
        gates.filter { it.left.first() !in "xy" && it.right.first() !in "xy" && it.output.first() != 'z' && it.type == "XOR" }

    for (gate in wrongXors) {
        val b = wrongOutputs.first { it.output == gates.firstZThatUsesC(gate.output) }
        val temp = gate.output
        gate.output = b.output
        b.output = temp
    }

    val falseCarry = (getWiresAsLong(start, 'x') + getWiresAsLong(start, 'y') xor calculateResult(
        gates,
        start,
    )).countTrailingZeroBits().toString()

    return (wrongOutputs + wrongXors + gates.filter { it.left.endsWith(falseCarry) && it.right.endsWith(falseCarry) }).map { it.output }
        .sorted()
        .joinToString(",")
}

private fun List<Gate>.firstZThatUsesC(c: String): String? {
    val x = filter { it.left == c || it.right == c }

    x.find { it.output.startsWith('z') }
        ?.let { return "z" + (it.output.drop(1).toInt() - 1).toString().padStart(2, '0') }

    return x.firstNotNullOfOrNull { firstZThatUsesC(it.output) }
}

private fun getWiresAsLong(registers: Map<String, Int?>, type: Char) =
    registers
        .filter { it.key.startsWith(type) }.toList().sortedBy { it.first }.map { it.second }.joinToString("").reversed()
        .toLong(2)

private fun calculateResult(
    gates: List<Gate>,
    start: Map<String, Int?>
): Long {
    val unusedGates = mutableSetOf<Gate>()
    unusedGates.addAll(gates)

    val wires = start.toMutableMap()

    while (unusedGates.isNotEmpty()) {
        val usedGates = mutableSetOf<Gate>()

        for (gate in unusedGates) {
            val left = wires[gate.left]
            val right = wires[gate.right]

            if (left != null && right != null) {
                wires[gate.output] = if (when (gate.type) {
                        "AND" -> left == 1 && right == 1
                        "OR" -> left == 1 || right == 1
                        "XOR" -> left != right
                        else -> throw Exception("Unknown operator")
                    }
                ) 1 else 0

                usedGates.add(gate)
            }
        }

        if (usedGates.size == 0) {
            return -1
        }

        unusedGates.removeAll(usedGates)
    }

    return wires.filter { it.key.startsWith("z") }
        .toList()
        .sortedByDescending { it.first }
        .joinToString("") { it.second!!.toString() }
        .toLong(2)
}

data class Gate(val type: String, val left: String, val right: String, var output: String)

fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> =
    sequence {
        val pool = this@combinations as? List<T> ?: toList()
        val n = pool.size
        if (length > n) return@sequence
        val indices = IntArray(length) { it }
        while (true) {
            yield(indices.map { pool[it] })
            var i = length
            do {
                i--
                if (i == -1) return@sequence
            } while (indices[i] == i + n - length)
            indices[i]++
            for (j in i + 1 until length) indices[j] = indices[j - 1] + 1
        }
    }
