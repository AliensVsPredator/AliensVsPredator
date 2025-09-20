package com.human.common.gameplay.item.gun.attack;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.lib.common.util.codec.stream.impl.MojangStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.UUID;

import com.avp.common.network.codec.GunHitResultCodec;

public sealed interface GunHitResult {

    GunHitResultCodec STREAM_CODEC = new GunHitResultCodec();

    StreamCodec<List<GunHitResult>> LIST_STREAM_CODEC = STREAM_CODEC.asList();

    record Block(
        BlockPos blockPos,
        Direction direction
    ) implements GunHitResult {

        public static final StreamCodec<Block> STREAM_CODEC = RecordStreamCodec.of(
            MojangStreamCodecs.BLOCK_POS,
            GunHitResult.Block::blockPos,
            MojangStreamCodecs.DIRECTION,
            GunHitResult.Block::direction,
            GunHitResult.Block::new
        );
    }

    record Entity(UUID entityUUID) implements GunHitResult {

        public static final StreamCodec<GunHitResult.Entity> STREAM_CODEC = RecordStreamCodec.of(
            StreamCodecs.UUID,
            GunHitResult.Entity::entityUUID,
            GunHitResult.Entity::new
        );
    }
}
