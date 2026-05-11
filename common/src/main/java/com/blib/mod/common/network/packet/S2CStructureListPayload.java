package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: list of structure ids authored in {@code projectName}'s datapack. Sent in response to
 * {@link C2SListStructuresPayload} and proactively after a successful delete.
 */
public record S2CStructureListPayload(
    String projectName,
    List<ResourceLocation> structureIds
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("structure_list");

    public static final Type<S2CStructureListPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CStructureListPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CStructureListPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CStructureListPayload::structureIds,
        S2CStructureListPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
