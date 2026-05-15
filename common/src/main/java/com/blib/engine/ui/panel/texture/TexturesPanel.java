package com.blib.engine.ui.panel.texture;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import com.blib.engine.modeler.texture.ModelerTextureUsage;
import com.blib.engine.modeler.texture.TextureLoader;
import com.blib.engine.modeler.texture.TextureResourceCatalog;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.SearchableSelect;
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

    private static final int MENU_BAR_BG_COLOR = 0xFF202024;

    private static final int MENU_BAR_BORDER_COLOR = 0xFF101013;

    private static final int MENU_CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int MENU_CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int THUMB_BG_COLOR = 0xFF101014;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int META_TEXT_COLOR = 0xFF808088;

    private static final int RESOLUTION_TEXT_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int MENU_BAR_HEIGHT = 14;

    private static final int MENU_CHIP_PADDING_X = 4;

    private static final int MENU_EDGE_PADDING = 6;

    private static final String MENU_FILE = "File";

    private static final int ROW_HEIGHT = 34;

    private static final int THUMB_SIZE = 28;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener panelMenuOpener;

    private static @Nullable TextureClipboard copiedTexture;

    private @Nullable Component hoveredTooltip;

    private int panelX, panelY, panelWidth, panelHeight;

    private int menuBarY, menuFileX, menuFileY, menuFileWidth, menuFileHeight;

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

        renderMenuBar(graphics, x, y, width, mouseX, mouseY, font);

        // Rows region: below the File menu strip, extending to the bottom of the panel.
        rowsTopY = y + MENU_BAR_HEIGHT + PADDING;
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
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseY >= menuBarY && mouseY < menuBarY + MENU_BAR_HEIGHT) {
            if (isFileChip(mouseX, mouseY) && panelMenuOpener != null) {
                panelMenuOpener.open(buildFileMenu());
            }
            return true;
        }
        return scroll.mouseClicked(mouseX, mouseY, button);
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
        var sourceAvailable = TextureLoader.canReload(texture);
        var refreshTexture = new DropdownMenu.Item(
            "Refresh",
            () -> refreshTexture(texture),
            sourceAvailable,
            Component.literal("The source texture is no longer available.")
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

        var pasteEnabled = copiedTexture != null && copiedTexture.isAvailable();
        var disabledTooltip = copiedTexture == null
            ? Component.literal("Copy a texture first.")
            : Component.literal("The copied texture source is no longer available.");
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
            LOGGER.warn("TexturesPanel: source folder is unavailable for {}", sourceDescription(texture));
            return;
        }
        Util.getPlatform().openUri(sourceDir.toUri());
    }

    private static void copyTexture(LoadedTexture texture) {
        copiedTexture = TextureClipboard.of(texture);
    }

    private static void pasteCopiedTexture() {
        var clipboard = copiedTexture;
        if (clipboard == null) {
            return;
        }
        clipboard.load();
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
        if (!TextureLoader.reload(texture)) {
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
        if (texture.sourcePath() == null) {
            return null;
        }
        var source = texture.sourcePath().toAbsolutePath().normalize();
        var dir = Files.isDirectory(source) ? source : source.getParent();
        if (dir == null || !Files.isDirectory(dir)) {
            return null;
        }
        return dir;
    }

    private static String sourceDescription(LoadedTexture texture) {
        if (texture.sourcePath() != null) {
            return texture.sourcePath().toString();
        }
        if (texture.sourceResource() != null) {
            return texture.sourceResource().toString();
        }
        return texture.displayName();
    }

    private void renderMenuBar(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY, Font font) {
        menuBarY = y;
        graphics.fill(x, y, x + width, y + MENU_BAR_HEIGHT, MENU_BAR_BG_COLOR);
        graphics.fill(x, y + MENU_BAR_HEIGHT - 1, x + width, y + MENU_BAR_HEIGHT, MENU_BAR_BORDER_COLOR);

        menuFileX = x + MENU_EDGE_PADDING;
        menuFileY = y + 2;
        menuFileWidth = font.width(MENU_FILE) + 2 * MENU_CHIP_PADDING_X;
        menuFileHeight = MENU_BAR_HEIGHT - 4;

        var hovered = isFileChip(mouseX, mouseY);
        graphics.fill(
            menuFileX,
            menuFileY,
            menuFileX + menuFileWidth,
            menuFileY + menuFileHeight,
            hovered ? MENU_CHIP_HOVER_BG_COLOR : MENU_CHIP_BG_COLOR
        );
        var textY = y + (MENU_BAR_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(MENU_FILE), menuFileX + MENU_CHIP_PADDING_X, textY, TEXT_COLOR, false);
    }

    private boolean isFileChip(double mouseX, double mouseY) {
        return mouseX >= menuFileX
            && mouseX < menuFileX + menuFileWidth
            && mouseY >= menuFileY
            && mouseY < menuFileY + menuFileHeight;
    }

    private DropdownMenu buildFileMenu() {
        var openItems = List
            .of(
                new DropdownMenu.Item("From File...", this::openTexturePicker),
                new DropdownMenu.Item("From Item...", this::openItemTexturePicker),
                new DropdownMenu.Item("From Block...", this::openBlockTexturePicker),
                new DropdownMenu.Item("From Entity...", this::openEntityTexturePicker)
            );
        var items = List.of(new DropdownMenu.Item("Open", () -> {}, openItems));
        return new DropdownMenu(menuFileX, menuFileY + menuFileHeight + 1, items);
    }

    private void openItemTexturePicker() {
        var ids = new java.util.ArrayList<ResourceLocation>();
        for (var item : BuiltInRegistries.ITEM) {
            ids.add(BuiltInRegistries.ITEM.getKey(item));
        }
        ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        var items = new java.util.ArrayList<SearchableSelect.Item<ResourceLocation>>(ids.size());
        for (var id : ids) {
            items.add(new SearchableSelect.Item<>(id, id.toString()));
        }

        SearchableSelect
            .openPopupAt(
                menuFileX,
                menuFileY,
                280,
                menuFileHeight,
                items,
                ResourceLocation::toString,
                null,
                this::loadItemTextures
            );
    }

    private void loadItemTextures(ResourceLocation itemId) {
        loadResourceTextures(TextureResourceCatalog.itemTextures(itemId), "item " + itemId);
    }

    private void openBlockTexturePicker() {
        var ids = new java.util.ArrayList<ResourceLocation>();
        for (var block : BuiltInRegistries.BLOCK) {
            ids.add(BuiltInRegistries.BLOCK.getKey(block));
        }
        ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        var items = new java.util.ArrayList<SearchableSelect.Item<ResourceLocation>>(ids.size());
        for (var id : ids) {
            items.add(new SearchableSelect.Item<>(id, id.toString()));
        }

        SearchableSelect
            .openPopupAt(
                menuFileX,
                menuFileY,
                280,
                menuFileHeight,
                items,
                ResourceLocation::toString,
                null,
                this::loadBlockTextures
            );
    }

    private void loadBlockTextures(ResourceLocation blockId) {
        loadResourceTextures(TextureResourceCatalog.blockTextures(blockId), "block " + blockId);
    }

    private void openEntityTexturePicker() {
        var ids = new java.util.ArrayList<ResourceLocation>();
        for (var entityType : BuiltInRegistries.ENTITY_TYPE) {
            ids.add(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
        }
        ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        var items = new java.util.ArrayList<SearchableSelect.Item<ResourceLocation>>(ids.size());
        for (var id : ids) {
            items.add(new SearchableSelect.Item<>(id, id.toString()));
        }

        SearchableSelect
            .openPopupAt(
                menuFileX,
                menuFileY,
                280,
                menuFileHeight,
                items,
                ResourceLocation::toString,
                null,
                this::loadEntityTextures
            );
    }

    private void loadEntityTextures(ResourceLocation entityTypeId) {
        loadResourceTextures(TextureResourceCatalog.entityTextures(entityTypeId), "entity " + entityTypeId);
    }

    private static void loadResourceTextures(List<ResourceLocation> resources, String sourceDescription) {
        if (resources.isEmpty()) {
            LOGGER.warn("TexturesPanel: no texture resources found for {}", sourceDescription);
            return;
        }
        for (var resource : resources) {
            var loaded = TextureLoader.loadFromResource(resource, TextureResourceCatalog.displayName(resource));
            if (loaded != null) {
                addLoadedTexture(loaded);
            }
        }
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
        var usageCount = ModelerTextureUsage.faceUsageCount(scene, texture);
        var usage = usageCount > 0 ? usageCount + (usageCount == 1 ? " face" : " faces") : "";
        var gap = 8;
        var available = Math.max(0, width);
        var resolutionWidth = font.width(resolution);
        var usageWidth = usage.isEmpty() ? 0 : font.width(usage);
        var reserved = resolutionWidth + (usage.isEmpty() ? 0 : gap + usageWidth);
        if (available <= reserved + gap) {
            UiText.drawClipped(graphics, font, dims, x, y, available, META_TEXT_COLOR);
            return;
        }

        var dimsWidth = Math.min(font.width(dims), available - reserved - gap);
        UiText.drawClipped(graphics, font, dims, x, y, dimsWidth, META_TEXT_COLOR);
        var resolutionX = x + dimsWidth + gap;
        UiText.drawClipped(graphics, font, resolution, resolutionX, y, resolutionWidth, RESOLUTION_TEXT_COLOR);
        if (!usage.isEmpty()) {
            UiText.drawClipped(graphics, font, usage, resolutionX + resolutionWidth + gap, y, usageWidth, META_TEXT_COLOR);
        }
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

    private record TextureClipboard(
        String displayName,
        @Nullable Path sourcePath,
        @Nullable net.minecraft.resources.ResourceLocation sourceResource
    ) {

        static TextureClipboard of(LoadedTexture texture) {
            var path = texture.sourcePath() == null ? null : texture.sourcePath().toAbsolutePath().normalize();
            return new TextureClipboard(texture.displayName(), path, texture.sourceResource());
        }

        boolean isAvailable() {
            if (sourcePath != null) {
                return Files.isRegularFile(sourcePath);
            }
            return sourceResource != null
                && net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(sourceResource).isPresent();
        }

        void load() {
            if (sourcePath != null) {
                loadTextureFromPath(sourcePath);
            } else if (sourceResource != null) {
                var loaded = TextureLoader.loadFromResource(sourceResource, displayName);
                if (loaded != null) {
                    addLoadedTexture(loaded);
                }
            }
        }
    }

}
