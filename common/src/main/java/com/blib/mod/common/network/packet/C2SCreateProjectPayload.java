package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: create a new BLib project under {@code <world>/datapacks/<name>/}. Server validates the name (regex
 * + reserved-name list), refuses to take over a non-BLib folder of the same name, writes {@code pack.mcmeta} and the
 * {@code blib_project.json} marker, and replies with {@link S2CProjectOpResultPayload} (op {@code CREATE}). Op-gated.
 */
public record C2SCreateProjectPayload(
    String name,
    String description
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("create_project");

    public static final Type<C2SCreateProjectPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SCreateProjectPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SCreateProjectPayload::name,
        StreamCodecs.STRING_UTF8,
        C2SCreateProjectPayload::description,
        C2SCreateProjectPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
