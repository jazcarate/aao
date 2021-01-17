package ar.com.florius.aao.semilattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.Unique;

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

    @Group
    class Semilattice {

        @Property
        void associativity(@ForAll TagName x, @ForAll TagName y, @ForAll TagName z) {
            sl.associativity(x, y, z);
        }

        @Property
        void commutativity(@ForAll TagName x, @ForAll TagName y) {
            sl.commutativity(x, y);
        }

        @Property
        void idempotency(@ForAll TagName x) {
            sl.idempotency(x);
        }

        @Property
        void identity(@ForAll TagName x) {
            sl.identity(x);
        }

    }
}