package com.blib.engine.ui.panel.texture;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TextureTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;

@ApiStatus.Internal
public final class TextureInspectorPanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int SECTION_BG_COLOR = 0xFF26262C;

    private static final int SECTION_TEXT_COLOR = 0xFFB8C0D0;

    private static final int BUTTON_BG = 0xFF202028;

    private static final int BUTTON_HOVER_BG = 0xFF2A2A36;

    private static final int BUTTON_ACTIVE_BG = 0xFF3A3A48;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int ACTIVE_BORDER_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int SECTION_HEIGHT = 12;

    private static final int ROW_HEIGHT = 16;

    private static final int GAP = 4;

    private static final int SWATCH_SIZE = 16;

    private static final int[] SWATCHES = {
        0xFF202020,
        0xFFFFFFFF,
        0xFFE74C3C,
        0xFFFFC857,
        0xFF4CD964,
        0xFF4F8FFF,
        0xFF8E5CF7,
        0x00000000
    };

    private final List<ToolButton> toolButtons = new ArrayList<>();

    private final List<SwatchButton> swatchButtons = new ArrayList<>();

    private int panelX, panelY, panelWidth, panelHeight;

    @Override
    public String title() {
        return "Texture Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        toolButtons.clear();
        swatchButtons.clear();

        graphics.fill(x, y, x + width, y + height, BG_COLOR);
        var cursorY = y + PADDING;
        cursorY = drawSection(graphics, x, cursorY, width, "Tools");
        cursorY = drawToolButtons(graphics, x, cursorY, width, mouseX, mouseY) + GAP;
        cursorY = drawSection(graphics, x, cursorY, width, "Primary Color");
        cursorY = drawSwatches(graphics, x, cursorY, width, mouseX, mouseY) + GAP;
        cursorY = drawSection(graphics, x, cursorY, width, "Texture");
        cursorY = drawTextureSummary(graphics, x, cursorY, width) + GAP;
        cursorY = drawSection(graphics, x, cursorY, width, "Selection");
        drawSelectionSummary(graphics, x, cursorY, width);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        for (var toolButton : toolButtons) {
            if (toolButton.contains(mouseX, mouseY)) {
                TextureEditorState.setTool(toolButton.tool());
                return true;
            }
        }
        for (var swatch : swatchButtons) {
            if (swatch.contains(mouseX, mouseY)) {
                TextureEditorState.setPrimaryColor(swatch.argb());
                return true;
            }
        }
        return true;
    }

    private int drawSection(GuiGraphics graphics, int x, int y, int width, String label) {
        var font = EngineFont.get();
        graphics.fill(x, y, x + width, y + SECTION_HEIGHT, SECTION_BG_COLOR);
        graphics.drawString(font, Component.literal(label), x + PADDING, y + 3, SECTION_TEXT_COLOR, false);
        return y + SECTION_HEIGHT + GAP;
    }

    private int drawToolButtons(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        var tool = TextureEditorState.tool();
        var contentX = x + PADDING;
        var contentW = Math.max(0, width - 2 * PADDING);
        for (var option : TextureTool.values()) {
            var active = option == tool;
            var hovered = mouseX >= contentX && mouseX < contentX + contentW && mouseY >= y && mouseY < y + ROW_HEIGHT;
            drawButton(graphics, contentX, y, contentW, ROW_HEIGHT, option.displayName(), active, hovered);
            toolButtons.add(new ToolButton(contentX, y, contentW, ROW_HEIGHT, option));
            y += ROW_HEIGHT + 2;
        }
        return y;
    }

    private int drawSwatches(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var contentX = x + PADDING;
        var current = TextureEditorState.primaryColor();
        var sx = contentX;
        var sy = y;
        var maxX = x + width - PADDING;
        for (var argb : SWATCHES) {
            if (sx + SWATCH_SIZE > maxX) {
                sx = contentX;
                sy += SWATCH_SIZE + GAP;
            }
            var hovered = mouseX >= sx && mouseX < sx + SWATCH_SIZE && mouseY >= sy && mouseY < sy + SWATCH_SIZE;
            drawSwatch(graphics, sx, sy, argb, argb == current, hovered);
            swatchButtons.add(new SwatchButton(sx, sy, SWATCH_SIZE, SWATCH_SIZE, argb));
            sx += SWATCH_SIZE + GAP;
        }
        var labelY = sy + SWATCH_SIZE + GAP;
        var label = String.format(Locale.ROOT, "#%08X", current);
        graphics.drawString(font, Component.literal(label), contentX, labelY + 3, TEXT_COLOR, false);
        return labelY + ROW_HEIGHT + GAP;
    }

    private int drawTextureSummary(GuiGraphics graphics, int x, int y, int width) {
        var font = EngineFont.get();
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        var name = active == null ? "(none)" : active.displayName();
        var dims = pixels == null ? "" : pixels.getWidth() + " x " + pixels.getHeight();
        drawLabelValue(graphics, font, x, y, width, "Name", name);
        y += ROW_HEIGHT;
        drawLabelValue(graphics, font, x, y, width, "Size", dims.isEmpty() ? "-" : dims);
        return y + ROW_HEIGHT;
    }

    private void drawSelectionSummary(GuiGraphics graphics, int x, int y, int width) {
        var font = EngineFont.get();
        var selection = TextureEditorState.selection();
        if (selection == null) {
            drawLabelValue(graphics, font, x, y, width, "Region", "(none)");
            return;
        }
        drawLabelValue(graphics, font, x, y, width, "Origin", selection.x0() + ", " + selection.y0());
        y += ROW_HEIGHT;
        drawLabelValue(graphics, font, x, y, width, "Size", selection.width() + " x " + selection.height());
    }

    private void drawLabelValue(GuiGraphics graphics, net.minecraft.client.gui.Font font, int x, int y, int width, String label, String value) {
        var labelX = x + PADDING;
        var valueX = x + 58;
        var maxValueWidth = Math.max(0, x + width - PADDING - valueX);
        graphics.drawString(font, Component.literal(label), labelX, y + 4, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(trimToWidth(font, value, maxValueWidth)), valueX, y + 4, TEXT_COLOR, false);
    }

    private void drawButton(GuiGraphics graphics, int x, int y, int width, int height, String label, boolean active, boolean hovered) {
        var font = EngineFont.get();
        var bg = active ? BUTTON_ACTIVE_BG : hovered ? BUTTON_HOVER_BG : BUTTON_BG;
        graphics.fill(x, y, x + width, y + height, bg);
        drawBorder(graphics, x, y, width, height, active ? ACTIVE_BORDER_COLOR : BORDER_COLOR);
        var labelW = font.width(label);
        graphics.drawString(font, Component.literal(label), x + (width - labelW) / 2, y + 4, TEXT_COLOR, false);
    }

    private void drawSwatch(GuiGraphics graphics, int x, int y, int argb, boolean active, boolean hovered) {
        if ((argb >>> 24) == 0) {
            graphics.fill(x, y, x + SWATCH_SIZE / 2, y + SWATCH_SIZE / 2, 0xFFB8B8C0);
            graphics.fill(x + SWATCH_SIZE / 2, y, x + SWATCH_SIZE, y + SWATCH_SIZE / 2, 0xFF707078);
            graphics.fill(x, y + SWATCH_SIZE / 2, x + SWATCH_SIZE / 2, y + SWATCH_SIZE, 0xFF707078);
            graphics.fill(x + SWATCH_SIZE / 2, y + SWATCH_SIZE / 2, x + SWATCH_SIZE, y + SWATCH_SIZE, 0xFFB8B8C0);
        } else {
            graphics.fill(x, y, x + SWATCH_SIZE, y + SWATCH_SIZE, argb);
        }
        drawBorder(graphics, x, y, SWATCH_SIZE, SWATCH_SIZE, active ? ACTIVE_BORDER_COLOR : hovered ? TEXT_COLOR : BORDER_COLOR);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    private static String trimToWidth(net.minecraft.client.gui.Font font, @Nullable String value, int width) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return width > 0 ? font.plainSubstrByWidth(value, width) : "";
    }

    private record ToolButton(
        int x,
        int y,
        int w,
        int h,
        TextureTool tool
    ) {

        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    private record SwatchButton(
        int x,
        int y,
        int w,
        int h,
        int argb
    ) {

        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }
}
