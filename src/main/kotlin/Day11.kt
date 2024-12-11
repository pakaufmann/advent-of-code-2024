import java.io.File
import kotlin.math.log10

fun main() {
    val lines = File("inputs/day11.txt").readLines()[0]

    val stones = lines.split(" ").map { it.toLong() }

    println(part1(stones))
    println(part2(stones))
}

private fun part1(stones: List<Long>) = calculateLength(stones, 25)

private fun part2(stones: List<Long>): Long = calculateLength(stones, 75)

private fun calculateLength(stones: List<Long>, turns: Int): Long {
    val cache = mutableMapOf<Pair<Long, Int>, Long>()

    fun calculateLength(stone: Long, turns: Int): Long {
        if (turns == 0) {
            return 1
        }

        val cached = cache[(Pair(stone, turns))]
        if (cached != null) {
            return cached
        }

        val length = (log10(stone.toDouble()) + 1).toInt()
        val remainingTurns = turns - 1
        return when {
            stone == 0L -> calculateLength(1, remainingTurns)
            length % 2 == 0 -> {
                val toSplit = stone.toString()
                val left = calculateLength(
                    toSplit.take(length / 2).toLong(),
                    remainingTurns
                )
                val right = calculateLength(
                    toSplit.drop(length / 2).toLong(),
                    remainingTurns
                )
                left + right
            }
            else -> calculateLength(stone * 2024, remainingTurns)
        }.also {
            cache[Pair(stone, turns)] = it
        }
    }

    return stones.sumOf { calculateLength(it, turns) }
}