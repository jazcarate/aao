package ar.com.florius.aao

import ar.com.florius.aao.semilattice.Namespace
import org.slf4j.LoggerFactory
import ar.com.florius.aao.strict.Tag.tag as strictTag

object Tag {
    private val logger = LoggerFactory.getLogger(Tag::class.java)

    @JvmStatic
    fun <T : Any> tag(o: T, tag: String): T {
        return tag(o, Namespace.of(tag))
    }

    @JvmStatic
    fun <T : Any> tag(o: T, tag: Namespace): T {
        if (!areAssertEnabled()) {
            return o
        }

        return strictTag(o, tag)
    }

    private fun areAssertEnabled(): Boolean {
        var assertEnabled = false
        try {
            assert(false)
        } catch (e: AssertionError) {
            assertEnabled = true
        }
        return assertEnabled
    }
}