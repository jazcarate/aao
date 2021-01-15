package ar.com.florius.aao;

import ar.com.florius.aao.poset.Namespace;

public interface Taggable<T> {
    Namespace getNamespace();

    T getUnTag();
}
