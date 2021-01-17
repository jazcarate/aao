package ar.com.florius.aao

import ar.com.florius.aao.semilattice.TagName

class SafeTag<T>(override val value: T, override val tag: TagName) : Taggable<T> {
    fun operate(argsTag: List<TagName>): TagName {
        val newTag = argsTag.stream().reduce(this.tag) { obj: TagName, a: TagName -> obj.join(a) }
        if (isIncompatible(newTag)) {
            throw IncompatibleTagsException("Tags are incompatible between this (" + this.tag + ") and the arguments (" + argsTag + ")")
        }
        return newTag
    }

    private fun isIncompatible(newTag: TagName): Boolean {
        return newTag == TagName.Incompatible
    }

    companion object {
        @JvmStatic
        fun <T> untag(tagged: SafeTag<T>): T {
            return tagged.value
        }

        @JvmStatic
        fun <T> getTag(tagged: SafeTag<T>): TagName {
            return tagged.tag
        }
    }
}