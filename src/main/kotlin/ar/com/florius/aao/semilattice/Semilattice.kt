package ar.com.florius.aao.semilattice

interface Semilattice<SELF : Semilattice<SELF>> {
    fun join(a: SELF): SELF
}