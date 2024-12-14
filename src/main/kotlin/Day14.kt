import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day14.txt").readLines()
    val regex = "p=([0-9]+),([0-9]+) v=([-0-9]+),([-0-9]+)".toRegex()

    val robots = lines.map {
        val (_, x, y, vX, vY) = regex.find(it)!!.groups.toList()
        Robot(x!!.value.toInt(), y!!.value.toInt(), vX!!.value.toInt(), vY!!.value.toInt())
    }

    println(part1(robots, 101, 103, 100))
    println(part2(robots, 101, 103))
}

private fun part1(robots: List<Robot>, width: Int, height: Int, rounds: Int): Int {
    val endPositions = positionsAt(robots, rounds, width, height)

    val center = Pair(width / 2, height / 2)
    val quadrants = mutableMapOf<Int, Set<Robot>>()

    for (robot in endPositions) {
        val left = robot.x < center.first
        val above = robot.y < center.second
        val right = robot.x > center.first
        val below = robot.y > center.second

        val quadrant = when {
            left && above -> 1
            right && above -> 2
            right && below -> 3
            left && below -> 4
            else -> null
        }
        if (quadrant != null) {
            val set = quadrants.getOrDefault(quadrant, emptySet())
            quadrants[quadrant] = set + robot
        }
    }
    return quadrants.mapValues { it.value.size }.values.reduce { acc, i -> acc * i }
}

private fun part2(robots: List<Robot>, width: Int, height: Int) {
    val rounds = generateSequence(0 to robots) { (round, _) ->
        round + 1 to positionsAt(robots, round + 1, width, height)
    }
    val (round, positions) = rounds.dropWhile { !areChristmasTree(it.second) }.first()

    val pos = positions.map { Coord14(it.x, it.y) }.toSet()
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (pos.contains(Coord14(x, y))) {
                print('#')
            } else {
                print('.')
            }
        }
        println("")
    }

    println(round)
}

private fun areChristmasTree(robots: List<Robot>): Boolean {
    val positions = robots.map { Coord14(it.x, it.y) }.toSet()

    return positions.any { it.neighbours().all { n -> positions.contains(n) } }
}

data class Robot(val x: Int, val y: Int, val vX: Int, val vY: Int)

data class Coord14(val x: Int, val y: Int) {
    fun neighbours() = listOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y + 1),
        copy(y = y - 1),
        //
        copy(x = x + 1, y = y - 1),
        copy(x = x - 1, y = y - 1),
        copy(x = x + 1, y = y + 1),
        copy(x = x - 1, y = y + 1)
    )
}

private fun positionsAt(
    robots: List<Robot>,
    rounds: Int,
    width: Int,
    height: Int
): List<Robot> = robots.map {
    it.copy(x = (it.x + it.vX * rounds).mod(width), y = (it.y + it.vY * rounds).mod(height))
}
