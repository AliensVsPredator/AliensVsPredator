package com.blib.mod.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.mod.BLib;

public record S2CEntityDispatchCommandPayload(
    int entityId,
    AzCommand dispatchCommand
) implements CustomPacketPayload {

    private static final ResourceLocation AZ_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID = BLib.MOD.resources()
        .createLocation("az_entity_dispatch_command_sync");

    public static final Type<S2CEntityDispatchCommandPayload> TYPE = new Type<>(
        AZ_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, S2CEntityDispatchCommandPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        S2CEntityDispatchCommandPayload::entityId,
        AzCommand.CODEC,
        S2CEntityDispatchCommandPayload::dispatchCommand,
        S2CEntityDispatchCommandPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
