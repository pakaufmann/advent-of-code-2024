import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val input = File("inputs/day3.txt").readLines().toString()
    val regex = "(mul\\(([0-9]{1,3}),([0-9]{1,3})\\))|(do\\(\\))|(don't\\(\\))".toRegex()
    val matches = regex.findAll(input).toList()
    println(part1(matches))
    println(part2(matches))
}

private fun part1(matches: List<MatchResult>) =
    matches.filter { it.isMul() }.sumOf { it.mul() }

private fun part2(matches: List<MatchResult>) =
    matches.fold(Pair(0L, true)) { (sum, use), result ->
        if (result.isMul()) {
            (sum + if (use) result.mul() else 0) to use
        } else {
            sum to result.isDo()
        }
    }.first

private fun MatchResult.isMul() = groupValues[0].startsWith("mul")
private fun MatchResult.isDo() = groupValues[0].startsWith("do()")
private fun MatchResult.mul() = groupValues[2].toLong() * groupValues[3].toLong()