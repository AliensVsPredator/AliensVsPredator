package com.blib.engine.selection;

import org.jetbrains.annotations.ApiStatus;

/**
 * Discriminator for what kind of object a {@link Selectable} wraps. Used by the universal inspector to dispatch to the
 * right per-type view, and by mixed-selection logic to determine which row sets to show vs. hide.
 * <p>
 * Adding a new type is enum-add + new {@link Selectable} implementation + matching inspector view; nothing else in the
 * framework needs to change.
 */
@ApiStatus.Internal
public enum SelectableType {

    ENTITY,
    JIGSAW_PIECE,
    BLOCK,
    BLOCK_VOLUME,
    LIMB
}
