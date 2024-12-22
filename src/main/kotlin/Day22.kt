import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day22.txt").readLines()

    val numbers = lines.map { it.toLong() }

    println(part1(numbers))
    println(part2(numbers))
}

private fun part1(numbers: List<Long>): Long =
    numbers.sumOf { secretNumbers(it).drop(2000).first() }

private fun part2(numbers: List<Long>): Long {
    val allChanges = numbers.map { number ->
        secretNumbers(number).take(2000)
            .map { it.toString().last().digitToInt() }
            .windowed(2) {
                it[1] to (it[1] - it[0])
            }
            .windowed(4) {
                it[3].first to it.map { it.second }
            }
            .withIndex()
            .groupBy { it.value.second }
            .mapValues { it.value.minByOrNull { it.index }!!.value.first }
            .toMap()
    }

    val max = allChanges.flatMap { it.keys }.toSet()
        .maxOf { change -> allChanges.sumOf { it[change] ?: 0 } }

    return max.toLong()
}


const val modNum = 16777216L

fun secretNumbers(seed: Long) = generateSequence(seed) {
    val first = (it xor (it * 64)) % modNum
    val second = (first xor (first / 32)) % modNum
    val third = (second xor (second * 2048)) % modNum
    third
}

