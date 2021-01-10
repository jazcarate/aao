package ar.com.florius.aao.poset;

/**
 * Operation over T where a total order exists.
 *
 * @param <T> closed set over which the operations will apply
 */
public interface Op<T> {
    T common(T a);
}
