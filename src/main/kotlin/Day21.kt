import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val codes = File("inputs/day21.txt").readLines()

    println(part1(codes))
    println(part2(codes))
}

private fun part1(codes: List<String>): Long =
    findSum(codes, 2)

private fun part2(codes: List<String>): Long =
    findSum(codes, 25)


val numPad = mapOf(
    '7' to Pos21(0, 0),
    '8' to Pos21(1, 0),
    '9' to Pos21(2, 0),
    '4' to Pos21(0, 1),
    '5' to Pos21(1, 1),
    '6' to Pos21(2, 1),
    '1' to Pos21(0, 2),
    '2' to Pos21(1, 2),
    '3' to Pos21(2, 2),
    '0' to Pos21(1, 3),
    'A' to Pos21(2, 3),
)

val directionalPad = mapOf(
    '^' to Pos21(1, 0),
    'A' to Pos21(2, 0),
    '<' to Pos21(0, 1),
    'v' to Pos21(1, 1),
    '>' to Pos21(2, 1),
)

private fun findSum(codes: List<String>, rounds: Int): Long =
    codes.sumOf { code ->
        val movements = typeCode(code, numPad, Pos21(2, 3))
        val solved = solvePad(movements, rounds)
        solved * code.replace("A", "").toInt()
    }

val cache = mutableMapOf<Pair<List<List<Char>>, Int>, Long>()

fun solvePad(movements: List<List<Char>>, remainingRobots: Int): Long {
    if (remainingRobots == 0) {
        return movements.sumOf { it.size + 1 }.toLong()
    }

    val cached = cache[movements to remainingRobots]
    if (cached != null) {
        return cached
    }

    return movements.sumOf { c ->
        solvePad(
            move("${c.joinToString("")}A", directionalPad, Pos21(2, 0)),
            remainingRobots - 1
        )
    }.also {
        cache[movements to remainingRobots] = it
    }
}

fun typeCode(code: String, pad: Map<Char, Pos21>, start: Pos21): List<List<Char>> {
    var pos = start
    val movements = mutableListOf<List<Char>>()

    for (char in code) {
        val toPos = pad[char]!!
        val dx = pos.x - toPos.x
        val dy = pos.y - toPos.y

        movements.add(
            if ((char == '1' || char == '4' || char == '7') && pos.y == 3) {
                listOf(List(dy.absoluteValue) { '^' }, List(dx.absoluteValue) { '<' }).flatten()
            } else if ((char == '0' || char == 'A') && pos.x == 0) {
                listOf(List(dx.absoluteValue) { '>' }, List(dy.absoluteValue) { 'v' }).flatten()
            } else {
                val movement = mutableListOf<Char>()
                if (dx > 0) {
                    movement.addAll(List(dx.absoluteValue) { '<' })
                }
                movement.addAll(List(dy.absoluteValue) { if (dy > 0) '^' else 'v' })
                if (dx < 0) {
                    movement.addAll(List(dx.absoluteValue) { '>' })
                }
                movement
            }
        )

        pos = toPos
    }

    return movements
}

fun move(code: String, pad: Map<Char, Pos21>, start: Pos21): List<List<Char>> {
    var pos = start
    val movements = mutableListOf<List<Char>>()

    for (char in code) {
        val toPos = pad[char]!!
        val dx = pos.x - toPos.x
        val dy = pos.y - toPos.y

        movements.add(if (pad['A'] == pos && char == 'v') {
            listOf('<', 'v')
        } else if (pad['v'] == pos && char == 'A') {
            listOf('^', '>')
        } else if (dy > 0) {
            listOf(
                List(dx.absoluteValue) { if (dx > 0) '<' else '>' },
                List(dy.absoluteValue) { '^' }
            ).flatten()
        } else {
            listOf(
                List(dy.absoluteValue) { 'v' },
                List(dx.absoluteValue) { if (dx > 0) '<' else '>' }
            ).flatten()
        })

        pos = toPos
    }

    return movements
}

data class Pos21(val x: Int, val y: Int)