package ar.com.florius.aao

data class Tagged<T>(
        private val unTag: T,
        private val namespace: Map<String, String>
) : Taggable<T> {
    constructor(unTag: T, namespace: String, tag: String) : this(unTag, mapOf(namespace to tag))

    override fun getTag(): String? {
        return this.namespace[Tag.GLOBAL_NAMESPACE]
    }

    override fun getTag(namespace: String): String? {
        return this.namespace[namespace]
    }

    override fun getNamespace(): MutableMap<String, String> {
        return this.namespace.toMutableMap()
    }

    override fun getUnTag(): T {
        return this.unTag
    }
}