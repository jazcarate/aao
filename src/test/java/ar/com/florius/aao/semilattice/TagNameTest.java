package ar.com.florius.aao.semilattice;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.Unique;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagNameTest {

    final private SemilatticeHelper<TagName> sl = new SemilatticeHelper<>(TagName::join, TagName.NoTag.INSTANCE);

    @Property
    void different_tags_are_incompatible(
            @ForAll @Size(2) List<@Unique String> tags
    ) {
        TagName.Tagged tag1 = new TagName.Tagged(tags.get(0));
        TagName.Tagged tag2 = new TagName.Tagged(tags.get(1));

        assertEquals(tag1.join(tag2), TagName.Incompatible.INSTANCE);
    }

    @Property
    void same_named_tags_are_compatible(@ForAll String tag) {
        TagName.Tagged tag1 = new TagName.Tagged(tag);
        TagName.Tagged tag2 = new TagName.Tagged(tag);

        assertEquals(tag1.join(tag2), new TagName.Tagged(tag));
    }

    @Provide
    public Arbitrary<TagName> tag() {
        return Arbitraries.oneOf(Arrays.asList(
                Arbitraries.strings().map(TagName.Tagged::new),
                Arbitraries.create(() -> TagName.NoTag.INSTANCE),
                Arbitraries.create(() -> TagName.Incompatible.INSTANCE)
        ));
    }

    @Group
    class Semilattice {

        @Property
        void associativity(
                @ForAll("tag") TagName tag1, @ForAll("tag") TagName tag2, @ForAll("tag") TagName tag3
        ) {
            sl.associativity(tag1, tag2, tag3);
        }

        @Property
        void commutativity(
                @ForAll("tag") TagName tag1, @ForAll("tag") TagName tag2
        ) {
            sl.commutativity(tag1, tag2);
        }

        @Property
        void idempotency(
                @ForAll("tag") TagName tag1
        ) {
            sl.idempotency(tag1);
        }

        @Property
        void identity(
                @ForAll("tag") TagName tag1
        ) {
            sl.identity(tag1);
        }

    }
}