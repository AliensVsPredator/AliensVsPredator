package com.blib.engine.ui.panel.recipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.blib.engine.recipe.RecipeAuthoringState;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class ItemBrowserPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int META_TEXT_COLOR = 0xFF8C8C96;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 6;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int ROW_HEIGHT = 20;

    private static final int MODE_TOGGLE_WIDTH = 76;

    private static final int MODE_TOGGLE_GAP = 4;

    private static final int ICON_SIZE = 16;

    private static final int ICON_CELL_SIZE = 24;

    private static final int ICON_TEXT_GAP = 5;

    private static final ResourceLocation ITEM_REGISTRY = Registries.ITEM.location();

    private final TextInput searchInput = new TextInput("Search items...");

    private final SegmentedControl modeToggle = new SegmentedControl(List.of("List", "Icons"), 1);

    private final ScrollViewport scroll = new ScrollViewport();

    private final List<RowHit> rowHits = new ArrayList<>();

    private List<Entry> filtered = List.of();

    private String lastQuery = "\u0000";

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable Component hoveredTooltip;

    @Override
    public String title() {
        return "Item Browser";
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

        var query = searchInput.content();
        if (!query.equals(lastQuery)) {
            filtered = filter(query);
            lastQuery = query;
            scroll.reset();
        }

        var searchY = y + CONTENT_PADDING;
        var toggleX = x + width - CONTENT_PADDING - MODE_TOGGLE_WIDTH;
        var searchW = Math.max(0, toggleX - (x + CONTENT_PADDING) - MODE_TOGGLE_GAP);
        searchInput.render(graphics, x + CONTENT_PADDING, searchY, searchW, mouseX, mouseY);
        modeToggle.render(graphics, toggleX, searchY, MODE_TOGGLE_WIDTH, mouseX, mouseY);

        var listX = x + CONTENT_PADDING;
        var listY = searchY + TextInput.HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            scroll.clear();
            renderDraggedStack(graphics, mouseX, mouseY);
            return;
        }

        if (filtered.isEmpty()) {
            scroll.clear();
            UiText.drawClipped(
                graphics,
                EngineFont.get(),
                query.isBlank() ? "(no items)" : "(no matches)",
                listX,
                listY,
                listW,
                EMPTY_TEXT_COLOR
            );
            renderDraggedStack(graphics, mouseX, mouseY);
            return;
        }

        var contentHeight = contentHeight(Math.max(0, listW - ScrollContainer.SCROLLBAR_GUTTER));
        var frame = scroll.begin(graphics, UiRect.of(listX, listY, listW, listH), contentHeight);
        try {
            if (isIconMode()) {
                renderIconGrid(graphics, frame, listY, listH, mouseX, mouseY);
            } else {
                var firstVisibleRow = Math.max(0, frame.scrollY() / ROW_HEIGHT);
                var lastVisibleRow = Math.min(filtered.size() - 1, (frame.scrollY() + listH) / ROW_HEIGHT);
                for (var i = firstVisibleRow; i <= lastVisibleRow; i++) {
                    var rowY = listY + i * ROW_HEIGHT - frame.scrollY();
                    renderRow(graphics, frame.contentX(), rowY, frame.contentWidth(), filtered.get(i), mouseX, mouseY);
                }
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }

        renderDraggedStack(graphics, mouseX, mouseY);
    }

    private void renderIconGrid(GuiGraphics graphics, ScrollViewport.Frame frame, int listY, int listH, int mouseX, int mouseY) {
        var columns = columnsForWidth(frame.contentWidth());
        var firstVisibleRow = Math.max(0, frame.scrollY() / ICON_CELL_SIZE);
        var lastVisibleRow = Math.min(rowCount(columns) - 1, (frame.scrollY() + listH) / ICON_CELL_SIZE);
        for (var row = firstVisibleRow; row <= lastVisibleRow; row++) {
            var cellY = listY + row * ICON_CELL_SIZE - frame.scrollY();
            for (var col = 0; col < columns; col++) {
                var index = row * columns + col;
                if (index >= filtered.size()) {
                    return;
                }
                var cellX = frame.contentX() + col * ICON_CELL_SIZE;
                renderIconCell(graphics, cellX, cellY, filtered.get(index), mouseX, mouseY);
            }
        }
    }

    private void renderIconCell(GuiGraphics graphics, int x, int y, Entry entry, int mouseX, int mouseY) {
        var hovered = mouseX >= x && mouseX < x + ICON_CELL_SIZE && mouseY >= y && mouseY < y + ICON_CELL_SIZE;
        if (hovered) {
            graphics.fill(x, y, x + ICON_CELL_SIZE, y + ICON_CELL_SIZE, ROW_BG_HOVER_COLOR);
        }

        var stack = displayStack(entry);
        var iconX = x + (ICON_CELL_SIZE - ICON_SIZE) / 2;
        var iconY = y + (ICON_CELL_SIZE - ICON_SIZE) / 2;
        if (!stack.isEmpty()) {
            graphics.renderItem(stack, iconX, iconY);
        }
        if (entry.tag()) {
            graphics.drawString(EngineFont.get(), Component.literal("#"), x + 2, y + 1, META_TEXT_COLOR, false);
        }
        rowHits.add(new RowHit(x, y, ICON_CELL_SIZE, ICON_CELL_SIZE, entry));

        if (hovered && !RecipeAuthoringState.hasDrag()) {
            hoveredTooltip = entryTooltip(entry, stack);
        }
    }

    private void renderRow(GuiGraphics graphics, int x, int y, int width, Entry entry, int mouseX, int mouseY) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + ROW_HEIGHT;
        if (hovered) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var stack = displayStack(entry);
        var iconX = x + 3;
        var iconY = y + (ROW_HEIGHT - ICON_SIZE) / 2;
        if (!stack.isEmpty()) {
            graphics.renderItem(stack, iconX, iconY);
        }

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        var meta = entry.tag() ? "#" : Integer.toString(stack.getMaxStackSize());
        var metaW = font.width(meta);
        var textX = iconX + ICON_SIZE + ICON_TEXT_GAP;
        UiText.drawClipped(
            graphics,
            font,
            entryLabel(entry),
            textX,
            textY,
            Math.max(0, width - (textX - x) - metaW - 8),
            hovered ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR
        );
        UiText.drawRight(graphics, font, meta, UiRect.of(x, y, width - 4, ROW_HEIGHT), META_TEXT_COLOR);
        rowHits.add(new RowHit(x, y, width, ROW_HEIGHT, entry));

        if (hovered && !RecipeAuthoringState.hasDrag()) {
            hoveredTooltip = entryTooltip(entry, stack);
        }
    }

    private static List<Entry> filter(String query) {
        var needle = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        var entries = new ArrayList<Entry>();
        for (var item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null) {
                continue;
            }
            var name = new ItemStack(item).getHoverName().getString();
            if (!needle.isEmpty()) {
                var hay = (id + " " + name).toLowerCase(Locale.ROOT);
                if (!hay.contains(needle)) {
                    continue;
                }
            }
            entries.add(new Entry(id, item, false));
        }
        for (var tagId : itemTagIds()) {
            var label = "#" + tagId;
            if (!needle.isEmpty() && !label.toLowerCase(Locale.ROOT).contains(needle)) {
                continue;
            }
            entries.add(new Entry(tagId, null, true));
        }
        entries.sort(Comparator.comparing(ItemBrowserPanel::entryLabel, String.CASE_INSENSITIVE_ORDER));
        return entries;
    }

    private static Set<ResourceLocation> itemTagIds() {
        var tagIds = new HashSet<ResourceLocation>();
        BuiltInRegistries.ITEM.getTagNames().forEach(tag -> tagIds.add(tag.location()));
        for (var entry : TagCatalogCache.all()) {
            if (entry.registryKey().equals(ITEM_REGISTRY)) {
                tagIds.add(entry.tagId());
            }
        }
        return tagIds;
    }

    private static String entryLabel(Entry entry) {
        return entry.tag() ? "#" + entry.id() : entry.id().toString();
    }

    private static ItemStack displayStack(Entry entry) {
        if (entry.tag()) {
            return RecipeAuthoringState.DraftSlot.tag(entry.id()).toStack();
        }
        return entry.item() == null ? ItemStack.EMPTY : new ItemStack(entry.item());
    }

    private static Component entryTooltip(Entry entry, ItemStack stack) {
        if (entry.tag()) {
            return Component.literal("Tag: #" + entry.id());
        }
        return Component.literal(stack.getHoverName().getString() + "\nID: " + entry.id() + "\nMax stack: " + stack.getMaxStackSize());
    }

    private int contentHeight(int width) {
        if (!isIconMode()) {
            return filtered.size() * ROW_HEIGHT;
        }
        return rowCount(columnsForWidth(width)) * ICON_CELL_SIZE;
    }

    private int rowCount(int columns) {
        return (filtered.size() + Math.max(1, columns) - 1) / Math.max(1, columns);
    }

    private static int columnsForWidth(int width) {
        return Math.max(1, width / ICON_CELL_SIZE);
    }

    private boolean isIconMode() {
        return modeToggle.selectedIndex() == 1;
    }

    private static void renderDraggedStack(GuiGraphics graphics, int mouseX, int mouseY) {
        var drag = RecipeAuthoringState.draggedStack();
        if (drag == null) {
            return;
        }
        var stack = drag.toStack();
        var font = EngineFont.get();
        if (!stack.isEmpty()) {
            graphics.renderItem(stack, mouseX - 8, mouseY - 8);
            graphics.renderItemDecorations(font, stack, mouseX - 8, mouseY - 8);
        } else if (drag.isTag()) {
            graphics.drawString(font, Component.literal("#"), mouseX - 3, mouseY - 4, ROW_TEXT_HOVER_COLOR, false);
        }
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        for (var row : rowHits) {
            if (row.contains(mouseX, mouseY)) {
                if (row.entry().tag()) {
                    RecipeAuthoringState.beginTagDrag(row.entry().id());
                } else if (row.entry().item() != null) {
                    RecipeAuthoringState.beginDrag(row.entry().item());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var previousMode = modeToggle.selectedIndex();
        if (modeToggle.mouseClicked(mouseX, mouseY, button)) {
            if (modeToggle.selectedIndex() != previousMode) {
                scroll.reset();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (RecipeAuthoringState.hasDrag()) {
            return true;
        }
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && RecipeAuthoringState.hasDrag()) {
            RecipeAuthoringState.dropDraggedAt(mouseX, mouseY);
            RecipeAuthoringState.clearDrag();
            return true;
        }
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + rectHeight;
    }

    private record Entry(ResourceLocation id, @Nullable Item item, boolean tag) {}

    private record RowHit(int x, int y, int w, int h, Entry entry) {

        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        }
    }
}
