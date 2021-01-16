package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.TagName;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class Tag {
    static final Logger logger = LoggerFactory.getLogger(Tag.class);

    public static <T> T tag(T o, String tag) {
        return tag(o, new TagName.Tagged(tag));
    }

    @SuppressWarnings("unchecked")
    public static <T> T tag(T o, TagName tag) {
        logger.trace("Tagging <{}> in {} with <{}>", o, o.getClass().getSimpleName(), tag);

        SafeTag<T> thisSafe = new SafeTag<>(o, tag);
        Optional<Taggable<T>> opTag = safeToTaggable(o);
        if (opTag.isPresent()) {
            Taggable<T> taggable = opTag.get();
            logger.trace("Already tagged with <{}>", taggable.getTag());

            TagName newTag = thisSafe.operate(List.of(taggable.getTag()));
            return tag(taggable.getValue(), newTag);
        } else {
            TagInterceptor<T> target = new TagInterceptor<>(thisSafe);
            Class<?> tagClass = new ByteBuddy()
                    .subclass(o.getClass())
                    .suffix("$tagged")
                    .defineField("original", o.getClass(), Visibility.PUBLIC)
                    .defineField("tag", TagName.class, Visibility.PUBLIC)
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
                tagClass.getDeclaredField("tag").set(newInstance, tag);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return newInstance;
        }
    }

    public static <T> T untag(T tagged) {
        return toTaggable(tagged).getValue();
    }

    public static <T> TagName getTag(T tagged) {
        return toTaggable(tagged).getTag();
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
