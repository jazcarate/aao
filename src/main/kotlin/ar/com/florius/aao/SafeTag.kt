package ar.com.florius.aao

import ar.com.florius.aao.semilattice.Namespace

class SafeTag<T>(override val value: T, override val tag: Namespace) : Taggable<T, Namespace> {
    fun operate(argsTag: List<Namespace>): Namespace {
        val newTag = argsTag.stream().reduce(this.tag) { obj: Namespace, a: Namespace -> obj.join(a) }
        if (isIncompatible(newTag)) {
            throw IncompatibleTagsException("Tags are incompatible between this «${this.tag}» and the arguments: ${
                argsTag.withIndex().joinToString(", ") { "${it.index}: «${it.value}»" }
            })")
        }
        return newTag
    }

    private fun isIncompatible(newTag: Namespace): Boolean {
        return newTag == Namespace.Incompatible
    }

    companion object {
        @JvmStatic
        fun <T> untag(tagged: SafeTag<T>): T {
            return tagged.value
        }

        @JvmStatic
        fun <T> getTag(tagged: SafeTag<T>): Namespace {
            return tagged.tag
        }
    }
}