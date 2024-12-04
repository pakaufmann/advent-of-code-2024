import java.io.File

fun main() {
    val lines = File("inputs/day4.txt").readLines()
    val crossword = lines.map { it.toCharArray().toList() }

    println(part1(crossword))
    println(part2(crossword))
}

private fun part1(crossword: List<List<Char>>) =
    crossword.withIndex().sumOf { (y, l) -> l.indices.sumOf { x -> crossword.hasXmas(x, y) } }

private fun part2(crossword: List<List<Char>>) =
    crossword.withIndex().sumOf { (y, l) -> l.indices.count { x -> crossword.isXmas2(x, y) } }

private val FORWARD = listOf('X', 'M', 'A', 'S')
private val BACKWARDS = FORWARD.reversed()

private fun List<List<Char>>.hasXmas(x: Int, y: Int): Int {
    if (this[y][x] != 'X') return 0

    return listOf(
        // horizontal forward
        this[y].safeSubList(x, x + 4) == FORWARD,
        // horizontal backwards
        this[y].safeSubList(x - 3, x + 1) == BACKWARDS,
        // vertical forward
        this.safeSubList(y, y + 4).map { it[x] } == FORWARD,
        // vertical backwards
        this.safeSubList(y - 3, y + 1).map { it[x] } == BACKWARDS,
        // diagonal down right
        this.safeSubList(y, y + 4).withIndex().mapNotNull { (i, l) -> l.getOrNull(x + i) } == FORWARD,
        // diagonal down left
        this.safeSubList(y, y + 4).withIndex().mapNotNull { (i, l) -> l.getOrNull(x - i) } == FORWARD,
        // diagonal up left
        this.safeSubList(y - 3, y + 1).withIndex().mapNotNull { (i, l) -> l.getOrNull(x + (3 - i)) } == BACKWARDS,
        // diagonal up right1
        this.safeSubList(y - 3, y + 1).withIndex().mapNotNull { (i, l) -> l.getOrNull(x - (3 - i)) } == BACKWARDS,
    ).count { it }
}

private fun List<List<Char>>.isXmas2(x: Int, y: Int): Boolean {
    if (this[y][x] != 'A') return false

    val matrix = this.safeSubList(y - 1, y + 2).map { it.safeSubList(x - 1, x + 2) }
    if (matrix.size < 3 || matrix[0].size < 3) return false

    val topLeft = matrix[0][0]
    val topRight = matrix[0][2]
    val bottomLeft = matrix[2][0]
    val bottomRight = matrix[2][2]
    return (topLeft == 'M' && topRight == 'S' && bottomLeft == 'M' && bottomRight == 'S') ||
        (topLeft == 'S' && topRight == 'M' && bottomLeft == 'S' && bottomRight == 'M') ||
        (topLeft == 'S' && topRight == 'S' && bottomLeft == 'M' && bottomRight == 'M') ||
        (topLeft == 'M' && topRight == 'M' && bottomLeft == 'S' && bottomRight == 'S')
}

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
    this.subList(fromIndex.coerceAtLeast(0), toIndex.coerceAtMost(this.size))