package com.alien.common.model.hive;

import com.just.core.functional.option.Option;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public record HiveMemberData(
    ResourceLocation entityTypeResourceLocation,
    BlockPos lastSeenPos,
    int lastSeenTimestampInTicks
) {

    public static final Codec<HiveMemberData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            // TODO: Change the field name here.
            ResourceLocation.CODEC.fieldOf("entityType").forGetter(HiveMemberData::entityTypeResourceLocation),
            BlockPos.CODEC.fieldOf("lastSeenPos").forGetter(HiveMemberData::lastSeenPos),
            Codec.INT.fieldOf("lastSeenTimestampInTicks").forGetter(HiveMemberData::lastSeenTimestampInTicks)
        )
            .apply(instance, HiveMemberData::new)
    );

    public Option<EntityType<?>> getEntityType() {
        var referenceOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(entityTypeResourceLocation);

        if (referenceOptional.isEmpty()) {
            return Option.none();
        }

        return Option.some(BuiltInRegistries.ENTITY_TYPE.get(entityTypeResourceLocation));
    }
}
