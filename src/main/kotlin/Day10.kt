import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day10.txt").readLines()

    val map = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, c) -> Coord10(x, y) to c.toString().toInt() }
    }.toMap()

    println(part1(map))
    println(part2(map))
}

private fun part1(map: Map<Coord10, Int>) =
    map.filter { it.value == 0 }
        .map { start -> findFinishes(start.toPair(), map).distinct().size }
        .sum()

private fun part2(map: Map<Coord10, Int>) =
    map.filter { it.value == 0 }
        .map { start -> findFinishes(start.toPair(), map).size }
        .sum()

private fun findFinishes(start: Pair<Coord10, Int>, map: Map<Coord10, Int>): List<Coord10> {
    val queue = ArrayDeque(listOf(start))
    val finishes = mutableListOf<Coord10>()

    while (queue.isNotEmpty()) {
        val (check, height) = queue.removeFirst()
        if (height == 9) {
            finishes.add(check)
        } else {
            queue.addAll(check.neighbours()
                .filter { map[it] == height + 1 }
                .map { it to height + 1 })
        }
    }

    return finishes
}

data class Coord10(val x: Int, val y: Int) {
    fun neighbours() = listOf(
        copy(y = y - 1),
        copy(y = y + 1),
        copy(x = x - 1),
        copy(x = x + 1)
    )
}