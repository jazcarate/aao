package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.Namespace;
import ar.com.florius.aao.shapes.State;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ar.com.florius.aao.SafeTag.getTag;
import static ar.com.florius.aao.SafeTag.untag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SafeTagTest {

    @Test
    void tagging_get_tags() {
        Namespace tag = Namespace.of("foo");
        SafeTag<State<Integer>> foo = new SafeTag<>(new State<>(3), tag);

        assertEquals(getTag(foo), tag);
    }

    @Test
    void tagging_get_value() {
        State<Integer> value = new State<>(3);
        SafeTag<State<Integer>> foo = new SafeTag<>(value, Namespace.of("foo"));

        assertEquals(untag(foo), value);
    }

    @Test
    void operate_with_another_tag() {
        SafeTag<State<Integer>> foo = new SafeTag<>(new State<>(3), Namespace.of("foo"));
        Namespace barTag = Namespace.of("bar");


        assertThrows(IncompatibleTagsException.class, () -> foo.operate(List.of(barTag)));
    }

}
