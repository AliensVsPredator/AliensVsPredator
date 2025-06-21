package com.lib.common.gameplay.gene;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public sealed interface Gene {

    ResourceLocation id();

    default String getTranslationKey() {
        return "gene.avp." + id();
    }

    record Attribute(
        ResourceLocation id,
        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeHolder
    ) implements Gene {}

    record Simple(ResourceLocation id) implements Gene {}
}
