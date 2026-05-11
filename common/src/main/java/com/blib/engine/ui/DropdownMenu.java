package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Popup menu rendered above the workspace's panels — a vertical stack of clickable items anchored at a logical-px
 * position (typically just below the menu-bar chip that spawned it). The {@link EngineWorkspaceScreen} owns the
 * lifecycle (one open at a time); this class is just the visual + hit testing.
 */
@ApiStatus.Internal
public final class DropdownMenu {

    public record Item(
        String label,
        Runnable action
    ) {}

    public static final int ITEM_HEIGHT = 14;

    private static final int PADDING_X = 8;

    private static final int BORDER_THICKNESS = 1;

    private static final int BACKGROUND_COLOR = 0xF01A1A1F;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int ITEM_HOVER_BG_COLOR = 0xFF353540;

    private static final int ITEM_TEXT_COLOR = 0xFFD0D0D0;

    private final int anchorX;

    private final int anchorY;

    private final List<Item> items;

    private final int width;

    public DropdownMenu(int anchorX, int anchorY, List<Item> items) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.items = List.copyOf(items);

        var font = EngineFont.get();
        var maxLabelWidth = 0;
        for (var item : this.items) {
            maxLabelWidth = Math.max(maxLabelWidth, font.width(item.label()));
        }
        this.width = maxLabelWidth + 2 * PADDING_X;
    }

    public int width() {
        return width;
    }

    public int height() {
        return items.size() * ITEM_HEIGHT + 2 * BORDER_THICKNESS;
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
        var idx = localY / ITEM_HEIGHT;
        if (idx < 0 || idx >= items.size()) {
            return -1;
        }
        return idx;
    }

    public Item itemAt(int index) {
        return items.get(index);
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
        for (var i = 0; i < items.size(); i++) {
            var itemY = anchorY + BORDER_THICKNESS + i * ITEM_HEIGHT;
            var hovered = mouseX >= anchorX && mouseX < anchorX + width && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;
            if (hovered) {
                graphics.fill(
                    anchorX + BORDER_THICKNESS,
                    itemY,
                    anchorX + width - BORDER_THICKNESS,
                    itemY + ITEM_HEIGHT,
                    ITEM_HOVER_BG_COLOR
                );
            }
            graphics.drawString(
                font,
                Component.literal(items.get(i).label()),
                anchorX + PADDING_X,
                // +2 compensates for MC font's descender padding so item labels visually center; see MenuBarPanel.
                itemY + (ITEM_HEIGHT - font.lineHeight + 2) / 2,
                ITEM_TEXT_COLOR,
                false
            );
        }
    }
}
