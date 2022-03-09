package ar.com.florius.aao

import ar.com.florius.aao.semilattice.Namespace

class IncompatibleTagsException(result: SafeTag<Any?>, argsTag: List<Taggable<Any?, Namespace>>) :
    IllegalArgumentException("Tags are incompatible between this «${result.tag}» and the arguments: ${
        argsTag.withIndex().joinToString(", ") { "${it.index + 1}: «${it}»" }
    }")