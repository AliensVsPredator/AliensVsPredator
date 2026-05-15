package com.blib.engine.ui.panel.texture;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.TextureLoader;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;

/**
 * Lists the PNG textures the user has imported via the "Load Texture…" button. Clicking a row sets
 * {@link ModelerScene#activeTexture} so the modeler renderer, UV map overlay, and texture editor pick it up; clicking
 * the active row toggles it off.
 * <p>
 * Thumbnails are rendered by re-blitting each entry's already-registered
 * {@link net.minecraft.resources.ResourceLocation} scaled to the row's thumb rect — no separate thumbnail generation.
 * The file dialog is native (LWJGL TinyFileDialogs) via {@code ModelerFilePicker.pickImage()} and blocks the render
 * thread until dismissed, which is fine since Minecraft pauses anyway while a native modal is up.
 */
@ApiStatus.Internal
public final class TexturesPanel implements Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TexturesPanel.class);

    private static final int BG_COLOR = 0xFF18181C;

    private static final int BUTTON_COLOR = 0xFF2A2A32;

    private static final int BUTTON_HOVER_COLOR = 0xFF3A3A48;

    private static final int BUTTON_BORDER_COLOR = 0xFF505058;

    private static final int BUTTON_ICON_COLOR = 0xFFD0D0D0;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int THUMB_BG_COLOR = 0xFF101014;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int PADDING = 6;

    private static final int BUTTON_SIZE = 16;

    private static final int ROW_HEIGHT = 22;

    private static final int THUMB_SIZE = 18;

    private final ScrollViewport scroll = new ScrollViewport();

    private @Nullable Component hoveredTooltip;

    private int panelX, panelY, panelWidth, panelHeight;

    private int buttonX, buttonY, buttonWidth;

    private int rowsTopY, rowsLeftX, rowsViewportHeight, rowsContentWidth;

    @Override
    public String title() {
        return "Textures";
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        hoveredTooltip = null;
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var scene = ModelerScene.get();
        var font = EngineFont.get();

        buttonX = x + PADDING;
        buttonY = y + PADDING;
        buttonWidth = BUTTON_SIZE;
        var buttonHovered = mouseX >= buttonX && mouseX < buttonX + buttonWidth && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE;
        if (buttonHovered) {
            hoveredTooltip = Component.literal("Load texture");
        }
        graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + BUTTON_SIZE, buttonHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
        graphics.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + 1, BUTTON_BORDER_COLOR);
        graphics.fill(buttonX, buttonY + BUTTON_SIZE - 1, buttonX + buttonWidth, buttonY + BUTTON_SIZE, BUTTON_BORDER_COLOR);
        graphics.fill(buttonX, buttonY, buttonX + 1, buttonY + BUTTON_SIZE, BUTTON_BORDER_COLOR);
        graphics.fill(buttonX + buttonWidth - 1, buttonY, buttonX + buttonWidth, buttonY + BUTTON_SIZE, BUTTON_BORDER_COLOR);
        drawPlusIcon(graphics, buttonX, buttonY);

        // Rows region: below the button (with one PADDING gap), extending to the bottom of the panel.
        rowsTopY = buttonY + BUTTON_SIZE + PADDING;
        rowsLeftX = x + PADDING;
        rowsViewportHeight = Math.max(0, (y + height) - rowsTopY - PADDING);
        var innerWidth = Math.max(0, width - 2 * PADDING);

        var rows = scene.textures;
        var contentHeight = rows.size() * ROW_HEIGHT;
        var frame = scroll.begin(graphics, UiRect.of(rowsLeftX, rowsTopY, innerWidth, rowsViewportHeight), contentHeight);
        rowsContentWidth = frame.contentWidth();
        try {
            var scrollY = frame.scrollY();
            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var rowTop = rowsTopY - scrollY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= rowsTopY || rowTop >= rowsTopY + rowsViewportHeight) {
                    continue;
                }
                var selected = scene.activeTexture == row;
                var hovered = mouseX >= rowsLeftX
                    && mouseX < rowsLeftX + rowsContentWidth
                    && mouseY >= rowTop
                    && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_HOVER_COLOR);
                }

                var thumbX = rowsLeftX + 2;
                var thumbY = rowTop + (ROW_HEIGHT - THUMB_SIZE) / 2;
                graphics.fill(thumbX, thumbY, thumbX + THUMB_SIZE, thumbY + THUMB_SIZE, THUMB_BG_COLOR);
                graphics.blit(row.textureId(), thumbX, thumbY, 0, 0, THUMB_SIZE, THUMB_SIZE, THUMB_SIZE, THUMB_SIZE);

                var labelX = thumbX + THUMB_SIZE + 6;
                var labelY = rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2;
                var nameMaxWidth = rowsLeftX + rowsContentWidth - labelX - 2;
                UiText.drawClipped(graphics, font, row.displayName(), labelX, labelY, nameMaxWidth, TEXT_COLOR);
            }
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
        if (mouseX >= buttonX && mouseX < buttonX + buttonWidth && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE) {
            openTexturePicker();
            return true;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseY < rowsTopY || mouseY >= rowsTopY + rowsViewportHeight) {
            return false;
        }
        if (mouseX < rowsLeftX || mouseX >= rowsLeftX + rowsContentWidth) {
            return false;
        }

        var contentY = (int) (mouseY - rowsTopY) + (int) scroll.scrollY();
        if (contentY < 0) {
            return false;
        }
        var idx = contentY / ROW_HEIGHT;
        var scene = ModelerScene.get();
        if (idx < 0 || idx >= scene.textures.size()) {
            return false;
        }
        var clicked = scene.textures.get(idx);
        scene.activeTexture = scene.activeTexture == clicked ? null : clicked;
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
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private void openTexturePicker() {
        var paths = ModelerFilePicker.pickImages();
        if (paths.isEmpty()) {
            return;
        }
        var scene = ModelerScene.get();
        LoadedTexture lastLoaded = null;
        for (var path : paths) {
            var loaded = TextureLoader.loadFromDisk(path);
            if (loaded == null) {
                continue;
            }
            scene.textures.add(loaded);
            lastLoaded = loaded;
            LOGGER.info("TexturesPanel: loaded {} → {}", path, loaded.textureId());
        }
        // Auto-select the last successfully loaded texture so the user immediately sees something on the model and
        // overlay; if every load failed (e.g. all corrupt PNGs), keep the previous active texture untouched.
        if (lastLoaded != null) {
            scene.activeTexture = lastLoaded;
        }
    }

    private static void drawPlusIcon(GuiGraphics graphics, int x, int y) {
        var cx = x + BUTTON_SIZE / 2;
        var cy = y + BUTTON_SIZE / 2;
        graphics.fill(cx - 4, cy, cx + 5, cy + 1, BUTTON_ICON_COLOR);
        graphics.fill(cx, cy - 4, cx + 1, cy + 5, BUTTON_ICON_COLOR);
    }

}
