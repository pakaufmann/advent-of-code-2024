import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day19.txt").readLines()
    val patterns = lines.first().split(",").map { it.trim() }
    val towels = lines.drop(2)

    println(part1(patterns, towels))
    println(part2(patterns, towels))
}

private fun part1(patterns: List<String>, towels: List<String>) =
    towels.count { isPossible(it, patterns) }

private fun part2(patterns: List<String>, towels: List<String>) =
    towels.sumOf { countPossible(it, patterns) }

fun isPossible(towel: String, patterns: List<String>): Boolean {
    fun isPossible(index: Int): Boolean {
        if (towel.length == index) {
            return true
        }

        val rest = towel.drop(index)

        return patterns.filter { rest.startsWith(it) }
            .any { isPossible(index + it.length) }
    }

    return isPossible(0)
}

fun countPossible(towel: String, patterns: List<String>): Long {
    val cache = mutableMapOf<String, Long>()

    fun countPossible(rest: String): Long {
        val cached = cache[rest]
        if (cached != null) {
            return cached
        }

        if (rest.isEmpty()) {
            return 1
        }

        val count = patterns.filter { rest.startsWith(it) }
            .sumOf { countPossible(rest.drop(it.length)) }
        cache[rest] = count

        return count
    }

    return countPossible(towel)
}
