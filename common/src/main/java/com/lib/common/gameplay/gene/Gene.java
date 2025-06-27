package com.lib.common.gameplay.gene;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public sealed interface Gene {

    ResourceLocation id();

    default String getTranslationKey() {
        return "gene.avp." + id();
    }

    record Attribute(
        ResourceLocation id,
        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeHolder
    ) implements Gene {}

    record Effect(
        ResourceLocation id,
        Consumer<LivingEntity> onChange
    ) implements Gene {}

    record Simple(ResourceLocation id) implements Gene {}
}
