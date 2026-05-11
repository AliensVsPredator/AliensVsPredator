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
 * Client → server: delete one pool's JSON file under {@code projectName}'s datapack. Op-gated; gated by a
 * {@code ConfirmDialog} client-side. Server replies with {@link S2CProjectOpResultPayload} (op {@code RELOAD}) and a
 * refreshed {@link S2CPoolListPayload}.
 * <p>
 * Note that deleting the file does <em>not</em> drop the pool from the running registry — that requires a Reload
 * Project. The confirm dialog surfaces this caveat to the user.
 */
public record C2SDeletePoolPayload(
    String projectName,
    ResourceLocation poolId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_pool");

    public static final Type<C2SDeletePoolPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeletePoolPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SDeletePoolPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeletePoolPayload::poolId,
        C2SDeletePoolPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
