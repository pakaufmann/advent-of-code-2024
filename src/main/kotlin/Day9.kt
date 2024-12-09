import java.io.File

fun main() {
    val input = File("inputs/day9.txt").readLines()[0]

    println(part1(readInput(input)))
    println(part2(readInput(input)))
}

private fun part1(blocks: List<Block>): Long {
    for (block in blocks.reversed()) {
        blocks.subList(0, block.index).asSequence()
            .filter { it.freeSize() > 0 }
            .takeWhile {
                it.addBlock(block)
                !block.empty()
            }
            .lastOrNull()
    }

    return blocks.flatMap { it.filled + List(it.freeSize()) { 0 } }.withIndex().sumOf { (i, v) -> i.toLong() * v }
}

private fun part2(blocks: List<Block>): Long {
    for (block in blocks.reversed().filter { !it.empty() }) {
        val moveTo = blocks.subList(0, block.index).firstOrNull { it.freeSize() >= block.size }
        moveTo?.addBlock(block)
    }
    return blocks.flatMap { it.filled + List(it.freeSize()) { 0 } }.withIndex().sumOf { (i, v) -> i.toLong() * v }
}

fun readInput(input: String) =
    input
        .toCharArray()
        .map { it.toString().toInt() }
        .withIndex()
        .map { (i, v) ->
            if (i % 2 == 0) {
                Block(i, v, List(v) { i / 2 })
            } else {
                Block(i, v, listOf())
            }
        }

data class Block(val index: Int, val size: Int, var filled: List<Int>) {
    fun freeSize() = size - filled.size

    fun empty() = filled.isEmpty()

    fun addBlock(block: Block) {
        val toMove = freeSize()
        filled = filled + block.filled.take(toMove)
        block.filled = block.filled.drop(toMove)
    }
}
