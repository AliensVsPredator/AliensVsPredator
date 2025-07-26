package com.alien.common.model.lifecycle.growth;

import com.lib.common.data.EntityTypePredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

public record GrowthStage(
    Optional<EntityTypePredicate> hostTypePredicate,
    EntityType<?> from,
    EntityType<?> to,
    int growthTimeInTicks
) {

    public static final Codec<GrowthStage> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            EntityTypePredicate.CODEC.optionalFieldOf("hostTypePredicate").forGetter(GrowthStage::hostTypePredicate),
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
        this(Optional.of(new EntityTypePredicate.Tag(hostTag)), from, to, growthTimeInTicks);
    }

    public GrowthStage(
        List<EntityType<?>> hostTypes,
        EntityType<?> from,
        EntityType<?> to,
        int growthTimeInTicks
    ) {
        this(Optional.of(new EntityTypePredicate.List(hostTypes)), from, to, growthTimeInTicks);
    }

    public GrowthStage(
        EntityType<?> hostType,
        EntityType<?> from,
        EntityType<?> to,
        int growthTimeInTicks
    ) {
        this(Optional.of(new EntityTypePredicate.Single(hostType)), from, to, growthTimeInTicks);
    }
}
