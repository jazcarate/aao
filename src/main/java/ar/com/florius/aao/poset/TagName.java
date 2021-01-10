package ar.com.florius.aao.poset;

import ar.com.florius.aao.IncompatibleTagsException;
import lombok.AccessLevel;
import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Value
public class TagName implements Op<TagName> {
    @With(AccessLevel.PRIVATE)
    private final List<String> name;

    public TagName(String name) {
        this.name = Arrays.asList(name.split(":"));
    }

    public TagName(List<String> names) {
        this.name = names;
    }

    static private List<String> commonGo(List<String> a, List<String> b) {
        String ea = head(a);
        String eb = head(b);

        if (ea != null && eb != null) {
            if (ea.equals(eb)) {
                return cons(ea, commonGo(tail(a), tail(b)));
            } else {
                throw new IncompatibleTagsException();
            }
        } else {
            return List.of(ea == null ? eb : ea);
        }
    }

    private static List<String> cons(String x, List<String> xs) {
        List<String> ret = new ArrayList<>();
        ret.add(x);
        ret.addAll(xs);
        return ret;
    }

    private static List<String> tail(List<String> list) {
        return list.subList(1, list.size());
    }

    private static String head(List<String> list) {
        if (list.size() == 0) return null;
        else return list.get(0);
    }


    @Override
    public TagName common(TagName a) {
        return new TagName(commonGo(this.name, a.name));
    }
}
