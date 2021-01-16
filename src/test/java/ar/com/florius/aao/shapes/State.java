package ar.com.florius.aao.shapes;

import java.util.Objects;

public class State<T> {

    private final T x;

    public State(T x) {
        this.x = x;
    }

    public T get() {
        return this.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State<?> state = (State<?>) o;
        return Objects.equals(get(), state.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x);
    }
}
