package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: list authored structures (datapack-loadable .nbt files) under {@code projectName}'s datapack tree.
 * Triggered by the project content browser on open / refresh. Server reply lands as {@link S2CStructureListPayload}.
 */
public record C2SListStructuresPayload(String projectName) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("list_structures");

    public static final Type<C2SListStructuresPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SListStructuresPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SListStructuresPayload::projectName,
        C2SListStructuresPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
