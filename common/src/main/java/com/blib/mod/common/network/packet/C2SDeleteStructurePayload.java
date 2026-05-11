package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: delete one structure's NBT file under {@code projectName}'s datapack. Op-gated; gated by a
 * {@code ConfirmDialog} client-side. Server replies with {@link S2CProjectOpResultPayload} (op {@code RELOAD}) and a
 * refreshed {@link S2CStructureListPayload}.
 * <p>
 * Note that deleting the file does <em>not</em> drop the structure from the live {@code StructureTemplateManager} —
 * that requires a Reload Project. The confirm dialog surfaces this caveat to the user.
 */
public record C2SDeleteStructurePayload(
    String projectName,
    ResourceLocation structureId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_structure");

    public static final Type<C2SDeleteStructurePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteStructurePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SDeleteStructurePayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeleteStructurePayload::structureId,
        C2SDeleteStructurePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
