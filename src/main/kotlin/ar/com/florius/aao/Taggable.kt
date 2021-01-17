package ar.com.florius.aao

import ar.com.florius.aao.semilattice.Semilattice
import ar.com.florius.aao.semilattice.TagName

interface Taggable<T, TAG : Semilattice<TAG>> {
    val tag: TAG
    val value: T
}