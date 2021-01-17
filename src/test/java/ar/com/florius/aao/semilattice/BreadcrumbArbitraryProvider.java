package ar.com.florius.aao.semilattice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;
import java.util.stream.Collectors;

public class BreadcrumbArbitraryProvider implements ArbitraryProvider {

    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return targetType.isOfType(Breadcrumb.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
        Set<Arbitrary<?>> options = new java.util.HashSet<>();

        options.add(Arbitraries.strings().filter(s -> !s.contains(":")).list()
                .ofMinSize(targetType.isAnnotated(NotBottom.class) ? 0 : 1)
                .map(strings -> strings.stream().map(TagName.Tagged::new).collect(Collectors.toList()))
                .map(Breadcrumb.Tagged::new)
        );

        if (!targetType.isAnnotated(NotTop.class)) {
            options.add(Arbitraries.create(() -> Breadcrumb.Incompatible.INSTANCE));
        }

        return options;
    }
}
