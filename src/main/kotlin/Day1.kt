import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day1.txt").readLines()

    val (first, second) = lines.fold(Pair(listOf<Int>(), listOf<Int>())) { (f, s), line ->
        val (first, second) = line.split(" +".toRegex())
        Pair(f + first.toInt(), s + second.toInt())
    }

    println(part1(first, second))
    println(part2(first, second))
}

private fun part1(first: List<Int>, second: List<Int>) =
    first.sorted().zip(second.sorted()).sumOf { (f, s) -> (f - s).absoluteValue }

private fun part2(first: List<Int>, second: List<Int>) =
    first.sumOf { number ->
        second.count { it == number } * number
    }
