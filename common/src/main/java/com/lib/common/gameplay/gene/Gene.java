package com.lib.common.gameplay.gene;

import com.just.core.functional.function.Function2;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public sealed interface Gene {

    ResourceLocation id();

    Function2<Double, GeneOperationType, Double> transformer();

    default String getTranslationKey() {
        return "gene.avp." + id();
    }

    record Attribute(
        ResourceLocation id,
        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attributeHolder,
        Function2<Double, GeneOperationType, Double> transformer
    ) implements Gene {}

    record Effect(
        ResourceLocation id,
        Consumer<LivingEntity> onChange,
        Function2<Double, GeneOperationType, Double> transformer
    ) implements Gene {}

    record Simple(
        ResourceLocation id,
        Function2<Double, GeneOperationType, Double> transformer
    ) implements Gene {}
}
