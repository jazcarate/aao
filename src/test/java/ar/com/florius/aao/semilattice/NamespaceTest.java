package ar.com.florius.aao.semilattice;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamespaceTest {

    final private SemilatticeHelper<Namespace> sl = new SemilatticeHelper<>(Namespace::join, Namespace.getMin());

    @Example
    void can_parse_a_complete_string_format() {
        Namespace namespace = Namespace.of("biz->foo:bar,buz->foo");

        assertEquals(new Namespace.Tagged(Map.of("biz",
                new Breadcrumb.Tagged(List.of(
                        new TagName.Tagged("foo"), new TagName.Tagged("bar")
                )),
                "buz", new Breadcrumb.Tagged(List.of(
                        new TagName.Tagged("foo")
                ))
        )), namespace);
    }

    @Example
    void defaults_to_a_namespace_when_none_is_provided() {
        Namespace namespace = Namespace.of("foo:bar");

        assertEquals(new Namespace.Tagged(Map.of("DEFAULT",
                new Breadcrumb.Tagged(List.of(
                        new TagName.Tagged("foo"), new TagName.Tagged("bar")
                ))
        )), namespace);
    }

    @Example
    void can_get_incompatible_tag_if_wrongly_given() {
        Namespace namespace = Namespace.of("foo:bar,biz");

        assertEquals(Namespace.Incompatible.INSTANCE, namespace);
    }

    @Group
    class Semilattice {

        @Property
        void associativity(
                @ForAll Namespace x, @ForAll Namespace y, @ForAll Namespace z
        ) {
            sl.associativity(x, y, z);
        }

        @Property
        void commutativity(
                @ForAll Namespace x, @ForAll Namespace y
        ) {
            sl.commutativity(x, y);
        }

        @Property
        void idempotency(
                @ForAll Namespace x
        ) {
            sl.idempotency(x);
        }

        @Property
        void identity(
                @ForAll Namespace x
        ) {
            sl.identity(x);
        }

    }
}