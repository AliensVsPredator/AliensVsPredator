package com.blib.engine.ui.modal;

import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for engine modal dialogs. Replaces the prior "nullable field on the workspace screen + supplier-based
 * z-order reconciliation" pattern with explicit {@link #open}/{@link #close} lifecycle hooks. A dialog registers itself
 * with {@link ModalManager} when opened and unregisters on close, so the screen no longer has to remember to null each
 * field at every close site — forgetting one used to ghost-mount the dialog in the z-order.
 * <p>
 * Subclasses keep their existing render + input method shapes; only the lifecycle moves into this base. The existing
 * five dialogs ({@code ConfirmDialog}, {@code CaptureDialog}, {@code LayoutNameDialog}, {@code ManageLayoutsDialog},
 * {@code PreferencesDialog}) have small signature differences (some {@code keyPressed} variants take scanCode +
 * modifiers, render takes int vs. double coords) so the dispatch in the workspace screen still calls each dialog's own
 * concrete method; the base only standardises lifecycle.
 */
@ApiStatus.Internal
public abstract class ModalDialog {

    private boolean open;

    /** Stable identifier used by the modal manager for z-order tracking. Lowercase snake_case. */
    public abstract String tag();

    /** True while the dialog is currently mounted on the {@link ModalManager}. */
    public final boolean isOpen() {
        return open;
    }

    /** Mount this dialog on top of the modal stack. Idempotent — re-opening pushes back to the top. */
    public final void open() {
        if (!open) {
            open = true;
        }
        ModalManager.push(this);
    }

    /** Unmount this dialog. Idempotent. */
    public final void close() {
        if (open) {
            open = false;
            ModalManager.pop(this);
            onClosed();
        }
    }

    /** Hook invoked once after the dialog leaves the stack. Override to fire callbacks, etc. */
    protected void onClosed() {}
}
