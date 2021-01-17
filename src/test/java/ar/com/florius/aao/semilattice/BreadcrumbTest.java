package ar.com.florius.aao.semilattice;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BreadcrumbTest {

    final private SemilatticeHelper<Breadcrumb> sl = new SemilatticeHelper<>(Breadcrumb::join, Breadcrumb.getMin());

    @Property
    void can_add_information_to_a_breadcrumb(
            @ForAll @Size(min = 1) List<@NotBottom @NotTop TagName> tags
    ) {
        Breadcrumb.Tagged headBreadcrumb = new Breadcrumb.Tagged(List.of(tags.get(0)));
        Breadcrumb.Tagged breadcrumb = new Breadcrumb.Tagged(tags);

        assertEquals(headBreadcrumb.join(breadcrumb), breadcrumb);
    }

    @Example
    void an_empty_string_is_interpreted_as_a_bottom() {
        Breadcrumb.Tagged intermediateBottom = new Breadcrumb.Tagged("foo::biz");
        Breadcrumb.Tagged fullyQualified = new Breadcrumb.Tagged("foo:bar:biz");

        assertEquals(intermediateBottom.join(fullyQualified),
                new Breadcrumb.Tagged("foo:bar:biz"));
    }

    @Group
    class Semilattice {

        @Property
        void associativity(
                @ForAll Breadcrumb x, @ForAll Breadcrumb y, @ForAll Breadcrumb z
        ) {
            sl.associativity(x, y, z);
        }

        @Property
        void commutativity(
                @ForAll Breadcrumb x, @ForAll Breadcrumb y
        ) {
            sl.commutativity(x, y);
        }

        @Property
        void idempotency(
                @ForAll Breadcrumb x
        ) {
            sl.idempotency(x);
        }

        @Property
        void identity(
                @ForAll Breadcrumb x
        ) {
            sl.identity(x);
        }

    }
}