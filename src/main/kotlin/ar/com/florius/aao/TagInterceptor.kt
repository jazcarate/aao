package ar.com.florius.aao

import ar.com.florius.aao.Tag.tag
import ar.com.florius.aao.semilattice.TagName
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class TagInterceptor<T>(private val safeTag: SafeTag<T>) {
    private val logger = LoggerFactory.getLogger(TagInterceptor::class.java)

    @RuntimeType
    @Suppress("UNUSED")
    fun intercept(@Origin method: Method, @AllArguments args: Array<Any?>): Any? {
        return when (method.name) {
            "getTag" -> safeTag.tag
            "getValue" -> safeTag.value
            else -> dispatch(method, args)
        }
    }

    @Throws(IllegalAccessException::class, InvocationTargetException::class)
    private fun dispatch(method: Method, args: Array<Any?>): Any? {
        val argsTag: List<TagName> = args
            .map { obj -> Tag.safeToTaggable(obj) }
            .map { objectTaggable -> objectTaggable.map { it.tag }.orElse(TagName.NoTag) }
        val newTag = safeTag.operate(argsTag)

        val result = method.invoke(safeTag.value, *unwrap(args))
        val resultType = TypeDescription.ForLoadedType.of(result.javaClass)

        return if (resultType.isPrimitive || resultType.isArray || resultType.isFinal) {
            logger.warn("Cannot tag primitive, array or final types ({}). Returning untagged ({})", resultType, result)
            result
        } else {
            tag(result, newTag)
        }
    }

    private fun unwrap(args: Array<Any?>): Array<Any?> {
        return args.map { if (it is Taggable<*>) it.value else it }.toTypedArray()
    }
}