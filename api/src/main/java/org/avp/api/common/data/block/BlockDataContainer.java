package org.avp.api.common.data.block;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public sealed abstract class BlockDataContainer permits ExtendedBlockDataContainer, SingleBlockDataContainer {

    private final Set<SingleBlockDataContainer> variants;

    protected BlockDataContainer() {
        this.variants = new HashSet<>();
    }

    protected final <T extends ExtendedBlockDataContainer> T addVariant(T extendedBlockDataContainer) {
        this.variants.addAll(extendedBlockDataContainer.getVariants());
        return extendedBlockDataContainer;
    }

    protected final SingleBlockDataContainer.Holder addVariant(SingleBlockDataContainer singleBlockDataContainer) {
        return this.addVariant(singleBlockDataContainer.withHolder());
    }

    protected final SingleBlockDataContainer.Holder addVariant(SingleBlockDataContainer.Holder singleBlockDataContainer) {
        if (variants.contains(singleBlockDataContainer)) {
            throw new IllegalArgumentException("Variant has already been added: " + singleBlockDataContainer.getRegistryName());
        }

        this.variants.add(singleBlockDataContainer);
        this.variants.addAll(singleBlockDataContainer.getVariants());
        return singleBlockDataContainer;
    }

    public final Set<SingleBlockDataContainer> getVariants() {
        return Collections.unmodifiableSet(variants);
    }
}
