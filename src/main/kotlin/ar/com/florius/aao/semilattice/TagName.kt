package ar.com.florius.aao.semilattice


sealed class TagName : Semilattice<TagName> {

    data class Tagged(private val name: String) : TagName() {
        override fun join(a: TagName): TagName {
            return when (a) {
                is Tagged -> if (a.name == this.name) a else Incompatible
                else -> a.join(this)
            }
        }
    }

    object Incompatible : TagName() {
        override fun join(a: TagName): TagName {
            return this
        }
    }

    object NoTag : TagName() {
        override fun join(a: TagName): TagName {
            return a
        }
    }

}