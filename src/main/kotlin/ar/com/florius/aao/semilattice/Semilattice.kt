package ar.com.florius.aao.semilattice

interface Semilattice<SELF : Semilattice<SELF>> {
    fun join(a: SELF): SELF

    @Suppress("UNCHECKED_CAST")
    fun join(aa: List<SELF>): SELF = aa.stream().reduce(this as SELF) { acc: SELF, a: SELF -> acc.join(a) }
}