import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day7.txt").readLines()

    val calculations = lines.map {
        it.split(" ")
            .map { n -> n.replace(":", "").toLong() }
    }.map {
        Calculation(it.first(), it.drop(1))
    }

    println(part1(calculations).sumOf { it.target })
    println(part2(calculations).sumOf { it.target })
}

private fun part1(calculations: List<Calculation>) =
    calculations.filter {
        it.isValid(listOf(Long::times, Long::plus))
    }

private fun part2(calculations: List<Calculation>) =
    calculations.filter {
        it.isValid(
            listOf(Long::times, Long::plus, { f, s -> "$f$s".toLong() })
        )
    }

data class Calculation(val target: Long, val numbers: List<Long>) {
    fun isValid(calc: List<(Long, Long) -> Long>): Boolean {
        fun isValid(sum: Long, rest: List<Long>): Boolean {
            if (sum > target && rest.isNotEmpty()) return false

            if (rest.isEmpty()) {
                return sum == target
            }

            val next = rest.first()
            val newRest = rest.drop(1)
            return calc.any { isValid(it(sum, next), newRest) }
        }

        return isValid(numbers.first(), numbers.drop(1))
    }
}