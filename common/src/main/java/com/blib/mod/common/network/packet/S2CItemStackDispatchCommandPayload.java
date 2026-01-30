package com.blib.mod.common.network.packet;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.mod.BLib;

public record S2CItemStackDispatchCommandPayload(
    UUID itemStackId,
    AzCommand dispatchCommand
) implements CustomPacketPayload {

    private static final ResourceLocation AZ_ITEM_STACK_DISPATCH_COMMAND_SYNC_PACKET_ID = BLib.MOD.resources()
        .createLocation("az_item_stack_dispatch_command_sync");

    public static final Type<S2CItemStackDispatchCommandPayload> TYPE = new Type<>(
        AZ_ITEM_STACK_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, S2CItemStackDispatchCommandPayload> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        S2CItemStackDispatchCommandPayload::itemStackId,
        AzCommand.CODEC,
        S2CItemStackDispatchCommandPayload::dispatchCommand,
        S2CItemStackDispatchCommandPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
