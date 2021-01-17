package ar.com.florius.aao.semilattice

fun uncons(a: List<String>): Pair<String, List<String>> {
    return a.first() to a.drop(1)
}