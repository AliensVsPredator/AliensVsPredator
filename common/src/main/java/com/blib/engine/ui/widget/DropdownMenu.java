package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.blib.engine.ui.EngineFont;

/**
 * Popup menu rendered above the workspace's panels — a vertical stack of clickable items anchored at a logical-px
 * position (typically just below the menu-bar chip that spawned it). The
 * {@link com.blib.engine.ui.EngineWorkspaceScreen} owns the lifecycle (one open at a time); this class is just the
 * visual + hit testing.
 */
@ApiStatus.Internal
public final class DropdownMenu {

    public record Item(
        String label,
        Runnable action,
        List<Item> children,
        boolean enabled,
        @Nullable Component disabledTooltip,
        boolean separator
    ) {

        public Item {
            if (separator) {
                label = "";
                children = List.of();
                enabled = false;
                disabledTooltip = null;
            } else {
                children = List.copyOf(children);
            }
            if (!enabled && disabledTooltip == null && !separator) {
                disabledTooltip = Component.literal("This action is unavailable right now.");
            }
        }

        public Item(String label, Runnable action) {
            this(label, action, List.of(), true, null, false);
        }

        public Item(String label, Runnable action, boolean enabled) {
            this(label, action, List.of(), enabled, null, false);
        }

        public Item(String label, Runnable action, boolean enabled, Component disabledTooltip) {
            this(label, action, List.of(), enabled, disabledTooltip, false);
        }

        public Item(String label, Runnable action, List<Item> children) {
            this(label, action, children, true, null, false);
        }

        public static Item divider() {
            return new Item("", () -> {}, List.of(), false, null, true);
        }

        public boolean hasSubmenu() {
            return !separator && !children.isEmpty();
        }
    }

    public static final int ITEM_HEIGHT = 14;

    private static final int DIVIDER_HEIGHT = 7;

    private static final int PADDING_X = 8;

    private static final int BORDER_THICKNESS = 1;

    private static final int BACKGROUND_COLOR = 0xF01A1A1F;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int ITEM_HOVER_BG_COLOR = 0xFF353540;

    private static final int ITEM_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ITEM_DISABLED_TEXT_COLOR = 0xFF777780;

    /** Right-pointing triangle drawn at the right edge of items that open a submenu. */
    private static final String SUBMENU_INDICATOR = "▸";

    /** Gap (in logical px) between the longest label and the submenu indicator on its right. */
    private static final int SUBMENU_INDICATOR_GAP = 6;

    private final int anchorX;

    private final int anchorY;

    private final List<Item> items;

    private final int width;

    public DropdownMenu(int anchorX, int anchorY, List<Item> items) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.items = List.copyOf(items);
        this.width = computeWidth(this.items);
    }

    /**
     * Width formula factored out so {@link #spawnSubmenu} can size + position a child menu before constructing it. When
     * any item has a submenu, we reserve extra horizontal space for the {@link #SUBMENU_INDICATOR} so labels never
     * overlap it.
     */
    private static int computeWidth(List<Item> items) {
        var font = EngineFont.get();
        var maxLabelWidth = 0;
        var anySubmenu = false;
        for (var item : items) {
            if (item.separator()) {
                continue;
            }
            maxLabelWidth = Math.max(maxLabelWidth, font.width(item.label()));
            if (item.hasSubmenu()) {
                anySubmenu = true;
            }
        }
        var indicatorReserve = anySubmenu ? font.width(SUBMENU_INDICATOR) + SUBMENU_INDICATOR_GAP : 0;
        return maxLabelWidth + 2 * PADDING_X + indicatorReserve;
    }

    /**
     * Build a child menu anchored to the right of {@code parent}'s item at {@code parentItemIndex}, flipped to the left
     * if the right side would overflow the viewport, and vertically shifted up if the bottom would overflow. Caller
     * owns the returned menu (parent + child lifecycle is tracked by {@link com.blib.engine.ui.EngineWorkspaceScreen}).
     */
    public static DropdownMenu spawnSubmenu(
        DropdownMenu parent,
        int parentItemIndex,
        List<Item> items,
        int viewportWidth,
        int viewportHeight
    ) {
        var width = computeWidth(items);
        var height = computeHeight(items);
        var x = parent.anchorX + parent.width;
        if (x + width > viewportWidth) {
            x = parent.anchorX - width;
        }
        var y = parent.anchorY + BORDER_THICKNESS + itemOffset(parent.items, parentItemIndex);
        if (y + height > viewportHeight) {
            y = viewportHeight - height;
        }
        if (y < 0) {
            y = 0;
        }
        return new DropdownMenu(x, y, items);
    }

    public int width() {
        return width;
    }

    public int height() {
        return computeHeight(items);
    }

    public boolean isInside(double mouseX, double mouseY) {
        return mouseX >= anchorX
            && mouseX < anchorX + width
            && mouseY >= anchorY
            && mouseY < anchorY + height();
    }

    /** Returns the index of the item under {@code (mouseX, mouseY)}, or -1 if none. */
    public int hitItemAt(double mouseX, double mouseY) {
        if (!isInside(mouseX, mouseY)) {
            return -1;
        }
        var localY = (int) (mouseY - anchorY - BORDER_THICKNESS);
        var y = 0;
        for (var i = 0; i < items.size(); i++) {
            var itemHeight = itemHeight(items.get(i));
            if (localY >= y && localY < y + itemHeight) {
                return i;
            }
            y += itemHeight;
        }
        return -1;
    }

    public Item itemAt(int index) {
        return items.get(index);
    }

    public @Nullable Component disabledTooltipAt(double mouseX, double mouseY) {
        var idx = hitItemAt(mouseX, mouseY);
        if (idx < 0) {
            return null;
        }
        var item = itemAt(idx);
        return item.enabled() || item.separator() ? null : item.disabledTooltip();
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        var height = height();
        graphics.fill(anchorX, anchorY, anchorX + width, anchorY + height, BACKGROUND_COLOR);

        // Border
        graphics.fill(anchorX, anchorY, anchorX + width, anchorY + BORDER_THICKNESS, BORDER_COLOR);
        graphics.fill(anchorX, anchorY + height - BORDER_THICKNESS, anchorX + width, anchorY + height, BORDER_COLOR);
        graphics.fill(anchorX, anchorY, anchorX + BORDER_THICKNESS, anchorY + height, BORDER_COLOR);
        graphics.fill(anchorX + width - BORDER_THICKNESS, anchorY, anchorX + width, anchorY + height, BORDER_COLOR);

        var font = EngineFont.get();
        var itemY = anchorY + BORDER_THICKNESS;
        for (var i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var itemHeight = itemHeight(item);
            if (item.separator()) {
                var lineY = itemY + itemHeight / 2;
                graphics.fill(anchorX + PADDING_X, lineY, anchorX + width - PADDING_X, lineY + 1, BORDER_COLOR);
                itemY += itemHeight;
                continue;
            }
            var hovered = item.enabled()
                && mouseX >= anchorX
                && mouseX < anchorX + width
                && mouseY >= itemY
                && mouseY < itemY + itemHeight;
            if (hovered) {
                graphics.fill(
                    anchorX + BORDER_THICKNESS,
                    itemY,
                    anchorX + width - BORDER_THICKNESS,
                    itemY + itemHeight,
                    ITEM_HOVER_BG_COLOR
                );
            }
            var labelY = itemY + (itemHeight - font.lineHeight + 2) / 2;
            graphics.drawString(
                font,
                Component.literal(item.label()),
                anchorX + PADDING_X,
                // +2 compensates for MC font's descender padding so item labels visually center; see MenuBarPanel.
                labelY,
                item.enabled() ? ITEM_TEXT_COLOR : ITEM_DISABLED_TEXT_COLOR,
                false
            );
            if (item.hasSubmenu()) {
                var indWidth = font.width(SUBMENU_INDICATOR);
                graphics.drawString(
                    font,
                    Component.literal(SUBMENU_INDICATOR),
                    anchorX + width - PADDING_X - indWidth,
                    labelY,
                    item.enabled() ? ITEM_TEXT_COLOR : ITEM_DISABLED_TEXT_COLOR,
                    false
                );
            }
            itemY += itemHeight;
        }
    }

    private static int computeHeight(List<Item> items) {
        var height = 2 * BORDER_THICKNESS;
        for (var item : items) {
            height += itemHeight(item);
        }
        return height;
    }

    private static int itemHeight(Item item) {
        return item.separator() ? DIVIDER_HEIGHT : ITEM_HEIGHT;
    }

    private static int itemOffset(List<Item> items, int targetIndex) {
        var y = 0;
        for (var i = 0; i < targetIndex && i < items.size(); i++) {
            y += itemHeight(items.get(i));
        }
        return y;
    }
}
