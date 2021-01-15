package ar.com.florius.aao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Tag {
    public static final String GLOBAL_NAMESPACE = "*";
    static final Logger logger = LoggerFactory.getLogger(Tag.class);

    public static <T> T tag(T o, String tag) {
        return tag(o, GLOBAL_NAMESPACE, tag);
    }

    @SuppressWarnings("unchecked")
    public static <T> T tag(T o, String namespace, String tag) {
        logger.trace("Tagging <{}> ({}) in {} with <{}>", o, o.getClass().getSimpleName(), namespace, tag);

        Optional<Taggable<T>> opTag = safeToTaggable(o);
        if (opTag.isPresent()) {
            Taggable<T> taggable = opTag.get();
            logger.trace("Already tagged with <{}>", taggable.getNamespace());

            // taggable.getNamespace().merge(namespace, tag, (s, s2) -> s); //TODO ^

            return (T) taggable;
        } else {
            TagInterceptor<T> target = new TagInterceptor<>(o, tag);
            Class<?> tagClass = new ByteBuddy()
                    .subclass(o.getClass())
                    .suffix("$tagged$" + tag)
                    .defineField("original", o.getClass(), Visibility.PUBLIC)
                    .implement(Taggable.class)
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.withDefaultConfiguration().to(target))
                    .make()
                    .load(o.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();
            Objenesis objenesis = new ObjenesisStd();

            T newInstance = (T) objenesis.getInstantiatorOf(tagClass).newInstance();
            try {
                tagClass.getDeclaredField("original").set(newInstance, o);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return newInstance;
        }
    }

    public static <T> T untag(T tagged) {
        return toTaggable(tagged).getUnTag();
    }

    public static <T> String getTag(T tagged) {
        return "foo";
    }

    private static <T> Taggable<T> toTaggable(T tagged) {
        return safeToTaggable(tagged).orElseThrow(() -> new RuntimeException(tagged + " was not tagged"));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Taggable<T>> safeToTaggable(T tagged) {
        try {
            return Optional.ofNullable(((Taggable<T>) tagged));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

}
