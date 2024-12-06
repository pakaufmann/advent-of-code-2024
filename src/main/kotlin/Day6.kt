import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day6.txt").readLines()

    val map = lines.withIndex()
        .flatMap { (y, l) ->
            l.toCharArray()
                .withIndex()
                .map { (x, t) -> Pos(x, y) to (t == '#') }
                .toList()
        }
        .toMap()

    val startPos = lines
        .withIndex()
        .flatMap { (y, l) ->
            l.withIndex()
                .filter { it.value == '^' }
                .map { it.index to y }
        }
        .first()

    println(part1(Pos(startPos.first, startPos.second), map))
    println(part2(Pos(startPos.first, startPos.second), map))
}

private fun part1(pos: Pos, map: Map<Pos, Boolean>): Int =
    findLoop(pos, map).second.map { it.first }.distinct().size

private fun part2(startPos: Pos, map: Map<Pos, Boolean>): Int {
    val validPosition = findLoop(startPos, map).second
    return validPosition
        .map { it.first }
        .distinct()
        .filter { it != startPos }
        .count { pos -> findLoop(startPos, map + (pos to true)).first }
}

private fun findLoop(
    pos: Pos,
    map: Map<Pos, Boolean>
): Pair<Boolean, Set<Pair<Pos, Orientation>>> {
    var curPos = pos
    var orientation = Orientation.UP
    val visited = mutableSetOf(curPos to orientation)

    while (true) {
        val check = when (orientation) {
            Orientation.UP -> curPos.up()
            Orientation.RIGHT -> curPos.right()
            Orientation.DOWN -> curPos.down()
            Orientation.LEFT -> curPos.left()
        }

        val next = map[check] ?: return false to visited
        if (next) {
            orientation = Orientation.values()[(orientation.ordinal + 1) % Orientation.values().size]
        } else {
            curPos = check
            if (visited.contains(curPos to orientation)) {
                return true to emptySet()
            }
            visited.add(curPos to orientation)
        }
    }
}

private data class Pos(val x: Int, val y: Int) {
    fun up() = copy(y = y - 1)
    fun down() = copy(y = y + 1)
    fun left() = copy(x = x - 1)
    fun right() = copy(x = x + 1)
}

private enum class Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT
}