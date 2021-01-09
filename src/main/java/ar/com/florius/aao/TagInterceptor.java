package ar.com.florius.aao;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ar.com.florius.aao.Tag.tag;

public class TagInterceptor<T> {
    final Logger log = LoggerFactory.getLogger(TagInterceptor.class);
    private final T original;
    private final String tagName;

    public TagInterceptor(T original, String tag) {
        this.original = original;
        this.tagName = tag;
    }

    @RuntimeType
    @SuppressWarnings("unused")
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        switch (method.getName()) {
            case "getTag":
                return this.tagName;
            case "getUnTag":
                return this.original;
            default:
                return methodMissing(method, args);
        }
    }

    private Object methodMissing(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(this.original, args);
        TypeDescription resultType = TypeDescription.ForLoadedType.of(result.getClass());

        if (resultType.isPrimitive() || resultType.isArray() || resultType.isFinal()) {
            log.warn("Cannot tag primitive, array or final types ({}). Returning untagged ({})", resultType, result);
            return result;
        } else {
            return tag(result, this.tagName);
        }
    }
}
