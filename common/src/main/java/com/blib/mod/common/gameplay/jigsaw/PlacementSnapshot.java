package com.blib.mod.common.gameplay.jigsaw;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Captured pre-placement state of every cell in a placement's AABB. Holds enough information to fully restore the
 * region: the block state per cell (so air-cells become air again, not whatever the placement put there) and the NBT of
 * any block entities that existed (so chest contents, sign text, etc. survive the round-trip).
 * <p>
 * {@code dimension} is captured because the global history queue spans dimensions; on undo we filter for matches before
 * popping. {@code timestamp} is for inspector display ("placed 12s ago"); not load-bearing for the restore logic
 * itself.
 */
@ApiStatus.Internal
public record PlacementSnapshot(
    ResourceKey<Level> dimension,
    BoundingBox aabb,
    Map<BlockPos, BlockState> states,
    Map<BlockPos, CompoundTag> blockEntityNbt,
    ResourceLocation templateId,
    long timestamp
) {}
