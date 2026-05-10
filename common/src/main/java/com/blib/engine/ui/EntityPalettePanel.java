package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.blib.engine.spawn.EntitySpawnSelection;

/**
 * Searchable, scrollable list of every summonable {@link EntityType} as a UI replacement for {@code /summon}. Selecting
 * a row arms it via {@link EntitySpawnSelection}; the next viewport LMB spawns the entity at the targeted block.
 * <p>
 * Difficulty awareness: if the loaded level is in {@link Difficulty#PEACEFUL}, hostile-mob rows render dimmed and
 * reject clicks — peaceful would despawn them on the next tick anyway, and a dead-letter spawn is a confusing UX. The
 * check mirrors the server-side gate in {@code BLibServerListener.handleSpawnEntity}; the client filter is just an
 * early disambiguation, the server remains authoritative.
 * <p>
 * Filtering: substring match on the translated display name and on the namespaced id, lowercase. Sorted alphabetically
 * by display name. Only entity types where {@link EntityType#canSummon} returns true are listed, so non-summonable
 * helpers (lightning bolt, fishing bobber, items, etc.) don't clutter the catalog.
 */
@ApiStatus.Internal
public final class EntityPalettePanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_BG_SELECTED_COLOR = 0xFF2A2A38;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int ROW_TEXT_BLOCKED_COLOR = 0xFF606068;

    private static final int CATEGORY_CHIP_TEXT_COLOR = 0xFF202024;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 6;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int ROW_HEIGHT = 13;

    private static final int ROW_INDENT_X = 4;

    /** Mob category palette — the small chip on the right of each row uses these as backgrounds. */
    private static int categoryColor(MobCategory cat) {
        return switch (cat) {
            case MONSTER -> 0xFFE06868;
            case CREATURE -> 0xFFB6E2A1;
            case AMBIENT -> 0xFF7CB6E0;
            case AXOLOTLS -> 0xFFC892D8;
            case UNDERGROUND_WATER_CREATURE -> 0xFF5C7CC8;
            case WATER_CREATURE -> 0xFF7CB0E0;
            case WATER_AMBIENT -> 0xFFA0C8E8;
            case MISC -> 0xFF9890A8;
        };
    }

    private static String categoryLabel(MobCategory cat) {
        return switch (cat) {
            case MONSTER -> "hostile";
            case CREATURE -> "passive";
            case AMBIENT -> "ambient";
            case AXOLOTLS -> "axolotl";
            case UNDERGROUND_WATER_CREATURE -> "u-water";
            case WATER_CREATURE -> "water";
            case WATER_AMBIENT -> "w-amb";
            case MISC -> "misc";
        };
    }

    private final TextInput searchInput = new TextInput("Search entities…");

    private final ScrollContainer scroll = new ScrollContainer();

    /** Cached filtered entries; rebuilt on search-content drift. */
    private List<Entry> filtered = List.of();

    private String lastQuery = " "; // sentinel — never equals actual content

    private final List<RowHit> rowHits = new ArrayList<>();

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable Component hoveredTooltip;

    @Override
    public String title() {
        return "Entity Palette";
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

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Recompute filtered list when the search content changes. Difficulty changes don't invalidate the list — they
        // only affect rendering / click acceptance, not the membership.
        var query = searchInput.content();
        if (!query.equals(lastQuery)) {
            this.filtered = filter(query);
            this.lastQuery = query;
            scroll.reset();
        }

        var searchY = y + CONTENT_PADDING;
        searchInput.render(graphics, x + CONTENT_PADDING, searchY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var listX = x + CONTENT_PADDING;
        var listY = searchY + TextInput.HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        if (filtered.isEmpty()) {
            var msg = query.isEmpty() ? "(no summonable entities)" : "(no matches)";
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, Component.literal(msg), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var contentHeight = filtered.size() * ROW_HEIGHT;
        scroll.layout(listH, contentHeight);

        var blockedByPeaceful = isPeaceful();
        var selectedId = EntitySpawnSelection.selectedTypeId();

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            // Skip rows entirely outside the visible window — even at 200 entries this keeps text-rendering work
            // bounded by visible-row count instead of total catalog size.
            var firstVisibleRow = Math.max(0, scrollY / ROW_HEIGHT);
            var lastVisibleRow = Math.min(filtered.size() - 1, (scrollY + listH) / ROW_HEIGHT);
            for (var i = firstVisibleRow; i <= lastVisibleRow; i++) {
                var entry = filtered.get(i);
                var rowY = listY + i * ROW_HEIGHT - scrollY;
                renderRow(graphics, listX, rowY, listW, entry, blockedByPeaceful, selectedId, mouseX, mouseY);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, listX, listY, listW, listH, mouseX, mouseY);
    }

    private void renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        Entry entry,
        boolean peaceful,
        @Nullable ResourceLocation selectedId,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + ROW_HEIGHT;
        var selected = entry.id.equals(selectedId);
        var blocked = peaceful && entry.category == MobCategory.MONSTER;

        if (selected) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_SELECTED_COLOR);
        } else if (hovered && !blocked) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        // Left-edge category accent stripe, mirrors the visual language of the outliner.
        graphics.fill(x, y, x + 2, y + ROW_HEIGHT, categoryColor(entry.category));

        var font = Minecraft.getInstance().font;
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;

        // Right-aligned category chip — drawn first so we know how much horizontal room is left for the name.
        var chipText = categoryLabel(entry.category);
        var chipPaddingX = 3;
        var chipWidth = font.width(chipText) + chipPaddingX * 2;
        var chipHeight = font.lineHeight + 2;
        var chipX = x + width - chipWidth - 6;
        var chipY = y + (ROW_HEIGHT - chipHeight) / 2;
        graphics.fill(chipX, chipY, chipX + chipWidth, chipY + chipHeight, categoryColor(entry.category));
        graphics.drawString(font, Component.literal(chipText), chipX + chipPaddingX, chipY + 1, CATEGORY_CHIP_TEXT_COLOR, false);

        var nameX = x + ROW_INDENT_X + 4;
        var nameMaxWidth = Math.max(0, chipX - nameX - 4);
        int textColor;
        if (blocked) {
            textColor = ROW_TEXT_BLOCKED_COLOR;
        } else if (hovered || selected) {
            textColor = ROW_TEXT_HOVER_COLOR;
        } else {
            textColor = ROW_TEXT_COLOR;
        }
        var truncated = font.plainSubstrByWidth(entry.displayName, nameMaxWidth);
        graphics.drawString(font, Component.literal(truncated), nameX, textY, textColor, false);

        rowHits.add(new RowHit(x, y, width, ROW_HEIGHT, entry.id, blocked));

        if (hovered) {
            // Tooltip carries the full id and (when blocked) the reason — both are non-obvious from the visible row
            // layout because the name is truncated to fit and the category is just an abbreviation.
            var sb = new StringBuilder();
            sb.append(entry.displayName).append("\n");
            sb.append("ID: ").append(entry.id).append("\n");
            sb.append("Category: ").append(entry.category.getName());
            if (blocked) {
                sb.append("\n").append("Cannot spawn in Peaceful (hostile mob)");
            }
            this.hoveredTooltip = Component.literal(sb.toString());
        }
    }

    /**
     * Snapshot the entity-type registry into a sorted, search-filtered list. Excludes non-summonable types so the
     * palette doesn't list helpers like lightning bolts and items that wouldn't behave as users expect.
     */
    private List<Entry> filter(String query) {
        var needle = query.toLowerCase(java.util.Locale.ROOT).trim();
        var out = new ArrayList<Entry>();
        for (var type : BuiltInRegistries.ENTITY_TYPE) {
            if (!type.canSummon()) {
                continue;
            }
            var id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (id == null) {
                continue;
            }
            var displayName = type.getDescription().getString();
            if (!needle.isEmpty()) {
                var hay = (displayName + " " + id).toLowerCase(java.util.Locale.ROOT);
                if (!hay.contains(needle)) {
                    continue;
                }
            }
            out.add(new Entry(id, type, displayName, type.getCategory()));
        }
        out.sort(Comparator.comparing(e -> e.displayName, String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private static boolean isPeaceful() {
        var mc = Minecraft.getInstance();
        return mc.level != null && mc.level.getDifficulty() == Difficulty.PEACEFUL;
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        // Scrollbar overlap with dock divider — capture-route ensures the bar grabs the click first.
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        for (var rh : rowHits) {
            if (mouseX >= rh.x && mouseX < rh.x + rh.w && mouseY >= rh.y && mouseY < rh.y + rh.h) {
                if (rh.blocked) {
                    // Consume the click but don't arm — the row's tooltip already explains why.
                    return true;
                }
                if (rh.id.equals(EntitySpawnSelection.selectedTypeId())) {
                    // Click the same row again to disarm — matches the toggle pattern users expect from "selected"
                    // chips in tools like this.
                    EntitySpawnSelection.clear();
                } else {
                    EntitySpawnSelection.select(rh.id);
                }
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

    /** Same scissor pattern as PiecePalettePanel / OutlinerPanel — see their docs for the rationale. */
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

    private record Entry(
        ResourceLocation id,
        EntityType<?> type,
        String displayName,
        MobCategory category
    ) {}

    private record RowHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation id,
        boolean blocked
    ) {}
}
