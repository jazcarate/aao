package ar.com.florius.aao.poset

import ar.com.florius.aao.IncompatibleTagsException

data class TagName(private val name: List<String>) {

    constructor(name: String) : this(name.split(":"))

    fun mappend(a: TagName): TagName {
        return TagName(commonGo(name, a.name, 0))
    }

    private fun commonGo(x: List<String>, y: List<String>, level: Int): List<String> {
        if (x.isEmpty() && y.isEmpty()) return emptyList()
        if (x.isEmpty()) return y
        if (y.isEmpty()) return x

        val (x1, xs) = uncons(x)
        val (y1, ys) = uncons(y)
        if (x1 == y1) return listOf(x1) + commonGo(xs, ys, level + 1)
        throw IncompatibleTagsException("$x is incompatible with $y at level $level of $name")
    }
}


