package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.selection.EntitySelectable;
import com.blib.engine.selection.Selectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;

/**
 * Right-side universal inspector. Reads {@link SelectionManager#current} each frame and dispatches to a per-type view;
 * with nothing selected, falls back to a tool-state view that surfaces the gizmo target, the active jigsaw piece, and
 * the placement-mode + collision-policy controls.
 * <p>
 * Phase 7 MVP — has the dispatch skeleton and two views (entity, none-selected/tool-state). Other selectable types
 * route through the entity-style "type + name + position" fallback for now; a richer JigsawPieceInspectorView lands
 * when world-side jigsaw selection (clicking a placed piece) becomes a feature.
 */
@ApiStatus.Internal
public final class DetailsPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF18181C;

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int HEADER_BAR_BG_COLOR = 0xFF1F1F26;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int VALUE_COLOR = 0xFFD8D8E0;

    private static final int HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int CONTENT_PADDING = 5;

    private static final int LINE_HEIGHT = 10;

    private static final int LABEL_COLUMN_WIDTH = 70;

    private static final int SECTION_HEADER_HEIGHT = 11;

    private static final int HEADER_BAR_HEIGHT = 14;

    @Override
    public String title() {
        return "Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var rowY = y;

        var selection = SelectionManager.current();
        var single = selection.single();

        rowY = drawHeaderBar(graphics, font, x, rowY, width, single);

        if (single == null) {
            renderToolStateView(graphics, font, x, rowY, width);
        } else {
            switch (single.type()) {
                case ENTITY -> renderEntityView(graphics, font, x, rowY, width, (EntitySelectable) single);
                default -> renderGenericView(graphics, font, x, rowY, width, single);
            }
        }
    }

    /**
     * Top-of-panel bar showing what's currently selected (or "Tool State" when nothing is). Visually distinct from the
     * section headers below so the user always knows which selectable they're inspecting.
     */
    private static int drawHeaderBar(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selection) {
        graphics.fill(x, y, x + width, y + HEADER_BAR_HEIGHT, HEADER_BAR_BG_COLOR);

        var label = selection == null ? "Tool State" : selection.displayName().getString();
        var typeLabel = selection == null ? "" : "  •  " + selection.type().name();
        var truncated = font.plainSubstrByWidth(label + typeLabel, width - 2 * CONTENT_PADDING);
        // +2 compensates for MC font's descender padding so the label visually centers; see MenuBarPanel.
        graphics.drawString(
            font,
            Component.literal(truncated),
            x + CONTENT_PADDING,
            y + (HEADER_BAR_HEIGHT - font.lineHeight + 2) / 2,
            ACCENT_COLOR,
            false
        );
        return y + HEADER_BAR_HEIGHT;
    }

    /**
     * Default view when nothing is selected: shows the tool's current state across engine + gizmo + jigsaw placement
     * subsystems. Mirrors the spec's "show something useful when nothing is selected — global tool settings or a hint
     * message" requirement (§3.3).
     */
    private static void renderToolStateView(GuiGraphics graphics, Font font, int x, int y, int width) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Engine");
        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, "Mode", EngineMode.get().isActive() ? "ON" : "OFF");

        var gizmoSnapshot = BLibGizmoState.lastRender();
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Gizmo");
        rowY += CONTENT_PADDING / 2;
        if (gizmoSnapshot == null) {
            rowY = drawNote(graphics, font, x, rowY, "(no tunable target)");
        } else {
            rowY = drawRow(graphics, font, x, rowY, "Item", gizmoSnapshot.itemId().toString());
            rowY = drawRow(graphics, font, x, rowY, "Mode", gizmoSnapshot.mode().name());
            rowY = drawRow(graphics, font, x, rowY, "Context", gizmoSnapshot.displayContext().name());
        }

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Placement");
        rowY += CONTENT_PADDING / 2;
        var pieceId = JigsawPieceSelection.selectedId();
        if (pieceId == null) {
            rowY = drawNote(graphics, font, x, rowY, "(no piece selected)");
        } else {
            rowY = drawRow(graphics, font, x, rowY, "Piece", pieceId.getPath());
            rowY = drawRow(graphics, font, x, rowY, "Mode", JigsawTool.activeMode().displayName());
            rowY = drawRow(graphics, font, x, rowY, "Rotation", JigsawPieceSelection.rotation().name());
            rowY = drawRow(graphics, font, x, rowY, "Mirror", JigsawPieceSelection.mirror().name());
            rowY = drawRow(graphics, font, x, rowY, "Policy", JigsawPlacementOptions.collisionPolicy().name());
            rowY = drawRow(graphics, font, x, rowY, "Grid", String.valueOf(JigsawPlacementOptions.gridSize()));
            if (JigsawPlacementFrameState.placement() != null) {
                rowY = drawRow(graphics, font, x, rowY, "Collisions", String.valueOf(JigsawPlacementFrameState.collisionCount()));
            }
        }
    }

    /**
     * Entity-specific view. Pulls the live entity off the {@link EntitySelectable} and surfaces a few useful fields
     * (type, position, health for living entities). The selectable handles staleness — if the entity has unloaded since
     * selection, {@code entity()} returns {@code null} and we degrade gracefully.
     */
    private static void renderEntityView(GuiGraphics graphics, Font font, int x, int y, int width, EntitySelectable selectable) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Entity");
        rowY += CONTENT_PADDING / 2;

        var entity = selectable.entity();
        if (entity == null) {
            rowY = drawNote(graphics, font, x, rowY, "(unloaded)");
            return;
        }

        rowY = drawRow(graphics, font, x, rowY, "Type", entity.getType().getDescriptionId());
        rowY = drawRow(graphics, font, x, rowY, "UUID", entity.getStringUUID().substring(0, 8));
        var pos = entity.position();
        rowY = drawRow(graphics, font, x, rowY, "X", String.format("%.2f", pos.x));
        rowY = drawRow(graphics, font, x, rowY, "Y", String.format("%.2f", pos.y));
        rowY = drawRow(graphics, font, x, rowY, "Z", String.format("%.2f", pos.z));
        rowY = drawRow(graphics, font, x, rowY, "Health", String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth()));
    }

    /**
     * Fallback for selectable types that don't have a dedicated view yet. Shows the selectable's display name and its
     * world-bounds center so the user at least sees that something is selected and where it is.
     */
    private static void renderGenericView(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selectable) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, selectable.type().name());
        rowY += CONTENT_PADDING / 2;
        var pivot = selectable.pivot();
        if (pivot != null) {
            rowY = drawRow(graphics, font, x, rowY, "X", String.format("%.2f", pivot.x));
            rowY = drawRow(graphics, font, x, rowY, "Y", String.format("%.2f", pivot.y));
            rowY = drawRow(graphics, font, x, rowY, "Z", String.format("%.2f", pivot.z));
        }
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        graphics.drawString(
            font,
            Component.literal(label),
            x + CONTENT_PADDING,
            // +2 compensates for MC font's descender padding so section headers visually center; see MenuBarPanel.
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            HEADER_TEXT_COLOR,
            false
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static int drawRow(GuiGraphics graphics, Font font, int x, int y, String label, String value) {
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(value), x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, y, VALUE_COLOR, false);
        return y + LINE_HEIGHT;
    }

    private static int drawNote(GuiGraphics graphics, Font font, int x, int y, String text) {
        graphics.drawString(font, Component.literal(text), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        return y + LINE_HEIGHT;
    }
}
