package com.blib.engine.ui.dialog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.regex.Pattern;

import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.common.capture.BlockCaptureEngine;
import com.blib.internal.common.capture.CaptureMode;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCaptureBlocksPayload;

/**
 * Floating modal for the Capture operation. Replaces the inline capture controls that used to live in the Selection
 * panel — opened from the viewport's right-click context menu when the cursor is over a block-volume selection. Carries
 * the same controls as the old panel (mode toggle, name input, Capture button) but in a self-contained dialog so the
 * workspace doesn't need a dedicated tab for them.
 * <p>
 * Lifecycle mirrors {@link ConfirmDialog}: hosting screen owns the instance, sets it non-null to show, routes mouse +
 * key events through it before any other input, and clears the reference when the dialog dismisses (success / cancel /
 * Esc).
 */
@ApiStatus.Internal
public final class CaptureDialog {

    private static final int DIM_COLOR = 0x80000000;

    private static final int BG_COLOR = 0xFF1F1F26;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int META_COLOR = 0xFF808088;

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

    private static final Pattern CAPTURE_NAME_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");

    private final Runnable onClose;

    private final SegmentedControl modeControl;

    private final TextInput nameInput;

    /**
     * Capture mode the user had set <em>before</em> opening this dialog. Restored on Cancel / Esc so a tentative
     * mode-toggle inside the dialog doesn't leak out and recolor every future block-volume wireframe. Successful
     * Capture intentionally keeps the new mode — the user committed, so their new preference sticks.
     */
    private final CaptureMode initialMode;

    private @Nullable String statusText;

    private boolean statusSuccess;

    private @Nullable Rect modeRect;

    private @Nullable Rect captureRect;

    private @Nullable Rect cancelRect;

    public CaptureDialog(Runnable onClose) {
        this.onClose = onClose;
        this.initialMode = BlockSelection.mode();
        this.modeControl = new SegmentedControl(List.of("General", "Jigsaw"), BlockSelection.mode().ordinal());
        this.nameInput = new TextInput("capture name (lowercase, '_' or '-')");
    }

    public void render(GuiGraphics graphics, int screenWidth, int screenHeight, int mouseX, int mouseY) {
        graphics.fill(0, 0, screenWidth, screenHeight, DIM_COLOR);

        var font = EngineFont.get();
        var contentW = BOX_WIDTH - 2 * BOX_PAD_X;

        // Box height: title + mode row + name row + (jigsaw split row) + button row + paddings + status (optional).
        var jigsaw = BlockSelection.mode() == CaptureMode.JIGSAW;
        var rows = 3 + (jigsaw ? 1 : 0) + (statusText != null ? 1 : 0);
        var boxH = BOX_PAD_Y + font.lineHeight + ROW_GAP + rows * (ROW_HEIGHT + ROW_GAP) + BUTTON_HEIGHT + BOX_PAD_Y;

        var boxX = (screenWidth - BOX_WIDTH) / 2;
        var boxY = (screenHeight - boxH) / 2;

        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + boxH, BG_COLOR);
        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + 1, BORDER_COLOR);
        graphics.fill(boxX, boxY + boxH - 1, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX, boxY, boxX + 1, boxY + boxH, BORDER_COLOR);
        graphics.fill(boxX + BOX_WIDTH - 1, boxY, boxX + BOX_WIDTH, boxY + boxH, BORDER_COLOR);

        // Title
        graphics.drawString(font, Component.literal("Capture Selection"), boxX + BOX_PAD_X, boxY + BOX_PAD_Y, TITLE_COLOR, false);

        var rowY = boxY + BOX_PAD_Y + font.lineHeight + ROW_GAP;

        // Mode row.
        graphics.drawString(font, Component.literal("Mode:"), boxX + BOX_PAD_X, rowY + 1, LABEL_COLOR, false);
        var modeLabelW = font.width("Mode: ");
        var modeX = boxX + BOX_PAD_X + modeLabelW + 2;
        var modeY = rowY + (ROW_HEIGHT - SegmentedControl.HEIGHT) / 2;
        var modeWidth = 96;
        modeControl.setSelectedIndex(BlockSelection.mode().ordinal());
        modeControl.render(graphics, modeX, modeY, modeWidth, mouseX, mouseY);
        modeRect = new Rect(modeX, modeY, modeWidth, SegmentedControl.HEIGHT);
        rowY += ROW_HEIGHT + ROW_GAP;

        // Jigsaw split preview (conditional).
        if (jigsaw) {
            var aabb = BlockSelection.aabb();
            String splitText;
            if (aabb.isPresent()) {
                var box = aabb.get();
                var sx = (int) (box.maxX - box.minX);
                var sy = (int) (box.maxY - box.minY);
                var sz = (int) (box.maxZ - box.minZ);
                var nx = ceilDiv(sx, BlockCaptureEngine.MAX_PIECE_SIZE);
                var ny = ceilDiv(sy, BlockCaptureEngine.MAX_PIECE_SIZE);
                var nz = ceilDiv(sz, BlockCaptureEngine.MAX_PIECE_SIZE);
                var pieces = nx * ny * nz;
                splitText = "Split: " + nx + " × " + ny + " × " + nz + " = " + pieces + " piece" + (pieces == 1 ? "" : "s");
            } else {
                splitText = "Split: (no selection)";
            }
            graphics.drawString(font, Component.literal(splitText), boxX + BOX_PAD_X, rowY + 1, META_COLOR, false);
            rowY += ROW_HEIGHT + ROW_GAP;
        }

        // Name row.
        graphics.drawString(font, Component.literal("Name:"), boxX + BOX_PAD_X, rowY + 1, LABEL_COLOR, false);
        var nameLabelW = font.width("Name: ");
        var nameX = boxX + BOX_PAD_X + nameLabelW + 2;
        var nameW = contentW - nameLabelW - 2;
        nameInput.render(graphics, nameX, rowY + (ROW_HEIGHT - TextInput.HEIGHT) / 2, nameW, mouseX, mouseY);
        rowY += ROW_HEIGHT + ROW_GAP;

        // Status (optional).
        if (statusText != null) {
            var color = statusSuccess ? 0xFF80E080 : 0xFFE06868;
            graphics.drawString(font, Component.literal(statusText), boxX + BOX_PAD_X, rowY + 1, color, false);
            rowY += ROW_HEIGHT + ROW_GAP;
        }

        // Buttons: Capture (right), Cancel (left of Capture).
        var enabled = canCapture();
        var buttonY = boxY + boxH - BOX_PAD_Y - BUTTON_HEIGHT;
        var captureX = boxX + BOX_WIDTH - BOX_PAD_X - BUTTON_WIDTH;
        var cancelX = captureX - BUTTON_GAP - BUTTON_WIDTH;
        captureRect = new Rect(captureX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        cancelRect = new Rect(cancelX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, captureRect, "Capture", mouseX, mouseY, enabled ? BUTTON_TEXT : BUTTON_DISABLED_TEXT);
        renderButton(graphics, cancelRect, "Cancel", mouseX, mouseY, BUTTON_TEXT);
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
        if (modeRect != null && modeRect.contains(mouseX, mouseY)) {
            modeControl.mouseClicked(mouseX, mouseY, button);
            BlockSelection.setMode(CaptureMode.fromOrdinal(modeControl.selectedIndex()));
            statusText = null;
            return true;
        }
        if (captureRect != null && captureRect.contains(mouseX, mouseY)) {
            if (canCapture()) {
                fireCapture();
            }
            return true;
        }
        if (cancelRect != null && cancelRect.contains(mouseX, mouseY)) {
            cancel();
            return true;
        }
        // Outside-clicks are consumed but no-op so accidental misses don't dismiss the dialog.
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cancel();
            return true;
        }
        var focused = TextInput.getFocused();
        if (focused != null) {
            return focused.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    /**
     * Dismiss without committing — restore any tentative mode-toggle done inside the dialog so global capture mode
     * (read by {@link com.blib.engine.render.volume.BlockSelectionWireframeRenderer}) reflects the user's actual
     * preference, not a discarded experiment.
     */
    private void cancel() {
        BlockSelection.setMode(initialMode);
        onClose.run();
    }

    private boolean canCapture() {
        if (ProjectSession.activeProject() == null) {
            return false;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return false;
        }
        var box = aabb.get();
        var volume = (long) (box.maxX - box.minX) * (long) (box.maxY - box.minY) * (long) (box.maxZ - box.minZ);
        if (volume <= 0 || volume > BlockCaptureEngine.MAX_TOTAL_VOLUME) {
            return false;
        }
        var name = nameInput.content();
        if (name == null || name.isEmpty() || !CAPTURE_NAME_PATTERN.matcher(name).matches()) {
            return false;
        }
        return true;
    }

    private void fireCapture() {
        var project = ProjectSession.activeProjectName();
        var name = nameInput.content();
        var a = BlockSelection.cornerA();
        var b = BlockSelection.cornerB();
        var mc = Minecraft.getInstance();
        if (project.isEmpty() || a == null || b == null || mc.player == null) {
            statusText = "Cannot capture (missing project or selection)";
            statusSuccess = false;
            return;
        }
        var dimensionId = mc.player.level().dimension().location();
        BLib.MOD.networking()
            .sendToServer(new C2SCaptureBlocksPayload(project, name, a, b, BlockSelection.mode().ordinal(), dimensionId));
        // Optimistic close — the server will surface success/failure via S2CProjectOpResultPayload, which the listener
        // routes into BlockSelection.pendingCaptureResult; if we want richer feedback we'll iterate later.
        onClose.run();
    }

    private static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
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
