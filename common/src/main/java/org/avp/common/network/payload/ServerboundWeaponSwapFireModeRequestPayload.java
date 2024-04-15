package org.avp.common.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.avp.common.AVPResources;
import org.avp.common.network.AVPServerListener;
import org.avp.common.network.ServerboundPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ServerboundWeaponSwapFireModeRequestPayload(
    UUID playerUUID
) implements ServerboundPacket, CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("weapon_swap_fire_mode_request");

    public ServerboundWeaponSwapFireModeRequestPayload(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readUUID());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(playerUUID);
    }

    @Override
    public void handleServer(ServerLevel serverLevel) {
        AVPServerListener.handleWeaponSwapFireModeRequest(serverLevel, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return PAYLOAD_ID;
    }
}
