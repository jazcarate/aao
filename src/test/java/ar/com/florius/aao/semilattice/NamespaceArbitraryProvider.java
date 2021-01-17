package ar.com.florius.aao.semilattice;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;

public class NamespaceArbitraryProvider implements ArbitraryProvider {

    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return targetType.isOfType(Namespace.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
        Set<Arbitrary<?>> options = new java.util.HashSet<>();

        options.add(Arbitraries.strings().filter(s -> !s.contains(":") || !s.contains("->")).tuple2()
                .map(tuple2 -> new Namespace.Tagged(tuple2.get1(), new Breadcrumb.Tagged(tuple2.get2())))
        );

        if (!targetType.isAnnotated(NotTop.class)) {
            options.add(Arbitraries.create(() -> Namespace.Incompatible.INSTANCE));
        }

        return options;
    }
}
