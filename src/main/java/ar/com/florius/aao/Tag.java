package ar.com.florius.aao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tag {
    @SuppressWarnings("unchecked")
    public static <T> T tag(T o, String tag) {
        TagInterceptor<T> target = new TagInterceptor<>(o, tag);
        Class<?> tagClass = new ByteBuddy()
                .subclass(o.getClass())
                .name(o.getClass().getName() + "$tagged$" + tag)
                .implement(Taggable.class)
                .method(ElementMatchers.any().and(
                        ElementMatchers.not(ElementMatchers.named("getTag")))
                        .or(ElementMatchers.not(ElementMatchers.named("getUnTag")))
                )
                .intercept(MethodDelegation.withDefaultConfiguration().to(target))
                .make()
                .load(o.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Objenesis objenesis = new ObjenesisStd();

        return (T) objenesis.getInstantiatorOf(tagClass).newInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T> T untag(T tagged) {
        try {
            return ((Taggable<T>) tagged).getUnTag();
        } catch (ClassCastException e) {
            throw new RuntimeException(tagged + " was not tagged", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> String getTag(T tagged) {
        try {
            return ((Taggable<T>) tagged).getTag();
        } catch (ClassCastException e) {
            throw new RuntimeException(tagged + " was not tagged", e);
        }
    }
}
