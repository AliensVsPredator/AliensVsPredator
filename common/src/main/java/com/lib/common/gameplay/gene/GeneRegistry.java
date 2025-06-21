package com.lib.common.gameplay.gene;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;

public class GeneRegistry {

    private static final Map<ResourceLocation, Gene> GENES_BY_RESOURCE_LOCATION = new HashMap<>();

    private static final Map<Gene, ResourceLocation> RESOURCE_LOCATION_BY_VALUE = new HashMap<>();

    public static @Nullable Gene getValueOrNull(ResourceLocation resourceLocation) {
        return GENES_BY_RESOURCE_LOCATION.get(resourceLocation);
    }

    public static @Nullable ResourceLocation getResourceLocationOrNull(AVPDeferredHolder<Gene> geneHolder) {
        return RESOURCE_LOCATION_BY_VALUE.get(geneHolder.get());
    }

    public static Option<Gene> getValue(ResourceLocation resourceLocation) {
        return Option.ofNullable(getValueOrNull(resourceLocation));
    }

    public static Option<ResourceLocation> getResourceLocation(AVPDeferredHolder<Gene> geneHolder) {
        return Option.ofNullable(getResourceLocationOrNull(geneHolder));
    }

    public static AVPDeferredHolder<Gene> register(Supplier<Gene> geneSupplier) {
        var gene = geneSupplier.get();
        var resourceLocation = gene.id();
        var holder = Holder.direct(gene);

        GENES_BY_RESOURCE_LOCATION.put(resourceLocation, gene);
        RESOURCE_LOCATION_BY_VALUE.put(gene, resourceLocation);

        return new AVPDeferredHolder<>(() -> gene, () -> holder);
    }
}
