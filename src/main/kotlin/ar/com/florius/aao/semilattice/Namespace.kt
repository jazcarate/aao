package ar.com.florius.aao.semilattice

sealed class Namespace : Semilattice<Namespace> {
    data class Tagged(private val map: Map<String, Breadcrumb>) : Namespace() {
        constructor(name: String, breadcrumb: Breadcrumb) : this(mapOf(name to breadcrumb))

        override fun join(a: Namespace): Namespace {
            return when (a) {
                is Tagged -> joinGo(map, a.map)
                else -> a.join(this)
            }
        }


        private fun joinGo(x: Map<String, Breadcrumb>, y: Map<String, Breadcrumb>): Namespace {
            val candidate = merge(x, y)
            if (candidate.any { it.value is Breadcrumb.Incompatible }) return Incompatible
            return Tagged(candidate)
        }

        private fun merge(a: Map<String, Breadcrumb>, b: Map<String, Breadcrumb>): Map<String, Breadcrumb> {
            return (a.asSequence() + b.asSequence())
                .groupBy({ it.key }, { it.value })
                .mapValues { (_, values) ->
                    values.foldRight(values.first(), { a, b -> a.join(b) })
                }
        }

        override fun toString(): String {
            return map.entries.joinToString(", ") { "${it.key}->${it.value}" }
        }
    }

    object Incompatible : Namespace() {
        override fun join(a: Namespace): Namespace {
            return this
        }
    }

    companion object {
        @JvmStatic
        val min: Namespace = Tagged(emptyMap())


        @JvmStatic
        fun of(map: String): Namespace {
            return map.split(",")
                .map(String::trim)
                .map {
                    val split = it.split("->", limit = 2)
                    if (split.isEmpty()) return@map Tagged(emptyMap())
                    if (split.size == 1) return@map Tagged("DEFAULT", Breadcrumb.Tagged(split[0]))
                    val (name, key) = split
                    Tagged(name, Breadcrumb.Tagged(key))
                }.reduce { acc: Namespace, tagged: Namespace -> acc.join(tagged) }
        }
    }
}

