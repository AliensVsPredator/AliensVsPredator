package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: detach every remaining limb from the entity with {@code entityId}. Sent by the engine workspace's
 * right-click → Dismember… → All action. Distinct from {@link C2SDismemberLimbPayload} so the server can do the
 * iteration in one call (mirrors {@code BLibDismembermentCommands.executeAll} before it was retired). Same op-level-2
 * gating as the per-limb variant.
 */
public record C2SDismemberAllLimbsPayload(int entityId) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("dismember_all_limbs");

    public static final Type<C2SDismemberAllLimbsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDismemberAllLimbsPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        C2SDismemberAllLimbsPayload::entityId,
        C2SDismemberAllLimbsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
