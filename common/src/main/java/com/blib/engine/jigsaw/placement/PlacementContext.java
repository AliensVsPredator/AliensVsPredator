package com.blib.engine.jigsaw.placement;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.session.EngineSession;

/**
 * Inputs to a {@link PlacementResolver}. Built once per per-frame resolve call (and again per LMB click); resolvers
 * read what they need without performing their own session lookups.
 * <p>
 * Carries the user's current rotation / mirror selections so resolvers can either honor them
 * ({@link FreePlacementResolver}) or override them ({@link PlacementMode#JIGSAW_SNAP}, where alignment is computed from
 * the target jigsaw face). Resolvers do their own raycasting against {@code session} as needed — different modes care
 * about different clip types (visual-shape vs collision vs jigsaw-block-only), so we don't precompute a single hit
 * result here.
 */
@ApiStatus.Internal
public record PlacementContext(
    EngineSession session,
    StructureTemplate template,
    Rotation userRotation,
    Mirror userMirror
) {}
