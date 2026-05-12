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
 * Client → server: set a single {@code BlockState} property on the block at {@code pos} from the generic block
 * inspector. {@code propertyName} matches {@link net.minecraft.world.level.block.state.properties.Property#getName} on
 * the live state; {@code valueString} is the canonical string form parsed by
 * {@link net.minecraft.world.level.block.state.properties.Property#getValue(String)}. Server validates op permission,
 * looks the property up on the current state, parses the value, and writes via
 * {@link net.minecraft.world.level.Level#setBlock} with {@link net.minecraft.world.level.block.Block#UPDATE_ALL}.
 * <p>
 * The inspector only generates valid options (it iterates {@code property.getPossibleValues()}), so a parse failure
 * means the world state diverged after the inspector last snapshotted it — the server silently drops the packet rather
 * than rolling the client back, since the inspector re-reads on the next frame.
 */
public record C2SSetBlockStatePropertyPayload(
    BlockPos pos,
    String propertyName,
    String valueString
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("set_block_state_property");

    public static final Type<C2SSetBlockStatePropertyPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSetBlockStatePropertyPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SSetBlockStatePropertyPayload::pos,
        StreamCodecs.STRING_UTF8,
        C2SSetBlockStatePropertyPayload::propertyName,
        StreamCodecs.STRING_UTF8,
        C2SSetBlockStatePropertyPayload::valueString,
        C2SSetBlockStatePropertyPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
