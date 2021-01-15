package ar.com.florius.aao

import ar.com.florius.aao.poset.Namespace
import ar.com.florius.aao.poset.TagName

data class Tagged<T>(
    private val unTag: T,
    private val namespace: Namespace
) : Taggable<T> {
    constructor(unTag: T, namespace: String, tag: String) : this(unTag, Namespace(namespace, TagName(tag)))

    override fun getNamespace(): Namespace {
        return this.namespace
    }

    override fun getUnTag(): T {
        return this.unTag
    }

    fun foo(x: Taggable<*>): Namespace {
        return this.namespace.mappend(x.namespace)
    }
}