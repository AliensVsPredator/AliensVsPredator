package com.blib.engine.ui.panel.outliner;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.popup.EntityContextMenuHandler;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.TextInput;

/**
 * Scene-graph view of every entity loaded around the engine camera. Entities are grouped into four collapsible
 * categories (Players / Mobs / Items / Other), sorted within each category by distance from the engine camera, and
 * filterable via a substring match on the display name.
 * <p>
 * Click a {@link LivingEntity} row to select it through {@link SelectionManager}; the right-side details panel and the
 * world-space selection highlight pick up the change automatically. Non-living entities (items, projectiles, vehicles)
 * are listed for visibility but are not selectable — clicking them is a no-op until the engine grows a non-living
 * selectable type.
 * <p>
 * Each render re-walks {@code level.entitiesForRendering()}; the per-frame allocation is cheap (entity counts are
 * bounded by chunk-load distance) and avoids the cache-invalidation pitfalls of mirror-state. Stale selection (entity
 * unloaded since the last selection) is handled by {@link SelectionManager}'s pruning.
 */
@ApiStatus.Internal
public final class OutlinerPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF101010;

    private static final int HEADER_BG_COLOR = 0xFF1A1A22;

    private static final int HEADER_BG_HOVER_COLOR = 0xFF22222C;

    private static final int HEADER_TEXT_COLOR = 0xFFB0B0B0;

    private static final int HEADER_COUNT_COLOR = 0xFF707078;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_BG_SELECTED_COLOR = 0xFF2A2A38;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_NONSELECTABLE_COLOR = 0xFF707078;

    private static final int DISTANCE_TEXT_COLOR = 0xFF606068;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int HEADER_HEIGHT = 12;

    private static final int ROW_HEIGHT = 11;

    private static final int ROW_INDENT_X = 10;

    private static final int CARET_WIDTH = 6;

    /**
     * Category bucket for grouping entities in the outliner. Declaration order is the render order. Color is used for
     * the header tint so users can quickly visually parse the sections without reading every label.
     */
    private enum Category {

        PLAYERS("Players", 0xFFB6E2A1),
        MOBS("Mobs", 0xFFE6C26B),
        ITEMS("Items", 0xFF7CB6E0),
        OTHER("Other", 0xFF9890A8);

        final String displayName;

        final int accentColor;

        Category(String displayName, int accentColor) {
            this.displayName = displayName;
            this.accentColor = accentColor;
        }
    }

    private final TextInput searchInput = new TextInput("Filter entities…");

    private final ScrollContainer scroll = new ScrollContainer();

    private final EnumSet<Category> collapsed = EnumSet.noneOf(Category.class);

    /**
     * Handler that opens the engine's entity context menu when a row is right-clicked. Same surface as the viewport's
     * right-click → entity menu, so users get a consistent action set regardless of where they invoke it from.
     */
    private final @Nullable EntityContextMenuHandler contextMenuHandler;

    public OutlinerPanel() {
        this(null);
    }

    public OutlinerPanel(@Nullable EntityContextMenuHandler contextMenuHandler) {
        this.contextMenuHandler = contextMenuHandler;
    }

    /**
     * Per-frame hit-test rects for clickable rows. Cleared at the start of {@link #render} and rebuilt as rows render;
     * consulted by {@link #mouseClicked} to map a click position back to its entity.
     */
    private final List<RowHit> rowHits = new ArrayList<>();

    private final List<HeaderHit> headerHits = new ArrayList<>();

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable Component hoveredTooltip;

    @Override
    public String title() {
        return "Outliner";
    }

    @Override
    public void onShown() {
        scroll.reset();
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        this.hoveredTooltip = null;
        rowHits.clear();
        headerHits.clear();

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // No world ⇒ no entities ⇒ no point rendering the search bar or list. Stop before touching mc.level anywhere
        // downstream.
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        // Search bar at the top — same logical-pixel width as the panel content area.
        var searchY = y + CONTENT_PADDING;
        searchInput.render(graphics, x + CONTENT_PADDING, searchY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var listY = searchY + TextInput.HEIGHT + SEARCH_GAP_BELOW;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        var grouped = snapshot();
        var totalEntries = grouped.values().stream().mapToInt(List::size).sum();

        if (totalEntries == 0) {
            renderEmptyMessage(graphics, x + CONTENT_PADDING, listY);
            return;
        }

        // Compute total content height: each category contributes a header row + (rows if expanded). Empty
        // categories are hidden entirely so users don't see a parade of "(0)" headers when their search filters
        // most things out.
        var contentHeight = 0;
        for (var cat : Category.values()) {
            var rows = grouped.get(cat);
            if (rows == null || rows.isEmpty()) {
                continue;
            }
            contentHeight += HEADER_HEIGHT;
            if (!collapsed.contains(cat)) {
                contentHeight += rows.size() * ROW_HEIGHT;
            }
        }
        scroll.layout(listH, contentHeight);

        var selectedEntity = currentSelectedEntity();

        // Scissor-clip the list region so rows don't bleed past the panel rect during scroll. Mirrors the pattern
        // used by PiecePalettePanel/GOAPDetailsPanel — raw scissor (not GuiGraphics's stack) for predictable behavior
        // even if upstream HUD code left dirty state on the stack.
        applyRawScissor(graphics, x + CONTENT_PADDING, listY, width - 2 * CONTENT_PADDING, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var cursorY = listY - scrollY;
            for (var cat : Category.values()) {
                var rows = grouped.get(cat);
                if (rows == null || rows.isEmpty()) {
                    continue;
                }
                renderHeader(graphics, x + CONTENT_PADDING, cursorY, width - 2 * CONTENT_PADDING, cat, rows.size(), mouseX, mouseY);
                cursorY += HEADER_HEIGHT;
                if (collapsed.contains(cat)) {
                    continue;
                }
                for (var entry : rows) {
                    renderRow(graphics, x + CONTENT_PADDING, cursorY, width - 2 * CONTENT_PADDING, entry, selectedEntity, mouseX, mouseY);
                    cursorY += ROW_HEIGHT;
                }
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, x + CONTENT_PADDING, listY, width - 2 * CONTENT_PADDING, listH, mouseX, mouseY);
    }

    private void renderEmptyMessage(GuiGraphics graphics, int x, int y) {
        var font = EngineFont.get();
        String msg;
        if (Minecraft.getInstance().level == null) {
            msg = "Load a world to view entities";
        } else if (searchInput.content().isEmpty()) {
            msg = "(no entities loaded)";
        } else {
            msg = "(no matches)";
        }
        graphics.drawString(font, Component.literal(msg), x, y, EMPTY_TEXT_COLOR, false);
    }

    private void renderHeader(GuiGraphics graphics, int x, int y, int width, Category cat, int count, int mouseX, int mouseY) {
        // Reserve the scrollbar gutter so the header background and right-aligned count don't slide under the bar.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + HEADER_HEIGHT;
        var bg = hovered ? HEADER_BG_HOVER_COLOR : HEADER_BG_COLOR;
        graphics.fill(x, y, rowRight, y + HEADER_HEIGHT, bg);
        // Left-edge accent stripe so the section's color is visible even when the header label is truncated.
        graphics.fill(x, y, x + 2, y + HEADER_HEIGHT, cat.accentColor);

        var font = EngineFont.get();
        var caret = collapsed.contains(cat) ? "▸" : "▾";
        var textY = y + (HEADER_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(caret), x + 4, textY, HEADER_TEXT_COLOR, false);
        graphics.drawString(font, Component.literal(cat.displayName), x + 4 + CARET_WIDTH + 2, textY, HEADER_TEXT_COLOR, false);

        var countLabel = "(" + count + ")";
        var countX = rowRight - 4 - font.width(countLabel);
        graphics.drawString(font, Component.literal(countLabel), countX, textY, HEADER_COUNT_COLOR, false);

        headerHits.add(new HeaderHit(x, y, rowRight - x, HEADER_HEIGHT, cat));
    }

    private void renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        EntityEntry entry,
        @Nullable Entity selectedEntity,
        int mouseX,
        int mouseY
    ) {
        // Reserve the scrollbar gutter so the row hover background and right-aligned distance label stay clear of it.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + ROW_HEIGHT;
        var selected = selectedEntity != null && selectedEntity == entry.entity;
        if (selected) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_SELECTED_COLOR);
        } else if (hovered) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;

        // Distance label is right-aligned and drawn first so we know how much room is left for the name (long names
        // would otherwise crowd the right edge and overlap the distance).
        var distLabel = formatDistance(entry.distance);
        var distWidth = font.width(distLabel);
        var distX = rowRight - distWidth - 4;
        graphics.drawString(font, Component.literal(distLabel), distX, textY, DISTANCE_TEXT_COLOR, false);

        var nameX = x + ROW_INDENT_X;
        var nameMaxWidth = Math.max(0, distX - nameX - 4);
        var nameColor = entry.selectable ? (hovered ? 0xFFFFFFFF : ROW_TEXT_COLOR) : ROW_TEXT_NONSELECTABLE_COLOR;
        var truncated = font.plainSubstrByWidth(entry.name, nameMaxWidth);
        graphics.drawString(font, Component.literal(truncated), nameX, textY, nameColor, false);

        rowHits.add(new RowHit(x, y, rowRight - x, ROW_HEIGHT, entry.entity, entry.selectable));

        if (hovered) {
            // Use a Component (vs raw String) so the workspace's tooltip box renders multi-line via Font.split.
            var pos = entry.entity.position();
            var coords = String.format("%s\nType: %s\nPos: %.1f, %.1f, %.1f", entry.name, entry.typeLabel, pos.x, pos.y, pos.z);
            this.hoveredTooltip = Component.literal(coords);
        }
    }

    /**
     * Walk every loaded entity, categorize, filter by the current search query, and sort each bucket by distance from
     * the engine camera. Empty result lists are still placed in the map so callers can iterate {@link Category#values}
     * uniformly — but the renderer skips empty categories to keep the UI quiet.
     */
    private EnumMap<Category, List<EntityEntry>> snapshot() {
        var grouped = new EnumMap<Category, List<EntityEntry>>(Category.class);
        for (var c : Category.values()) {
            grouped.put(c, new ArrayList<>());
        }
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return grouped;
        }
        var camera = cameraPos();
        var query = searchInput.content().toLowerCase(java.util.Locale.ROOT).trim();

        for (var e : mc.level.entitiesForRendering()) {
            var cat = categoryOf(e);
            var name = displayNameFor(e);
            if (!query.isEmpty() && !name.toLowerCase(java.util.Locale.ROOT).contains(query)) {
                continue;
            }
            var dist = camera.distanceTo(e.position());
            var typeLabel = e.getType().getDescriptionId();
            grouped.get(cat).add(new EntityEntry(e, cat, name, typeLabel, dist, e instanceof LivingEntity));
        }
        for (var rows : grouped.values()) {
            rows.sort(Comparator.comparingDouble(EntityEntry::distance));
        }
        return grouped;
    }

    /**
     * Engine-camera position when an engine session is active; otherwise the player position. Falls back to the world
     * origin if neither is available so distance math doesn't blow up.
     */
    private static Vec3 cameraPos() {
        var session = EngineMode.get().session();
        if (session != null) {
            return session.cameraPosition();
        }
        var mc = Minecraft.getInstance();
        return mc.player != null ? mc.player.position() : Vec3.ZERO;
    }

    private static Category categoryOf(Entity e) {
        if (e instanceof Player) {
            return Category.PLAYERS;
        }
        if (e instanceof LivingEntity) {
            return Category.MOBS;
        }
        if (e instanceof ItemEntity) {
            return Category.ITEMS;
        }
        return Category.OTHER;
    }

    /**
     * Display name for an entity row. ItemEntity gets "<item> ×N" so dropped stacks distinguish themselves; everything
     * else uses {@link Entity#getName} which respects custom names + falls back to the type translation.
     */
    private static String displayNameFor(Entity e) {
        if (e instanceof ItemEntity ie) {
            var stack = ie.getItem();
            var base = stack.getHoverName().getString();
            var count = stack.getCount();
            return count > 1 ? base + " ×" + count : base;
        }
        return e.getName().getString();
    }

    private static String formatDistance(double dist) {
        if (dist < 10.0) {
            return String.format("%.1fm", dist);
        }
        return ((int) Math.round(dist)) + "m";
    }

    private @Nullable Entity currentSelectedEntity() {
        var sel = SelectionManager.current().single();
        if (sel instanceof EntitySelectable es) {
            return es.entity();
        }
        return null;
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        // Scrollbar overlaps the dock divider's hit zone; capture-route ensures the bar grabs the click first.
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 1) {
            // RMB on a LivingEntity row opens the same context menu as right-clicking the entity in the viewport
            // (View GOAP Details, Delete Entity, etc.). Headers and non-living rows ignore RMB; the click is
            // consumed in either case so the panel beneath (the dock divider, etc.) doesn't get it.
            for (var rh : rowHits) {
                if (mouseX >= rh.x && mouseX < rh.x + rh.w && mouseY >= rh.y && mouseY < rh.y + rh.h) {
                    if (rh.selectable && rh.entity instanceof LivingEntity le && contextMenuHandler != null) {
                        SelectionManager.selectSingle(new EntitySelectable(le));
                        contextMenuHandler.onEntityRightClick(le, mouseX, mouseY);
                    }
                    return true;
                }
            }
            return false;
        }
        if (button != 0) {
            return false;
        }
        for (var hh : headerHits) {
            if (mouseX >= hh.x && mouseX < hh.x + hh.w && mouseY >= hh.y && mouseY < hh.y + hh.h) {
                if (collapsed.contains(hh.category)) {
                    collapsed.remove(hh.category);
                } else {
                    collapsed.add(hh.category);
                }
                return true;
            }
        }
        for (var rh : rowHits) {
            if (mouseX >= rh.x && mouseX < rh.x + rh.w && mouseY >= rh.y && mouseY < rh.y + rh.h) {
                if (rh.selectable && rh.entity instanceof LivingEntity le) {
                    SelectionManager.selectSingle(new EntitySelectable(le));
                }
                // Non-living rows are still consumed (return true) so a click on, say, an item row doesn't bubble
                // through to the panel underneath; we just don't change selection.
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
            return false;
        }
        return scroll.mouseScrolled(scrollY);
    }

    /**
     * GL scissor in workspace logical-pixel space. Same approach as PiecePalettePanel: transform through the active
     * pose stack to get raw window pixels, then enable scissor directly so we don't depend on GuiGraphics's scissor
     * stack being clean from upstream HUD callbacks.
     */
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

    private record EntityEntry(
        Entity entity,
        Category cat,
        String name,
        String typeLabel,
        double distance,
        boolean selectable
    ) {}

    private record RowHit(
        int x,
        int y,
        int w,
        int h,
        Entity entity,
        boolean selectable
    ) {}

    private record HeaderHit(
        int x,
        int y,
        int w,
        int h,
        Category category
    ) {}
}
