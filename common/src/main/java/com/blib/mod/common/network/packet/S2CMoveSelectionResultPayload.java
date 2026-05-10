package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Server → client: result of a Move Blocks request. {@code success} drives the client's AABB-shift logic — on success,
 * {@code BLibClientListener} shifts the AABB by the offset that was sent (so the user's selection follows the moved
 * blocks, Photoshop-style). {@code blockCount} is the volume that was processed, used by the panel for status display
 * ("Moved 1234 blocks" / "Copied 1234 blocks"). {@code message} is empty on success or carries the failure reason.
 */
public record S2CMoveSelectionResultPayload(
    boolean success,
    String message,
    int blockCount
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("move_selection_result");

    public static final Type<S2CMoveSelectionResultPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CMoveSelectionResultPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.BOOLEAN,
        S2CMoveSelectionResultPayload::success,
        StreamCodecs.STRING_UTF8,
        S2CMoveSelectionResultPayload::message,
        StreamCodecs.INT,
        S2CMoveSelectionResultPayload::blockCount,
        S2CMoveSelectionResultPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
