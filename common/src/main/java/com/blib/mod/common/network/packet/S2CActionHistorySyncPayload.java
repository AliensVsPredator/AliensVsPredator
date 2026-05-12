package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.mod.BLib;

/**
 * Server → client: full descriptor mirror of the server's {@code ActionHistory}, broadcast after every push / undo /
 * redo / clear. Sent in full each time (no delta) — a 64-entry cap means the wire size stays under 10 KB and the
 * implementation is dead simple.
 * <p>
 * {@code entries} is the concatenation of the undo stack (newest first) followed by the redo stack (oldest first), so
 * the panel can render one continuous list with {@code undoCursor} marking the boundary: indices
 * {@code [0, undoCursor)} are undoable; {@code [undoCursor, entries.size())} are redoable.
 */
public record S2CActionHistorySyncPayload(
    List<ActionDescriptor> entries,
    int undoCursor
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("action_history_sync");

    public static final Type<S2CActionHistorySyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CActionHistorySyncPayload> CODEC = RecordStreamCodec.of(
        ActionDescriptor.CODEC.asList(),
        S2CActionHistorySyncPayload::entries,
        StreamCodecs.INT,
        S2CActionHistorySyncPayload::undoCursor,
        S2CActionHistorySyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
