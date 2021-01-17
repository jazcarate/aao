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

object Tag {
    private val logger = LoggerFactory.getLogger(Tag::class.java)

    @JvmStatic
    fun <T : Any> tag(o: T, tag: String): T {
        return tag(o, TagName.Tagged(tag))
    }

    @JvmStatic
    fun <T : Any> tag(o: T, tag: TagName): T {
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
                .suffix("\$tagged")
                .defineField("original", o.javaClass, Visibility.PUBLIC)
                .defineField("tag", TagName::class.java, Visibility.PUBLIC)
                .implement(Taggable::class.java)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.withDefaultConfiguration().to(target))
                .make()
                .load(this.javaClass.classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .loaded
            val objenesis: Objenesis = ObjenesisStd()
            val newInstance = objenesis.getInstantiatorOf(tagClass).newInstance()
            try {
                tagClass.getDeclaredField("original")[newInstance] = o
                tagClass.getDeclaredField("tag")[newInstance] = tag
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            newInstance
        }
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