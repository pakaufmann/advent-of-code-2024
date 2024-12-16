import java.io.File
import java.rmi.dgc.Lease
import java.sql.Time
import java.time.LocalDateTime
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val lines = File("inputs/day16.txt").readLines()

    var start = Coord16(0, 0)
    var end = Coord16(0, 0)
    val map = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, c) ->
            if (c == 'S') start = Coord16(x, y)
            if (c == 'E') end = Coord16(x, y)
            if (c == '#') {
                Coord16(x, y)
            } else {
                null
            }
        }.filterNotNull()
    }.toSet()

    val allBestRoutes = findAllBestRoutes(start, end, map)
    println(part1(allBestRoutes))
    println(part2(allBestRoutes))
}

fun part1(allBestRoutes: Pair<State?, Set<Coord16>>): Int =
    allBestRoutes.first?.score ?: Int.MAX_VALUE

private fun part2(allBestRoutes: Pair<State?, Set<Coord16>>): Int =
    allBestRoutes.second.size

private fun findAllBestRoutes(start: Coord16, end: Coord16, map: Set<Coord16>): Pair<State?, Set<Coord16>> {
    val queue =
        PriorityQueue(compareBy<State> { it.score })
    queue.add(State(start, 0, Direction.EAST, setOf(start)))

    val visited = mutableMapOf<Pair<Coord16, Direction>, Int>()

    var best: State? = null
    val allBest = mutableSetOf<Coord16>()

    while (queue.isNotEmpty()) {
        val curBest = best
        val current = queue.poll()

        if (curBest != null && current.score > curBest.score) {
            break
        }

        if (current.pos == end && current.score <= (best?.score ?: Int.MAX_VALUE)) {
            best = current

            if (current.score < (best?.score ?: Int.MAX_VALUE)) {
                allBest.clear()
            }
            allBest.addAll(current.visited)
        }

        val score = visited[current.pos to current.dir]

        if (score != null && score < current.score) {
            continue
        }
        if (score != null && score <= current.score && allBest.contains(current.pos)) {
            allBest.addAll(current.visited)
            continue
        }

        visited[current.pos to current.dir] = current.score

        val moved = current.pos.move(current.dir)
        if (!map.contains(moved)) {
            if (current.visited.contains(moved)) {
                continue
            }
            queue.add(State(moved, current.score + 1, current.dir, current.visited + moved))
        }

        queue.addAll(
            listOf(
                State(current.pos, current.score + 1000, current.dir.turnRight(), current.visited),
                State(current.pos, current.score + 1000, current.dir.turnLeft(), current.visited)
            )
        )
    }

    return best to allBest
}

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    fun turnRight() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }

    fun turnLeft() = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }
}

data class State(val pos: Coord16, val score: Int, val dir: Direction, val visited: Set<Coord16>)

data class Coord16(val x: Int, val y: Int) {
    fun move(dir: Direction) = when (dir) {
        Direction.NORTH -> copy(y = y - 1)
        Direction.SOUTH -> copy(y = y + 1)
        Direction.EAST -> copy(x = x + 1)
        Direction.WEST -> copy(x = x - 1)
    }
}