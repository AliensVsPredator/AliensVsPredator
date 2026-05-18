package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.core.BlockPos;

/**
 * Compact serializable block position used by pathfinding debug payloads.
 */
public record PathDebugBlockPos(
    int x,
    int y,
    int z
) {

    public static final PathDebugBlockPos NONE = new PathDebugBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public static final StreamCodec<PathDebugBlockPos> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        PathDebugBlockPos::x,
        StreamCodecs.INT,
        PathDebugBlockPos::y,
        StreamCodecs.INT,
        PathDebugBlockPos::z,
        PathDebugBlockPos::new
    );

    public static PathDebugBlockPos of(BlockPos pos) {
        return new PathDebugBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean present() {
        return this != NONE && x != Integer.MIN_VALUE && y != Integer.MIN_VALUE && z != Integer.MIN_VALUE;
    }

    public String label() {
        return present() ? "(" + x + "," + y + "," + z + ")" : "---";
    }
}
