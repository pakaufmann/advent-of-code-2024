import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day2.txt").readLines()
    val reports = lines.map { it.split(" ").map { n -> n.toInt() } }

    println(part1(reports))
    println(part2(reports))
}

private fun part1(reports: List<List<Int>>): Int = reports.count { isValid(it) }

private fun part2(reports: List<List<Int>>): Int =
    reports.count { subReports(it).any { r -> isValid(r) } }

private fun subReports(report: List<Int>): List<List<Int>> =
    (report.indices).map { i ->
        report.subList(0, i) + report.subList(i + 1, report.size)
    } + listOf(report)

private fun isValid(report: List<Int>): Boolean {
    val first = report.first()
    val second = report.drop(1).first()
    if (first == second) {
        return false
    }
    val ascending =  first < second

    return report.windowed(2, 1).all { (f, s) ->
        (f-s).absoluteValue <= 3 && if (ascending) f < s  else f > s
    }
}