package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.session.ToolMode;

/**
 * Bottom-of-screen status bar — full-bleed (no chrome). Shows engine + gizmo state on the left and the workspace name
 * on the right. Placeholder for what will eventually carry build / save / hint indicators.
 */
@ApiStatus.Internal
public final class StatusBarPanel implements Panel {

    public static final int HEIGHT = 13;

    private static final int BACKGROUND_COLOR = 0xFF1A1A1F;

    private static final int BORDER_COLOR = 0xFF0E0E11;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int VALUE_COLOR = 0xFFB8C0D0;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int EDGE_PADDING = 6;

    @Override
    public String title() {
        return "Status";
    }

    @Override
    public boolean hasChrome() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x, y, x + width, y + 1, BORDER_COLOR);

        var font = Minecraft.getInstance().font;
        // +2 compensates for MC font's descender padding so labels visually center; see MenuBarPanel.
        var textY = y + (height - font.lineHeight + 2) / 2;

        var engineActive = EngineMode.get().isActive();
        var engineLabel = engineActive ? "ENGINE: ON" : "ENGINE: OFF";
        var gizmoLabel = "GIZMO: " + BLibGizmoState.mode().name();

        graphics.drawString(
            font,
            Component.literal(engineLabel),
            x + EDGE_PADDING,
            textY,
            engineActive ? ACCENT_COLOR : LABEL_COLOR,
            false
        );

        var gizmoX = x + EDGE_PADDING + font.width(engineLabel) + 12;
        graphics.drawString(font, Component.literal(gizmoLabel), gizmoX, textY, VALUE_COLOR, false);

        // Tool-mode chip: SELECT (default arrow / context-menu / entity-pick) or PLACE (jigsaw piece on cursor).
        // Currently derived from JigsawPieceSelection — a future explicit tool system will swap this for a stored
        // mode on EngineSession with no caller change. PLACE pops in accent color so the user notices when their
        // click semantics have shifted from "select" to "place".
        var session = EngineMode.get().session();
        var toolMode = session != null ? session.toolMode() : ToolMode.SELECT;
        var modeLabel = "MODE: " + toolMode.name();
        var modeX = gizmoX + font.width(gizmoLabel) + 12;
        var modeColor = toolMode == ToolMode.PLACE ? ACCENT_COLOR : VALUE_COLOR;
        graphics.drawString(font, Component.literal(modeLabel), modeX, textY, modeColor, false);

        var projectName = ProjectSession.activeProjectName();
        var projectLabel = projectName.isEmpty() ? "PROJECT: (none)" : "PROJECT: " + projectName;
        var projectX = modeX + font.width(modeLabel) + 12;
        var projectColor = projectName.isEmpty() ? LABEL_COLOR : ACCENT_COLOR;
        graphics.drawString(font, Component.literal(projectLabel), projectX, textY, projectColor, false);

        var picking = BlockSelection.picking();
        if (picking != BlockSelection.PickingState.NONE) {
            var pickingLabel = "PICKING: " + picking.name();
            var pickingX = projectX + font.width(projectLabel) + 12;
            graphics.drawString(font, Component.literal(pickingLabel), pickingX, textY, ACCENT_COLOR, false);
        }

        // Placement state on the right side: mode + piece id + rotation + mirror + collision count, but only while
        // a piece is selected. Gives the user feedback for the R / M / T / scroll hotkeys (otherwise rotating or
        // mode-switching is invisible until the world preview updates next frame, and even then "is that a 90° or
        // a 180° rotation?" isn't always obvious). Collision count comes from the renderer's per-frame scan via
        // JigsawPlacementFrameState — only shown when the resolver returned a valid placement this frame, so the
        // user doesn't see "coll:0" while the cursor's actually over open sky.
        var selectedId = JigsawPieceSelection.selectedId();
        if (selectedId != null) {
            var rotation = JigsawPieceSelection.rotation();
            var mirror = JigsawPieceSelection.mirror();
            var mode = JigsawTool.activeMode();
            var placement = JigsawPlacementFrameState.placement();
            var placementLabel = "[" + mode.displayName() + "]  " + selectedId.getPath() + "  rot:" + rotationLabel(rotation) + "  mir:"
                + mirror.name();
            if (placement != null) {
                placementLabel += "  coll:" + JigsawPlacementFrameState.collisionCount();
            }
            var truncated = font.plainSubstrByWidth(placementLabel, width / 2);
            var rightX = x + width - EDGE_PADDING - font.width(truncated);
            graphics.drawString(font, Component.literal(truncated), rightX, textY, ACCENT_COLOR, false);
        }
    }

    private static String rotationLabel(net.minecraft.world.level.block.Rotation rotation) {
        return switch (rotation) {
            case NONE -> "0";
            case CLOCKWISE_90 -> "90";
            case CLOCKWISE_180 -> "180";
            case COUNTERCLOCKWISE_90 -> "270";
        };
    }
}
