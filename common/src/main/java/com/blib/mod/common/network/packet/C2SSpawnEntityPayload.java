package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: spawn the entity type identified by {@code entityTypeId} at world position {@code anchor} in
 * dimension {@code dimensionId}. Triggered from the engine workspace's entity-palette flow as a UI replacement for
 * {@code /summon}. The server validates op permissions, refuses spawns that the difficulty would make pointless
 * (peaceful + hostile mob), and only spawns if {@link net.minecraft.world.entity.EntityType#canSummon} returns true for
 * the resolved type.
 * <p>
 * The dimension is sent so the server doesn't accidentally spawn into the player's <em>current</em> dimension when the
 * client meant the world they were just looking at — engine workspace runs against integrated server only, but treating
 * dimension as authoritative-from-client matches the rest of the engine's packet contract.
 */
public record C2SSpawnEntityPayload(
    ResourceLocation entityTypeId,
    BlockPos anchor,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("spawn_entity");

    public static final Type<C2SSpawnEntityPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSpawnEntityPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSpawnEntityPayload::entityTypeId,
        BLibCodecs.Stream.BLOCK_POS,
        C2SSpawnEntityPayload::anchor,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSpawnEntityPayload::dimensionId,
        C2SSpawnEntityPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
