package ar.com.florius.aao

import ar.com.florius.aao.semilattice.TagName

interface Taggable<T> {
    val tag: TagName
    val value: T
}