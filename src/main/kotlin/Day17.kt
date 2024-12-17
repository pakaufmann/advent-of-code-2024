import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.pow

fun main() {
    val registerRegex = "Register .: ([0-9]+)".toRegex()
    val lines = File("inputs/day17.txt").readLines()
    val a = registerRegex.find(lines[0])!!.groupValues[1].toLong()
    val b = registerRegex.find(lines[1])!!.groupValues[1].toLong()
    val c = registerRegex.find(lines[2])!!.groupValues[1].toLong()
    val program = lines[4]
        .replace("Program: ", "")
        .split(",")
        .filter { it != "" }
        .map { it.toLong() }

    println(part1(program, mapOf(0 to a, 1 to b, 2 to c)))
    println(part2(program, mapOf(0 to a, 1 to b, 2 to c)))
}

fun part1(program: List<Long>, start: Map<Int, Long>): String =
    runProgram(start, program).last().third.joinToString(",")


private fun findAMatchingOutput(program: List<Long>, target: List<Long>, start: Map<Int, Long>): Long {
    var aStart = if (target.size == 1) {
        0
    } else {
        8 * findAMatchingOutput(program, target.subList(1, target.size), start)
    }

    while (runProgram(start + (0 to aStart), program).last().third != target) {
        aStart++
    }
    return aStart
}

fun part2(program: List<Long>, start: Map<Int, Long>) {
    println(findAMatchingOutput(program, program, start))
}

private fun runProgram(
    start: Map<Int, Long>,
    program: List<Long>
) = generateSequence(Triple(start, 0, emptyList<Long>())) { (register, instructionPointer, output) ->
    val instruction = program[instructionPointer]
    val operand = program[instructionPointer + 1]

    when (instruction) {
        0L -> {
            val newRegister =
                register + (0 to register.getValue(0) / 2.0.pow(combo(operand.toInt(), register).toDouble()).toInt())
            Triple(newRegister, (instructionPointer + 2), output)
        }
        1L -> {
            val newRegister = register + (1 to register.getValue(1).xor(operand))
            Triple(newRegister, (instructionPointer + 2), output)
        }
        2L -> {
            val newRegister = register + (1 to combo(operand.toInt(), register) % 8)
            Triple(newRegister, (instructionPointer + 2), output)
        }
        3L ->
            if (register.getValue(0) == 0L) {
                Triple(register, (instructionPointer + 2), output)
            } else {
                Triple(register, operand.toInt(), output)
            }
        4L -> {
            val newRegister = register + (1 to (register.getValue(1) xor register.getValue(2)))
            Triple(newRegister, (instructionPointer + 2), output)
        }
        5L -> {
            Triple(register, (instructionPointer + 2), output + (combo(operand.toInt(), register) % 8))
        }
        6L -> {
            val newRegister =
                register + (1 to register.getValue(0) / 2.0.pow(combo(operand.toInt(), register).toDouble()).toInt())
            Triple(newRegister, (instructionPointer + 2), output)
        }
        7L -> {
            val newRegister =
                register + (2 to register.getValue(0) / 2.0.pow(combo(operand.toInt(), register).toDouble()).toInt())
            Triple(newRegister, (instructionPointer + 2), output)
        }
        else -> throw Exception("Invalid opcode")
    }
}.takeWhile { (_, instructionPointer) ->
    instructionPointer < program.size - 1
}

fun combo(i: Int, register: Map<Int, Long>) =
    when (i) {
        0, 1, 2, 3 -> i.toLong()
        4, 5, 6 -> register.getValue(i - 4)
        else -> throw Exception("Invalid value")
    }
