package org.avp.common.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import org.avp.common.AVPResources;
import org.avp.common.network.AVPServerListener;
import org.avp.api.common.network.ServerboundPacket;

public record ServerboundWeaponReloadRequestPayload(
    UUID playerUUID
) implements ServerboundPacket, CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("weapon_reload_request");

    public ServerboundWeaponReloadRequestPayload(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readUUID());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(playerUUID);
    }

    @Override
    public void handleServer(ServerLevel serverLevel) {
        AVPServerListener.handleWeaponReloadRequest(serverLevel, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return PAYLOAD_ID;
    }
}
