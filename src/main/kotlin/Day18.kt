import java.io.File
import kotlin.coroutines.suspendCoroutine
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day18.txt").readLines()
    val bytes = lines.map {
        val coords = it.split(",")
        Coord18(coords.first().toInt(), coords[1].toInt())
    }

    println(part1(bytes, 1024, 71, 71))
    println(part2(bytes, 71, 71))
}

private fun part1(bytes: List<Coord18>, amount: Int, width: Int, height: Int): Int {
    return run(bytes, amount, width, height)
}

private fun part2(bytes: List<Coord18>, width: Int, height: Int): Coord18? {
    for (i in bytes.indices) {
        if (run(bytes, i, width, height) == -1) {
            return bytes[i-1]
        }
    }
    return null
}

private fun run(
    bytes: List<Coord18>,
    amount: Int,
    width: Int,
    height: Int
): Int {
    val corrupted = bytes.take(amount).toSet()

    val queue = ArrayDeque(listOf(Coord18(0, 0) to 0))
    val visited = mutableSetOf<Coord18>()

    while (queue.isNotEmpty()) {
        val (pos, steps) = queue.removeFirst()

        if (pos.x == width - 1 && pos.y == height - 1) {
            return steps
        }

        if (visited.contains(pos)) {
            continue
        }

        visited.add(pos)

        queue.addAll(pos.neighbours()
            .filter {
                !corrupted.contains(it) &&
                        pos.x >= 0 && pos.y >= 0 &&
                        pos.x < width && pos.y < height
            }
            .map { it to steps + 1 }
        )
    }

    return -1
}

data class Coord18(val x: Int, val y: Int) {
    fun neighbours() = listOf(
        copy(y = y - 1),
        copy(y = y + 1),
        copy(x = x - 1),
        copy(x = x + 1)
    )
}