package com.avp.common.hive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public record HiveMemberData(
    ResourceLocation entityType,
    BlockPos lastSeenPos,
    int lastSeenTimestampInTicks
) {

    public static final Codec<HiveMemberData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entityType").forGetter(HiveMemberData::entityType),
            BlockPos.CODEC.fieldOf("lastSeenPos").forGetter(HiveMemberData::lastSeenPos),
            Codec.INT.fieldOf("lastSeenTimestampInTicks").forGetter(HiveMemberData::lastSeenTimestampInTicks)
        )
            .apply(instance, HiveMemberData::new)
    );
}
