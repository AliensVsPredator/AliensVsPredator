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
 * Client → server: remove the entry at {@code rawIndex} from the pool's {@code rawTemplates} list. Used by the Pool
 * Editor's per-row "×" button. Server validates the index is in range; out-of-range or stale indices are silently
 * ignored (e.g. if the pool was concurrently mutated by another packet).
 * <p>
 * Like the other pool-mutation packets, edits are live in the registry but not persisted until the user clicks Save.
 */
public record C2SRemovePoolElementPayload(
    ResourceLocation poolId,
    int rawIndex
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_pool_element");

    public static final Type<C2SRemovePoolElementPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemovePoolElementPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemovePoolElementPayload::poolId,
        StreamCodecs.INT,
        C2SRemovePoolElementPayload::rawIndex,
        C2SRemovePoolElementPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
