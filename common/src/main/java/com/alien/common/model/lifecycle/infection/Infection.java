package com.alien.common.model.lifecycle.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record Infection(
    EntityType<?> parasiteType,
    Optional<TagKey<EntityType<?>>> hostTag,
    EntityType<?> embryoType,
    int impregnationDelayInTicks,
    int detachDelayInTicks,
    int gestationTimeInTicks
) {

    public static final Codec<Infection> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("parasiteType").forGetter(Infection::parasiteType),
            TagKey.hashedCodec(Registries.ENTITY_TYPE)
                .optionalFieldOf("hostTag")
                .forGetter(Infection::hostTag),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("embryoType").forGetter(Infection::embryoType),
            Codec.INT.fieldOf("impregnationDelayInTicks").forGetter(Infection::impregnationDelayInTicks),
            Codec.INT.fieldOf("detachDelayInTicks").forGetter(Infection::detachDelayInTicks),
            Codec.INT.fieldOf("gestationTimeInTicks").forGetter(Infection::gestationTimeInTicks)
        ).apply(instance, Infection::new)
    );

    public Infection(
        EntityType<?> parasiteType,
        EntityType<?> embryoType,
        int impregnationDelayInTicks,
        int detachDelayInTicks,
        int gestationTimeInTicks
    ) {
        this(parasiteType, Optional.empty(), embryoType, impregnationDelayInTicks, detachDelayInTicks, gestationTimeInTicks);
    }
}
