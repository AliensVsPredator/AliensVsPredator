package com.alien.common.model.lifecycle.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record GrowthStage(
    Optional<TagKey<EntityType<?>>> hostTag,
    EntityType<?> from,
    EntityType<?> to,
    int growthTimeInTicks
) {

    public static final Codec<GrowthStage> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            TagKey.hashedCodec(Registries.ENTITY_TYPE)
                .optionalFieldOf("hostTag")
                .forGetter(GrowthStage::hostTag),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("from").forGetter(GrowthStage::from),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("to").forGetter(GrowthStage::to),
            Codec.INT.fieldOf("growthTimeInTicks").forGetter(GrowthStage::growthTimeInTicks)
        ).apply(instance, GrowthStage::new)
    );

    public GrowthStage(
        EntityType<?> from,
        EntityType<?> to,
        int growthTimeInTicks
    ) {
        this(Optional.empty(), from, to, growthTimeInTicks);
    }

    public GrowthStage(
        TagKey<EntityType<?>> hostTag,
        EntityType<?> from,
        EntityType<?> to,
        int growthTimeInTicks
    ) {
        this(Optional.of(hostTag), from, to, growthTimeInTicks);
    }
}
