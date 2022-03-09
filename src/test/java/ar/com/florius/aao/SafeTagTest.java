package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.Namespace;
import org.junit.jupiter.api.Test;

import static ar.com.florius.aao.SafeTag.getTag;
import static ar.com.florius.aao.SafeTag.untag;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeTagTest {

    @Test
    void tagging_get_tags() {
        Namespace tag = Namespace.of("foo");
        SafeTag<Integer> foo = new SafeTag<>(3, tag);

        assertEquals(getTag(foo), tag);
    }

    @Test
    void tagging_get_value() {
        Integer value = 3;
        SafeTag<Integer> foo = new SafeTag<>(value, Namespace.of("foo"));

        assertEquals(untag(foo), value);
    }

    @Test
    void operate_with_another_tag() {
        SafeTag<Integer> foo = new SafeTag<>(1, Namespace.of("foo"));
        SafeTag<Integer> bar = new SafeTag<>(3, Namespace.of("bar"));
        SafeTag<Integer> result = foo.apply(Integer::sum, bar);


        assertEquals(untag(result), 3 + 1);
        assertEquals(getTag(result), Namespace.Incompatible.INSTANCE);
    }

    @Test
    void operate_with_another_safely_tagged_value() {
        SafeTag<Integer> foo = new SafeTag<>(1, Namespace.of("foo"));
        SafeTag<Integer> foobar = new SafeTag<>(3, Namespace.of("foo:bar"));
        SafeTag<Integer> result = foo.apply(Integer::sum, foobar);


        assertEquals(untag(result), 3 + 1);
        assertEquals(getTag(result), Namespace.of("foo:bar"));
    }


    @Test
    void operate_with_another_two_tags() {
        SafeTag<Integer> foo = new SafeTag<>(1, Namespace.of("foo"));
        SafeTag<Integer> bar = new SafeTag<>(3, Namespace.of("foo"));
        SafeTag<Integer> biz = new SafeTag<>(6, Namespace.of("foo"));
        SafeTag<Integer> result = foo.apply((a, b, c) -> a + b + c, bar, biz);


        assertEquals(untag(result), 1 + 3 + 6);
        assertEquals(getTag(result), Namespace.of("foo"));
    }

}
