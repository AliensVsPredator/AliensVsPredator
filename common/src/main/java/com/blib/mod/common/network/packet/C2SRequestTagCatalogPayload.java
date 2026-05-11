package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: request the full tag catalog (every {@code (registryKey, tagId)} known to the live registry plus any
 * project-only tags from the active project's datapack). Server replies with {@link S2CTagCatalogPayload}. Fired by the
 * Tag Browser on first show, project swap, and Refresh.
 */
public record C2SRequestTagCatalogPayload(String projectName) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_tag_catalog");

    public static final Type<C2SRequestTagCatalogPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestTagCatalogPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRequestTagCatalogPayload::projectName,
        C2SRequestTagCatalogPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
