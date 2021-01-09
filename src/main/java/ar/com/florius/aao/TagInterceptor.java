package ar.com.florius.aao;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.Method;

public class TagInterceptor<T> {
    private final T unTag;
    private final String tag;

    public TagInterceptor(T unTag, String tag) {
        this.unTag = unTag;
        this.tag = tag;
    }

    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        return method.invoke(this.unTag, args);
    }
}
