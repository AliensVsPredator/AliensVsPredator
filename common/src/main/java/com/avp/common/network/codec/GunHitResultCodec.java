package com.avp.common.network.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import com.avp.common.item.gun.attack.GunHitResult;

public class GunHitResultCodec implements StreamCodec<FriendlyByteBuf, GunHitResult> {

    private static final int BLOCK_HIT_RESULT_CODE = 0;

    private static final int ENTITY_HIT_RESULT_CODE = 1;

    @Override
    public @NotNull GunHitResult decode(@NotNull FriendlyByteBuf friendlyByteBuf) {
        var typeCode = friendlyByteBuf.readByte();

        return switch (typeCode) {
            case BLOCK_HIT_RESULT_CODE -> GunHitResult.Block.STREAM_CODEC.decode(friendlyByteBuf);
            case ENTITY_HIT_RESULT_CODE -> GunHitResult.Entity.STREAM_CODEC.decode(friendlyByteBuf);
            default -> throw new IllegalStateException("Unexpected gun hit result code: " + typeCode);
        };
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull GunHitResult gunHitResult) {
        var typeCode = switch (gunHitResult) {
            case GunHitResult.Block ignored -> BLOCK_HIT_RESULT_CODE;
            case GunHitResult.Entity ignored -> ENTITY_HIT_RESULT_CODE;
        };

        friendlyByteBuf.writeByte(typeCode);

        switch (gunHitResult) {
            case GunHitResult.Block block -> GunHitResult.Block.STREAM_CODEC.encode(friendlyByteBuf, block);
            case GunHitResult.Entity entity -> GunHitResult.Entity.STREAM_CODEC.encode(friendlyByteBuf, entity);
        }
    }
}
