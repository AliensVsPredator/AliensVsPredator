package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * Modal yes/no confirmation overlay shared by destructive actions across the engine UI (project delete from the
 * workspace FILE menu and from the picker's per-card Delete button). Dim background, centered box with a title +
 * message and two buttons; the confirm button can be flagged {@code destructive} to render its label in the red-error
 * color so users notice they're about to do something irreversible.
 * <p>
 * Like {@link DropdownMenu}, this is a passive renderable + hit-tester — the hosting screen owns the lifecycle: sets a
 * non-null instance to show it, calls {@link #render} during its render pass, routes mouse + key events through it
 * before any other input handling, and clears its reference back to {@code null} on either button click or Esc.
 * Outside-clicks intentionally do nothing — destructive confirmations should require an explicit decision.
 */
@ApiStatus.Internal
public final class ConfirmDialog {

    private static final int DIM_COLOR = 0x80000000;

    private static final int BG_COLOR = 0xFF1F1F26;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int MESSAGE_COLOR = 0xFFD0D0D0;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DESTRUCTIVE_TEXT = 0xFFE06868;

    private static final int BOX_WIDTH = 280;

    private static final int BOX_PAD_X = 16;

    private static final int BOX_PAD_Y = 12;

    private static final int TITLE_GAP = 8;

    private static final int MESSAGE_GAP = 12;

    private static final int BUTTON_HEIGHT = 14;

    private static final int BUTTON_WIDTH = 80;

    private static final int BUTTON_GAP = 8;

    private final String title;

    private final String message;

    private final String confirmLabel;

    private final String cancelLabel;

    private final boolean destructive;

    private final Runnable onConfirm;

    private final Runnable onCancel;

    /** Cached during render so click handlers can hit-test without recomputing the centered layout. */
    private @Nullable Rect confirmRect;

    private @Nullable Rect cancelRect;

    public ConfirmDialog(
        String title,
        String message,
        String confirmLabel,
        String cancelLabel,
        boolean destructive,
        Runnable onConfirm,
        Runnable onCancel
    ) {
        this.title = title;
        this.message = message;
        this.confirmLabel = confirmLabel;
        this.cancelLabel = cancelLabel;
        this.destructive = destructive;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    /**
     * Render the dim overlay + centered box. {@code screenWidth}/{@code screenHeight} are in whatever coord system the
     * host renders in — the workspace passes its logical pixels (post-{@code SCALE} divide), the picker passes raw
     * screen pixels.
     */
    public void render(GuiGraphics graphics, int screenWidth, int screenHeight, int mouseX, int mouseY) {
        graphics.fill(0, 0, screenWidth, screenHeight, DIM_COLOR);

        var font = EngineFont.get();
        var contentW = BOX_WIDTH - 2 * BOX_PAD_X;
        var lines = font.split(Component.literal(message), contentW);
        var messageH = Math.max(1, lines.size()) * font.lineHeight;
        var boxH = BOX_PAD_Y + font.lineHeight + TITLE_GAP + messageH + MESSAGE_GAP + BUTTON_HEIGHT + BOX_PAD_Y;

        var boxX = (screenWidth - BOX_WIDTH) / 2;
        var boxY = (screenHeight - boxH) / 2;

        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + boxH, BG_COLOR);
        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + 1, BORDER_COLOR);
        graphics.fill(boxX, boxY + boxH - 1, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX, boxY, boxX + 1, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX + BOX_WIDTH - 1, boxY, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);

        graphics.drawString(font, Component.literal(title), boxX + BOX_PAD_X, boxY + BOX_PAD_Y, TITLE_COLOR, false);

        var msgY = boxY + BOX_PAD_Y + font.lineHeight + TITLE_GAP;
        for (var line : lines) {
            graphics.drawString(font, line, boxX + BOX_PAD_X, msgY, MESSAGE_COLOR, false);
            msgY += font.lineHeight;
        }

        var buttonY = boxY + boxH - BOX_PAD_Y - BUTTON_HEIGHT;
        var confirmX = boxX + BOX_WIDTH - BOX_PAD_X - BUTTON_WIDTH;
        var cancelX = confirmX - BUTTON_GAP - BUTTON_WIDTH;
        confirmRect = new Rect(confirmX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancelRect = new Rect(cancelX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, confirmRect, confirmLabel, mouseX, mouseY, destructive ? BUTTON_DESTRUCTIVE_TEXT : BUTTON_TEXT);
        renderButton(graphics, cancelRect, cancelLabel, mouseX, mouseY, BUTTON_TEXT);
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
        // +2 compensates for MC font's descender padding so labels visually center; same convention as MenuBarPanel.
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    /**
     * Returns true if the click landed on a button (and the matching handler ran). False otherwise — host should
     * <em>not</em> dismiss the dialog on a false return; outside-clicks are a no-op so destructive actions require an
     * explicit decision.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (confirmRect != null && confirmRect.contains(mouseX, mouseY)) {
            onConfirm.run();
            return true;
        }
        if (cancelRect != null && cancelRect.contains(mouseX, mouseY)) {
            onCancel.run();
            return true;
        }
        return false;
    }

    /** Returns true if Esc was pressed (cancel handler runs). Other keys ignored. */
    public boolean keyPressed(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onCancel.run();
            return true;
        }
        return false;
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
