package ar.com.florius.aao

import ar.com.florius.aao.semilattice.TagName
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import org.objenesis.Objenesis
import org.objenesis.ObjenesisStd
import org.slf4j.LoggerFactory
import java.util.*

private const val FIELD_ORIGINAL = "aao_original"
private const val FIELD_TAG = "aao_tag"

object Tag {
    private val logger = LoggerFactory.getLogger(Tag::class.java)

    @JvmStatic
    fun <T : Any> tag(o: T, tag: String): T {
        return tag(o, TagName.Tagged(tag))
    }

    @JvmStatic
    fun <T : Any> tag(o: T, tag: TagName): T {
        if (!o.javaClass.desiredAssertionStatus()) {
            return o
        }

        logger.trace("Tagging <{}> in {} with <{}>", o, o.javaClass.simpleName, tag)
        val thisSafe = SafeTag(o, tag)
        val opTag = safeToTaggable(o)
        return if (opTag.isPresent) {
            val taggable = opTag.get()
            logger.trace("Already tagged with <{}>", taggable.tag)
            val newTag = thisSafe.operate(listOf(taggable.tag))
            tag(taggable.value, newTag)
        } else {
            val target = TagInterceptor(thisSafe)
            val tagClass: Class<out T> = ByteBuddy()
                .subclass(o.javaClass)
                .suffix("tagged")
                .defineField(FIELD_ORIGINAL, o.javaClass, Visibility.PUBLIC)
                .defineField(FIELD_TAG, TagName::class.java, Visibility.PUBLIC)
                .implement(Taggable::class.java)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.withDefaultConfiguration().to(target))
                .make()
                .load(this.javaClass.classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .loaded
            val objenesis: Objenesis = ObjenesisStd()
            val newInstance = objenesis.getInstantiatorOf(tagClass).newInstance()
            injectFields(tagClass, newInstance, o, tag)
            newInstance
        }
    }

    private fun <T : Any> injectFields(tagClass: Class<out T>, newInstance: T, original: T, tag: TagName) {
        tagClass.getDeclaredField(FIELD_ORIGINAL)[newInstance] = original
        tagClass.getDeclaredField(FIELD_TAG)[newInstance] = tag
    }

    @JvmStatic
    fun <T> untag(tagged: T): T {
        return toTaggable(tagged).value
    }

    @JvmStatic
    fun <T> getTag(tagged: T): TagName {
        return toTaggable(tagged).tag
    }

    private fun <T> toTaggable(tagged: T): Taggable<T> {
        return safeToTaggable(tagged).orElseThrow { RuntimeException(tagged.toString() + " was not tagged") }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> safeToTaggable(tagged: T): Optional<Taggable<T>> {
        return if (tagged is Taggable<*>) {
            Optional.ofNullable(tagged as Taggable<T>)
        } else {
            Optional.empty()
        }
    }
}