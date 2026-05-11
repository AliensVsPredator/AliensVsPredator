package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;

/**
 * Modal text-input dialog used by every layout-naming flow: Save As New, Rename, Duplicate, and New from Template. One
 * dialog class with a {@link Mode} enum rather than four near-identical classes — the only differences are the title,
 * the placeholder text, and the initial input value.
 * <p>
 * Lifecycle mirrors {@link CaptureDialog}: the hosting screen owns the instance, sets it non-null to show, routes mouse
 * + key events through it before any other input, and clears the reference when the dialog dismisses.
 * <p>
 * Validation is live: the derived id is recomputed from the display name on every render and the Confirm button is
 * disabled when the name is empty / too long / produces an unavailable id (for modes where availability matters —
 * {@link Mode#RENAME} accepts the original id even if it would otherwise count as "taken").
 */
@ApiStatus.Internal
public final class LayoutNameDialog {

    public enum Mode {

        SAVE_AS("Save Layout As", "New layout name"),
        RENAME("Rename Layout", "New layout name"),
        DUPLICATE("Duplicate Layout", "Copy name"),
        NEW_FROM_TEMPLATE("New Layout from Template", "New layout name");

        private final String title;

        private final String placeholder;

        Mode(String title, String placeholder) {
            this.title = title;
            this.placeholder = placeholder;
        }

        public String title() {
            return title;
        }

        public String placeholder() {
            return placeholder;
        }
    }

    private static final int DIM_COLOR = 0x80000000;

    private static final int BG_COLOR = 0xFF1F1F26;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int META_COLOR = 0xFF808088;

    private static final int ERROR_COLOR = 0xFFE06868;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DISABLED_TEXT = 0xFF606068;

    private static final int BOX_WIDTH = 320;

    private static final int BOX_PAD_X = 16;

    private static final int BOX_PAD_Y = 12;

    private static final int ROW_HEIGHT = 14;

    private static final int ROW_GAP = 4;

    private static final int BUTTON_HEIGHT = 14;

    private static final int BUTTON_WIDTH = 80;

    private static final int BUTTON_GAP = 8;

    private final Mode mode;

    /**
     * Predicate consulted with the proposed id to decide whether Confirm should enable. Lets the host distinguish "id
     * available" from "id taken but it's the layout we're renaming" without baking that knowledge into this class.
     */
    private final Predicate<String> idAvailability;

    private final Consumer<String> onConfirm;

    private final Runnable onCancel;

    private final TextInput nameInput;

    private @Nullable Rect confirmRect;

    private @Nullable Rect cancelRect;

    private String lastDerivedId = "";

    private @Nullable String validationError;

    public LayoutNameDialog(
        Mode mode,
        String initialDisplayName,
        Predicate<String> idAvailability,
        Consumer<String> onConfirm,
        Runnable onCancel
    ) {
        this.mode = mode;
        this.idAvailability = idAvailability;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.nameInput = new TextInput(mode.placeholder());
        if (initialDisplayName != null && !initialDisplayName.isEmpty()) {
            this.nameInput.setContent(initialDisplayName);
        }
        this.nameInput.focus();
    }

    public void render(GuiGraphics graphics, int screenWidth, int screenHeight, int mouseX, int mouseY) {
        graphics.fill(0, 0, screenWidth, screenHeight, DIM_COLOR);

        var font = EngineFont.get();
        var contentW = BOX_WIDTH - 2 * BOX_PAD_X;

        recomputeValidation();

        // Box height: title + name row + id-preview row + (optional error row) + button row + paddings.
        var rows = 2 + (validationError != null ? 1 : 0);
        var boxH = BOX_PAD_Y + font.lineHeight + ROW_GAP + rows * (ROW_HEIGHT + ROW_GAP) + BUTTON_HEIGHT + BOX_PAD_Y;

        var boxX = (screenWidth - BOX_WIDTH) / 2;
        var boxY = (screenHeight - boxH) / 2;

        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + boxH, BG_COLOR);
        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + 1, BORDER_COLOR);
        graphics.fill(boxX, boxY + boxH - 1, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX, boxY, boxX + 1, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX + BOX_WIDTH - 1, boxY, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);

        graphics.drawString(font, Component.literal(mode.title()), boxX + BOX_PAD_X, boxY + BOX_PAD_Y, TITLE_COLOR, false);

        var rowY = boxY + BOX_PAD_Y + font.lineHeight + ROW_GAP;

        // Name row.
        graphics.drawString(font, Component.literal("Name:"), boxX + BOX_PAD_X, rowY + 1, LABEL_COLOR, false);
        var labelW = font.width("Name: ");
        var inputX = boxX + BOX_PAD_X + labelW + 2;
        var inputW = contentW - labelW - 2;
        nameInput.render(graphics, inputX, rowY + (ROW_HEIGHT - TextInput.HEIGHT) / 2, inputW, mouseX, mouseY);
        rowY += ROW_HEIGHT + ROW_GAP;

        // ID preview row (read-only). Empty content -> placeholder text.
        var idLabel = "ID: " + (lastDerivedId.isEmpty() ? "—" : lastDerivedId);
        graphics.drawString(font, Component.literal(idLabel), boxX + BOX_PAD_X, rowY + 1, META_COLOR, false);
        rowY += ROW_HEIGHT + ROW_GAP;

        if (validationError != null) {
            graphics.drawString(font, Component.literal(validationError), boxX + BOX_PAD_X, rowY + 1, ERROR_COLOR, false);
            rowY += ROW_HEIGHT + ROW_GAP;
        }

        // Buttons.
        var enabled = canConfirm();
        var buttonY = boxY + boxH - BOX_PAD_Y - BUTTON_HEIGHT;
        var confirmX = boxX + BOX_WIDTH - BOX_PAD_X - BUTTON_WIDTH;
        var cancelX = confirmX - BUTTON_GAP - BUTTON_WIDTH;
        confirmRect = new Rect(confirmX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancelRect = new Rect(cancelX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, confirmRect, confirmLabel(), mouseX, mouseY, enabled ? BUTTON_TEXT : BUTTON_DISABLED_TEXT);
        renderButton(graphics, cancelRect, "Cancel", mouseX, mouseY, BUTTON_TEXT);
    }

    private String confirmLabel() {
        return switch (mode) {
            case SAVE_AS, NEW_FROM_TEMPLATE -> "Create";
            case RENAME -> "Rename";
            case DUPLICATE -> "Duplicate";
        };
    }

    private void recomputeValidation() {
        var name = nameInput.content().trim();
        if (name.isEmpty()) {
            lastDerivedId = "";
            validationError = null;
            return;
        }
        try {
            LayoutDoc.validateDisplayName(name);
        } catch (IllegalArgumentException e) {
            lastDerivedId = "";
            validationError = e.getMessage();
            return;
        }
        var id = LayoutCatalog.suggestId(name);
        lastDerivedId = id;
        if (!idAvailability.test(id)) {
            validationError = "An existing layout already uses this id (" + id + ").";
        } else {
            validationError = null;
        }
    }

    private boolean canConfirm() {
        return !lastDerivedId.isEmpty() && validationError == null;
    }

    private static void renderButton(GuiGraphics graphics, Rect rect, String label, int mouseX, int mouseY, int textColor) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + 1, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y + rect.h - 1, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x + rect.w - 1, rect.y, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);

        var font = EngineFont.get();
        var textX = rect.x + (rect.w - font.width(label)) / 2;
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        nameInput.mouseClicked(mouseX, mouseY, button);
        if (confirmRect != null && confirmRect.contains(mouseX, mouseY)) {
            if (canConfirm()) {
                fireConfirm();
            }
            return true;
        }
        if (cancelRect != null && cancelRect.contains(mouseX, mouseY)) {
            onCancel.run();
            return true;
        }
        // Outside-clicks are absorbed but no-op so accidental misses don't dismiss the dialog.
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onCancel.run();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (canConfirm()) {
                fireConfirm();
            }
            return true;
        }
        var focused = TextInput.getFocused();
        if (focused != null) {
            return focused.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public boolean charTyped(char ch, int modifiers) {
        var focused = TextInput.getFocused();
        return focused != null && focused.charTyped(ch, modifiers);
    }

    private void fireConfirm() {
        var name = nameInput.content().trim();
        onConfirm.accept(name);
    }

    private record Rect(
        int x,
        int y,
        int w,
        int h
    ) {

        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }
}
