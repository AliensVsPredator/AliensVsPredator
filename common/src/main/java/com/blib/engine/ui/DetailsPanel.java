package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibGizmoState;

/**
 * Right-side properties / inspector panel. Reads the currently-tuned gizmo target (item id + transform mode) from
 * {@link BLibGizmoState} and surfaces the data as labeled rows. Static text for now; future iterations make these
 * editable fields backed by {@code BLibItemTransformOverrides}.
 */
@ApiStatus.Internal
public final class DetailsPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF18181C;

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int VALUE_COLOR = 0xFFD8D8E0;

    private static final int HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int CONTENT_PADDING = 5;

    private static final int LINE_HEIGHT = 10;

    private static final int LABEL_COLUMN_WIDTH = 50;

    private static final int SECTION_HEADER_HEIGHT = 11;

    @Override
    public String title() {
        return "Details";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var snapshot = BLibGizmoState.lastRender();
        var rowY = y;

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Selection");

        if (snapshot == null) {
            rowY += CONTENT_PADDING;
            graphics.drawString(font, Component.literal("(no selection)"), x + CONTENT_PADDING, rowY, LABEL_COLOR, false);
            return;
        }

        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, width, "Item", snapshot.itemId().toString());
        rowY = drawRow(graphics, font, x, rowY, width, "Mode", snapshot.mode().name());
        rowY = drawRow(graphics, font, x, rowY, width, "Context", snapshot.displayContext().name());
    }

    private static int drawSectionHeader(GuiGraphics graphics, net.minecraft.client.gui.Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        graphics.drawString(
            font,
            Component.literal(label),
            x + CONTENT_PADDING,
            // +2 compensates for MC font's descender padding so section headers visually center; see MenuBarPanel.
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            HEADER_TEXT_COLOR,
            false
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static int drawRow(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        int x,
        int y,
        int width,
        String label,
        String value
    ) {
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(value), x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, y, VALUE_COLOR, false);
        return y + LINE_HEIGHT;
    }
}
