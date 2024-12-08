import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day8.txt").readLines()

    val antennas = lines.withIndex().flatMap { (y, l) ->
        l.withIndex().filter { it.value != '.' }.map { (x, c) ->
            Pair(c, Coord(x, y))
        }
    }.fold(mapOf<Char, List<Coord>>()) { acc, (c, coord) ->
        val set = acc.getOrDefault(c, emptyList())
        acc + (c to (set + coord))
    }

    println(part1(antennas, lines.first().length, lines.size))
    println(part2(antennas, lines.first().length, lines.size))
}

private fun part1(antennas: Map<Char, List<Coord>>, width: Int, height: Int): Int {
    val antinodes = createAntinodes(antennas) { s ->
        s.drop(1).take(1).filter { it.inBounds(width, height) }
    }
    return antinodes.toSet().count()
}

private fun part2(antennas: Map<Char, List<Coord>>, width: Int, height: Int): Int {
    val antinodes = createAntinodes(antennas) { s ->
        s.takeWhile { it.inBounds(width, height) }
    }
    return antinodes.toSet().count()
}

private fun createAntinodes(
    antennas: Map<Char, List<Coord>>,
    take: (Sequence<Coord>) -> Sequence<Coord>
): List<Coord> =
    antennas.values.flatMap { coords ->
        coords.withIndex().flatMap { (i, coord) ->
            coord.createAntinodes(coords.drop(i + 1), take)
        }
    }

data class Coord(val x: Int, val y: Int) {
    fun diff(other: Coord) = Coord(x - other.x, y - other.y)

    fun plus(diff: Coord) = Coord(x + diff.x, y + diff.y)

    fun minus(diff: Coord) = Coord(x - diff.x, y - diff.y)

    fun inBounds(w: Int, h: Int) = x >= 0 && y >= 0 && x < w && y < h

    fun createAntinodes(coords: List<Coord>, take: (Sequence<Coord>) -> Sequence<Coord>) =
        coords.flatMap { other ->
            val diff = diff(other)
            val plusSeq = generateSequence(this) {
                it.plus(diff)
            }
            val minusSeq = generateSequence(other) {
                it.minus(diff)
            }
            take(plusSeq) + take(minusSeq)
        }
}
