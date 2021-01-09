package ar.com.florius.aao.shapes;

public class State<T> {

    private final T x;

    public State(T x) {
        this.x = x;
    }

    public T get() {
        return this.x;
    }

}
