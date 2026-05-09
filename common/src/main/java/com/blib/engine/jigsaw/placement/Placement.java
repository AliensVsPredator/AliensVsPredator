package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Resolved placement output: the world {@link BlockPos} the structure's local origin will land at, plus the rotation
 * and mirror to apply. The fields fully describe what {@code StructureTemplate.placeInWorld} will receive — both the
 * world preview and the placement packet read from the same {@link Placement} instance, so what the user sees is what
 * gets built.
 * <p>
 * For {@link PlacementMode#FREE} this is just {@code (cursor anchor, user rotation, user mirror)}. For
 * {@link PlacementMode#JIGSAW_SNAP} the resolver may override the user's rotation to align with the target jigsaw's
 * face.
 */
@ApiStatus.Internal
public record Placement(
    BlockPos anchor,
    Rotation rotation,
    Mirror mirror
) {}
