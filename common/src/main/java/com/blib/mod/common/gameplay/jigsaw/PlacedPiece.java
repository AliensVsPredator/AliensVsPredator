package com.blib.mod.common.gameplay.jigsaw;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Server-canonical identity of a jigsaw piece that was placed via the engine workspace. Captured at placement time and
 * never mutated afterwards — the AABB represents "what was stamped into the world", not "what blocks are currently
 * there". If the user breaks blocks inside the piece the record stays unchanged; the piece is conceptually a placement
 * receipt, not a live block-ownership map.
 * <p>
 * Synced to engine-mode clients so they can hover / select / context-menu the piece as a single thing.
 */
@ApiStatus.Internal
public record PlacedPiece(
    UUID id,
    ResourceLocation templateId,
    ResourceKey<Level> dimension,
    BoundingBox aabb,
    BlockPos anchor,
    Rotation rotation,
    Mirror mirror,
    long placedAtTick
) {

    /** AABB in world-space doubles for the selection renderer / hover raycast. */
    public AABB worldAabb() {
        return new AABB(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX() + 1, aabb.maxY() + 1, aabb.maxZ() + 1);
    }

    /**
     * Wire codec — encodes every field in declaration order. {@link Rotation} / {@link Mirror} go as ordinals;
     * {@link BoundingBox} as six raw ints; {@code dimension} as its {@link ResourceLocation}. Inline rather than
     * record-style because the record-style {@code RecordStreamCodec.of} doesn't compose cleanly with the multi-int
     * decomposition of {@code BoundingBox} without introducing one-off adapter codecs.
     */
    public static final StreamCodec<PlacedPiece> CODEC = new StreamCodec<>() {

        @Override
        public @NotNull <T> PlacedPiece decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
            var id = StreamCodecs.UUID.decode(schema, input);
            var templateId = BLibCodecs.Stream.RESOURCE_LOCATION.decode(schema, input);
            var dimensionLoc = BLibCodecs.Stream.RESOURCE_LOCATION.decode(schema, input);
            var dimension = ResourceKey.create(Registries.DIMENSION, dimensionLoc);
            var minX = StreamCodecs.INT.decode(schema, input);
            var minY = StreamCodecs.INT.decode(schema, input);
            var minZ = StreamCodecs.INT.decode(schema, input);
            var maxX = StreamCodecs.INT.decode(schema, input);
            var maxY = StreamCodecs.INT.decode(schema, input);
            var maxZ = StreamCodecs.INT.decode(schema, input);
            var aabb = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            var anchor = BLibCodecs.Stream.BLOCK_POS.decode(schema, input);
            var rotOrdinal = StreamCodecs.INT.decode(schema, input);
            var rotation = Rotation.values()[Math.floorMod(rotOrdinal, Rotation.values().length)];
            var mirOrdinal = StreamCodecs.INT.decode(schema, input);
            var mirror = Mirror.values()[Math.floorMod(mirOrdinal, Mirror.values().length)];
            var placedAtTick = StreamCodecs.LONG.decode(schema, input);
            return new PlacedPiece(id, templateId, dimension, aabb, anchor, rotation, mirror, placedAtTick);
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull PlacedPiece value) {
            StreamCodecs.UUID.encode(schema, input, value.id());
            BLibCodecs.Stream.RESOURCE_LOCATION.encode(schema, input, value.templateId());
            BLibCodecs.Stream.RESOURCE_LOCATION.encode(schema, input, value.dimension().location());
            var a = value.aabb();
            StreamCodecs.INT.encode(schema, input, a.minX());
            StreamCodecs.INT.encode(schema, input, a.minY());
            StreamCodecs.INT.encode(schema, input, a.minZ());
            StreamCodecs.INT.encode(schema, input, a.maxX());
            StreamCodecs.INT.encode(schema, input, a.maxY());
            StreamCodecs.INT.encode(schema, input, a.maxZ());
            BLibCodecs.Stream.BLOCK_POS.encode(schema, input, value.anchor());
            StreamCodecs.INT.encode(schema, input, value.rotation().ordinal());
            StreamCodecs.INT.encode(schema, input, value.mirror().ordinal());
            StreamCodecs.LONG.encode(schema, input, value.placedAtTick());
        }
    };
}
