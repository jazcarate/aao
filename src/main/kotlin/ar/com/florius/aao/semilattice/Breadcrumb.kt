package ar.com.florius.aao.semilattice

import kotlin.math.max


sealed class Breadcrumb {
    abstract fun join(a: Breadcrumb): Breadcrumb

    data class Tagged(private val names: List<TagName>) : Breadcrumb() {

        constructor(name: String) : this(name.split(":").map {
            if (it.isEmpty()) TagName.NoTag else TagName.Tagged(it)
        })

        override fun join(a: Breadcrumb): Breadcrumb {
            return when (a) {
                is Tagged -> joinGo(names, a.names)
                else -> a.join(this)
            }
        }

        private fun joinGo(x: List<TagName>, y: List<TagName>): Breadcrumb {
            val max = max(x.size, y.size)
            val filledX = fill(x, max, TagName.NoTag)
            val filledY = fill(y, max, TagName.NoTag)

            val newTagNames = filledX.zip(filledY) { a, b -> a.join(b) }
            if (newTagNames.any { it is TagName.Incompatible }) return Incompatible
            return Tagged(newTagNames)
        }
    }

    object Incompatible : Breadcrumb() {
        override fun join(a: Breadcrumb): Breadcrumb {
            return this
        }
    }

    companion object {
        @JvmStatic
        val min: Breadcrumb = Tagged(emptyList())
    }
}
