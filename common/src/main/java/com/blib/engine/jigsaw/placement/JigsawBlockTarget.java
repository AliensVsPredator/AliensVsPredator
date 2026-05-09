package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;

/**
 * A jigsaw block found in the world via the cursor raycast — the "anchor" the snap resolver aligns the candidate piece
 * to. Captures everything the alignment + compatibility checks need without the resolver having to keep a reference to
 * the live {@link JigsawBlockEntity} (which can become stale if the chunk unloads or the block changes mid-frame).
 */
@ApiStatus.Internal
public record JigsawBlockTarget(
    BlockPos worldPos,
    Direction front,
    Direction top,
    ResourceLocation name,
    ResourceLocation target,
    ResourceKey<StructureTemplatePool> pool,
    JigsawBlockEntity.JointType joint
) {}
