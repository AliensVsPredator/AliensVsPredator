package com.avp.common.item.gun.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

import com.avp.common.network.codec.GunHitResultCodec;

public sealed interface GunHitResult {

    GunHitResultCodec STREAM_CODEC = new GunHitResultCodec();

    record Block(
        BlockPos blockPos,
        Direction direction
    ) implements GunHitResult {

        public static final StreamCodec<FriendlyByteBuf, GunHitResult.Block> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            GunHitResult.Block::blockPos,
            Direction.STREAM_CODEC,
            GunHitResult.Block::direction,
            GunHitResult.Block::new
        );
    }

    record Entity(UUID entityUUID) implements GunHitResult {

        public static final StreamCodec<FriendlyByteBuf, GunHitResult.Entity> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            GunHitResult.Entity::entityUUID,
            GunHitResult.Entity::new
        );
    }
}
