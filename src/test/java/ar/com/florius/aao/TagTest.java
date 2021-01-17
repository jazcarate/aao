package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.TagName;
import ar.com.florius.aao.shapes.State;
import ar.com.florius.aao.shapes.Unary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static ar.com.florius.aao.Tag.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TagTest {

    @Test
    void tagging_get_tags() {
        TagName aTag = new TagName.Tagged("foo");

        State<Integer> taggedState = tag(new State<>(3), aTag);

        assertEquals(getTag(taggedState), aTag);
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
        TagName aTag = new TagName.Tagged("foo");

        State<Integer> taggedState = tag(new State<>(3), aTag);
        State<Integer> reTaggedState = tag(taggedState, aTag);

        assertEquals(getTag(taggedState), aTag);
        assertEquals(getTag(reTaggedState), aTag);
        assertEquals(taggedState, reTaggedState);
    }

    @Test
    void cant_tag_an_already_tagged_thing_with_another_different_tag() {

        State<Integer> taggedState = tag(new State<>(3), "foo");

        assertThrows(IncompatibleTagsException.class, () -> tag(taggedState, "bar"));
    }

    @Test
    void result_of_tag_is_tagged_with_the_parent() {
        TagName aTag = new TagName.Tagged("foo");
        State<State<Integer>> taggedState = tag(new State<>(new State<>(3)), aTag);

        assertEquals(getTag(taggedState.get()), aTag);
    }

    @Test
    void tagged_can_interact_with_untagged_and_result_is_tagged() {
        TagName aTag = new TagName.Tagged("foo");
        Unary taggedUnary = tag(new Unary(), aTag);

        State<Integer> result = new State<>(3);
        State<Integer> operated = taggedUnary.operate(result);

        assertEquals(operated, result);
        assertEquals(getTag(operated), aTag);
    }

    @Test
    void tagged_can_interact_with_same_tag_and_result_is_tagged() {
        TagName aTag = new TagName.Tagged("foo");
        Unary taggedUnary = tag(new Unary(), aTag);

        State<Integer> result = tag(new State<>(3), aTag);
        State<Integer> operated = taggedUnary.operate(result);

        assertEquals(getTag(operated), aTag);
        assertEquals(operated, result);
    }

    @Test
    void tagged_can_not_interact_with_different_tags() {
        Unary taggedUnary = tag(new Unary(), "foo");
        State<Integer> result = tag(new State<>(3), "bar");

        assertThrows(IncompatibleTagsException.class, () -> taggedUnary.operate(result));
    }
}
