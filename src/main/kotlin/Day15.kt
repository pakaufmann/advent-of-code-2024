import java.io.File

fun main() {
    val lines = File("inputs/day15.txt").readLines()

    val map = lines
        .takeWhile { it != "" }
        .withIndex()
        .flatMap { (y, line) ->
            line.withIndex().map { (x, c) ->
                when (c) {
                    '#' -> Coord15(x, y) to c
                    'O' -> Coord15(x, y) to c
                    '@' -> Coord15(x, y) to c
                    else -> null
                }
            }.filterNotNull()
        }.toMap()

    val movements = lines
        .dropWhile { it != "" }
        .joinToString("")
        .split("")
        .filter { it != "" }

    val start = map.entries.first { it.value == '@' }.key

    println(part1((map - start).toMutableMap(), movements, start))
    println(part2((map - start).toMutableMap(), movements, start))
}

private fun part1(map: MutableMap<Coord15, Char>, movements: List<String>, start: Coord15): Int {
    val expandedMap = map.map { (k, v) ->
        k to (v to Key(k, true))
    }.toMap().toMutableMap()

    run(expandedMap, movements, start) { map, _, newPos, movement ->
        val nextFree = findNextFree(map, newPos, movement)
        if (nextFree != null) {
            map.remove(newPos)
            map[nextFree] = 'O' to Key(nextFree, true)
            true
        } else {
            false
        }
    }

    return sumMap(expandedMap)
}

private fun part2(map: MutableMap<Coord15, Char>, movements: List<String>, start: Coord15): Int {
    val expandedMap = map.flatMap { (k, v) ->
        listOf(
            k.copy(x = k.x * 2, y = k.y) to (v to Key(k, true)),
            k.copy(x = k.x * 2 + 1, y = k.y) to (v to Key(k, false)),
        )
    }.toMap().toMutableMap()

    run(expandedMap, movements, start.copy(x = start.x * 2, y = start.y)) { map, atPos, newPos, movement ->
        val toMove = findAllToMove(map, newPos, atPos.second, movement)
        val moved = toMove.map { it.move(movement) }
        val foo = toMove.associate { it.move(movement) to ('O' to map[it]!!.second) }

        if (moved.all { map[it]?.first != '#' }) {
            toMove.forEach { map.remove(it) }
            map.putAll(foo)
            true
        } else {
            false
        }
    }

    return sumMap(expandedMap)
}

fun run(
    map: MutableMap<Coord15, Pair<Char, Key>>,
    movements: List<String>,
    start: Coord15,
    update: (MutableMap<Coord15, Pair<Char, Key>>, Pair<Char, Key>, Coord15, String) -> Boolean
) {
    var pos = start
    for (movement in movements) {
        val newPos = pos.move(movement)
        val atPos = map[newPos]
        pos = when (atPos?.first) {
            '#' -> pos
            'O' -> if (update(map, atPos, newPos, movement)) newPos else  pos
            else -> newPos
        }
    }
}

private fun printMap(expandedMap: MutableMap<Coord15, Pair<Char, Key>>) {
    for (y in 0 until 10) {
        for (x in 0 until 20) {
            if (expandedMap.containsKey(Coord15(x, y))) {
                print(expandedMap[Coord15(x, y)]?.first)
            } else {
                print(".")
            }
        }
        println("")
    }
}

private fun sumMap(map: MutableMap<Coord15, Pair<Char, Key>>) =
    map.filter { it.value.first == 'O' && it.value.second.left }.map { it.key.y * 100 + it.key.x }.sum()

fun findAllToMove(
    map: Map<Coord15, Pair<Char, Key>>,
    pos: Coord15,
    id: Key,
    movement: String
): Set<Coord15> {
    val toCheck = ArrayDeque(listOf(pos, pos.neighbour(map, id)))
    val checked = mutableSetOf<Coord15>()

    while (toCheck.isNotEmpty()) {
        val current = toCheck.removeFirst()
        if (checked.contains(current)) {
            continue
        }
        checked.add(current)

        val next = current.move(movement)
        val nextPos = map[next]
        if (nextPos?.first == 'O') {
            toCheck.addAll(listOf(next, next.neighbour(map, nextPos.second)))
        }
    }

    return checked
}

fun findNextFree(map: Map<Coord15, Pair<Char, Key>>, pos: Coord15, movement: String): Coord15? {
    val newPos = pos.move(movement)
    return when (map[newPos]?.first) {
        '#' -> null
        'O' -> findNextFree(map, newPos, movement)
        else -> newPos
    }
}

data class Key(val coord: Coord15, val left: Boolean)

data class Coord15(val x: Int, val y: Int) {
    fun move(movement: String): Coord15 =
        when (movement) {
            "^" -> copy(y = y - 1)
            ">" -> copy(x = x + 1)
            "v" -> copy(y = y + 1)
            "<" -> copy(x = x - 1)
            else -> this
        }

    fun neighbour(map: Map<Coord15, Pair<Char, Key>>, id: Key): Coord15 =
        if (map[this.copy(x = x - 1)]?.second?.coord == id.coord) {
            this.copy(x = x - 1)
        } else {
            this.copy(x = x + 1)
        }
}