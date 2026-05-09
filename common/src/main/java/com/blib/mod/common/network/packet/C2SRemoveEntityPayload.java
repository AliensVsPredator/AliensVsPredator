package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: ask the server to remove {@code entityId} from the world (used by the engine workspace's right-
 * click → "Delete Entity" action). The server validates that the sender has operator permissions, that the target
 * exists, that it isn't a player, and then discards it. Editor convenience for cleaning up test mobs without typing
 * {@code /kill}.
 */
public record C2SRemoveEntityPayload(int entityId) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_entity");

    public static final Type<C2SRemoveEntityPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemoveEntityPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        C2SRemoveEntityPayload::entityId,
        C2SRemoveEntityPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
