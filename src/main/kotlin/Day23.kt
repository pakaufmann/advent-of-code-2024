import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val lines = File("inputs/day23.txt").readLines()
    val connections = lines.map {
        val (f, s) = it.split("-")
        Connection(f, s)
    }

    val graph = buildGraph(connections)
    println(part1(graph))
    println(part2(graph).joinToString(","))
}

private fun part1(graph: Map<String, Set<String>>): Int {
    val triples = graph.flatMap { (node, _) ->
        findTriples(node, graph)
    }.toSet()

    return triples.filter { triple -> triple.any { it.startsWith("t") } }.size
}

private fun part2(graph: Map<String, Set<String>>): List<String> {
    val sets = ArrayDeque(graph.keys.map { setOf(it) })
    var largestSet = emptySet<String>()

    while (sets.isNotEmpty()) {
        val set = sets.removeFirst()
        val next = graph.getValue(set.first())
            .firstOrNull { n -> !set.contains(n) && set.all { graph[it]!!.contains(n) } }

        if (next != null) {
            sets.add(set + next)
        }
        if (set.size > largestSet.size) {
            largestSet = set
        }
    }

    return largestSet.sortedBy { it }
}

fun buildGraph(connections: List<Connection>): Map<String, Set<String>> {
    val graph = mutableMapOf<String, Set<String>>()

    for (connection in connections) {
        graph[connection.first] = graph.getOrDefault(connection.first, emptySet()) + connection.second
        graph[connection.second] = graph.getOrDefault(connection.second, emptySet()) + connection.first
    }
    return graph
}

fun findTriples(node: String, graph: Map<String, Set<String>>): Set<Set<String>> {
    val toCheck = ArrayDeque(listOf(node to setOf(node)))
    val checked = mutableSetOf<String>()

    val triples = mutableSetOf<Set<String>>()

    while (toCheck.isNotEmpty()) {
        val (current, nodes) = toCheck.removeFirst()

        if (nodes.size == 3) {
            if (nodes.all { n ->
                    graph[n]!!.containsAll(nodes.filter { it != n })
                }) {
                triples.add(nodes)
            }
            continue
        }
        if (checked.contains(current)) continue
        checked.add(current)

        toCheck.addAll(graph[node]!!.filter { !nodes.contains(it) }.map { it to (nodes + it) })
    }

    return triples
}

data class Connection(val first: String, val second: String)
