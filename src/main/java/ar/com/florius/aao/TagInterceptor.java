package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.TagName;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.com.florius.aao.Tag.tag;

public class TagInterceptor<T> {
    final Logger logger = LoggerFactory.getLogger(Tag.class);
    private final SafeTag<T> safeTag;

    public TagInterceptor(SafeTag<T> safeTag) {
        this.safeTag = safeTag;
    }

    @RuntimeType
    @SuppressWarnings("unused")
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        switch (method.getName()) {
            case "getTag":
                return this.safeTag.getTag();
            case "getValue":
                return this.safeTag.getValue();
            default:
                return dispatch(method, args);
        }
    }

    private Object dispatch(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        List<TagName> argsTag = Arrays.stream(args)
                .map(Tag::safeToTaggable)
                .map(objectTaggable -> objectTaggable.map(Taggable::getTag).orElse(TagName.NoTag.INSTANCE))
                .collect(Collectors.toList());

        TagName newTag = this.safeTag.operate(argsTag);
        Object result = method.invoke(this.safeTag.getValue(), args);
        TypeDescription resultType = TypeDescription.ForLoadedType.of(result.getClass());

        if (resultType.isPrimitive() || resultType.isArray() || resultType.isFinal()) {
            logger.warn("Cannot tag primitive, array or final types ({}). Returning untagged ({})", resultType, result);
            return result;
        } else {
            return tag(result, newTag);
        }
    }

}
