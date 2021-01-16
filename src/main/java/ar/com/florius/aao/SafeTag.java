package ar.com.florius.aao;

import ar.com.florius.aao.semilattice.TagName;

import java.util.List;

public class SafeTag<T> implements Taggable<T> {
    private final T value;
    private final TagName tag;

    public SafeTag(T value, TagName tag) {
        this.value = value;
        this.tag = tag;
    }

    public static <T> T untag(SafeTag<T> tagged) {
        return tagged.getValue();
    }

    public static <T> TagName getTag(SafeTag<T> tagged) {
        return tagged.getTag();
    }

    @Override
    public TagName getTag() {
        return tag;
    }

    @Override
    public T getValue() {
        return value;
    }

    public TagName operate(List<TagName> argsTag) {
        TagName newTag = argsTag.stream().reduce(this.getTag(), TagName::join);
        if (isIncompatible(newTag)) {
            throw new IncompatibleTagsException("Tags are incompatible between this (" + this.getTag() + ") and the arguments (" + argsTag + ")");
        }
        return newTag;
    }

    private boolean isIncompatible(TagName newTag) {
        return newTag.equals(TagName.Incompatible.INSTANCE);
    }
}
