package com.lib.common.gameplay.gene;

import net.minecraft.core.Holder;

public sealed interface Gene {

    String id();

    default String getTranslationKey() {
        return "gene.avp." + id();
    }

    record Attribute(
        String id,
        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeHolder
    ) implements Gene {}

    record Simple(String id) implements Gene {}
}
