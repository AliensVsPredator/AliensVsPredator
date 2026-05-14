package com.blib.engine.ui.panel.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.mod.client.render.debug.PathfindingDebugState;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;

@ApiStatus.Internal
public final class PathfindingDebugPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int CONTENT_PADDING = 6;

    private static final int LINE_HEIGHT = 10;

    private static final int HEADER_COLOR = 0xFF55FFFF;

    private static final int SECTION_COLOR = 0xFFB8C0D0;

    private static final int TEXT_COLOR = 0xFFE0E0E0;

    private static final int DIM_COLOR = 0xFF888892;

    private static final int TARGET_NODE_COLOR = 0xFFFFD900;

    private static final int CURRENT_NODE_COLOR = 0xFF00FFFF;

    private static final int NEXT_NODE_COLOR = 0xFFFF4DFF;

    private static final int PREVIOUS_NODE_COLOR = 0xFFFF8000;

    private static final int NO_HIGHLIGHT = -1;

    private static final int PHYSICS_COLOR = 0xFFFF88FF;

    private static final int VELOCITY_COLOR = 0xFF00FF00;

    private static final int TIMING_COLOR = 0xFF00FF88;

    private static final int STUCK_WARN_TICKS = 40;

    private static final int STUCK_BAD_TICKS = 100;

    private static final int STUCK_OK_COLOR = 0xFF88FF88;

    private static final int STUCK_WARN_COLOR = 0xFFFFFF00;

    private static final int STUCK_BAD_COLOR = 0xFFFF4444;

    private static final int PATH_WINDOW_RADIUS = 2;

    private final ScrollViewport scroll = new ScrollViewport();

    private @Nullable Component hoveredTooltip;

    @Override
    public String title() {
        return "Pathfinding";
    }

    @Override
    public Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        hoveredTooltip = null;

        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        var font = EngineFont.get();
        var rows = buildRows(PathfindingDebugState.INSTANCE.latestPayload());
        var contentWidth = Math.max(0, maxRowWidth(font, rows) + CONTENT_PADDING * 2);
        var contentHeight = rows.size() * LINE_HEIGHT + CONTENT_PADDING * 2;
        var frame = scroll.begin(graphics, UiRect.of(x, y, width, height), contentWidth, contentHeight);
        var rowY = frame.contentY() + CONTENT_PADDING;
        var cursorInContent = frame.visibleContentRect().contains(mouseX, mouseY);

        for (var row : rows) {
            var rowX = frame.contentX() + CONTENT_PADDING + row.indent();
            graphics.drawString(font, row.text(), rowX, rowY, row.color(), false);
            if (
                cursorInContent
                    && mouseY >= rowY
                    && mouseY < rowY + LINE_HEIGHT
                    && !row.text().isEmpty()
                    && font.width(row.text()) > frame.visibleContentRect().width()
            ) {
                hoveredTooltip = Component.literal(row.text());
            }
            rowY += LINE_HEIGHT;
        }

        scroll.end(graphics, mouseX, mouseY);
    }

    private static int maxRowWidth(Font font, List<Row> rows) {
        var max = 0;
        for (var row : rows) {
            max = Math.max(max, row.indent() + font.width(row.text()));
        }
        return max;
    }

    private static List<Row> buildRows(@Nullable S2CPathfindingNavDebugPayload payload) {
        if (payload == null) {
            return List.of(
                new Row(0, "No pathfinding data.", DIM_COLOR),
                new Row(0, "Track a GOAP agent to stream its BLib pathfinding state.", DIM_COLOR)
            );
        }

        var rows = new ArrayList<Row>();
        rows.add(new Row(0, "PathNav: " + payload.entityName() + " [" + payload.entityId() + "]", HEADER_COLOR));
        rows.add(rowf(TEXT_COLOR, "Pos: (%.2f, %.2f, %.2f)", payload.entityX(), payload.entityY(), payload.entityZ()));
        rows.add(new Row(0, "Status: " + status(payload), TEXT_COLOR));
        rows.add(rowf(TEXT_COLOR, "Node: %d / %d", payload.currentNodeIndex(), payload.totalNodes()));
        rows.add(rowf(TEXT_COLOR, "Dist: cur=%.2f  tgt=%.2f", payload.distanceToCurrentNode(), payload.distanceToTarget()));
        rows.add(
            rowf(
                stuckColor(payload.ticksOnCurrentNode()),
                "Stuck: %d ticks  PathAge: %d ticks",
                payload.ticksOnCurrentNode(),
                payload.pathAgeTicks()
            )
        );
        rows.add(emptyRow());

        addTimingRows(rows);
        rows.add(emptyRow());

        rows.add(new Row(0, "Movement", SECTION_COLOR));
        rows.add(rowf(8, VELOCITY_COLOR, "Velocity: (%.4f, %.4f, %.4f)", payload.deltaX(), payload.deltaY(), payload.deltaZ()));
        rows.add(rowf(8, PHYSICS_COLOR, "Wanted:   (%.2f, %.2f, %.2f)", payload.wantedX(), payload.wantedY(), payload.wantedZ()));
        rows.add(new Row(8, "Physics: " + (payload.onGround() ? "ON_GROUND" : "airborne") + " | " + (payload.inWater() ? "IN_WATER" : "dry"), PHYSICS_COLOR));
        rows.add(rowf(8, PHYSICS_COLOR, "Move: op=%s  speed=%.4f", payload.moveOperation(), payload.resolvedSpeed()));
        rows.add(new Row(8, "Solid: " + formatSurfaceBitmap(payload.surfaceSolidBitmap()), PHYSICS_COLOR));
        rows.add(emptyRow());

        addPathRows(rows, payload);
        rows.add(emptyRow());
        addLegendRows(rows);

        return rows;
    }

    private static String status(S2CPathfindingNavDebugPayload payload) {
        return "navigating=" + payload.navigating()
            + " | reached=" + payload.reached()
            + " | blockBreak=" + payload.waitingForBlockBreak();
    }

    private static void addTimingRows(List<Row> rows) {
        var timings = PathfindingDebugState.INSTANCE.timingHistoryNanos();
        if (timings.isEmpty()) {
            rows.add(new Row(0, "Pathfind Timing: no data", DIM_COLOR));
            return;
        }

        rows.add(new Row(0, "Pathfind Timing (last " + timings.size() + ")", TIMING_COLOR));
        long totalNanos = 0L;
        for (var i = 0; i < timings.size(); i++) {
            var nanos = timings.get(i);
            totalNanos += nanos;
            rows.add(rowf(8, TEXT_COLOR, "#%d: %.3f ms", i + 1, nanos / 1_000_000.0));
        }
        rows.add(rowf(8, TIMING_COLOR, "Avg: %.3f ms", (totalNanos / (double) timings.size()) / 1_000_000.0));
    }

    private static void addPathRows(List<Row> rows, S2CPathfindingNavDebugPayload payload) {
        rows.add(new Row(0, "Path Window", SECTION_COLOR));
        if (payload.windowNodes().isEmpty()) {
            rows.add(new Row(8, "No path nodes.", DIM_COLOR));
            return;
        }

        rows.add(new Row(8, "Slot  Terrain      Position", DIM_COLOR));
        for (var offset = -PATH_WINDOW_RADIUS; offset <= PATH_WINDOW_RADIUS; offset++) {
            rows.add(buildSlotRow(payload, offset));
        }
    }

    private static Row buildSlotRow(S2CPathfindingNavDebugPayload payload, int offset) {
        var absoluteIndex = payload.currentNodeIndex() + offset;
        var node = lookupSlotNode(payload, absoluteIndex);
        var label = formatSlotLabel(offset, offset == 0);
        var highlight = resolveSlotHighlight(payload, absoluteIndex, offset);

        if (node == null) {
            return new Row(8, "%-5s %-12s %s".formatted(label, "---", "---"), DIM_COLOR);
        }

        var color = highlight != NO_HIGHLIGHT ? highlight : terrainColor(node.terrainType());
        return new Row(8, "%-5s %-12s %s".formatted(label, terrainName(node.terrainType()), positionLabel(node)), color);
    }

    private static @Nullable DebugNodeEntry lookupSlotNode(S2CPathfindingNavDebugPayload payload, int absoluteIndex) {
        if (absoluteIndex < 0 || absoluteIndex >= payload.totalNodes()) {
            return null;
        }

        var localIndex = absoluteIndex - payload.windowStartIndex();
        var windowNodes = payload.windowNodes();

        if (localIndex < 0 || localIndex >= windowNodes.size()) {
            return null;
        }

        return windowNodes.get(localIndex);
    }

    private static int resolveSlotHighlight(S2CPathfindingNavDebugPayload payload, int absoluteIndex, int offset) {
        if (payload.totalNodes() > 0 && absoluteIndex == payload.totalNodes() - 1) {
            return TARGET_NODE_COLOR;
        }
        if (offset == 0) {
            return CURRENT_NODE_COLOR;
        }
        if (offset == 1) {
            return NEXT_NODE_COLOR;
        }
        if (offset == -1) {
            return PREVIOUS_NODE_COLOR;
        }
        return NO_HIGHLIGHT;
    }

    private static void addLegendRows(List<Row> rows) {
        rows.add(new Row(0, "Legend", SECTION_COLOR));
        for (var i = 0; i < TerrainType.values().length; i++) {
            rows.add(new Row(8, terrainName(i), terrainColor(i)));
        }
        rows.add(new Row(8, "TARGET", TARGET_NODE_COLOR));
        rows.add(new Row(8, "CURRENT", CURRENT_NODE_COLOR));
        rows.add(new Row(8, "NEXT", NEXT_NODE_COLOR));
        rows.add(new Row(8, "PREV", PREVIOUS_NODE_COLOR));
    }

    private static String formatSlotLabel(int offset, boolean current) {
        if (current) {
            return "P*";
        }
        return offset > 0 ? "P+" + offset : "P" + offset;
    }

    private static String positionLabel(DebugNodeEntry node) {
        return "(%d,%d,%d)".formatted(node.x(), node.y(), node.z());
    }

    private static String terrainName(int terrainTypeOrdinal) {
        var values = TerrainType.values();
        if (terrainTypeOrdinal < 0 || terrainTypeOrdinal >= values.length) {
            return "???";
        }
        return values[terrainTypeOrdinal].name();
    }

    private static int terrainColor(int terrainTypeOrdinal) {
        var values = TerrainType.values();
        if (terrainTypeOrdinal < 0 || terrainTypeOrdinal >= values.length) {
            return TEXT_COLOR;
        }
        return switch (values[terrainTypeOrdinal]) {
            case GROUND -> 0xFF00FF00;
            case WATER -> 0xFF0080FF;
            case AIR -> 0xFFCCCCFF;
            case BREAKABLE -> 0xFFFF0000;
            case BURROWABLE -> 0xFF994D00;
        };
    }

    private static int stuckColor(int ticksOnCurrentNode) {
        if (ticksOnCurrentNode >= STUCK_BAD_TICKS) {
            return STUCK_BAD_COLOR;
        }
        if (ticksOnCurrentNode >= STUCK_WARN_TICKS) {
            return STUCK_WARN_COLOR;
        }
        return STUCK_OK_COLOR;
    }

    private static String formatSurfaceBitmap(int bitmap) {
        var directions = Direction.values();
        var sb = new StringBuilder("[");

        for (var i = 0; i < directions.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(directions[i].getName().substring(0, 1).toUpperCase());
            sb.append((bitmap & (1 << i)) != 0 ? '#' : '.');
        }

        return sb.append(']').toString();
    }

    private static Row rowf(int color, String format, Object... args) {
        return rowf(0, color, format, args);
    }

    private static Row rowf(int indent, int color, String format, Object... args) {
        return new Row(indent, format.formatted(args), color);
    }

    private static Row emptyRow() {
        return new Row(0, "", DIM_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private record Row(
        int indent,
        String text,
        int color
    ) {}
}
