package com.blib.engine.ui;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dialog.ConfirmDialog;
import com.blib.engine.ui.popup.EntityContextMenuHandler;

/**
 * Open a destructive {@link ConfirmDialog} via the engine workspace screen. Implemented by
 * {@code EngineWorkspaceScreen} and forwarded through {@link com.blib.engine.layout.PanelRegistry.Context} so panels
 * can request modal confirmation without holding a screen reference. Same pattern as {@link EntityContextMenuHandler}.
 * <p>
 * Used today by {@code ContentBrowserPanel} to confirm Pool / Structure / Capture deletions before firing the C2S
 * delete packet.
 */
@ApiStatus.Internal
public interface ProjectContentActionHandler {

    /**
     * General confirm dialog. {@code confirmLabel} is shown on the confirm button; {@code destructive} flips the
     * confirm-button text to the red-error palette for irreversible actions.
     */
    void confirm(String title, String message, String confirmLabel, boolean destructive, Runnable onConfirm);

    /** Convenience for the common destructive-delete case — confirm button labeled "Delete", red text. */
    default void confirmDelete(String title, String message, Runnable onConfirm) {
        confirm(title, message, "Delete", true, onConfirm);
    }
}
