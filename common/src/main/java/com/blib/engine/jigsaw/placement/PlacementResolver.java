package com.blib.engine.jigsaw.placement;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Strategy that turns a {@link PlacementContext} into a concrete {@link Placement}. Returning {@code null} means "no
 * valid placement at this cursor position right now" — the world preview should suppress the ghost, and a click should
 * be ignored. This matches the spec's {@code resolvePlacement(...) -> Placement | null} contract.
 * <p>
 * Implementations are stateless singletons selected by {@link JigsawTool#activeResolver}.
 */
@ApiStatus.Internal
public interface PlacementResolver {

    @Nullable
    Placement resolve(PlacementContext ctx);
}
