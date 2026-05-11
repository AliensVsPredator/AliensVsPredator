package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.engine.session.ProjectSession;
import com.blib.engine.session.SelectionTool;
import com.blib.engine.session.SelectionToolState;
import com.blib.engine.tag.TagStagingCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;

/**
 * Editor toolbar — sits below the menu bar, full-bleed. Houses the play / pause button and a step-forward button at the
 * horizontal center (Unity-style), with a state label to their right, and a right-aligned Reload Project button that
 * applies project-level edits (tags, pools, etc.) to the live registry. Future builds will add tool-mode buttons (move
 * / rotate / scale) and view-mode controls along this bar.
 */
@ApiStatus.Internal
public final class ToolbarPanel implements Panel {

    public static final int HEIGHT = 22;

    private static final int BACKGROUND_COLOR = 0xFF26262C;

    private static final int BORDER_COLOR = 0xFF101013;

    private static final int BUTTON_BG_COLOR = 0xFF1A1A1F;

    private static final int BUTTON_HOVER_BG_COLOR = 0xFF353540;

    private static final int BUTTON_PAUSED_BG_COLOR = 0xFF463A1A;

    private static final int BUTTON_DISABLED_ICON_COLOR = 0xFF606068;

    private static final int LABEL_COLOR = 0xFF909098;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int ICON_COLOR = 0xFFE0E0E0;

    private static final int BUTTON_WIDTH = 22;

    private static final int BUTTON_HEIGHT = 14;

    /** Pixels between the play/pause button and the step button (a tighter gap signals they're a control group). */
    private static final int INTRA_GROUP_GAP = 2;

    /** Pixels between the button cluster and the state label to its right. */
    private static final int LABEL_GAP = 6;

    /** Step button advances the integrated server by one game tick per click. */
    private static final int STEP_TICKS = 1;

    private static final int RELOAD_BUTTON_WIDTH = 64;

    private static final int RELOAD_EDGE_PADDING = 6;

    /** Per-segment width of the Inspect / Marquee tool control. Tuned to fit "Marquee" comfortably without crowding. */
    private static final int TOOL_SEGMENT_WIDTH = 50;

    private static final int TOOL_CONTROL_WIDTH = TOOL_SEGMENT_WIDTH * 2;

    private static final int TOOL_EDGE_PADDING = 6;

    private static final int RELOAD_BG_COLOR = 0xFF1A1A1F;

    private static final int RELOAD_BG_HOVER_COLOR = 0xFF353540;

    private static final int RELOAD_BORDER_COLOR = 0xFF353540;

    private static final int RELOAD_BORDER_HOVER_COLOR = 0xFF4F8FFF;

    private static final int RELOAD_TEXT_COLOR = 0xFFD0D0D0;

    private static final int RELOAD_DISABLED_TEXT_COLOR = 0xFF606068;

    private static final int RELOAD_RELOADED_TEXT_COLOR = 0xFF80E080;

    /** Brief "Reloading…" feedback window after a Reload click before flipping to "✓ Reloaded". */
    private static final long RELOADING_FEEDBACK_MS = 200L;

    /** Total feedback window — after this elapses, the button returns to idle "Reload". */
    private static final long RELOADED_FEEDBACK_MS = 2200L;

    private int playButtonX;

    private int stepButtonX;

    private int buttonY;

    private int reloadButtonX;

    /** When > 0, drives the Reload button's "Reloading…" → "✓ Reloaded" → idle state machine. Set on Reload click. */
    private long lastReloadAttemptMs;

    /**
     * Inspect / Marquee selection-tool toggle. Sits left-of-center on the toolbar, mirroring the right-aligned Reload
     * button. Selection state is mirrored from {@link SelectionToolState} at the start of each frame and written back on
     * click, so hotkeys (Q / V) and the toolbar always reflect the same active tool.
     */
    private final SegmentedControl selectionToolControl = new SegmentedControl(
        List.of("Inspect", "Marquee"),
        SelectionToolState.current().ordinal()
    );

    @Override
    public String title() {
        return "Toolbar";
    }

    @Override
    public boolean hasChrome() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);

        var font = EngineFont.get();
        var paused = EngineTickControl.isPaused();
        var stateLabel = paused ? "Paused" : "Playing";
        var stateColor = paused ? ACCENT_COLOR : LABEL_COLOR;

        var labelWidth = font.width(stateLabel);
        // Layout: [play/pause] (2px) [step] (6px) [label]. Center the entire group horizontally.
        var groupWidth = BUTTON_WIDTH + INTRA_GROUP_GAP + BUTTON_WIDTH + LABEL_GAP + labelWidth;

        this.playButtonX = x + (width - groupWidth) / 2;
        this.stepButtonX = playButtonX + BUTTON_WIDTH + INTRA_GROUP_GAP;
        this.buttonY = y + (height - BUTTON_HEIGHT) / 2;

        // ---- Selection-tool toggle ----
        // Sync from SelectionToolState first so a Q / V hotkey press flips the visible segment, then render. The
        // segmented control's HEIGHT matches BUTTON_HEIGHT so we share buttonY for vertical centering.
        selectionToolControl.setSelectedIndex(SelectionToolState.current().ordinal());
        selectionToolControl.render(graphics, x + TOOL_EDGE_PADDING, buttonY, TOOL_CONTROL_WIDTH, mouseX, mouseY);

        // ---- Play / pause button ----
        var playHovered = mouseX >= playButtonX
            && mouseX < playButtonX + BUTTON_WIDTH
            && mouseY >= buttonY
            && mouseY < buttonY + BUTTON_HEIGHT;

        var playBg = playHovered ? BUTTON_HOVER_BG_COLOR : (paused ? BUTTON_PAUSED_BG_COLOR : BUTTON_BG_COLOR);
        graphics.fill(playButtonX, buttonY, playButtonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, playBg);

        if (paused) {
            drawPlayIcon(graphics, playButtonX, buttonY, ICON_COLOR);
        } else {
            drawPauseIcon(graphics, playButtonX, buttonY);
        }

        // ---- Step button ----
        // Step is only meaningful when the world is paused (the underlying API only advances a frozen game). When
        // playing, the icon dims to indicate the button is inert; we let it stay clickable as a no-op rather than
        // change layout based on state.
        var stepHovered = mouseX >= stepButtonX
            && mouseX < stepButtonX + BUTTON_WIDTH
            && mouseY >= buttonY
            && mouseY < buttonY + BUTTON_HEIGHT;
        var stepIconColor = paused ? ICON_COLOR : BUTTON_DISABLED_ICON_COLOR;
        var stepBg = stepHovered && paused ? BUTTON_HOVER_BG_COLOR : BUTTON_BG_COLOR;
        graphics.fill(stepButtonX, buttonY, stepButtonX + BUTTON_WIDTH, buttonY + BUTTON_HEIGHT, stepBg);
        drawStepIcon(graphics, stepButtonX, buttonY, stepIconColor);

        // ---- State label ----
        // +2 compensates for MC font's descender padding so the state label visually centers; see MenuBarPanel.
        var labelY = y + (height - font.lineHeight + 2) / 2;
        graphics.drawString(
            font,
            Component.literal(stateLabel),
            stepButtonX + BUTTON_WIDTH + LABEL_GAP,
            labelY,
            stateColor,
            false
        );

        // ---- Reload Project button ----
        this.reloadButtonX = x + width - RELOAD_EDGE_PADDING - RELOAD_BUTTON_WIDTH;
        renderReloadButton(graphics, font, reloadButtonX, buttonY, mouseX, mouseY);
    }

    private void renderReloadButton(GuiGraphics graphics, net.minecraft.client.gui.Font font, int x, int y, int mouseX, int mouseY) {
        var enabled = ProjectSession.activeProject() != null;
        var hovered = enabled
            && mouseX >= x
            && mouseX < x + RELOAD_BUTTON_WIDTH
            && mouseY >= y
            && mouseY < y + BUTTON_HEIGHT;

        var bg = hovered ? RELOAD_BG_HOVER_COLOR : RELOAD_BG_COLOR;
        var border = hovered ? RELOAD_BORDER_HOVER_COLOR : RELOAD_BORDER_COLOR;
        graphics.fill(x, y, x + RELOAD_BUTTON_WIDTH, y + BUTTON_HEIGHT, bg);
        graphics.fill(x, y, x + RELOAD_BUTTON_WIDTH, y + 1, border);
        graphics.fill(x, y + BUTTON_HEIGHT - 1, x + RELOAD_BUTTON_WIDTH, y + BUTTON_HEIGHT, border);
        graphics.fill(x, y, x + 1, y + BUTTON_HEIGHT, border);
        graphics.fill(x + RELOAD_BUTTON_WIDTH - 1, y, x + RELOAD_BUTTON_WIDTH, y + BUTTON_HEIGHT, border);

        var label = reloadButtonLabel();
        var color = !enabled
            ? RELOAD_DISABLED_TEXT_COLOR
            : (label.startsWith("✓") ? RELOAD_RELOADED_TEXT_COLOR : RELOAD_TEXT_COLOR);
        var labelWidth = font.width(label);
        var textX = x + (RELOAD_BUTTON_WIDTH - labelWidth) / 2;
        // +2 compensates for MC font's descender padding so the label visually centers; see MenuBarPanel.
        var textY = y + (BUTTON_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, color, false);
    }

    private String reloadButtonLabel() {
        if (lastReloadAttemptMs <= 0) {
            return "Reload";
        }
        var elapsed = System.currentTimeMillis() - lastReloadAttemptMs;
        if (elapsed < RELOADING_FEEDBACK_MS) {
            return "Reloading…";
        }
        if (elapsed < RELOADED_FEEDBACK_MS) {
            return "✓ Reloaded";
        }
        return "Reload";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        // Selection-tool toggle takes first crack so its click rect (left edge of the toolbar) isn't shadowed by the
        // play / step / reload hit tests if those ever grow to overlap.
        if (selectionToolControl.mouseClicked(mouseX, mouseY, button)) {
            SelectionToolState.set(SelectionTool.values()[selectionToolControl.selectedIndex()]);
            return true;
        }

        if (mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
            if (mouseX >= playButtonX && mouseX < playButtonX + BUTTON_WIDTH) {
                EngineTickControl.toggle();
                return true;
            }
            if (mouseX >= stepButtonX && mouseX < stepButtonX + BUTTON_WIDTH) {
                // Step is a no-op when not paused (server's stepGameIfPaused only does anything while frozen), so we
                // can fire unconditionally and let the underlying API gate it.
                EngineTickControl.step(STEP_TICKS);
                return true;
            }
            if (
                mouseX >= reloadButtonX
                    && mouseX < reloadButtonX + RELOAD_BUTTON_WIDTH
                    && ProjectSession.activeProject() != null
            ) {
                BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(ProjectSession.activeProjectName()));
                // After reload the runtime registry catches up to disk, so the tag-staging overlay is no longer
                // needed — wipe it. The red staging tint now disappears from every tag row in the workspace,
                // settling into green / blue based on the now-committed project-ownership state. If reload fails
                // the loss is acceptable: staging would otherwise grow unbounded across sessions.
                TagStagingCache.clear();
                lastReloadAttemptMs = System.currentTimeMillis();
                return true;
            }
        }

        return false;
    }

    /** Right-pointing triangle drawn via tapered horizontal rows, centered in the button rect. */
    private static void drawPlayIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var startX = btnX + (BUTTON_WIDTH - triangleWidth) / 2;
        var startY = btnY + (BUTTON_HEIGHT - rows) / 2;

        for (var row = 0; row < rows; row++) {
            var w = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + w, startY + row + 1, color);
        }
    }

    /** Two vertical bars centered in the button rect. */
    private static void drawPauseIcon(GuiGraphics graphics, int btnX, int btnY) {
        var cx = btnX + BUTTON_WIDTH / 2;
        var cy = btnY + BUTTON_HEIGHT / 2;
        var barWidth = 2;
        var barHalfHeight = 4;
        var gap = 2;

        graphics.fill(cx - gap - barWidth, cy - barHalfHeight, cx - gap, cy + barHalfHeight, ICON_COLOR);
        graphics.fill(cx + gap, cy - barHalfHeight, cx + gap + barWidth, cy + barHalfHeight, ICON_COLOR);
    }

    /**
     * "Step forward" glyph: a right-pointing triangle followed by a thin vertical bar — the standard skip-to-end /
     * step-forward iconography seen in media players and game-engine debug toolbars. Drawn with the supplied color so
     * the icon can dim to indicate the button is inert when the world isn't paused.
     */
    private static void drawStepIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var barWidth = 1;
        var bartriangleGap = 1;
        // Horizontally center the (triangle + gap + bar) cluster as a whole.
        var clusterWidth = triangleWidth + bartriangleGap + barWidth;
        var startX = btnX + (BUTTON_WIDTH - clusterWidth) / 2;
        var startY = btnY + (BUTTON_HEIGHT - rows) / 2;

        // Triangle (same shape as play icon, just left-aligned within the cluster).
        for (var row = 0; row < rows; row++) {
            var w = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + w, startY + row + 1, color);
        }
        // Vertical bar to the right of the triangle.
        var barX = startX + triangleWidth + bartriangleGap;
        graphics.fill(barX, startY, barX + barWidth, startY + rows, color);
    }
}
