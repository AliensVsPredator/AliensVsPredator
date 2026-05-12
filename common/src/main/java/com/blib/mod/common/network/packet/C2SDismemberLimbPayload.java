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
 * Client → server: detach the specific limb identified by {@code limbId} from the entity with {@code entityId}. Sent by
 * the engine workspace's right-click → Dismember… submenu when the user picks an individual limb. The server validates
 * operator permissions (op-level-2 — same threshold as {@link C2SRemoveEntityPayload}), looks up the entity, and
 * delegates to {@code LimbDismemberer.detach}. Already-detached or unknown-id requests are clean no-ops on the server.
 */
public record C2SDismemberLimbPayload(
    int entityId,
    ResourceLocation limbId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("dismember_limb");

    public static final Type<C2SDismemberLimbPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDismemberLimbPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        C2SDismemberLimbPayload::entityId,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDismemberLimbPayload::limbId,
        C2SDismemberLimbPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
