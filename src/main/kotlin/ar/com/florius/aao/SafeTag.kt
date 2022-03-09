package ar.com.florius.aao

import ar.com.florius.aao.semilattice.Namespace
import java.lang.reflect.Method

class SafeTag<T>(override val value: T, override val tag: Namespace) : Taggable<T, Namespace> {
    fun <A, RETURN> apply(f: (t: T, op1: A) -> RETURN, op1: SafeTag<A>): SafeTag<RETURN> {
        val newTag = tag.join(op1.tag)
        return SafeTag(f(this.value, op1.value), newTag)
    }

    fun <A, B, RETURN> apply(f: (t: T, op1: A, op2: B) -> RETURN, op1: SafeTag<A>, op2: SafeTag<B>): SafeTag<RETURN> {
        val newTag = tag.join(listOf(op1.tag, op2.tag))
        return SafeTag(f(this.value, op1.value, op2.value), newTag)
    }

    fun apply(method: Method, args: List<Taggable<Any?, Namespace>>): SafeTag<Any?> {
        val newTag = tag.join(args.map { it.tag })
        val result = method.invoke(this.value, *args.map { it.value }.toTypedArray())
        return SafeTag(result, newTag)
    }

    override fun toString(): String {
        return "$value«$tag»"
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