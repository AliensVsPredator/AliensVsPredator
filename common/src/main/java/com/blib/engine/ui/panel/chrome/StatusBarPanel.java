package com.blib.engine.ui.panel.chrome;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.input.ActiveKeybindings;
import com.blib.engine.input.Keybinding;
import com.blib.engine.input.Keybindings;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.runtime.tool.ActiveTool;
import com.blib.engine.runtime.tool.ToolStateMachine;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.workspace.ViewportSelectionDelete;

/**
 * Bottom-of-screen status bar — full-bleed (no chrome). Two regions:
 * <ul>
 * <li><b>Left:</b> context-sensitive control hints, pulled from {@link Keybindings}. Cascade: paint mode active →
 * jigsaw piece held → selection-based → default viewport navigation. Truncates with "…" if the workspace is too narrow
 * to fit every hint before the state region.</li>
 * <li><b>Right:</b> state indicators — PROJECT name, LAYOUT name, tool MODE, conditional PICKING state, conditional
 * placement-detail readout (piece id + rotation + mirror + collision count when a piece is held). Packed left-to-right,
 * right-aligned as a group.</li>
 * </ul>
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

    /** Gap between adjacent state segments on the right side. */
    private static final int STATE_SEGMENT_GAP = 12;

    /** Gap between the hint region and the leftmost state segment. */
    private static final int HINT_STATE_GAP = 16;

    /** Separator drawn between adjacent hints. */
    private static final String HINT_SEPARATOR = " · ";

    /** Suffix drawn when hints had to be dropped to fit. */
    private static final String HINT_ELLIPSIS = " …";

    @Override
    public String title() {
        return "Status";
    }

    @Override
    public boolean hasChrome() {
        return false;
    }

    @Override
    public boolean isTrim() {
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x, y, x + width, y + 1, BORDER_COLOR);

        var font = EngineFont.get();
        // +2 compensates for MC font's descender padding so labels visually center; see MenuBarPanel.
        var textY = y + (height - font.lineHeight + 2) / 2;

        // Right region first — its left edge determines how much room hints can claim.
        var stateSegments = collectStateSegments(font, width);
        var stateWidth = totalSegmentWidth(font, stateSegments);
        var stateLeftX = stateSegments.isEmpty() ? (x + width - EDGE_PADDING) : (x + width - EDGE_PADDING - stateWidth);
        renderStateSegments(graphics, font, stateLeftX, textY, stateSegments);

        var hintRightLimit = stateSegments.isEmpty() ? (x + width - EDGE_PADDING) : (stateLeftX - HINT_STATE_GAP);
        renderHints(graphics, font, x + EDGE_PADDING, textY, hintRightLimit, currentHints());
    }

    // ============================== State region ==============================

    /**
     * One right-side segment, split into a label (always rendered in {@link #ACCENT_COLOR}, including its trailing
     * {@code ": "}) and a value (rendered in {@link #valueColor}). Splitting at the colon keeps the visual pattern
     * consistent across the whole status bar — the user can scan labels at a glance because they're all the same color.
     * {@code label} may be empty for segments that are pure value with no prefix (e.g. the placement readout).
     */
    private record StateSegment(
        String label,
        String value,
        int valueColor
    ) {}

    private List<StateSegment> collectStateSegments(Font font, int width) {
        var segments = new ArrayList<StateSegment>();

        var projectName = ProjectSession.activeProjectName();
        var projectValue = projectName.isEmpty() ? "(none)" : projectName;
        // Subdued color when no project is loaded so the "(none)" placeholder is visually quieter than a real name.
        var projectValueColor = projectName.isEmpty() ? LABEL_COLOR : VALUE_COLOR;
        segments.add(new StateSegment("PROJECT: ", projectValue, projectValueColor));

        var layoutId = EngineWorkspaceScreen.activeLayoutId();
        var layoutDoc = LayoutCatalog.get(layoutId);
        var layoutValue = layoutDoc != null ? layoutDoc.displayName() : layoutId;
        segments.add(new StateSegment("LAYOUT: ", layoutValue, VALUE_COLOR));

        var session = EngineMode.get().session();
        // Tool readout uses {@link ActiveTool} (the canonical state machine), not the old derived SELECT/PLACE view.
        // Outside an engine session we have no state machine to ask, so fall back to the default.
        var tool = session != null ? ToolStateMachine.get().active() : ActiveTool.SELECT;
        segments.add(new StateSegment("MODE: ", tool.name(), VALUE_COLOR));

        var picking = BlockSelection.picking();
        if (picking != BlockSelection.PickingState.NONE) {
            segments.add(new StateSegment("PICKING: ", picking.name(), VALUE_COLOR));
        }

        // Placement state on the right: mode + piece id + rotation + mirror + collision count, but only while a piece
        // is selected. Gives the user feedback for the R / M / T / scroll hotkeys (otherwise rotating or mode-switching
        // is invisible until the world preview updates next frame, and even then "is that a 90° or a 180° rotation?"
        // isn't always obvious). Collision count comes from JigsawPlacementFrameState — only shown when the resolver
        // returned a valid placement this frame, so the user doesn't see "coll:0" while the cursor's over open sky.
        // Rendered as a pure value (no "FOO: " label) because the composite has its own internal labels.
        var selectedId = JigsawPieceSelection.selectedId();
        if (selectedId != null) {
            var rotation = JigsawPieceSelection.rotation();
            var mirror = JigsawPieceSelection.mirror();
            var mode = JigsawTool.activeMode();
            var placement = JigsawPlacementFrameState.placement();
            var placementValue = "[" + mode.displayName() + "]  " + selectedId.getPath() + "  rot:" + rotationLabel(rotation)
                + "  mir:" + mirror.name();
            if (placement != null) {
                placementValue += "  coll:" + JigsawPlacementFrameState.collisionCount();
            }
            var truncated = font.plainSubstrByWidth(placementValue, width / 2);
            segments.add(new StateSegment("", truncated, VALUE_COLOR));
        }

        return segments;
    }

    private int totalSegmentWidth(Font font, List<StateSegment> segments) {
        if (segments.isEmpty()) {
            return 0;
        }
        var total = 0;
        for (var i = 0; i < segments.size(); i++) {
            if (i > 0) {
                total += STATE_SEGMENT_GAP;
            }
            var seg = segments.get(i);
            total += font.width(seg.label()) + font.width(seg.value());
        }
        return total;
    }

    private void renderStateSegments(GuiGraphics graphics, Font font, int leftX, int textY, List<StateSegment> segments) {
        var x = leftX;
        for (var i = 0; i < segments.size(); i++) {
            if (i > 0) {
                x += STATE_SEGMENT_GAP;
            }
            var seg = segments.get(i);
            if (!seg.label().isEmpty()) {
                graphics.drawString(font, Component.literal(seg.label()), x, textY, ACCENT_COLOR, false);
                x += font.width(seg.label());
            }
            graphics.drawString(font, Component.literal(seg.value()), x, textY, seg.valueColor(), false);
            x += font.width(seg.value());
        }
    }

    // ============================== Hint region ==============================

    /**
     * Pick the hint set for the current world state. First match wins — most-specific contexts (paint mode, piece held)
     * dominate over selection-based hints, which in turn dominate over default viewport navigation.
     */
    private List<Keybinding> currentHints() {
        var hints = new ArrayList<Keybinding>();

        if (ClaimPaintTool.isActive()) {
            hints.add(ActiveKeybindings.resolve(Keybindings.PAINT_CLAIM));
            hints.add(ActiveKeybindings.resolve(Keybindings.PAINT_UNCLAIM));
            hints.add(ActiveKeybindings.resolve(Keybindings.CANCEL.withLabel("Exit paint")));
            return hints;
        }

        if (JigsawPieceSelection.hasSelection()) {
            hints.add(ActiveKeybindings.resolve(Keybindings.JIGSAW_PLACE));
            hints.add(ActiveKeybindings.resolve(Keybindings.JIGSAW_ROTATE));
            hints.add(ActiveKeybindings.resolve(Keybindings.JIGSAW_MIRROR));
            var modeLabel = "Cycle mode (" + JigsawTool.activeMode().displayName() + ")";
            hints.add(ActiveKeybindings.resolve(Keybindings.JIGSAW_CYCLE_MODE.withLabel(modeLabel)));
            hints.add(ActiveKeybindings.resolve(Keybindings.UNDO));
            return hints;
        }

        var single = SelectionManager.current().single();
        if (single instanceof EntitySelectable) {
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_TRANSLATE));
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_SCALE));
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_SNAP_INT));
            hints.add(ActiveKeybindings.resolve(Keybindings.COPY));
            addDeleteHintIfAvailable(hints, single);
            return hints;
        }
        if (single instanceof BlockVolumeSelectable) {
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_TRANSLATE));
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_SCALE));
            hints.add(ActiveKeybindings.resolve(Keybindings.GIZMO_MOVE_BLOCKS));
            hints.add(ActiveKeybindings.resolve(Keybindings.COPY));
            hints.add(ActiveKeybindings.resolve(Keybindings.CUT));
            hints.add(ActiveKeybindings.resolve(Keybindings.PASTE));
            addDeleteHintIfAvailable(hints, single);
            hints.add(ActiveKeybindings.resolve(Keybindings.CANCEL.withLabel("Clear")));
            return hints;
        }
        if (single instanceof BlockSelectable || single instanceof PlacedJigsawPieceSelectable) {
            defaultViewportHints(hints);
            addDeleteHintIfAvailable(hints, single);
            return hints;
        }
        if (single instanceof FactionSelectable) {
            // No faction-specific hotkeys today — Paint Claims is a button on the Inspector, not a keybind. Show the
            // default viewport set so the user still has navigation cues.
            return defaultViewportHints(hints);
        }

        return defaultViewportHints(hints);
    }

    private static void addDeleteHintIfAvailable(ArrayList<Keybinding> hints, Selectable selectable) {
        if (ViewportSelectionDelete.canDelete(selectable)) {
            hints.add(ActiveKeybindings.resolve(Keybindings.DELETE));
        }
    }

    private List<Keybinding> defaultViewportHints(ArrayList<Keybinding> hints) {
        hints.add(ActiveKeybindings.resolve(Keybindings.VIEWPORT_SELECT));
        hints.add(ActiveKeybindings.resolve(Keybindings.VIEWPORT_CONTEXT));
        hints.add(ActiveKeybindings.resolve(Keybindings.VIEWPORT_ORBIT));
        hints.add(ActiveKeybindings.resolve(Keybindings.VIEWPORT_PAN));
        hints.add(ActiveKeybindings.resolve(Keybindings.VIEWPORT_ZOOM));
        return hints;
    }

    /**
     * Pack hints from {@code leftX} rightward, separator-delimited. Each hint renders as {@code {KEY}:{label}} in two
     * colors — key in {@link #ACCENT_COLOR}, label in {@link #VALUE_COLOR}, separator + colon in {@link #LABEL_COLOR}.
     * If the next hint wouldn't fit (after reserving room for "…" while more hints remain), draw "…" and stop.
     */
    private void renderHints(GuiGraphics graphics, Font font, int leftX, int textY, int rightLimit, List<Keybinding> hints) {
        if (hints.isEmpty() || leftX >= rightLimit) {
            return;
        }

        var x = leftX;
        var sepWidth = font.width(HINT_SEPARATOR);
        var ellipsisWidth = font.width(HINT_ELLIPSIS);

        for (var i = 0; i < hints.size(); i++) {
            var h = hints.get(i);
            var keyStr = h.formatInput();
            var label = h.label();
            var keyWidth = font.width(keyStr);
            var colonWidth = font.width(": ");
            var labelWidth = font.width(label);
            var prefixWidth = (i == 0) ? 0 : sepWidth;
            var hasMore = i < hints.size() - 1;
            var reserved = hasMore ? ellipsisWidth : 0;

            if (x + prefixWidth + keyWidth + colonWidth + labelWidth + reserved > rightLimit) {
                graphics.drawString(font, Component.literal(HINT_ELLIPSIS), x, textY, LABEL_COLOR, false);
                return;
            }

            if (i > 0) {
                graphics.drawString(font, Component.literal(HINT_SEPARATOR), x, textY, LABEL_COLOR, false);
                x += sepWidth;
            }
            graphics.drawString(font, Component.literal(keyStr), x, textY, ACCENT_COLOR, false);
            x += keyWidth;
            graphics.drawString(font, Component.literal(": "), x, textY, LABEL_COLOR, false);
            x += colonWidth;
            graphics.drawString(font, Component.literal(label), x, textY, VALUE_COLOR, false);
            x += labelWidth;
        }
    }

    private static String rotationLabel(Rotation rotation) {
        return switch (rotation) {
            case NONE -> "0";
            case CLOCKWISE_90 -> "90";
            case CLOCKWISE_180 -> "180";
            case COUNTERCLOCKWISE_90 -> "270";
        };
    }
}
