package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;

/**
 * Default sizing constants for the body region of an engine layout. Centralized here so {@link LayoutTemplate} (which
 * builds factory bodies) and {@code EngineWorkspaceScreen} (which reads them for divider clamping) share the same
 * source of truth.
 */
@ApiStatus.Internal
public final class LayoutDefaults {

    public static final int OUTLINER_WIDTH = 150;

    public static final int DETAILS_WIDTH = 190;

    public static final int CONTENT_BROWSER_HEIGHT = 140;

    public static final int UV_MAP_HEIGHT = 220;

    private LayoutDefaults() {}
}
