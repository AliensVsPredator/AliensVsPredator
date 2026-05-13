package com.blib.engine.ui.workspace;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.workspace.dock.DividerHit;
import com.blib.engine.ui.workspace.dock.DockTreeHitTest;

/**
 * Stateless render helpers for the workspace's two hover-driven overlays — the divider-highlight stripe and the panel
 * tooltip box. Lives outside {@code EngineWorkspaceScreen} so adding a third hover overlay doesn't require expanding
 * the screen's per-frame render pipeline.
 */
@ApiStatus.Internal
public final class HoverOverlayRenderer {

    private static final int DIVIDER_HIGHLIGHT_COLOR = 0xFF4F8FFF;

    private static final int TOOLTIP_BG_COLOR = 0xF01A1A1F;

    private static final int TOOLTIP_BORDER_COLOR = 0xFF353540;

    private static final int TOOLTIP_TEXT_COLOR = 0xFFD0D0D0;

    /**
     * Maximum tooltip body width before {@link Font#split} wraps. Picked so multi-sentence help text breaks across 3-4
     * lines at typical workspace logical-pixel sizes — wide enough to avoid awkward 1-2 word lines, narrow enough that
     * the tooltip doesn't span half the screen.
     */
    private static final int TOOLTIP_MAX_WIDTH = 240;

    private HoverOverlayRenderer() {}

    /**
     * Light up the docking divider under the cursor (or the actively-dragged one). Caller passes the panel-mouse
     * coordinates — substituting {@code OFFSCREEN_MOUSE} when the cursor is over a modal/menu/popup — so divider
     * highlight stays suppressed under those overlays without an explicit check here.
     *
     * @param dragActive whether a divider drag is currently in progress; the renderer uses {@code activeDragger} if so,
     *                   otherwise probes the tree at {@code (mouseX, mouseY)}.
     */
    public static void renderHoveredDivider(
        GuiGraphics graphics,
        DockNode root,
        int logicalWidth,
        int logicalHeight,
        int mouseX,
        int mouseY,
        int dividerHitPx,
        boolean dragActive,
        DividerHit activeDragger
    ) {
        var dragger = dragActive
            ? activeDragger
            : DockTreeHitTest.findDivider(root, 0, 0, logicalWidth, logicalHeight, mouseX, mouseY, dividerHitPx);
        if (dragger == null) {
            return;
        }

        var bx = dragger.boundaryStartX();
        var by = dragger.boundaryStartY();
        // Highlight stripe is intentionally a fixed 4-pixel band straddling the boundary line, regardless of the
        // hit-zone width.
        if (dragger.split().orientation() == Orientation.HORIZONTAL) {
            graphics.fill(bx - 2, by, bx + 2, by + dragger.parentHeight(), DIVIDER_HIGHLIGHT_COLOR);
        } else {
            graphics.fill(bx, by - 2, bx + dragger.parentWidth(), by + 2, DIVIDER_HIGHLIGHT_COLOR);
        }
    }

    /**
     * Ask the panel under the cursor for tooltip text and, if any, draw it as a small floating box near the cursor.
     * Suppression for menus / popups / drag-state is the caller's responsibility — pass {@code false} for
     * {@code tooltipsAllowed} to skip rendering.
     */
    public static void renderHoverTooltip(
        GuiGraphics graphics,
        DockNode root,
        int logicalWidth,
        int logicalHeight,
        int mouseX,
        int mouseY,
        boolean tooltipsAllowed
    ) {
        if (!tooltipsAllowed) {
            return;
        }
        var leaf = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth, logicalHeight, mouseX, mouseY);
        if (leaf == null) {
            return;
        }
        var tip = leaf.tooltipText();
        if (tip == null) {
            return;
        }
        drawTooltipBox(graphics, tip, mouseX, mouseY, logicalWidth, logicalHeight);
    }

    /**
     * Draw a tooltip box for {@code text} positioned next to the cursor, kept inside the workspace bounds. Wraps long
     * text via {@link Font#split} so multi-sentence help text renders as multiple lines instead of overflowing past the
     * right edge. Manual rendering (rather than {@code GuiGraphics.renderTooltip}) so the styling matches the
     * workspace's flat dark theme and so we control sizing in workspace-logical pixels.
     */
    private static void drawTooltipBox(
        GuiGraphics graphics,
        Component text,
        int mouseX,
        int mouseY,
        int logicalWidth,
        int logicalHeight
    ) {
        var font = EngineFont.get();
        var paddingX = 3;
        var paddingY = 2;
        var lineHeight = font.lineHeight;

        var lines = font.split(text, TOOLTIP_MAX_WIDTH - 2 * paddingX);
        if (lines.isEmpty()) {
            return;
        }
        var textWidth = 0;
        for (var line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        var boxW = textWidth + paddingX * 2;
        var boxH = lines.size() * lineHeight + paddingY * 2;

        var tipX = mouseX + 8;
        var tipY = mouseY + 8;
        if (tipX + boxW > logicalWidth) {
            tipX = mouseX - 4 - boxW;
        }
        if (tipY + boxH > logicalHeight) {
            tipY = mouseY - 4 - boxH;
        }
        graphics.fill(tipX, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BG_COLOR);
        graphics.fill(tipX, tipY, tipX + boxW, tipY + 1, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY + boxH - 1, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY, tipX + 1, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX + boxW - 1, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);

        // Top-anchored layout: first line at tipY + paddingY (+1 for descender padding so the glyph sits visually
        // centered on its baseline), subsequent lines stacked by lineHeight.
        var lineY = tipY + paddingY + 1;
        for (var line : lines) {
            graphics.drawString(font, line, tipX + paddingX, lineY, TOOLTIP_TEXT_COLOR, false);
            lineY += lineHeight;
        }
    }
}
