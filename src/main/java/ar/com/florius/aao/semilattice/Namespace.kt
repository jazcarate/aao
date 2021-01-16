package ar.com.florius.aao.semilattice

data class Namespace(private val namespace: Map<String, TagName>) {

    constructor(name: String, tag: TagName) : this(mapOf(name to tag))

    fun join(a: Namespace): Namespace {
        return Namespace(merge(namespace, a.namespace))
    }

    private fun merge(a: Map<String, TagName>, b: Map<String, TagName>): Map<String, TagName> {
        return (a.asSequence() + b.asSequence())
                .groupBy({ it.key }, { it.value })
                .mapValues { (_, values) ->
                    values.foldRight(values.first(), { a, b -> a.join(b) })
                }
    }
}

