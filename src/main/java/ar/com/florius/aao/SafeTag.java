package ar.com.florius.aao;

import static ar.com.florius.aao.Tag.GLOBAL_NAMESPACE;

public class SafeTag {
    public static <T> Taggable<T> tag(Taggable<T> o, String tag) {
        return o;
    }

    public static <T> Taggable<T> tag(T o, String tag) {
        return tag(o, GLOBAL_NAMESPACE, tag);
    }

    public static <T> Taggable<T> tag(T o, String namespace, String tag) {
        return new Tagged<>(o, namespace, tag);
    }

}
