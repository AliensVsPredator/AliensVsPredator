package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

import com.blib.engine.selection.FactionSelectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SRemoveChunkClaimPayload;

/**
 * Top-down 2D chunk-grid editor. Each cell represents one chunk; cell color follows the same rules as the world overlay
 * (inspected faction = solid + bright, other = dim, contested = striped). LMB on a cell claims for the inspected
 * faction; RMB unclaims. MMB or drag-on-empty pans the view; scroll zooms (adjusts {@code cellSize}).
 * <p>
 * The current player chunk gets a small "+" marker. Without an inspected faction selected, claim/unclaim clicks no-op
 * (the user gets a hint in the header instead of a silent failure).
 */
@ApiStatus.Internal
public final class TerritoryMapPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1F1F26;

    private static final int LEGEND_BG_COLOR = 0xFF1A1A22;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int TEXT_MUTED_COLOR = 0xFF808088;

    private static final int CELL_UNCLAIMED_BG = 0xFF14141A;

    private static final int CELL_GRID_BORDER = 0xFF202028;

    private static final int CELL_HOVER_BORDER = 0xFFE6C26B;

    private static final int CELL_OWN_ALPHA = 0xC0; // out of 255

    private static final int CELL_OTHER_ALPHA = 0x60;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int PLAYER_MARKER_COLOR = 0xFFFFFFFF;

    private static final int CONTENT_PADDING = 5;

    private static final int HEADER_HEIGHT = 14;

    private static final int LEGEND_HEIGHT = 12;

    private static final int MIN_CELL_SIZE = 4;

    private static final int MAX_CELL_SIZE = 24;

    private static final int DEFAULT_CELL_SIZE = 10;

    /** View origin — the chunk centered in the map area. Floats so MMB pan accumulates smoothly. */
    private double viewCenterX;

    private double viewCenterZ;

    private boolean viewInitialized;

    private int cellSize = DEFAULT_CELL_SIZE;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Map drawing area rect (panel minus header + legend), captured each render for click hit-tests. */
    private int mapX;

    private int mapY;

    private int mapW;

    private int mapH;

    private @Nullable Rect paintButtonRect;

    private @Nullable Rect centerButtonRect;

    /** Pan-drag state: middle-mouse drag (or LMB-drag when no faction is inspected) pans the view. */
    private boolean panning;

    private double panAnchorMouseX;

    private double panAnchorMouseY;

    private double panAnchorViewX;

    private double panAnchorViewZ;

    /** Per-drag claim dedup set, keyed by {@link ChunkPos#toLong}. */
    private final Set<Long> paintedThisDrag = new HashSet<>();

    private @Nullable Integer paintButton;

    @Override
    public String title() {
        return "Territory Map";
    }

    @Override
    public void onShown() {
        centerOnPlayer();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;

        if (!viewInitialized) {
            centerOnPlayer();
            viewInitialized = true;
        }

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var inspected = inspectedFactionId();
        var inspectedEntry = inspected == null ? null : ClientFactionDirectoryCache.get(inspected);
        var chunkCount = inspected == null ? 0 : ClientTerritoryCache.INSTANCE.chunkCountForFaction(inspected);

        // --- Header ---
        graphics.fill(x, y, x + width, y + HEADER_HEIGHT, HEADER_BG_COLOR);
        var headerTextY = y + (HEADER_HEIGHT - font.lineHeight + 2) / 2;
        var headerLabel = inspectedEntry != null
            ? inspectedEntry.name() + "  (" + chunkCount + " chunks)"
            : "(no faction selected)";
        var truncated = font.plainSubstrByWidth(headerLabel, Math.max(0, width - 2 * CONTENT_PADDING - 100));
        graphics.drawString(
            font,
            Component.literal(truncated),
            x + CONTENT_PADDING,
            headerTextY,
            inspectedEntry != null ? TEXT_COLOR : TEXT_MUTED_COLOR,
            false
        );

        var centerLabel = "Center";
        var centerW = font.width(centerLabel) + 8;
        var centerBtnX = x + width - CONTENT_PADDING - centerW;
        var centerBtnY = y + 1;
        centerButtonRect = new Rect(centerBtnX, centerBtnY, centerW, HEADER_HEIGHT - 2);
        drawButton(graphics, font, centerButtonRect, centerLabel, mouseX, mouseY, false);

        var paintLabel = (ClaimPaintTool.isActive() && inspected != null && inspected.equals(ClaimPaintTool.paintTarget()))
            ? "Stop Paint"
            : "Paint Viewport";
        var paintW = font.width(paintLabel) + 8;
        var paintBtnX = centerBtnX - CONTENT_PADDING - paintW;
        paintButtonRect = new Rect(paintBtnX, centerBtnY, paintW, HEADER_HEIGHT - 2);
        var paintActive = ClaimPaintTool.isActive() && inspected != null && inspected.equals(ClaimPaintTool.paintTarget());
        drawButton(graphics, font, paintButtonRect, paintLabel, mouseX, mouseY, paintActive);

        // --- Map area ---
        mapX = x;
        mapY = y + HEADER_HEIGHT;
        mapW = width;
        mapH = Math.max(0, height - HEADER_HEIGHT - LEGEND_HEIGHT);

        if (mapH > 0) {
            renderMap(graphics, font, mouseX, mouseY, inspected);
        }

        // --- Legend ---
        var legendY = y + height - LEGEND_HEIGHT;
        graphics.fill(x, legendY, x + width, y + height, LEGEND_BG_COLOR);
        renderLegend(graphics, font, x + CONTENT_PADDING, legendY, inspectedEntry == null ? 0 : (inspectedEntry.color() | 0xFF000000));
    }

    private void renderMap(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        int mouseX,
        int mouseY,
        @Nullable ResourceLocation inspected
    ) {
        applyRawScissor(graphics, mapX, mapY, mapW, mapH);
        try {
            // Compute the top-left chunk in view coords. viewCenter is the chunk that lands at the pixel center.
            var halfCellsX = (mapW / (double) cellSize) / 2.0;
            var halfCellsY = (mapH / (double) cellSize) / 2.0;
            var topLeftChunkX = viewCenterX - halfCellsX;
            var topLeftChunkZ = viewCenterZ - halfCellsY;
            var firstX = (int) Math.floor(topLeftChunkX);
            var firstZ = (int) Math.floor(topLeftChunkZ);
            var pixelOffsetX = (int) Math.round((firstX - topLeftChunkX) * cellSize);
            var pixelOffsetY = (int) Math.round((firstZ - topLeftChunkZ) * cellSize);
            var visibleCellsX = (mapW + cellSize) / cellSize + 1;
            var visibleCellsY = (mapH + cellSize) / cellSize + 1;
            var lastX = firstX + visibleCellsX;
            var lastZ = firstZ + visibleCellsY;

            // Grid lines — drawn once per row/column as long strips spanning the full map area, instead of two per-
            // cell border fills × every visible cell. At 30×30 visible chunks this is ~60 fills vs ~1800 before.
            if (cellSize >= 8) {
                for (var col = 0; col <= visibleCellsX; col++) {
                    var lineX = mapX + pixelOffsetX + col * cellSize;
                    if (lineX >= mapX && lineX < mapX + mapW) {
                        graphics.fill(lineX, mapY, lineX + 1, mapY + mapH, CELL_GRID_BORDER);
                    }
                }
                for (var row = 0; row <= visibleCellsY; row++) {
                    var lineY = mapY + pixelOffsetY + row * cellSize;
                    if (lineY >= mapY && lineY < mapY + mapH) {
                        graphics.fill(mapX, lineY, mapX + mapW, lineY + 1, CELL_GRID_BORDER);
                    }
                }
            }

            // Iterate ONLY claimed chunks in the visible viewport — previously this loop walked every cell on the
            // grid (~900 at default zoom), each emitting 2-4 fills regardless of claim state. Now it touches at
            // most the claim count, and unclaimed cells inherit the panel background for free.
            for (var entry : ClientTerritoryCache.INSTANCE.factionsByChunk().entrySet()) {
                var pos = entry.getKey();
                if (pos.x < firstX || pos.x >= lastX || pos.z < firstZ || pos.z >= lastZ) {
                    continue;
                }
                var ids = entry.getValue();
                if (ids.isEmpty()) {
                    continue;
                }
                var pxX = mapX + pixelOffsetX + (pos.x - firstX) * cellSize;
                var pxY = mapY + pixelOffsetY + (pos.z - firstZ) * cellSize;
                var isOwn = inspected != null && ids.contains(inspected);
                var contested = ids.size() > 1;
                if (contested && !isOwn) {
                    renderContestedCell(graphics, pxX, pxY, ids);
                } else {
                    var srcId = isOwn ? inspected : ids.get(0);
                    var argb = colorForFaction(srcId, isOwn ? CELL_OWN_ALPHA : CELL_OTHER_ALPHA);
                    graphics.fill(pxX, pxY, pxX + cellSize, pxY + cellSize, argb);
                    if (contested) {
                        renderContestedStripeOverlay(graphics, pxX, pxY, ids, inspected);
                    }
                }
            }

            // Hover lookup — direct computation via chunkAt; no full-grid loop needed.
            var hoveredChunk = chunkAt(mouseX, mouseY);

            // Player marker — a small "+" at the player's current chunk so the user has a "you are here" anchor.
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                var pcx = mc.player.chunkPosition().x;
                var pcz = mc.player.chunkPosition().z;
                var ppxX = mapX + pixelOffsetX + (pcx - firstX) * cellSize + cellSize / 2;
                var ppxY = mapY + pixelOffsetY + (pcz - firstZ) * cellSize + cellSize / 2;
                if (ppxX >= mapX && ppxX < mapX + mapW && ppxY >= mapY && ppxY < mapY + mapH) {
                    graphics.fill(ppxX - 3, ppxY, ppxX + 3, ppxY + 1, PLAYER_MARKER_COLOR);
                    graphics.fill(ppxX, ppxY - 3, ppxX + 1, ppxY + 3, PLAYER_MARKER_COLOR);
                }
            }

            // Hover highlight on top.
            if (hoveredChunk != null) {
                var hxX = mapX + pixelOffsetX + (hoveredChunk.x - firstX) * cellSize;
                var hxY = mapY + pixelOffsetY + (hoveredChunk.z - firstZ) * cellSize;
                graphics.fill(hxX, hxY, hxX + cellSize, hxY + 1, CELL_HOVER_BORDER);
                graphics.fill(hxX, hxY + cellSize - 1, hxX + cellSize, hxY + cellSize, CELL_HOVER_BORDER);
                graphics.fill(hxX, hxY, hxX + 1, hxY + cellSize, CELL_HOVER_BORDER);
                graphics.fill(hxX + cellSize - 1, hxY, hxX + cellSize, hxY + cellSize, CELL_HOVER_BORDER);
            }

            // Compass markers at the edges — short letters in muted text so they don't compete with claim colors.
            graphics.drawString(font, Component.literal("N"), mapX + mapW / 2 - 2, mapY + 1, TEXT_MUTED_COLOR, false);
            graphics.drawString(
                font,
                Component.literal("S"),
                mapX + mapW / 2 - 2,
                mapY + mapH - font.lineHeight - 1,
                TEXT_MUTED_COLOR,
                false
            );
            graphics.drawString(font, Component.literal("W"), mapX + 2, mapY + mapH / 2 - 4, TEXT_MUTED_COLOR, false);
            graphics.drawString(font, Component.literal("E"), mapX + mapW - 6, mapY + mapH / 2 - 4, TEXT_MUTED_COLOR, false);
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }
    }

    /**
     * Single cell drawn as alternating chevron stripes between the first two claimants — only used when the inspected
     * faction isn't a claimant.
     */
    private void renderContestedCell(GuiGraphics graphics, int pxX, int pxY, java.util.List<ResourceLocation> ids) {
        // Two half-cell fills instead of one per row — same visual, ~10× fewer fills per contested cell.
        var colorA = colorForFaction(ids.get(0), CELL_OTHER_ALPHA);
        var colorB = colorForFaction(ids.get(1), CELL_OTHER_ALPHA);
        var halfY = pxY + cellSize / 2;
        graphics.fill(pxX, pxY, pxX + cellSize, halfY, colorA);
        graphics.fill(pxX, halfY, pxX + cellSize, pxY + cellSize, colorB);
    }

    /**
     * Stripe overlay for contested cells where the inspected faction IS one of the claimants — keeps own-color visible.
     */
    private void renderContestedStripeOverlay(
        GuiGraphics graphics,
        int pxX,
        int pxY,
        java.util.List<ResourceLocation> ids,
        @Nullable ResourceLocation inspected
    ) {
        // One bottom-half band in the other faction's color — the per-row stripe pattern the earlier version used
        // dwarfed the inspected-faction fill at small cell sizes and cost cellSize/2 fills per contested cell.
        ResourceLocation other = null;
        for (var id : ids) {
            if (!id.equals(inspected)) {
                other = id;
                break;
            }
        }
        if (other == null) {
            return;
        }
        var otherColor = colorForFaction(other, 0x80);
        graphics.fill(pxX, pxY + cellSize / 2, pxX + cellSize, pxY + cellSize, otherColor);
    }

    private void renderLegend(GuiGraphics graphics, net.minecraft.client.gui.Font font, int x, int y, int ownColor) {
        var swatchSize = 6;
        var entries = new String[] { "Inspected", "Other", "Contested", "Unclaimed" };
        var colors = new int[] {
            ownColor == 0 ? TEXT_MUTED_COLOR : (ownColor & 0x00FFFFFF) | (CELL_OWN_ALPHA << 24),
            0xFF668888,
            0xFFC04848,
            CELL_UNCLAIMED_BG
        };
        var cursor = x;
        for (var i = 0; i < entries.length; i++) {
            graphics.fill(
                cursor,
                y + (LEGEND_HEIGHT - swatchSize) / 2,
                cursor + swatchSize,
                y + (LEGEND_HEIGHT + swatchSize) / 2,
                colors[i]
            );
            graphics.fill(
                cursor,
                y + (LEGEND_HEIGHT - swatchSize) / 2,
                cursor + swatchSize,
                y + (LEGEND_HEIGHT - swatchSize) / 2 + 1,
                BUTTON_BORDER
            );
            graphics.fill(
                cursor,
                y + (LEGEND_HEIGHT + swatchSize) / 2 - 1,
                cursor + swatchSize,
                y + (LEGEND_HEIGHT + swatchSize) / 2,
                BUTTON_BORDER
            );
            graphics.fill(cursor, y + (LEGEND_HEIGHT - swatchSize) / 2, cursor + 1, y + (LEGEND_HEIGHT + swatchSize) / 2, BUTTON_BORDER);
            graphics.fill(
                cursor + swatchSize - 1,
                y + (LEGEND_HEIGHT - swatchSize) / 2,
                cursor + swatchSize,
                y + (LEGEND_HEIGHT + swatchSize) / 2,
                BUTTON_BORDER
            );
            cursor += swatchSize + 2;
            var textY = y + (LEGEND_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(entries[i]), cursor, textY, TEXT_MUTED_COLOR, false);
            cursor += font.width(entries[i]) + 8;
        }
    }

    private static void drawButton(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        Rect rect,
        String label,
        int mouseX,
        int mouseY,
        boolean active
    ) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, active ? 0xFF3C3C46 : (hovered ? BUTTON_BG_HOVER : BUTTON_BG));
        var borderColor = active ? CELL_HOVER_BORDER : BUTTON_BORDER;
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + 1, borderColor);
        graphics.fill(rect.x, rect.y + rect.h - 1, rect.x + rect.w, rect.y + rect.h, borderColor);
        graphics.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.h, borderColor);
        graphics.fill(rect.x + rect.w - 1, rect.y, rect.x + rect.w, rect.y + rect.h, borderColor);
        var textColor = active ? CELL_HOVER_BORDER : BUTTON_TEXT;
        var textX = rect.x + (rect.w - font.width(label)) / 2;
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    private static int colorForFaction(ResourceLocation factionId, int alpha) {
        var entry = ClientFactionDirectoryCache.get(factionId);
        var rgb = entry == null ? 0x888888 : entry.color() & 0xFFFFFF;
        return (alpha << 24) | rgb;
    }

    private static @Nullable ResourceLocation inspectedFactionId() {
        var single = SelectionManager.current().single();
        return single instanceof FactionSelectable fs ? fs.factionId() : null;
    }

    private void centerOnPlayer() {
        var mc = Minecraft.getInstance();
        if (mc.player != null) {
            viewCenterX = mc.player.chunkPosition().x + 0.5;
            viewCenterZ = mc.player.chunkPosition().z + 0.5;
        }
    }

    /** Translate a mouse position inside the map area to a chunk coordinate, or null if outside the map area. */
    private @Nullable ChunkPos chunkAt(double mouseX, double mouseY) {
        if (mouseX < mapX || mouseX >= mapX + mapW || mouseY < mapY || mouseY >= mapY + mapH) {
            return null;
        }
        var halfCellsX = (mapW / (double) cellSize) / 2.0;
        var halfCellsY = (mapH / (double) cellSize) / 2.0;
        var chunkX = (int) Math.floor(viewCenterX - halfCellsX + (mouseX - mapX) / (double) cellSize);
        var chunkZ = (int) Math.floor(viewCenterZ - halfCellsY + (mouseY - mapY) / (double) cellSize);
        return new ChunkPos(chunkX, chunkZ);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Header buttons first.
        if (paintButtonRect != null && paintButtonRect.contains(mouseX, mouseY) && button == 0) {
            var inspected = inspectedFactionId();
            if (inspected == null) {
                return true;
            }
            if (ClaimPaintTool.isActive() && inspected.equals(ClaimPaintTool.paintTarget())) {
                ClaimPaintTool.deactivate();
            } else {
                ClaimPaintTool.setPaintTarget(inspected);
                ClaimPaintTool.activate();
            }
            return true;
        }
        if (centerButtonRect != null && centerButtonRect.contains(mouseX, mouseY) && button == 0) {
            centerOnPlayer();
            return true;
        }

        // Map clicks.
        var chunk = chunkAt(mouseX, mouseY);
        if (chunk == null) {
            return false;
        }
        var inspected = inspectedFactionId();
        // No faction selected → start a pan drag instead (LMB only). RMB no-ops.
        if (inspected == null) {
            if (button == 0) {
                startPan(mouseX, mouseY);
            }
            return true;
        }
        // MMB always pans, regardless of which faction is inspected.
        if (button == 2) {
            startPan(mouseX, mouseY);
            return true;
        }
        if (button == 0 || button == 1) {
            paintButton = button;
            paintedThisDrag.clear();
            sendClaimOp(inspected, chunk, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (panning) {
            var dx = mouseX - panAnchorMouseX;
            var dy = mouseY - panAnchorMouseY;
            viewCenterX = panAnchorViewX - dx / cellSize;
            viewCenterZ = panAnchorViewZ - dy / cellSize;
            return true;
        }
        if (paintButton != null && button == paintButton) {
            var inspected = inspectedFactionId();
            if (inspected == null) {
                return true;
            }
            var chunk = chunkAt(mouseX, mouseY);
            if (chunk != null && paintedThisDrag.add(chunk.toLong())) {
                sendClaimOp(inspected, chunk, button);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (panning) {
            panning = false;
            return true;
        }
        if (paintButton != null && button == paintButton) {
            paintButton = null;
            paintedThisDrag.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
            return false;
        }
        var newCell = cellSize + (scrollY > 0 ? 1 : -1);
        cellSize = Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, newCell));
        return true;
    }

    private void startPan(double mouseX, double mouseY) {
        panning = true;
        panAnchorMouseX = mouseX;
        panAnchorMouseY = mouseY;
        panAnchorViewX = viewCenterX;
        panAnchorViewZ = viewCenterZ;
    }

    private void sendClaimOp(ResourceLocation factionId, ChunkPos chunk, int button) {
        paintedThisDrag.add(chunk.toLong());
        if (button == 0) {
            BLib.MOD.networking().sendToServer(new C2SAddChunkClaimPayload(factionId, chunk.x, chunk.z));
        } else {
            BLib.MOD.networking().sendToServer(new C2SRemoveChunkClaimPayload(factionId, chunk.x, chunk.z));
        }
    }

    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());
        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
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
