package com.blib.engine.ui.panel.texture;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.TextureLoader;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Lists the PNG textures the user has imported via the import button. Clicking a row sets
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

    private static final int META_TEXT_COLOR = 0xFF808088;

    private static final int RESOLUTION_TEXT_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int BUTTON_SIZE = 16;

    private static final int ROW_HEIGHT = 34;

    private static final int THUMB_SIZE = 28;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener panelMenuOpener;

    private static @Nullable Path copiedTexturePath;

    private @Nullable Component hoveredTooltip;

    private int panelX, panelY, panelWidth, panelHeight;

    private int buttonX, buttonY, buttonWidth;

    private int rowsTopY, rowsLeftX, rowsViewportHeight, rowsContentWidth;

    public TexturesPanel() {
        this(null);
    }

    public TexturesPanel(@Nullable PanelMenuOpener panelMenuOpener) {
        this.panelMenuOpener = panelMenuOpener;
    }

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
                var labelY = rowTop + 5;
                var nameMaxWidth = rowsLeftX + rowsContentWidth - labelX - 2;
                UiText.drawClipped(graphics, font, row.displayName(), labelX, labelY, nameMaxWidth, TEXT_COLOR);

                var metaY = labelY + font.lineHeight + 1;
                drawTextureMeta(graphics, font, row, scene, labelX, metaY, nameMaxWidth);
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        if (button == 1) {
            return openTextureContextMenu(mouseX, mouseY);
        }
        if (button != 0) {
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

        var idx = rowIndexAt(mouseX, mouseY);
        var scene = ModelerScene.get();
        if (idx < 0 || idx >= scene.textures.size()) {
            return false;
        }
        var clicked = scene.textures.get(idx);
        scene.activeTexture = scene.activeTexture == clicked ? null : clicked;
        return true;
    }

    private boolean openTextureContextMenu(double mouseX, double mouseY) {
        var texture = textureAt(mouseX, mouseY);
        if (texture == null) {
            return openEmptyTextureContextMenu(mouseX, mouseY);
        }
        if (panelMenuOpener == null) {
            return true;
        }

        var copyTexture = new DropdownMenu.Item("Copy", () -> copyTexture(texture));
        var duplicateTexture = new DropdownMenu.Item("Duplicate", () -> duplicateTexture(texture));
        var sourceFileAvailable = Files.isRegularFile(texture.sourcePath());
        var refreshTexture = new DropdownMenu.Item(
            "Refresh",
            () -> refreshTexture(texture),
            sourceFileAvailable,
            Component.literal("The source file is no longer available.")
        );
        var deleteTexture = new DropdownMenu.Item("Delete", () -> deleteTexture(texture));
        var sourceDir = sourceDirectory(texture);
        var openSource = new DropdownMenu.Item(
            "Open in File Explorer",
            () -> openTextureSourceFolder(texture),
            sourceDir != null,
            Component.literal("The source folder is no longer available.")
        );
        panelMenuOpener.open(
            new DropdownMenu((int) mouseX, (int) mouseY, List.of(copyTexture, duplicateTexture, refreshTexture, deleteTexture, openSource))
        );
        return true;
    }

    private boolean openEmptyTextureContextMenu(double mouseX, double mouseY) {
        if (!isRowsArea(mouseX, mouseY)) {
            return false;
        }
        if (panelMenuOpener == null) {
            return true;
        }

        var pasteEnabled = copiedTexturePath != null && Files.isRegularFile(copiedTexturePath);
        var disabledTooltip = copiedTexturePath == null
            ? Component.literal("Copy a texture first.")
            : Component.literal("The copied texture source file is no longer available.");
        var pasteTexture = new DropdownMenu.Item(
            "Paste",
            TexturesPanel::pasteCopiedTexture,
            pasteEnabled,
            disabledTooltip
        );
        panelMenuOpener.open(new DropdownMenu((int) mouseX, (int) mouseY, List.of(pasteTexture)));
        return true;
    }

    private @Nullable LoadedTexture textureAt(double mouseX, double mouseY) {
        var idx = rowIndexAt(mouseX, mouseY);
        var textures = ModelerScene.get().textures;
        if (idx < 0 || idx >= textures.size()) {
            return null;
        }
        return textures.get(idx);
    }

    private int rowIndexAt(double mouseX, double mouseY) {
        if (!isRowsArea(mouseX, mouseY)) {
            return -1;
        }

        var contentY = (int) (mouseY - rowsTopY) + scroll.scrollY();
        if (contentY < 0) {
            return -1;
        }
        return contentY / ROW_HEIGHT;
    }

    private boolean isRowsArea(double mouseX, double mouseY) {
        return mouseY >= rowsTopY
            && mouseY < rowsTopY + rowsViewportHeight
            && mouseX >= rowsLeftX
            && mouseX < rowsLeftX + rowsContentWidth;
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

    private static void openTextureSourceFolder(LoadedTexture texture) {
        var sourceDir = sourceDirectory(texture);
        if (sourceDir == null) {
            LOGGER.warn("TexturesPanel: source folder is unavailable for {}", texture.sourcePath());
            return;
        }
        Util.getPlatform().openUri(sourceDir.toUri());
    }

    private static void copyTexture(LoadedTexture texture) {
        copiedTexturePath = texture.sourcePath().toAbsolutePath().normalize();
    }

    private static void pasteCopiedTexture() {
        if (copiedTexturePath == null) {
            return;
        }
        loadTextureFromPath(copiedTexturePath);
    }

    private static @Nullable LoadedTexture loadTextureFromPath(Path path) {
        var loaded = TextureLoader.loadFromDisk(path);
        if (loaded == null) {
            return null;
        }
        addLoadedTexture(loaded);
        return loaded;
    }

    private static void duplicateTexture(LoadedTexture texture) {
        var duplicate = TextureLoader.duplicate(texture);
        if (duplicate != null) {
            addLoadedTexture(duplicate);
        }
    }

    private static void deleteTexture(LoadedTexture texture) {
        var scene = ModelerScene.get();
        var idx = scene.textures.indexOf(texture);
        if (idx < 0) {
            return;
        }
        scene.textures.remove(idx);
        if (scene.activeTexture == texture) {
            scene.activeTexture = null;
            TextureEditorState.clearSelection();
        }
        TextureLoader.release(texture);
    }

    private static void refreshTexture(LoadedTexture texture) {
        if (!TextureLoader.reloadFromDisk(texture)) {
            return;
        }
        if (ModelerScene.get().activeTexture == texture) {
            TextureEditorState.clearSelection();
        }
    }

    private static void addLoadedTexture(LoadedTexture loaded) {
        var scene = ModelerScene.get();
        scene.textures.add(loaded);
        scene.activeTexture = loaded;
    }

    private static @Nullable Path sourceDirectory(LoadedTexture texture) {
        var source = texture.sourcePath().toAbsolutePath().normalize();
        var dir = Files.isDirectory(source) ? source : source.getParent();
        if (dir == null || !Files.isDirectory(dir)) {
            return null;
        }
        return dir;
    }

    private static void drawPlusIcon(GuiGraphics graphics, int x, int y) {
        var cx = x + BUTTON_SIZE / 2;
        var cy = y + BUTTON_SIZE / 2;
        graphics.fill(cx - 4, cy, cx + 5, cy + 1, BUTTON_ICON_COLOR);
        graphics.fill(cx, cy - 4, cx + 1, cy + 5, BUTTON_ICON_COLOR);
    }

    private static void drawTextureMeta(
        GuiGraphics graphics,
        Font font,
        LoadedTexture texture,
        ModelerScene scene,
        int x,
        int y,
        int width
    ) {
        var dims = textureDimensions(texture);
        var resolution = textureResolution(texture, scene);
        var gap = 8;
        var resolutionWidth = font.width(resolution);
        var available = Math.max(0, width);
        if (available <= resolutionWidth + gap) {
            UiText.drawClipped(graphics, font, dims, x, y, available, META_TEXT_COLOR);
            return;
        }

        var dimsWidth = Math.min(font.width(dims), available - resolutionWidth - gap);
        UiText.drawClipped(graphics, font, dims, x, y, dimsWidth, META_TEXT_COLOR);
        UiText.drawClipped(graphics, font, resolution, x + dimsWidth + gap, y, resolutionWidth, RESOLUTION_TEXT_COLOR);
    }

    private static String textureDimensions(LoadedTexture texture) {
        var pixels = texture.texture().getPixels();
        if (pixels == null) {
            return "-";
        }
        return pixels.getWidth() + "x" + pixels.getHeight();
    }

    private static String textureResolution(LoadedTexture texture, ModelerScene scene) {
        var pixels = texture.texture().getPixels();
        if (pixels == null) {
            return "-";
        }
        var baseWidth = scene.textureWidth > 0.0 ? scene.textureWidth : pixels.getWidth();
        var baseHeight = scene.textureHeight > 0.0 ? scene.textureHeight : pixels.getHeight();
        var scale = Math.max(pixels.getWidth() / baseWidth, pixels.getHeight() / baseHeight);
        return Math.max(1, (int) Math.round(16.0 * scale)) + "x";
    }

}
