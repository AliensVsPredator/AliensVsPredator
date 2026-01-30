package com.blib.mod.common.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.mod.BLib;

public record S2CBlockEntityDispatchCommandPayload(
    BlockPos blockPos,
    AzCommand dispatchCommand
) implements CustomPacketPayload {

    private static final ResourceLocation AZ_BLOCK_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID = BLib.MOD.resources()
        .createLocation("az_block_entity_dispatch_command_sync");

    public static final CustomPacketPayload.Type<S2CBlockEntityDispatchCommandPayload> TYPE = new Type<>(
        AZ_BLOCK_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, S2CBlockEntityDispatchCommandPayload> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        S2CBlockEntityDispatchCommandPayload::blockPos,
        AzCommand.CODEC,
        S2CBlockEntityDispatchCommandPayload::dispatchCommand,
        S2CBlockEntityDispatchCommandPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
