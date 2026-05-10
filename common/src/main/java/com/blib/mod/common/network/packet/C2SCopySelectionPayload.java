package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: copy the selection's blocks into the server-side clipboard. {@code deleteSource=true} additionally
 * clears the source to air (the Cut semantic). Server replies via {@link S2CClipboardStatusPayload} with the new
 * clipboard size + timestamp so the client can enable its Paste button.
 */
public record C2SCopySelectionPayload(
    BlockPos cornerA,
    BlockPos cornerB,
    boolean deleteSource,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("copy_selection");

    public static final Type<C2SCopySelectionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SCopySelectionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SCopySelectionPayload::cornerA,
        BLibCodecs.Stream.BLOCK_POS,
        C2SCopySelectionPayload::cornerB,
        StreamCodecs.BOOLEAN,
        C2SCopySelectionPayload::deleteSource,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCopySelectionPayload::dimensionId,
        C2SCopySelectionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
