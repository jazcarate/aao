package ar.com.florius.aao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagTest {

    @Test
    void tagTest() {
        int x = 3;
        Foo foo = new Foo(x);

        Foo taggedFoo = tag(foo, "foo");
        assertEquals(((Taggable) taggedFoo).getTag(), "foo");

        assertEquals(taggedFoo.bar(), x);
    }

    @SuppressWarnings("unchecked")
    private <T> T tag(T o, String tag) {
        TagInterceptor<T> target = new TagInterceptor<>(o, tag);
        Class<?> tagClass = new ByteBuddy()
                .subclass(o.getClass())
                .implement(Taggable.class)
                .method(ElementMatchers.named("getTag")).intercept(FixedValue.value(tag))
                .method(ElementMatchers.any().and(ElementMatchers.not(ElementMatchers.named("getTag"))))
                .intercept(MethodDelegation.withDefaultConfiguration().to(target))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        Objenesis objenesis = new ObjenesisStd();

        return (T) objenesis.getInstantiatorOf(tagClass).newInstance();
    }
}
