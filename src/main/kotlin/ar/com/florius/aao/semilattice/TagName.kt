package ar.com.florius.aao.semilattice


sealed class TagName : Semilattice<TagName> {

    data class Tagged(private val name: String) : TagName() {
        override fun join(a: TagName): TagName {
            return when (a) {
                is Tagged -> if (a.name == this.name) a else Incompatible
                else -> a.join(this)
            }
        }

        override fun toString(): String = name
    }

    object Incompatible : TagName() {
        override fun join(a: TagName): TagName = this

        override fun toString(): String = "⊤"
    }

    object NoTag : TagName() {
        override fun join(a: TagName): TagName = a

        override fun toString(): String = "⊥"
    }

}