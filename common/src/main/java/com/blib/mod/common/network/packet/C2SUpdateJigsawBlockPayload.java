package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: rewrite the editable NBT fields of the jigsaw block at {@code pos} — name, target, pool, joint,
 * and the leave-behind block-state string. Triggered from the engine workspace's jigsaw-block inspector when the user
 * commits a field change. Server validates op permissions, looks up the {@link net.minecraft.world.level.block.entity.JigsawBlockEntity},
 * and applies all setters atomically before flagging the chunk dirty + sending a block-update sync.
 * <p>
 * Joint is sent as an enum ordinal ({@link net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType#ordinal})
 * for compactness; the server defends against out-of-range values.
 */
public record C2SUpdateJigsawBlockPayload(
    BlockPos pos,
    ResourceLocation name,
    ResourceLocation target,
    ResourceLocation pool,
    int jointOrdinal,
    String finalState
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("update_jigsaw_block");

    public static final Type<C2SUpdateJigsawBlockPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUpdateJigsawBlockPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SUpdateJigsawBlockPayload::pos,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SUpdateJigsawBlockPayload::name,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SUpdateJigsawBlockPayload::target,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SUpdateJigsawBlockPayload::pool,
        StreamCodecs.INT,
        C2SUpdateJigsawBlockPayload::jointOrdinal,
        StreamCodecs.STRING_UTF8,
        C2SUpdateJigsawBlockPayload::finalState,
        C2SUpdateJigsawBlockPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
