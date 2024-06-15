package org.avp.common.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import org.avp.api.common.network.ClientboundPacket;
import org.avp.common.AVPResources;
import org.avp.common.network.AVPClientListener;

public record ClientboundBulletHitBlockPayload(
    BlockPos blockPos,
    Direction direction
) implements ClientboundPacket, CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("bullet_hit_block");

    public ClientboundBulletHitBlockPayload(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBlockPos(), friendlyByteBuf.readEnum(Direction.class));
    }

    @Override
    public void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(blockPos);
        friendlyByteBuf.writeEnum(direction);
    }

    @Override
    public void handleClient() {
        AVPClientListener.handleBulletHitBlockPayload(this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return PAYLOAD_ID;
    }
}
