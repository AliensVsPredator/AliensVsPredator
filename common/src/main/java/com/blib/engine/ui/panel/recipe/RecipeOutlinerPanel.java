package com.blib.engine.ui.panel.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.blib.engine.recipe.RecipeAuthoringState;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.UiCaret;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class RecipeOutlinerPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1B1B22;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_BG_SELECTED_COLOR = 0xFF2A2A38;

    private static final int HEADER_TEXT_COLOR = 0xFFE6C26B;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int META_TEXT_COLOR = 0xFF8C8C96;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 6;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int HEADER_HEIGHT = 16;

    private static final int ROW_HEIGHT = 15;

    private final TextInput searchInput = new TextInput("Filter recipes...");

    private final ScrollViewport scroll = new ScrollViewport();

    private final Set<String> collapsedTypes = new HashSet<>();

    private final List<RowHit> rowHits = new ArrayList<>();

    private final List<HeaderHit> headerHits = new ArrayList<>();

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    @Override
    public String title() {
        return "Recipe Outliner";
    }

    @Override
    public void onShown() {
        scroll.reset();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        rowHits.clear();
        headerHits.clear();

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        if (Minecraft.getInstance().level == null) {
            scroll.clear();
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        var searchY = y + CONTENT_PADDING;
        searchInput.render(graphics, x + CONTENT_PADDING, searchY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var listX = x + CONTENT_PADDING;
        var listY = searchY + TextInput.HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            scroll.clear();
            return;
        }

        var groups = groupedRecipes(searchInput.content());
        if (groups.isEmpty()) {
            scroll.clear();
            UiText.drawClipped(
                graphics,
                EngineFont.get(),
                searchInput.content().isBlank() ? "(no recipes)" : "(no matches)",
                listX,
                listY,
                listW,
                EMPTY_TEXT_COLOR
            );
            return;
        }

        var contentHeight = 0;
        for (var group : groups) {
            contentHeight += HEADER_HEIGHT;
            if (!collapsedTypes.contains(group.typeId())) {
                contentHeight += group.recipes().size() * ROW_HEIGHT;
            }
        }

        var selected = RecipeAuthoringState.selectedRecipeId();
        var frame = scroll.begin(graphics, UiRect.of(listX, listY, listW, listH), contentHeight);
        try {
            var cursorY = frame.contentY();
            for (var group : groups) {
                if (cursorY + HEADER_HEIGHT >= listY && cursorY <= listY + listH) {
                    renderHeader(graphics, frame.contentX(), cursorY, frame.contentWidth(), group, mouseX, mouseY);
                }
                cursorY += HEADER_HEIGHT;
                if (collapsedTypes.contains(group.typeId())) {
                    continue;
                }
                for (var recipe : group.recipes()) {
                    if (cursorY + ROW_HEIGHT >= listY && cursorY <= listY + listH) {
                        renderRecipeRow(graphics, frame.contentX(), cursorY, frame.contentWidth(), recipe, selected, mouseX, mouseY);
                    }
                    cursorY += ROW_HEIGHT;
                }
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    private void renderHeader(GuiGraphics graphics, int x, int y, int width, RecipeGroup group, int mouseX, int mouseY) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + HEADER_HEIGHT;
        graphics.fill(x, y, x + width, y + HEADER_HEIGHT, hovered ? ROW_BG_HOVER_COLOR : HEADER_BG_COLOR);

        var font = EngineFont.get();
        var arrow = UiCaret.glyph(collapsedTypes.contains(group.typeId()));
        var label = arrow + " " + group.typeId();
        var count = Integer.toString(group.recipes().size());
        var countW = font.width(count);
        var textY = y + (HEADER_HEIGHT - font.lineHeight + 2) / 2;
        UiText.drawClipped(graphics, font, label, x + 5, textY, Math.max(0, width - countW - 16), HEADER_TEXT_COLOR);
        UiText.drawRight(graphics, font, count, UiRect.of(x, y, width - 5, HEADER_HEIGHT), META_TEXT_COLOR);
        headerHits.add(new HeaderHit(x, y, width, HEADER_HEIGHT, group.typeId()));
    }

    private void renderRecipeRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        RecipeHolder<?> recipe,
        ResourceLocation selected,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + ROW_HEIGHT;
        var isSelected = recipe.id().equals(selected);
        if (isSelected) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_SELECTED_COLOR);
        } else if (hovered) {
            graphics.fill(x, y, x + width, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        UiText.drawClipped(
            graphics,
            font,
            recipe.id().toString(),
            x + 14,
            textY,
            Math.max(0, width - 18),
            hovered || isSelected ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR
        );
        rowHits.add(new RowHit(x, y, width, ROW_HEIGHT, recipe));
    }

    private static List<RecipeGroup> groupedRecipes(String query) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        var needle = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        var grouped = new LinkedHashMap<String, List<RecipeHolder<?>>>();
        for (var holder : level.getRecipeManager().getRecipes()) {
            var id = holder.id();
            var typeKey = BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
            var typeId = typeKey == null ? holder.value().getType().toString() : typeKey.toString();
            var hay = (id + " " + typeId).toLowerCase(Locale.ROOT);
            if (!needle.isEmpty() && !hay.contains(needle)) {
                continue;
            }
            grouped.computeIfAbsent(typeId, ignored -> new ArrayList<>()).add(holder);
        }

        var groups = new ArrayList<RecipeGroup>();
        for (var entry : grouped.entrySet()) {
            var recipes = entry.getValue();
            recipes.sort(Comparator.comparing(r -> r.id().toString(), String.CASE_INSENSITIVE_ORDER));
            groups.add(new RecipeGroup(entry.getKey(), List.copyOf(recipes)));
        }
        groups.sort(Comparator.comparing(RecipeGroup::typeId, String.CASE_INSENSITIVE_ORDER));
        return groups;
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
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
        for (var header : headerHits) {
            if (header.contains(mouseX, mouseY)) {
                if (!collapsedTypes.add(header.typeId())) {
                    collapsedTypes.remove(header.typeId());
                }
                return true;
            }
        }
        for (var row : rowHits) {
            if (row.contains(mouseX, mouseY)) {
                RecipeAuthoringState.loadRecipe(row.recipe());
                return true;
            }
        }
        return isInside(mouseX, mouseY);
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
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + rectHeight;
    }

    private record RecipeGroup(String typeId, List<RecipeHolder<?>> recipes) {}

    private record HeaderHit(int x, int y, int w, int h, String typeId) {

        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        }
    }

    private record RowHit(int x, int y, int w, int h, RecipeHolder<?> recipe) {

        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        }
    }
}
