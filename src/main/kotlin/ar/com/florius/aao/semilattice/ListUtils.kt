package ar.com.florius.aao.semilattice

fun uncons(a: List<String>): Pair<String, List<String>> {
    return a.first() to a.drop(1)
}

fun <T> fill(a: List<T>, size: Int, elem: T): List<T> {
    if (a.size > size) return a.dropLast(size - a.size)
    if (a.size == size) return a
    val missing = List(size - a.size) { elem }
    return a + missing
}