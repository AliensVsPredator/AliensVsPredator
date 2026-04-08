package com.blib.mod.client.render.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;

@ApiStatus.Internal
public final class PathfindingNavDebugHUD {

    public static final PathfindingNavDebugHUD INSTANCE = new PathfindingNavDebugHUD();

    private static final int PADDING = 4;

    private static final int LINE_HEIGHT = 10;

    private static final int BACKGROUND_COLOR = 0xAA000000;

    private static final int HEADER_COLOR = 0xFF55FFFF;

    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private static final int DIM_COLOR = 0xFF888888;

    private static final int CURRENT_HIGHLIGHT_COLOR = 0xFFFFFF00;

    private static final int PHYSICS_COLOR = 0xFFFF88FF;

    private static final int ARROW_VELOCITY_COLOR = 0xFF00FF00;

    private static final int STUCK_WARN_TICKS = 40;

    private static final int STUCK_BAD_TICKS = 100;

    private static final int STUCK_OK_COLOR = 0xFF88FF88;

    private static final int STUCK_WARN_COLOR = 0xFFFFFF00;

    private static final int STUCK_BAD_COLOR = 0xFFFF4444;

    private static final int PATH_WINDOW_RADIUS = 2;

    private static final String COL_LABEL_SAMPLE = "P-2  ";

    private static final String COL_TERRAIN_SAMPLE = "BURROWABLE  ";

    private static final String COL_POSITION_SAMPLE = "(-99999,-99999,-99999)  ";

    private static int colLabelWidth = -1;

    private static int colTerrainWidth;

    private static int colPositionWidth;

    private @Nullable S2CPathfindingNavDebugPayload latestPayload;

    private PathfindingNavDebugHUD() {}

    public void update(S2CPathfindingNavDebugPayload payload) {
        this.latestPayload = payload;
    }

    public void clear() {
        this.latestPayload = null;
    }

    public void render(GuiGraphics graphics, float partialTick) {
        if (latestPayload == null) {
            return;
        }

        var font = Minecraft.getInstance().font;
        var screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        var lines = buildLines(font, latestPayload);
        var maxWidth = 0;

        for (var line : lines) {
            maxWidth = Math.max(maxWidth, measureLine(font, line));
        }

        var totalWidth = maxWidth + PADDING * 2;
        var totalHeight = lines.size() * LINE_HEIGHT + PADDING * 2;
        var x = (screenWidth - totalWidth / 2) - PADDING;
        var y = PADDING;

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(0.5f, 0.5f, 1.0f);

        var scaledX = x * 2;
        var scaledY = y * 2;

        graphics.fill(scaledX, scaledY, scaledX + totalWidth, scaledY + totalHeight, BACKGROUND_COLOR);

        var textY = scaledY + PADDING;

        for (var line : lines) {
            renderLine(graphics, font, line, scaledX + PADDING, textY);
            textY += LINE_HEIGHT;
        }

        pose.popPose();
    }

    private List<HUDSegmentLine> buildLines(Font font, S2CPathfindingNavDebugPayload payload) {
        var lines = new ArrayList<HUDSegmentLine>();

        addHeaderSection(lines, payload);

        lines.add(singleSegment("", TEXT_COLOR));

        addPhysicsSection(lines, payload);
        addPathWindowSection(lines, font, payload);

        return lines;
    }

    private static void addHeaderSection(List<HUDSegmentLine> lines, S2CPathfindingNavDebugPayload payload) {
        lines.add(fmtLine(HEADER_COLOR, "PathNav: %s [%d]", payload.entityName(), payload.entityId()));
        lines.add(fmtLine(TEXT_COLOR, "Pos: (%.2f, %.2f, %.2f)", payload.entityX(), payload.entityY(), payload.entityZ()));

        var statusParts = new ArrayList<String>();
        statusParts.add("navigating=" + payload.navigating());
        statusParts.add("reached=" + payload.reached());
        statusParts.add("blockBreak=" + payload.waitingForBlockBreak());

        lines.add(singleSegment("Status: " + String.join(" | ", statusParts), TEXT_COLOR));
        lines.add(fmtLine(TEXT_COLOR, "Node: %d / %d", payload.currentNodeIndex(), payload.totalNodes()));
        lines.add(fmtLine(TEXT_COLOR, "Dist: cur=%.2f  tgt=%.2f", payload.distanceToCurrentNode(), payload.distanceToTarget()));
        lines.add(
            fmtLine(
                stuckColor(payload.ticksOnCurrentNode()),
                "Stuck: %d ticks  PathAge: %d ticks",
                payload.ticksOnCurrentNode(),
                payload.pathAgeTicks()
            )
        );
    }

    private static void addPhysicsSection(List<HUDSegmentLine> lines, S2CPathfindingNavDebugPayload payload) {
        lines.add(fmtLine(ARROW_VELOCITY_COLOR, "Velocity: (%.4f, %.4f, %.4f)", payload.deltaX(), payload.deltaY(), payload.deltaZ()));
        lines.add(fmtLine(PHYSICS_COLOR, "Wanted:   (%.2f, %.2f, %.2f)", payload.wantedX(), payload.wantedY(), payload.wantedZ()));

        var physicsFlags = new ArrayList<String>();
        physicsFlags.add(payload.onGround() ? "ON_GROUND" : "airborne");
        physicsFlags.add(payload.inWater() ? "IN_WATER" : "dry");

        lines.add(singleSegment("Physics: " + String.join(" | ", physicsFlags), PHYSICS_COLOR));
        lines.add(fmtLine(PHYSICS_COLOR, "Move: op=%s  speed=%.4f", payload.moveOperation(), payload.resolvedSpeed()));
        lines.add(singleSegment("Solid: " + formatSurfaceBitmap(payload.surfaceSolidBitmap()), PHYSICS_COLOR));
    }

    private void addPathWindowSection(List<HUDSegmentLine> lines, Font font, S2CPathfindingNavDebugPayload payload) {
        if (payload.windowNodes().isEmpty()) {
            lines.add(singleSegment("No path nodes.", DIM_COLOR));
            lines.add(singleSegment("", TEXT_COLOR));
            addLegend(lines);
            return;
        }

        ensureColumnWidths(font);

        lines.add(singleSegment("", TEXT_COLOR));
        lines.add(buildTableHeader());

        for (int offset = -PATH_WINDOW_RADIUS; offset <= PATH_WINDOW_RADIUS; offset++) {
            lines.add(buildSlotRow(payload, offset));
        }

        lines.add(singleSegment("", TEXT_COLOR));
        addLegend(lines);
    }

    private static void ensureColumnWidths(Font font) {
        if (colLabelWidth >= 0) {
            return;
        }

        colLabelWidth = font.width(COL_LABEL_SAMPLE);
        colTerrainWidth = font.width(COL_TERRAIN_SAMPLE);
        colPositionWidth = font.width(COL_POSITION_SAMPLE);
    }

    private static HUDSegmentLine buildTableHeader() {
        var segments = new ArrayList<TextSegment>();
        segments.add(new TextSegment("", DIM_COLOR, colLabelWidth));
        segments.add(new TextSegment("Terrain", DIM_COLOR, colTerrainWidth));
        segments.add(new TextSegment("Position", DIM_COLOR, colPositionWidth));

        return new HUDSegmentLine(segments);
    }

    private static HUDSegmentLine buildSlotRow(S2CPathfindingNavDebugPayload payload, int offset) {
        var absoluteIndex = payload.currentNodeIndex() + offset;
        var node = lookupSlotNode(payload, absoluteIndex);
        var isCurrent = offset == 0;
        var labelText = formatSlotLabel(offset, isCurrent);
        var labelColor = isCurrent ? CURRENT_HIGHLIGHT_COLOR : DIM_COLOR;
        var segments = new ArrayList<TextSegment>();
        segments.add(new TextSegment(labelText, labelColor, colLabelWidth));

        if (node == null) {
            segments.add(new TextSegment("---", DIM_COLOR, colTerrainWidth));
            segments.add(new TextSegment("---", DIM_COLOR, colPositionWidth));
            return new HUDSegmentLine(segments);
        }

        var color = isCurrent ? CURRENT_HIGHLIGHT_COLOR : terrainColor(node.terrainType());
        segments.add(new TextSegment(terrainName(node.terrainType()), color, colTerrainWidth));
        segments.add(new TextSegment(positionLabel(node), color, colPositionWidth));

        return new HUDSegmentLine(segments);
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

    private static String formatSlotLabel(int offset, boolean isCurrent) {
        if (isCurrent) {
            return "P*";
        }

        if (offset > 0) {
            return "P+" + offset;
        }

        return "P" + offset;
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

        for (int i = 0; i < directions.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }

            sb.append(directions[i].getName().substring(0, 1).toUpperCase());
            sb.append((bitmap & (1 << i)) != 0 ? '#' : '.');
        }

        sb.append(']');

        return sb.toString();
    }

    private static final int LEGEND_ENTRIES_PER_LINE = 3;

    private static void addLegend(List<HUDSegmentLine> lines) {
        var entries = new ArrayList<LegendEntry>();
        var terrainTypes = TerrainType.values();

        for (int i = 0; i < terrainTypes.length; i++) {
            if (terrainTypes[i] == TerrainType.BURROWABLE) {
                continue;
            }

            entries.add(new LegendEntry(terrainTypes[i].name(), terrainColor(i)));
        }

        entries.add(new LegendEntry("CURRENT", CURRENT_HIGHLIGHT_COLOR));

        for (int start = 0; start < entries.size(); start += LEGEND_ENTRIES_PER_LINE) {
            var segments = new ArrayList<TextSegment>();
            segments.add(new TextSegment(start == 0 ? "Legend: " : "        ", DIM_COLOR));

            var end = Math.min(start + LEGEND_ENTRIES_PER_LINE, entries.size());

            for (int i = start; i < end; i++) {
                if (i > start) {
                    segments.add(new TextSegment("  ", DIM_COLOR));
                }

                var entry = entries.get(i);
                segments.add(new TextSegment("\u2588 ", entry.color));
                segments.add(new TextSegment(entry.label, entry.color));
            }

            lines.add(new HUDSegmentLine(segments));
        }
    }

    private record LegendEntry(
        String label,
        int color
    ) {}

    private static HUDSegmentLine singleSegment(String text, int color) {
        return new HUDSegmentLine(List.of(new TextSegment(text, color)));
    }

    private static HUDSegmentLine fmtLine(int color, String format, Object... args) {
        return singleSegment(format.formatted(args), color);
    }

    private static int measureLine(Font font, HUDSegmentLine line) {
        var width = 0;

        for (var segment : line.segments) {
            width += Math.max(font.width(segment.text), segment.minWidth);
        }

        return width;
    }

    private static void renderLine(GuiGraphics graphics, Font font, HUDSegmentLine line, int x, int y) {
        var currentX = x;

        for (var segment : line.segments) {
            graphics.drawString(font, segment.text, currentX, y, segment.color, false);
            currentX += Math.max(font.width(segment.text), segment.minWidth);
        }
    }

    private record TextSegment(
        String text,
        int color,
        int minWidth
    ) {

        TextSegment(String text, int color) {
            this(text, color, 0);
        }
    }

    private record HUDSegmentLine(List<TextSegment> segments) {}
}
