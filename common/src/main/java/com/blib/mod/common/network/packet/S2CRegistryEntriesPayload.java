package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: the full element set + tag-name set for one registry. Used to populate the Tag Editor's footer
 * Add-entry picker with both direct entries (e.g. {@code minecraft:cherry_log}) and tag refs (e.g.
 * {@code #minecraft:logs_that_burn}, displayed with the {@code #} prefix).
 */
public record S2CRegistryEntriesPayload(
    ResourceLocation registryKey,
    List<ResourceLocation> entries,
    List<ResourceLocation> tagIds
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("registry_entries");

    public static final Type<S2CRegistryEntriesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CRegistryEntriesPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CRegistryEntriesPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CRegistryEntriesPayload::entries,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CRegistryEntriesPayload::tagIds,
        S2CRegistryEntriesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
