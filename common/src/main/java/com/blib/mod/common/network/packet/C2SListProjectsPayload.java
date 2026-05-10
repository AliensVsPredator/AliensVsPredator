package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: request the list of BLib projects in the current world. The server replies with
 * {@link S2CProjectListPayload}. Triggered by the {@code ProjectPickerScreen} on open and after any create / delete so
 * the picker UI reflects fresh state. Empty payload — server reads the world from the player's connection.
 */
public record C2SListProjectsPayload() implements CustomPacketPayload {

    public static final C2SListProjectsPayload INSTANCE = new C2SListProjectsPayload();

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("list_projects");

    public static final Type<C2SListProjectsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SListProjectsPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
