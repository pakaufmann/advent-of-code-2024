import java.io.File


fun main() {
    val lines = File("inputs/day13.txt").readLines()
    val btnRegex = "Button .: X\\+([0-9]+), Y\\+([0-9]+)".toRegex()
    val priceRegex = "Prize: X=([0-9]+), Y=([0-9]+)".toRegex()

    val machines = lines.windowed(4, 4) { (a, b, price) ->
        Machine(
            Pos13.fromString(a, btnRegex),
            Pos13.fromString(b, btnRegex),
            Pos13.fromString(price, priceRegex)
        )
    }

    println(part1(machines))
    println(part2(machines))
}

private fun part1(machines: List<Machine>): Long = sumTokens(machines)

private fun part2(machines: List<Machine>): Long =
    sumTokens(machines.map { it.copy(price = it.price.add(10000000000000)) })


data class Machine(val a: Pos13, val b: Pos13, val price: Pos13) {
    fun isValid(a: Long, b: Long) =
        this.a.x * a + this.b.x * b == price.x && this.a.y * a + this.b.y * b == price.y
}

data class Pos13(val x: Long, val y: Long) {
    fun add(i: Long) = copy(x = x + i, y = y + i)

    companion object {
        fun fromString(s: String, regex: Regex): Pos13 {
            val (_, x, y) = regex.find(s)!!.groups.toList()
            return Pos13(x!!.value.toLong(), y!!.value.toLong())
        }
    }
}

private fun sumTokens(machines: List<Machine>) =
    machines
        .map { machine ->
            val b =
                (machine.price.x * machine.a.y - machine.price.y * machine.a.x) / (machine.a.y * machine.b.x - machine.b.y * machine.a.x)
            val a = (machine.price.y - b * machine.b.y) / machine.a.y
            machine to Pair(a, b)
        }
        .filter { (m, x) -> m.isValid(x.first, x.second) }
        .sumOf { (_, movement) ->
            movement.first * 3 + movement.second
        }