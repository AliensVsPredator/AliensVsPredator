package com.blib.engine.ui.dock;

import org.jetbrains.annotations.ApiStatus;

/**
 * Split direction for a {@link DockNode.Split}: HORIZONTAL puts the two children side-by-side (left | right), VERTICAL
 * stacks them top-to-bottom (top / bottom).
 */
@ApiStatus.Internal
public enum Orientation {
    HORIZONTAL,
    VERTICAL
}
