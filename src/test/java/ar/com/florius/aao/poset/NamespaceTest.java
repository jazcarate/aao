package ar.com.florius.aao.poset;

import ar.com.florius.aao.IncompatibleTagsException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NamespaceTest {
    @Test
    void reflexive() {
        Namespace foo = new Namespace("foo", new TagName("tag"));

        assertMappend(foo, foo, foo);
    }

    @Test
    void combine_distinct_namespaces_with_the_same_tags() {
        TagName tag = new TagName("tag");
        Namespace foo = new Namespace("foo", tag);
        Namespace bar = new Namespace("bar", tag);

        assertMappend(foo, bar, new Namespace(Map.of("foo", tag, "bar", tag)));
    }

    @Test
    void combine_same_namespaces_with_combinable_tags() {
        String aNamespace = "foo";
        Namespace foo = new Namespace(aNamespace, new TagName("foo"));
        Namespace fooBar = new Namespace(aNamespace, new TagName("foo:bar"));

        assertMappend(foo, fooBar, fooBar);
    }

    @Test
    void combine_same_namespaces_with_non_combinable_tags() {
        String aNamespace = "foo";
        Namespace foo = new Namespace(aNamespace, new TagName("foo"));
        Namespace bar = new Namespace(aNamespace, new TagName("bar"));

        assertIncompatible(foo, bar);
    }

    private void assertCommutative(Namespace a, Namespace b, BiConsumer<Namespace, Namespace> assertion) {
        assertion.accept(a, b);
        assertion.accept(b, a);
    }

    private void assertMappend(Namespace a, Namespace b, Namespace result) {
        assertCommutative(a, b, (x, y) ->
                assertEquals(x.mappend(y), result)
        );
    }

    private void assertIncompatible(Namespace a, Namespace b) {
        assertCommutative(a, b, (x, y) ->
                assertThrows(IncompatibleTagsException.class, () -> x.mappend(y))
        );
    }
}