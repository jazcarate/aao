package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.TagName;

public interface Taggable<T> {
    TagName getTag();

    T getValue();
}
