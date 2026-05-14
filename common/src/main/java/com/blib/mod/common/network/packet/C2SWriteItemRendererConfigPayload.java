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
 * Client → server: persist an edited item-renderer config to the project's resource pack.
 * <p>
 * Triggered by the modeler inspector on each commit (gizmo release, text-field blur). The {@code json} field is the
 * full serialized {@code RawItemRendererConfig} body — the server doesn't reinterpret it beyond validating that the
 * destination project exists and writing the bytes to
 * {@code <project>/resourcepack/assets/<configId.namespace>/blib/item_renderers/<configId.path>.json}.
 * <p>
 * Op-gated like other write packets — see {@code BLibServerListener#handleWriteItemRendererConfig}. The reload of the
 * client's resource-pack repo is a separate user action (the menu's "Reload Project") so a stream of in-flight edits
 * doesn't redundantly reload after every saved frame.
 */
public record C2SWriteItemRendererConfigPayload(
    String projectName,
    ResourceLocation configId,
    String json
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("write_item_renderer_config");

    public static final Type<C2SWriteItemRendererConfigPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SWriteItemRendererConfigPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SWriteItemRendererConfigPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SWriteItemRendererConfigPayload::configId,
        StreamCodecs.STRING_UTF8,
        C2SWriteItemRendererConfigPayload::json,
        C2SWriteItemRendererConfigPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
