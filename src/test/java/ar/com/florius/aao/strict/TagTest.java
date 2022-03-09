package ar.com.florius.aao.strict;

import ar.com.florius.aao.IncompatibleTagsException;
import ar.com.florius.aao.semilattice.Namespace;
import ar.com.florius.aao.shapes.State;
import ar.com.florius.aao.shapes.Unary;
import org.junit.jupiter.api.Test;

import static ar.com.florius.aao.strict.Tag.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagTest {

    @Test
    void tagging_get_tags() {
        String aTag = "foo";

        State<Integer> taggedState = tag(new State<>(3), aTag);

        assertEquals(getTag(taggedState), Namespace.of(aTag));
    }

    @Test
    void tagging_can_unbox() {
        State<Integer> original = new State<>(3);
        State<Integer> taggedState = tag(original, "foo");

        assertEquals(untag(taggedState), original);
    }


    @Test
    void tag_is_equal_to_untagged() {
        State<Integer> original = new State<>(3);
        State<Integer> tagged = tag(original, "foo");

        assertEquals(tagged, original);
    }

    @Test
    void tag_an_already_tagged_thing() {
        String aTag = "foo";

        State<Integer> taggedState = tag(new State<>(3), aTag);
        State<Integer> reTaggedState = tag(taggedState, aTag);

        assertEquals(getTag(taggedState), Namespace.of(aTag));
        assertEquals(getTag(reTaggedState), Namespace.of(aTag));
        assertEquals(taggedState, reTaggedState);
    }

    @Test
    void cant_retag_an_already_tagged_thing_with_another_different_tag_to_yeild_an_incompatible() {
        State<Integer> taggedState = tag(new State<>(3), "foo");
        State<Integer> retagged = tag(taggedState, "bar");

        assertThrows(IncompatibleTagsException.class, retagged::get);
    }

    @Test
    void result_of_tag_is_tagged_with_the_parent() {
        String aTag = "foo";
        State<State<Integer>> taggedState = tag(new State<>(new State<>(3)), aTag);

        assertEquals(getTag(taggedState.get()), Namespace.of(aTag));
    }

    @Test
    void tagged_can_interact_with_untagged_and_result_is_tagged() {
        String aTag = "foo";
        Unary taggedUnary = tag(new Unary(), aTag);

        State<Integer> result = new State<>(3);
        State<Integer> operated = taggedUnary.operate(result);

        assertEquals(operated, result);
        assertEquals(getTag(operated), Namespace.of(aTag));
    }

    @Test
    void tagged_can_interact_with_same_tag_and_result_is_tagged() {
        String aTag = "foo";
        Unary taggedUnary = tag(new Unary(), aTag);

        State<Integer> result = tag(new State<>(3), aTag);
        State<Integer> operated = taggedUnary.operate(result);

        assertEquals(getTag(operated), Namespace.of(aTag));
        assertEquals(operated, result);
    }

    @Test
    void tagged_can_not_interact_with_different_tags() {
        Unary taggedUnary = tag(new Unary(), "foo");
        State<Integer> result = tag(new State<>(3), "bar");

        assertThrows(IncompatibleTagsException.class, () -> taggedUnary.operate(result));
    }
}
