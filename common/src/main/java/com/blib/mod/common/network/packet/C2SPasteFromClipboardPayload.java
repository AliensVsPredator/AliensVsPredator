package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: paste the server-side clipboard contents at {@code destination}. Server uses its own
 * {@code ServerBlockClipboard} state — no NBT travels over the wire (it would for a 256³ Cut). Server replies via
 * {@link S2CClipboardStatusPayload} (unchanged after paste, since paste doesn't modify the clipboard) only if the paste
 * fails for some reason worth surfacing.
 */
public record C2SPasteFromClipboardPayload(
    BlockPos destination,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("paste_from_clipboard");

    public static final Type<C2SPasteFromClipboardPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SPasteFromClipboardPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SPasteFromClipboardPayload::destination,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SPasteFromClipboardPayload::dimensionId,
        C2SPasteFromClipboardPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
