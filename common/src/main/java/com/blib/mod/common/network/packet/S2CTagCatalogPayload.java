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
 * Server → client: complete catalog of tags across every registry that has tags at runtime, merged with the active
 * project's authored tags (project-only tags appear with {@code inProject=true}; tags in both vanilla/upstream and the
 * project also flip {@code inProject=true}; tags only in upstream are {@code inProject=false}). Sent in response to
 * {@link C2SRequestTagCatalogPayload}, and pushed unsolicited after Create/Delete tag operations so the browser
 * refreshes.
 */
public record S2CTagCatalogPayload(
    String projectName,
    List<TagCatalogEntry> entries
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("tag_catalog");

    public static final Type<S2CTagCatalogPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CTagCatalogPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CTagCatalogPayload::projectName,
        TagCatalogEntry.CODEC.asList(),
        S2CTagCatalogPayload::entries,
        S2CTagCatalogPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
