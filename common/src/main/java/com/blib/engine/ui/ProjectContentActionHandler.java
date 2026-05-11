package com.blib.engine.ui;

import org.jetbrains.annotations.ApiStatus;

/**
 * Open a destructive {@link ConfirmDialog} via the engine workspace screen. Implemented by
 * {@code EngineWorkspaceScreen} and forwarded through {@link com.blib.engine.layout.PanelRegistry.Context} so panels
 * can request modal confirmation without holding a screen reference. Same pattern as {@link EntityContextMenuHandler}.
 * <p>
 * Used today by {@code ContentBrowserPanel} to confirm Pool / Structure / Capture deletions before firing the C2S
 * delete packet.
 */
@ApiStatus.Internal
@FunctionalInterface
public interface ProjectContentActionHandler {

    void confirmDelete(String title, String message, Runnable onConfirm);
}
