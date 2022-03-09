package ar.com.florius.aao

import ar.com.florius.aao.Tag.tag
import ar.com.florius.aao.semilattice.Namespace
import ar.com.florius.aao.strict.Tag.safeToTaggable
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
        val argsTag: List<Taggable<Any?, Namespace>> = args
            .map { obj -> safeToTaggable<Any?, Namespace>(obj).orElse(SafeTag(obj, Namespace.min)) }

        val result = safeTag.apply(method, argsTag)
        if (result.tag == Namespace.Incompatible) {
            throw IncompatibleTagsException(result, argsTag)
        }

        val resultType = result.value?.let { TypeDescription.ForLoadedType.of(it.javaClass) }

        if (resultType == null || resultType.isPrimitive || resultType.isArray || resultType.isFinal) {
            logger.warn(
                "Cannot tag null, primitive, array or final types ({}). Returning untagged ({})",
                resultType,
                result
            )
            return result.value
        }
        return tag(result.value, result.tag)
    }
}