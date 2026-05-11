package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: ask for the full faction directory snapshot. Triggered by the Faction Browser on workspace open and
 * on Refresh. Reply is {@link S2CFactionDirectoryPayload}. Empty payload — the singleton {@code INSTANCE} carries no
 * wire bytes via {@link StreamCodec#unit}.
 */
public record C2SRequestFactionDirectoryPayload() implements CustomPacketPayload {

    public static final C2SRequestFactionDirectoryPayload INSTANCE = new C2SRequestFactionDirectoryPayload();

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_faction_directory");

    public static final Type<C2SRequestFactionDirectoryPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestFactionDirectoryPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
