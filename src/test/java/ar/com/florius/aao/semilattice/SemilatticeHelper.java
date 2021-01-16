package ar.com.florius.aao.semilattice;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Algebra: https://en.wikipedia.org/wiki/Semilattice#Algebraic_definition
 */
public class SemilatticeHelper<T> {

    final private BiFunction<T, T, T> join;
    final private T identityElement;

    SemilatticeHelper(BiFunction<T, T, T> join, T identityElement) {
        this.join = join;
        this.identityElement = identityElement;
    }

    // x ∨ (y ∨ z) = (x ∨ y) ∨ z
    public void associativity(T x, T y, T z) {
        assertEquals(
                join.apply(x, join.apply(y, z)),
                join.apply(join.apply(x, y), z),
                "the operation is not associative");
    }

    // x ∨ y = y ∨ x
    public void commutativity(T x, T y) {
        assertEquals(
                join.apply(x, y),
                join.apply(y, x),
                "the operation is not commutative");
    }

    // x ∨ x = x
    public void idempotency(T x) {
        assertEquals(
                join.apply(x, x),
                x,
                "the operation is not idempotent");
    }

    // x ∨ 1 = x
    public void identity(T x) {
        assertEquals(
                join.apply(x, this.identityElement),
                x,
                x.toString() + " is not the idendity element");
    }
}