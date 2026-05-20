package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Serializable snapshot of a single A* node for debug visualization.
 *
 * @param x           block x position
 * @param y           block y position
 * @param z           block z position
 * @param terrainType ordinal of {@link com.blib.api.common.pathfinding.v1.terrain.TerrainType}
 * @param pathIndex   0-based index of this node in the final reconstructed path, or {@code -1} if not on the path
 * @param gCost       accumulated path cost from the start node
 * @param hCost       heuristic estimate from this node to the goal
 * @param costMalus   extra traversal cost assigned to this node
 * @param blockBreakPlan blocks this path node requires breaking before traversal
 */
public record DebugNodeEntry(
    int x,
    int y,
    int z,
    int terrainType,
    int pathIndex,
    float gCost,
    float hCost,
    float costMalus,
    int expansionOrder,
    PathDebugBlockPos parent,
    List<PathDebugBlockPos> blockBreakPlan
) {

    public DebugNodeEntry {
        blockBreakPlan = blockBreakPlan == null ? List.of() : List.copyOf(blockBreakPlan);
    }

    public static final StreamCodec<DebugNodeEntry> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull DebugNodeEntry decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                return new DebugNodeEntry(
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.FLOAT.decode(schema, input),
                    StreamCodecs.FLOAT.decode(schema, input),
                    StreamCodecs.FLOAT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.asList().decode(schema, input)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull DebugNodeEntry value) {
                StreamCodecs.INT.encode(schema, output, value.x);
                StreamCodecs.INT.encode(schema, output, value.y);
                StreamCodecs.INT.encode(schema, output, value.z);
                StreamCodecs.INT.encode(schema, output, value.terrainType);
                StreamCodecs.INT.encode(schema, output, value.pathIndex);
                StreamCodecs.FLOAT.encode(schema, output, value.gCost);
                StreamCodecs.FLOAT.encode(schema, output, value.hCost);
                StreamCodecs.FLOAT.encode(schema, output, value.costMalus);
                StreamCodecs.INT.encode(schema, output, value.expansionOrder);
                PathDebugBlockPos.CODEC.encode(schema, output, value.parent);
                PathDebugBlockPos.CODEC.asList().encode(schema, output, value.blockBreakPlan);
            }
        }
    );

    public boolean onPath() {
        return pathIndex >= 0;
    }

    public float fCost() {
        return gCost + hCost;
    }

    public boolean hasParent() {
        return parent.present();
    }

    public boolean requiresBlockBreaking() {
        return !blockBreakPlan.isEmpty();
    }
}
