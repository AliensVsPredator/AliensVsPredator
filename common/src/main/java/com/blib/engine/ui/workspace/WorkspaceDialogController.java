package com.blib.engine.ui.workspace;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.ui.dialog.CaptureDialog;
import com.blib.engine.ui.dialog.ConfirmDialog;
import com.blib.engine.ui.dialog.LayoutNameDialog;
import com.blib.engine.ui.dialog.ManageLayoutsDialog;
import com.blib.engine.ui.dialog.PreferencesDialog;
import com.blib.engine.ui.workspace.modal.ModalStack;

/**
 * Owns the five workspace modal dialogs (confirm, capture, layout-name, manage-layouts, preferences) and the modal
 * z-order stack that orders them. The screen delegates render + input dispatch + lifecycle to this controller; the
 * {@code openXxxDialog} helpers on the screen call {@link #setConfirmDialog(ConfirmDialog)} etc. to install a new
 * dialog and the controller's dispatch methods route input to whichever dialog is on top.
 * <p>
 * Modal stack reconciliation is supplier-polled — dialog fields move via the setters, and the stack syncs on the next
 * query. Push / pop is implicit from the field state, mirroring the prior on-screen pattern but in one isolated
 * location instead of scattered across the workspace.
 */
@ApiStatus.Internal
public final class WorkspaceDialogController {

    private static final String MODAL_CONFIRM = "confirm";

    private static final String MODAL_CAPTURE = "capture";

    private static final String MODAL_LAYOUT_NAME = "layoutName";

    private static final String MODAL_MANAGE_LAYOUTS = "manageLayouts";

    private static final String MODAL_PREFERENCES = "preferences";

    private @Nullable ConfirmDialog confirmDialog;

    private @Nullable CaptureDialog captureDialog;

    private @Nullable LayoutNameDialog layoutNameDialog;

    private @Nullable ManageLayoutsDialog manageLayoutsDialog;

    private @Nullable PreferencesDialog preferencesDialog;

    private final ModalStack modalStack = new ModalStack();

    public WorkspaceDialogController() {
        modalStack.register(MODAL_CONFIRM, () -> confirmDialog != null);
        modalStack.register(MODAL_CAPTURE, () -> captureDialog != null);
        modalStack.register(MODAL_LAYOUT_NAME, () -> layoutNameDialog != null);
        modalStack.register(MODAL_MANAGE_LAYOUTS, () -> manageLayoutsDialog != null);
        modalStack.register(MODAL_PREFERENCES, () -> preferencesDialog != null);
    }

    public @Nullable ConfirmDialog confirmDialog() {
        return confirmDialog;
    }

    public void setConfirmDialog(@Nullable ConfirmDialog d) {
        this.confirmDialog = d;
    }

    public @Nullable CaptureDialog captureDialog() {
        return captureDialog;
    }

    public void setCaptureDialog(@Nullable CaptureDialog d) {
        this.captureDialog = d;
    }

    public @Nullable LayoutNameDialog layoutNameDialog() {
        return layoutNameDialog;
    }

    public void setLayoutNameDialog(@Nullable LayoutNameDialog d) {
        this.layoutNameDialog = d;
    }

    public @Nullable ManageLayoutsDialog manageLayoutsDialog() {
        return manageLayoutsDialog;
    }

    public void setManageLayoutsDialog(@Nullable ManageLayoutsDialog d) {
        this.manageLayoutsDialog = d;
    }

    public @Nullable PreferencesDialog preferencesDialog() {
        return preferencesDialog;
    }

    public void setPreferencesDialog(@Nullable PreferencesDialog d) {
        this.preferencesDialog = d;
    }

    public boolean isAnyOpen() {
        return modalStack.isAnyOpen();
    }

    public @Nullable String topTag() {
        return modalStack.top();
    }

    /**
     * Render every open dialog in z-order so a child dialog (e.g. a Delete-Profile confirm spawned from Preferences)
     * sits on top of its parent. The order is reconciled with field state by {@link ModalStack#sync()} so individual
     * open / close sites don't need to push or pop manually.
     */
    public void render(GuiGraphics graphics, int logicalWidth, int logicalHeight, int logicalMouseX, int logicalMouseY) {
        for (var tag : modalStack.order()) {
            switch (tag) {
                case MODAL_CONFIRM -> {
                    if (confirmDialog != null) {
                        confirmDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_CAPTURE -> {
                    if (captureDialog != null) {
                        captureDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_MANAGE_LAYOUTS -> {
                    if (manageLayoutsDialog != null) {
                        manageLayoutsDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_PREFERENCES -> {
                    if (preferencesDialog != null) {
                        preferencesDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_LAYOUT_NAME -> {
                    if (layoutNameDialog != null) {
                        layoutNameDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                default -> {}
            }
        }
    }

    /**
     * Dispatch a key press to the topmost open dialog. Returns {@code true} when a dialog absorbed the key (or no
     * dialog was open to handle it but the caller should still swallow the event because a modal is sitting on top —
     * the per-dialog {@code keyPressed} returns are what gate field nulling).
     */
    public boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        var top = topTag();
        if (top == null) {
            return false;
        }
        switch (top) {
            case MODAL_CONFIRM -> {
                if (confirmDialog != null && confirmDialog.keyPressed(keyCode)) {
                    confirmDialog = null;
                }
                // Swallow non-Esc keys too — typing into nothing while a confirm is pending would feel unresponsive.
                return true;
            }
            case MODAL_CAPTURE -> {
                if (captureDialog != null) {
                    return captureDialog.keyPressed(keyCode, scanCode, modifiers);
                }
                return true;
            }
            case MODAL_LAYOUT_NAME -> {
                if (layoutNameDialog != null) {
                    return layoutNameDialog.keyPressed(keyCode, scanCode, modifiers);
                }
                return true;
            }
            case MODAL_MANAGE_LAYOUTS -> {
                if (manageLayoutsDialog != null) {
                    return manageLayoutsDialog.keyPressed(keyCode, scanCode, modifiers);
                }
                return true;
            }
            case MODAL_PREFERENCES -> {
                if (preferencesDialog != null) {
                    return preferencesDialog.keyPressed(keyCode, scanCode, modifiers);
                }
                return true;
            }
            default -> {
                return true;
            }
        }
    }

    /**
     * Dispatch a click to the topmost open dialog. Returns {@code true} if a dialog was on top — even if it didn't
     * consume the click, the caller should still treat the click as absorbed so it doesn't reach panels under the
     * modal's dim.
     */
    public boolean handleMouseClicked(double logicalX, double logicalY, int button) {
        var top = topTag();
        if (top == null) {
            return false;
        }
        switch (top) {
            case MODAL_CONFIRM -> {
                if (confirmDialog != null && confirmDialog.mouseClicked(logicalX, logicalY, button)) {
                    confirmDialog = null;
                }
            }
            case MODAL_CAPTURE -> {
                if (captureDialog != null) {
                    captureDialog.mouseClicked(logicalX, logicalY, button);
                }
            }
            case MODAL_LAYOUT_NAME -> {
                if (layoutNameDialog != null) {
                    layoutNameDialog.mouseClicked(logicalX, logicalY, button);
                }
            }
            case MODAL_MANAGE_LAYOUTS -> {
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.mouseClicked(logicalX, logicalY, button);
                }
            }
            case MODAL_PREFERENCES -> {
                if (preferencesDialog != null) {
                    preferencesDialog.mouseClicked(logicalX, logicalY, button);
                }
            }
            default -> {}
        }
        return true;
    }

    /**
     * Dispatch a scroll to the preferences dialog (the only modal with internal scrollable content). Returns
     * {@code true} if the preferences dialog absorbed the scroll, {@code false} otherwise.
     */
    public boolean handleMouseScrolled(double logicalX, double logicalY, double scrollX, double scrollY) {
        if (MODAL_PREFERENCES.equals(topTag()) && preferencesDialog != null) {
            return preferencesDialog.mouseScrolled(logicalX, logicalY, scrollX, scrollY);
        }
        return false;
    }
}
