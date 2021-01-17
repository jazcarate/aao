package ar.com.florius.aao.semilattice

import ar.com.florius.aao.IncompatibleTagsException


sealed class Breadcrumb {
    abstract fun join(a: Breadcrumb): Breadcrumb

    data class Tagged(private val name: List<String>) : Breadcrumb() {

        constructor(name: String) : this(name.split(":"))

        override fun join(a: Breadcrumb): Breadcrumb {
            return when (a) {
                is Tagged -> {
                    try {
                        Tagged(joinGo(name, a.name, 0))
                    } catch (e: IncompatibleTagsException) {
                        return Incompatible
                    }
                }
                else -> a.join(this)
            }
        }

        private fun joinGo(x: List<String>, y: List<String>, level: Int): List<String> {
            if (x.isEmpty() && y.isEmpty()) return emptyList()
            if (x.isEmpty()) return y
            if (y.isEmpty()) return x

            val (x1, xs) = uncons(x)
            val (y1, ys) = uncons(y)
            if (x1 == y1) return listOf(x1) + joinGo(xs, ys, level + 1)
            throw IncompatibleTagsException("$x is incompatible with $y at level $level of $name")
        }
    }

    object NoTag : Breadcrumb() {
        override fun join(a: Breadcrumb): Breadcrumb {
            return a
        }
    }

    object Incompatible : Breadcrumb() {
        override fun join(a: Breadcrumb): Breadcrumb {
            return this
        }
    }
}
