package ar.com.florius.aao;

import java.util.Map;

public interface Taggable<T> {
    String getTag();

    String getTag(String namespace);

    Map<String, String> getNamespace();

    T getUnTag();
}
