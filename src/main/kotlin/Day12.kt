import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day12.txt").readLines()

    val map = lines.withIndex().flatMap { (y, line) ->
        line.withIndex().map { (x, c) ->
            Coord12(x, y) to c
        }
    }.toMap()

    println(part1(map))
    println(part2(map))
}

private fun part1(map: Map<Coord12, Char>): Int {
    val regions = findRegions(map)

    return regions.sumOf {
        it.area * it.perimeter
    }
}

private fun part2(map: Map<Coord12, Char>): Int {
    val regions = findRegions(map)
    return regions.sumOf { it.area * it.sides() }
}

fun findRegions(map: Map<Coord12, Char>): MutableList<Region> {
    val seen = mutableSetOf<Coord12>()
    val regions = mutableListOf<Region>()

    for ((coord, plant) in map) {
        if (seen.contains(coord)) {
            continue
        }

        val region = findRegion(map, coord, plant)
        seen.addAll(region.region)
        regions.add(region)
    }

    return regions
}

fun findRegion(map: Map<Coord12, Char>, coord12: Coord12, plant: Char): Region {
    val queue = ArrayDeque(listOf(coord12))
    val region = mutableSetOf<Coord12>()

    while (queue.isNotEmpty()) {
        val check = queue.removeFirst()
        if (!region.contains(check)) {
            region.add(check)
            queue.addAll(check.neighbours().filter {
                map[it] == plant
            })
        }
    }

    return Region(region)
}

data class Region(val region: Set<Coord12>) {
    val area = region.size

    val perimeter = region.sumOf {
        it.neighbours().count { coord ->
            !region.contains(coord)
        }
    }

    fun sides() = findSides { it.copy(x = it.x + 1) } +
            findSides { it.copy(x = it.x - 1) } +
            findSides { it.copy(y = it.y + 1) } +
            findSides { it.copy(y = it.y - 1) }

    private fun findSides(change: (Coord12) -> Coord12): Int {
        val map = region.filter { !region.contains(change(it)) }.associateWith { 'A' }

        return findRegions(map).size
    }
}

data class Coord12(val x: Int, val y: Int) {
    fun neighbours() = listOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y + 1),
        copy(y = y - 1),
    )
}