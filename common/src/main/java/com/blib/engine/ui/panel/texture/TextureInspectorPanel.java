package com.blib.engine.ui.panel.texture;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TextureTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.layout.VerticalLayout;
import com.blib.engine.ui.widget.ScrollContainer;

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

    private final ScrollViewport scroll = new ScrollViewport();

    private int panelX, panelY, panelWidth, panelHeight;

    @Override
    public String title() {
        return "Texture Inspector";
    }

    @Override
    public Panel.TabIndicator tabIndicator() {
        return TextureTabIndicators.activeTextureDirty();
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
        var measuredWidth = Math.max(0, width - ScrollContainer.SCROLLBAR_GUTTER);
        var frame = scroll.begin(graphics, UiRect.of(x, y, width, height), measureContentHeight(measuredWidth));
        try {
            var layout = new VerticalLayout(frame.contentX(), frame.contentY() + PADDING, frame.contentWidth());
            drawSection(graphics, layout.take(SECTION_HEIGHT, GAP), "Tools");
            drawToolButtons(graphics, layout, mouseX, mouseY);
            layout.gap(GAP);
            drawSection(graphics, layout.take(SECTION_HEIGHT, GAP), "Primary Color");
            drawSwatches(graphics, layout, mouseX, mouseY);
            layout.gap(GAP);
            drawSection(graphics, layout.take(SECTION_HEIGHT, GAP), "Texture");
            drawTextureSummary(graphics, layout);
            layout.gap(GAP);
            drawSection(graphics, layout.take(SECTION_HEIGHT, GAP), "Selection");
            drawSelectionSummary(graphics, layout);
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
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
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int measureContentHeight(int width) {
        var h = PADDING;
        h += SECTION_HEIGHT + GAP;
        h += TextureTool.values().length * (ROW_HEIGHT + 2) + GAP;
        h += SECTION_HEIGHT + GAP;
        h += swatchesHeight(width) + GAP;
        h += SECTION_HEIGHT + GAP;
        h += 2 * ROW_HEIGHT + GAP;
        h += SECTION_HEIGHT + GAP;
        h += TextureEditorState.selection() == null ? ROW_HEIGHT : 2 * ROW_HEIGHT;
        return h + PADDING;
    }

    private static int swatchesHeight(int width) {
        var perRow = Math.max(1, (Math.max(0, width - 2 * PADDING) + GAP) / (SWATCH_SIZE + GAP));
        var rows = Math.max(1, (SWATCHES.length + perRow - 1) / perRow);
        return rows * SWATCH_SIZE + Math.max(0, rows - 1) * GAP + GAP + ROW_HEIGHT + GAP;
    }

    private void drawSection(GuiGraphics graphics, UiRect rect, String label) {
        var font = EngineFont.get();
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), SECTION_BG_COLOR);
        UiText.drawClipped(
            graphics,
            font,
            label,
            rect.x() + PADDING,
            rect.y() + 3,
            Math.max(0, rect.width() - 2 * PADDING),
            SECTION_TEXT_COLOR
        );
    }

    private void drawToolButtons(GuiGraphics graphics, VerticalLayout layout, int mouseX, int mouseY) {
        var tool = TextureEditorState.tool();
        for (var option : TextureTool.values()) {
            var row = layout.take(ROW_HEIGHT, 2).inset(0, PADDING, 0, PADDING);
            var active = option == tool;
            var hovered = row.contains(mouseX, mouseY);
            drawButton(graphics, row, option.displayName(), active, hovered);
            toolButtons.add(new ToolButton(row.x(), row.y(), row.width(), row.height(), option));
        }
    }

    private void drawSwatches(GuiGraphics graphics, VerticalLayout layout, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var startY = layout.y();
        var origin = layout.take(0);
        var contentX = origin.x() + PADDING;
        var contentW = Math.max(0, origin.width() - 2 * PADDING);
        var current = TextureEditorState.primaryColor();
        var sx = contentX;
        var sy = startY;
        var maxX = contentX + contentW;
        for (var argb : SWATCHES) {
            if (sx != contentX && sx + SWATCH_SIZE > maxX) {
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
        UiText.drawClipped(graphics, font, label, contentX, labelY + 3, contentW, TEXT_COLOR);
        while (layout.y() < labelY + ROW_HEIGHT + GAP) {
            layout.gap(labelY + ROW_HEIGHT + GAP - layout.y());
        }
    }

    private void drawTextureSummary(GuiGraphics graphics, VerticalLayout layout) {
        var font = EngineFont.get();
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        var name = active == null ? "(none)" : active.displayName();
        var dims = pixels == null ? "" : pixels.getWidth() + " x " + pixels.getHeight();
        drawLabelValue(graphics, font, layout.take(ROW_HEIGHT).inset(0, PADDING, 0, PADDING), "Name", name);
        drawLabelValue(graphics, font, layout.take(ROW_HEIGHT).inset(0, PADDING, 0, PADDING), "Size", dims.isEmpty() ? "-" : dims);
    }

    private void drawSelectionSummary(GuiGraphics graphics, VerticalLayout layout) {
        var font = EngineFont.get();
        var selection = TextureEditorState.selection();
        if (selection == null) {
            drawLabelValue(graphics, font, layout.take(ROW_HEIGHT).inset(0, PADDING, 0, PADDING), "Region", "(none)");
            return;
        }
        drawLabelValue(
            graphics,
            font,
            layout.take(ROW_HEIGHT).inset(0, PADDING, 0, PADDING),
            "Origin",
            selection.x0() + ", " + selection.y0()
        );
        drawLabelValue(
            graphics,
            font,
            layout.take(ROW_HEIGHT).inset(0, PADDING, 0, PADDING),
            "Size",
            selection.width() + " x " + selection.height()
        );
    }

    private void drawLabelValue(GuiGraphics graphics, Font font, UiRect row, String label, String value) {
        UiText.drawLabelValue(graphics, font, row, label, value, 48, 5, LABEL_COLOR, TEXT_COLOR);
    }

    private void drawButton(GuiGraphics graphics, UiRect rect, String label, boolean active, boolean hovered) {
        var font = EngineFont.get();
        var bg = active ? BUTTON_ACTIVE_BG : hovered ? BUTTON_HOVER_BG : BUTTON_BG;
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), bg);
        drawBorder(graphics, rect.x(), rect.y(), rect.width(), rect.height(), active ? ACTIVE_BORDER_COLOR : BORDER_COLOR);
        UiText.drawCentered(graphics, font, label, rect.inset(0, 3, 0, 3), TEXT_COLOR);
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
