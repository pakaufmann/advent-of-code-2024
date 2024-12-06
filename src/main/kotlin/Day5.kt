import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day5.txt").readLines()

    val rules = lines.takeWhile { it != "" }.fold(mapOf<Int, Set<Int>>()) { map, line ->
        val (f, s) = line.split("|")
        map + (f.toInt() to map.getOrElse(f.toInt()) { emptySet() } + s.toInt())
    }

    val updates = lines.dropWhile { it != "" }.drop(1).map { it.split(",").map { n -> n.toInt() } }

    println(part1(rules, updates))
    println(part2(rules, updates))
}

private fun part1(rules: Map<Int, Set<Int>>, updates: List<List<Int>>) =
    updates.filter { it.isValid(rules) }.sumOf { it[it.size / 2] }

private fun part2(rules: Map<Int, Set<Int>>, updates: List<List<Int>>) =
    updates
        .filter { !it.isValid(rules) }
        .map { it.reorder(rules) }
        .sumOf { it[it.size / 2] }

private fun List<Int>.reorder(rules: Map<Int, Set<Int>>): List<Int> =
    sortedByDescending { rules.getOrDefault(it, emptySet()).intersect(this).size }

private fun List<Int>.isValid(rules: Map<Int, Set<Int>>): Boolean =
    indices.all { index ->
        val after = this.subList(index + 1, this.size)
        val number = this[index]
        !after.any { a -> rules.getOrDefault(a, emptySet()).contains(number) }
    }