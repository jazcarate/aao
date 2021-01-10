package ar.com.florius.aao;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static ar.com.florius.aao.Tag.GLOBAL_NAMESPACE;
import static ar.com.florius.aao.Tag.tag;

public class TagInterceptor<T> {
    final Logger logger = LoggerFactory.getLogger(Tag.class);
    private final T original;
    private final Map<String, String> namespace;


    public TagInterceptor(T original, String namespace, String tag) {
        this.original = original;
        this.namespace = Map.of(namespace, tag);
    }

    public TagInterceptor(T original, String tag) {
        this(original, GLOBAL_NAMESPACE, tag);
    }

    @RuntimeType
    @SuppressWarnings("unused")
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        switch (method.getName()) {
            case "getTag":
                if (args.length == 0) {
                    return this.namespace.get(GLOBAL_NAMESPACE);
                } else {
                    return this.namespace.get((String) args[0]);
                }
            case "getUnTag":
                return this.original;
            case "getNamespace":
                return this.namespace;
            default:
                return dispatch(method, args);
        }
    }

    private Object dispatch(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        checkArgsTags(args);

        Object result = method.invoke(this.original, args);
        TypeDescription resultType = TypeDescription.ForLoadedType.of(result.getClass());

        if (resultType.isPrimitive() || resultType.isArray() || resultType.isFinal()) {
            logger.warn("Cannot tag primitive, array or final types ({}). Returning untagged ({})", resultType, result);
            return result;
        } else {
            return tag(result, "TODO");
        }
    }

    private void checkArgsTags(Object[] args) throws IncompatibleTagsException {
        /*Arrays.stream(args)
                .map(Tag::safeToTaggable)
                .filter(Optional::isPresent)
                .filter()
        culprit.ifPresent(o -> {
            throw new IncompatibleTagsException();
        });*/
    }

    private boolean isCompatible(Object o) {
        return false;
    }
}
