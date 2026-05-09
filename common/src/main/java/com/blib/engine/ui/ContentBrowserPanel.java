package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibItemTransformOverrides;

/**
 * Bottom-center content browser. Lists the currently-registered tunable item ids as a placeholder for a future asset
 * grid. The list is read live from {@link BLibItemTransformOverrides} so the user can see what's available to edit.
 */
@ApiStatus.Internal
public final class ContentBrowserPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int TILE_BG_COLOR = 0xFF22222A;

    private static final int TILE_TEXT_COLOR = 0xFFD0D0D8;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int TILE_WIDTH = 90;

    private static final int TILE_HEIGHT = 20;

    private static final int TILE_GAP = 4;

    private static final int TILE_PADDING = 4;

    @Override
    public String title() {
        return "Content Browser";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var ids = BLibItemTransformOverrides.tunableItemIds();

        if (ids.isEmpty()) {
            graphics.drawString(
                font,
                Component.literal("(no tunable items registered)"),
                x + CONTENT_PADDING,
                y + CONTENT_PADDING,
                EMPTY_TEXT_COLOR,
                false
            );
            return;
        }

        int columns = Math.max(1, (width - 2 * CONTENT_PADDING + TILE_GAP) / (TILE_WIDTH + TILE_GAP));
        int index = 0;

        for (var id : ids) {
            var col = index % columns;
            var row = index / columns;
            var tileX = x + CONTENT_PADDING + col * (TILE_WIDTH + TILE_GAP);
            var tileY = y + CONTENT_PADDING + row * (TILE_HEIGHT + TILE_GAP);

            if (tileY + TILE_HEIGHT > y + height - CONTENT_PADDING) {
                break;
            }

            graphics.fill(tileX, tileY, tileX + TILE_WIDTH, tileY + TILE_HEIGHT, TILE_BG_COLOR);

            var label = id.getPath();
            var truncated = font.plainSubstrByWidth(label, TILE_WIDTH - 2 * TILE_PADDING);
            graphics.drawString(
                font,
                Component.literal(truncated),
                tileX + TILE_PADDING,
                // +2 compensates for MC font's descender padding so tile labels visually center; see MenuBarPanel.
                tileY + (TILE_HEIGHT - font.lineHeight + 2) / 2,
                TILE_TEXT_COLOR,
                false
            );

            index++;
        }
    }
}
