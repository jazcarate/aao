package ar.com.florius.aao.semilattice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;

public class TagNameArbitraryProvider implements ArbitraryProvider {

    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return targetType.isOfType(TagName.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
        Set<Arbitrary<?>> options = new java.util.HashSet<>();

        options.add(Arbitraries.strings().map(TagName.Tagged::new));

        if (!targetType.isAnnotated(NotBottom.class)) {
            options.add(Arbitraries.create(() -> TagName.NoTag.INSTANCE));
        }

        if (!targetType.isAnnotated(NotTop.class)) {
            options.add(Arbitraries.create(() -> TagName.Incompatible.INSTANCE));
        }

        return options;
    }
}
