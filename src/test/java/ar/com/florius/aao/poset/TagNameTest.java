package ar.com.florius.aao.poset;

import ar.com.florius.aao.IncompatibleTagsException;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagNameTest {
    @Test
    void complete_different_tags_do_not_have_nothing_in_common() {
        TagName foo = new TagName("foo");
        TagName bar = new TagName("bar");

        assertIncompatible(foo, bar);
    }

    @Test
    void the_same_name_is_the_same_thing_in_common() {
        TagName foo1 = new TagName("foo");
        TagName foo2 = new TagName("foo");

        assertMappend(foo1, foo2, foo1);
        assertMappend(foo1, foo2, foo2);
    }

    @Test
    void keeps_the_more_specific() {
        TagName foo = new TagName("foo");
        TagName fooBar = new TagName("foo:bar");

        assertMappend(foo, fooBar, fooBar);
    }

    @Test
    void different_sub_tags_do_not_have_nothing_in_common_then_nothing_does() {
        TagName foo = new TagName("foo:biz");
        TagName fooBar = new TagName("foo:bar");

        assertIncompatible(foo, fooBar);
    }

    @Test
    void only_commonality_with_colons() {
        TagName biz = new TagName("biz");
        TagName bar = new TagName("bar");


        assertIncompatible(biz, bar);
    }

    private void assertCommutative(TagName a, TagName b, BiConsumer<TagName, TagName> assertion) {
        assertion.accept(a, b);
        assertion.accept(b, a);
    }

    private void assertMappend(TagName a, TagName b, TagName result) {
        assertCommutative(a, b, (x, y) ->
                assertEquals(x.mappend(y), result)
        );
    }

    private void assertIncompatible(TagName a, TagName b) {
        assertCommutative(a, b, (x, y) ->
                assertThrows(IncompatibleTagsException.class, () -> x.mappend(y))
        );
    }
}