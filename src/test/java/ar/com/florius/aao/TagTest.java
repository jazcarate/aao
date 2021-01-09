package ar.com.florius.aao;

import ar.com.florius.aao.shapes.State;
import ar.com.florius.aao.shapes.Unary;
import org.junit.jupiter.api.Test;

import static ar.com.florius.aao.Tag.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TagTest {

    @Test
    void tagging_get_tags() {
        String aTag = "foo";

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
    void result_of_tag_is_tagged_with_the_parent() {
        String aTag = "foo";
        State<State<Integer>> taggedState = tag(new State<>(new State<>(3)), aTag);

        assertEquals(getTag(taggedState.get()), aTag);
    }

    @Test
    void tagged_can_interact_with_untagged_and_result_is_tagged() {
        String aTag = "foo";
        Unary taggedUnary = tag(new Unary(), aTag);

        State<Integer> result = new State<>(3);
        State<Integer> operated = taggedUnary.operate(result);

        assertEquals(operated, result);
        assertEquals(getTag(operated), aTag);
    }
}
