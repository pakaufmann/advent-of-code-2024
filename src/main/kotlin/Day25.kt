import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day25.txt").readLines()
    val keys = mutableListOf<List<Int>>()
    val locks = mutableListOf<List<Int>>()

    val key = mutableListOf<Int>()
    val lock = mutableListOf<Int>()
    var current = key
    var start = true

    for (line in lines) {
        if (line.isEmpty()) {
            if (current == key) keys.add(key.map { it - 1 }.toList())
            if (current == lock) {
                locks.add(lock.toList())
            }
            key.clear()
            lock.clear()
            start = true
        } else {
            if (start) {
                current = if (line.all { it == '#' }) lock else key
                start = false
                for (i in line.indices) {
                    key.add(0)
                    lock.add(0)
                }
            } else {
                for ((i, c) in line.withIndex()) {
                    current[i] = current[i] + if (c == '#') 1 else 0
                }
            }
        }
    }
    if (current == key) keys.add(key.map { it - 1 }.toList())
    if (current == lock) locks.add(lock.toList())

    println(part1(keys, locks))
}

private fun part1(keys: List<List<Int>>, locks: List<List<Int>>): Int =
    locks.sumOf { lock -> keys.count { match(lock, it) } }

fun match(lock: List<Int>, key: List<Int>): Boolean =
    lock.withIndex().all { (i, c) -> key[i] + c < 6 }
