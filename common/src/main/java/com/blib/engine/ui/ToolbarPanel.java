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
 * Editor toolbar — sits below the menu bar, full-bleed. Left edge: the Inspect / Marquee selection toggle. Right edge:
 * Reload Project (applies project-level edits to the live registry). Play / pause / step transport controls used to
 * live here at the horizontal center, but they were moved onto {@link ViewportPanel}'s top-left corner since they only
 * matter in the viewport's context — see {@link ViewportTransportToolbar}.
 */
@ApiStatus.Internal
public final class ToolbarPanel implements Panel {

    public static final int HEIGHT = 22;

    private static final int BACKGROUND_COLOR = 0xFF26262C;

    private static final int BORDER_COLOR = 0xFF101013;

    private static final int BUTTON_HEIGHT = 14;

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

    private int buttonY;

    private int reloadButtonX;

    /** When > 0, drives the Reload button's "Reloading…" → "✓ Reloaded" → idle state machine. Set on Reload click. */
    private long lastReloadAttemptMs;

    /**
     * Inspect / Marquee selection-tool toggle. Sits left-of-center on the toolbar, mirroring the right-aligned Reload
     * button. Selection state is mirrored from {@link SelectionToolState} at the start of each frame and written back
     * on click, so hotkeys (Q / V) and the toolbar always reflect the same active tool.
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
        this.buttonY = y + (height - BUTTON_HEIGHT) / 2;

        // ---- Selection-tool toggle (left edge) ----
        // Sync from SelectionToolState first so a Q / V hotkey press flips the visible segment, then render. The
        // segmented control's HEIGHT matches BUTTON_HEIGHT so we share buttonY for vertical centering.
        selectionToolControl.setSelectedIndex(SelectionToolState.current().ordinal());
        selectionToolControl.render(graphics, x + TOOL_EDGE_PADDING, buttonY, TOOL_CONTROL_WIDTH, mouseX, mouseY);

        // ---- Reload Project button (right edge) ----
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
        // reload-button hit test if it ever grows to overlap.
        if (selectionToolControl.mouseClicked(mouseX, mouseY, button)) {
            SelectionToolState.set(SelectionTool.values()[selectionToolControl.selectedIndex()]);
            return true;
        }

        if (mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT) {
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

}
