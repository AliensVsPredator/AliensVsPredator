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
 * Client → server: enumerate one registry's elements + tag names so the Tag Editor's footer Add-entry picker can show
 * every valid choice (both direct entries and tag refs). Server replies with {@link S2CRegistryEntriesPayload}. The
 * client caches per-registry — a single fetch covers all subsequent edits to tags in that registry until the workspace
 * closes.
 */
public record C2SRequestRegistryEntriesPayload(
    String projectName,
    ResourceLocation registryKey
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_registry_entries");

    public static final Type<C2SRequestRegistryEntriesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestRegistryEntriesPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRequestRegistryEntriesPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestRegistryEntriesPayload::registryKey,
        C2SRequestRegistryEntriesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
