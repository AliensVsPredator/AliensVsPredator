package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Pre-rotation jigsaw block information extracted from a {@code StructureTemplate}'s palette. Position and direction
 * are in template-local space (the resolver applies the alignment rotation before computing world placement). Pool is
 * intentionally omitted: the candidate's pool only matters when validating against the anchor's pool field, which is a
 * soft check phase 3 doesn't enforce — the user explicitly picked this template, so we honor that even if the pool name
 * doesn't match.
 */
@ApiStatus.Internal
public record TemplateJigsawInfo(
    BlockPos localPos,
    Direction frontLocal,
    Direction topLocal,
    ResourceLocation name,
    ResourceLocation target,
    JigsawBlockEntity.JointType joint
) {}
