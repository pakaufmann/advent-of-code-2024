import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day20.txt").readLines()

    var start = Coord20(0, 0)
    var end = Coord20(0, 0)

    val obstacles = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().mapNotNull { (x, c) ->
            if (c == '#') {
                Coord20(x, y)
            } else {
                if (c == 'S') {
                    start = Coord20(x, y)
                }
                if (c == 'E') {
                    end = Coord20(x, y)
                }

                null
            }
        }
    }.toSet()

    println(part1(obstacles, start, end))
    println(part2(obstacles, start, end))
}

private fun part1(obstacles: Set<Coord20>, start: Coord20, end: Coord20): Int =
    finishTimes(obstacles, start, end, 2).count { it >= 100 }

private fun part2(obstacles: Set<Coord20>, start: Coord20, end: Coord20): Int =
    finishTimes(obstacles, start, end, 20).count { it >= 100 }

private fun finishTimes(
    obstacles: Set<Coord20>,
    start: Coord20,
    end: Coord20,
    jumps: Int
): List<Int> {
    val maxX = obstacles.maxOf { it.x } + 1
    val maxY = obstacles.maxOf { it.y } + 1
    val path = path(obstacles, start, end)
    val baseTime = (maxX * maxY - obstacles.size) - 1

    val toFinish = path.reversed().withIndex().associate { it.value to it.index }

    return path.withIndex().flatMap { (count, begin) ->
        val finishTimes = startPath(begin, jumps, toFinish)
        finishTimes.map { it + count }.filter { it < baseTime }.map { baseTime - it }
    }
}

fun startPath(start: Coord20, jumps: Int, toFinish: Map<Coord20, Int>): List<Int> =
    start.neighboursReach(jumps)
        .filter { toFinish.contains(it) }
        .map { it.diffTo(start) + (toFinish[it] ?: 0) }

fun path(obstacles: Set<Coord20>, start: Coord20, end: Coord20): List<Coord20> {
    val queue = ArrayDeque(listOf(start to listOf(start)))
    val visited = mutableSetOf<Coord20>()

    while (queue.isNotEmpty()) {
        val (next, count) = queue.removeFirst()

        if (next == end) {
            return count
        }

        if (visited.contains(next)) {
            continue
        }
        visited.add(next)

        queue.addAll(next.neighbours()
            .filter { !obstacles.contains(it) && it.x >= 0 && it.y >= 0 }
            .map { it to (count + it) }
        )
    }

    return emptyList()
}

data class Coord20(val x: Int, val y: Int) {
    fun neighbours() = listOf(
        copy(y = y - 1),
        copy(y = y + 1),
        copy(x = x - 1),
        copy(x = x + 1)
    )

    fun neighboursReach(reach: Int): List<Coord20> {
        val l = mutableListOf<Coord20>()
        for (dx in -reach until (reach + 1)) {
            for (dy in -reach until (reach + 1)) {
                if (dx.absoluteValue + dy.absoluteValue <= reach && (dx != 0 || dy != 0)) {
                    l.add(copy(x = x + dx, y = y + dy))
                }
            }
        }
        return l.toList()
    }

    fun diffTo(other: Coord20): Int =
        (x - other.x).absoluteValue + (y - other.y).absoluteValue
}