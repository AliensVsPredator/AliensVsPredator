package com.blib.engine.ui.panel.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.animation.AnimationEditorState.AnimationDocumentRef;
import com.blib.engine.modeler.animation.AnimationEditorState.AnimationKey;
import com.blib.engine.modeler.animation.AnimationRecentFiles;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.common.storage.EngineProjectIO;

@ApiStatus.Internal
public final class AnimationsPanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int MENU_BAR_BG_COLOR = 0xFF202024;

    private static final int MENU_BAR_BORDER_COLOR = 0xFF101013;

    private static final int MENU_CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int MENU_CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int ROW_MULTI_SELECTED_COLOR = 0xFF30303A;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int META_TEXT_COLOR = 0xFF808088;

    private static final int DIRTY_COLOR = 0xFFE6C26B;

    private static final int PADDING = 6;

    private static final int MENU_BAR_HEIGHT = 14;

    private static final int MENU_CHIP_PADDING_X = 4;

    private static final int ROW_HEIGHT = TextInput.HEIGHT + 3;

    private static final String MENU_FILE = "File";

    private static final long DOUBLE_CLICK_MS = 350L;

    private final ScrollViewport scroll = new ScrollViewport();

    private final @Nullable PanelMenuOpener menuOpener;

    private final TextInput renameInput = new TextInput("Name", this::commitRename, this::cancelRename);

    private final List<Row> rows = new ArrayList<>();

    private final Set<Integer> collapsedDocuments = new HashSet<>();

    private List<AnimationDocumentRef> cachedDocuments = List.of();

    private long cachedRowsContentRevision = Long.MIN_VALUE;

    private long cachedRowsCollapsedRevision = Long.MIN_VALUE;

    private int cachedRowsDocumentSignature;

    private long collapsedDocumentsRevision;

    private @Nullable AnimationKey renameTarget;

    private @Nullable AnimationKey lastClickedAnimation;

    private @Nullable AnimationKey selectionAnchorAnimation;

    private long lastClickMillis;

    private int panelX, panelY, panelWidth, panelHeight;

    private int menuFileX, menuFileY, menuFileWidth, menuFileHeight;

    private int rowsTopY, rowsLeftX, rowsViewportHeight, rowsContentWidth;

    public AnimationsPanel(@Nullable PanelMenuOpener menuOpener) {
        this.menuOpener = menuOpener;
    }

    @Override
    public String title() {
        return "Animations";
    }

    @Override
    public @Nullable Panel.TabIndicator tabIndicator() {
        return AnimationEditorState.get().isDirty()
            ? new Panel.TabIndicator(DIRTY_COLOR, Component.literal("Unsaved animation changes"))
            : null;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var font = EngineFont.get();
        renderMenuBar(graphics, x, y, width, mouseX, mouseY);

        var state = AnimationEditorState.get();
        ensureRows(state);
        if (renameTarget != null && rows.stream().noneMatch(row -> row.matches(renameTarget))) {
            cancelRename();
        }
        if (renameTarget != null && TextInput.getFocused() != renameInput) {
            commitActiveRename();
        }

        var metaY = y + MENU_BAR_HEIGHT + 4;
        var documentCount = cachedDocuments.size();
        var meta = documentCount == 0
            ? "Open or create an animation JSON"
            : documentCount + (documentCount == 1 ? " animation file" : " animation files");
        UiText.drawClipped(graphics, font, meta, x + PADDING, metaY, Math.max(0, width - 2 * PADDING), state.isDirty() ? DIRTY_COLOR : META_TEXT_COLOR);

        rowsTopY = metaY + font.lineHeight + 5;
        rowsLeftX = x + PADDING;
        rowsViewportHeight = Math.max(0, y + height - rowsTopY - PADDING);
        var innerWidth = Math.max(0, width - 2 * PADDING);
        var frame = scroll.begin(graphics, UiRect.of(rowsLeftX, rowsTopY, innerWidth, rowsViewportHeight), rows.size() * ROW_HEIGHT);
        rowsContentWidth = frame.contentWidth();
        try {
            var contentY = frame.contentY();
            var visibleTop = rowsTopY;
            var visibleBottom = rowsTopY + rowsViewportHeight;
            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var rowTop = contentY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= visibleTop || rowTop >= visibleBottom) {
                    continue;
                }
                var active = row.isAnimation()
                    && state.selectedDocumentId() != null
                    && row.documentId() == state.selectedDocumentId()
                    && row.animationName().equals(state.selectedAnimationName());
                var selected = row.isAnimation() && state.isAnimationSelected(row.documentId(), row.animationName());
                var activeDocument = row.isFile() && state.selectedDocumentId() != null && row.documentId() == state.selectedDocumentId();
                var hovered = mouseX >= rowsLeftX && mouseX < rowsLeftX + rowsContentWidth && mouseY >= rowTop && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, active ? ROW_SELECTED_COLOR : ROW_MULTI_SELECTED_COLOR);
                } else if (activeDocument) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_MULTI_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_HOVER_COLOR);
                }

                if (row.isFile()) {
                    renderFileRow(graphics, font, row, rowTop);
                    continue;
                }

                var textX = rowsLeftX + 18;
                if (row.matches(renameTarget)) {
                    renameInput.render(graphics, textX, rowTop + 1, Math.max(0, rowsContentWidth - 6), mouseX, mouseY);
                } else {
                    UiText.drawClipped(
                        graphics,
                        font,
                        row.animationName(),
                        textX,
                        rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                        Math.max(0, rowsContentWidth - 22),
                        TEXT_COLOR
                    );
                }
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }

        if (rows.isEmpty()) {
            var note = state.hasDraft() ? "(no animations)" : "(no file open)";
            UiText.drawClipped(graphics, font, note, rowsLeftX + 4, rowsTopY + 4, Math.max(0, rowsContentWidth - 8), META_TEXT_COLOR);
        }
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseY >= panelY && mouseY < panelY + MENU_BAR_HEIGHT && isFileChip(mouseX, mouseY)) {
            if (menuOpener != null) {
                menuOpener.open(buildFileMenu());
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
            return openContextMenu(mouseX, mouseY);
        }
        if (button != 0) {
            return false;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (renameTarget != null) {
            if (renameInput.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            commitActiveRename();
        }

        var idx = rowIndexAt(mouseX, mouseY);
        if (idx < 0 || idx >= rows.size()) {
            return false;
        }
        var row = rows.get(idx);
        var state = AnimationEditorState.get();
        if (row.isFile()) {
            state.selectDocument(row.documentId());
            if (mouseX < rowsLeftX + 18) {
                toggleDocument(row.documentId());
            }
            selectionAnchorAnimation = null;
            lastClickedAnimation = null;
            return true;
        }

        var key = row.key();
        var shiftSelection = Screen.hasShiftDown();
        var toggleSelection = Screen.hasControlDown();
        if (shiftSelection) {
            var anchor = selectionAnchorAnimation != null ? selectionAnchorAnimation : selectedAnimationKey(state);
            state.selectAnimationRange(visibleAnimationKeys(), anchor, key);
            selectionAnchorAnimation = anchor;
            lastClickedAnimation = null;
        } else if (toggleSelection) {
            state.toggleAnimationSelection(key.documentId(), key.animationName());
            selectionAnchorAnimation = key;
            lastClickedAnimation = null;
        } else {
            var now = System.currentTimeMillis();
            var doubleClick = key.equals(lastClickedAnimation) && now - lastClickMillis <= DOUBLE_CLICK_MS;
            state.selectAnimation(key.documentId(), key.animationName());
            selectionAnchorAnimation = key;
            if (!doubleClick) {
                lastClickedAnimation = key;
                lastClickMillis = now;
            }
            if (!doubleClick) {
                return true;
            }
            beginRename(key);
            lastClickedAnimation = null;
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
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private void renderMenuBar(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + MENU_BAR_HEIGHT, MENU_BAR_BG_COLOR);
        graphics.fill(x, y + MENU_BAR_HEIGHT - 1, x + width, y + MENU_BAR_HEIGHT, MENU_BAR_BORDER_COLOR);
        var font = EngineFont.get();
        menuFileX = x + PADDING;
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
        graphics.drawString(
            font,
            Component.literal(MENU_FILE),
            menuFileX + MENU_CHIP_PADDING_X,
            y + (MENU_BAR_HEIGHT - font.lineHeight + 2) / 2,
            TEXT_COLOR,
            false
        );
    }

    private boolean isFileChip(double mouseX, double mouseY) {
        return mouseX >= menuFileX
            && mouseX < menuFileX + menuFileWidth
            && mouseY >= menuFileY
            && mouseY < menuFileY + menuFileHeight;
    }

    private void ensureRows(AnimationEditorState state) {
        var documents = state.documents();
        var documentSignature = documentsSignature(documents);
        if (state.contentRevision() == cachedRowsContentRevision
            && collapsedDocumentsRevision == cachedRowsCollapsedRevision
            && documentSignature == cachedRowsDocumentSignature) {
            return;
        }
        cachedDocuments = List.copyOf(documents);
        rows.clear();
        for (var document : cachedDocuments) {
            rows.add(Row.file(document.id()));
            if (collapsedDocuments.contains(document.id())) {
                continue;
            }
            for (var animationName : state.animationNames(document.id())) {
                rows.add(Row.animation(document.id(), animationName));
            }
        }
        cachedRowsContentRevision = state.contentRevision();
        cachedRowsCollapsedRevision = collapsedDocumentsRevision;
        cachedRowsDocumentSignature = documentSignature;
    }

    private void renderFileRow(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        Row row,
        int rowTop
    ) {
        var collapsed = collapsedDocuments.contains(row.documentId());
        drawCaret(graphics, rowsLeftX + 6, rowTop + (ROW_HEIGHT - 7) / 2, collapsed, META_TEXT_COLOR);
        var document = documentRef(row.documentId());
        var label = document == null ? "(missing)" : document.label();
        if (document != null && document.dirty()) {
            label = "* " + label;
        }
        UiText.drawClipped(
            graphics,
            font,
            label,
            rowsLeftX + 18,
            rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, rowsContentWidth - 22),
            label.startsWith("* ") ? DIRTY_COLOR : TEXT_COLOR
        );
    }

    private DropdownMenu buildFileMenu() {
        var state = AnimationEditorState.get();
        var items = List
            .of(
                new DropdownMenu.Item("New", state::newDraft),
                new DropdownMenu.Item("Recent", () -> {}, buildRecentSubmenu()),
                new DropdownMenu.Item("Open", () -> {}, buildOpenSubmenu()),
                new DropdownMenu.Item(
                    "Save",
                    state::save,
                    state.canSave(),
                    Component.literal("No animation changes or save target available.")
                ),
                new DropdownMenu.Item("Save As...", this::saveAsFile, state.hasDraft(), Component.literal("Open or create an animation file first."))
            );
        return new DropdownMenu(menuFileX, menuFileY + menuFileHeight + 1, items);
    }

    private List<DropdownMenu.Item> buildOpenSubmenu() {
        return List.of(new DropdownMenu.Item("From File...", this::openFromFile));
    }

    private List<DropdownMenu.Item> buildRecentSubmenu() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return List.of(new DropdownMenu.Item("(no project active)", () -> {}));
        }
        var recents = AnimationRecentFiles.list(project);
        if (recents.isEmpty()) {
            return List.of(new DropdownMenu.Item("(no recent files)", () -> {}));
        }
        var items = new ArrayList<DropdownMenu.Item>(recents.size());
        for (var entry : recents) {
            items.add(new DropdownMenu.Item(recentLabel(entry), () -> openRecent(entry)));
        }
        return items;
    }

    private static String recentLabel(AnimationRecentFiles.Entry entry) {
        var name = entry.source() == AnimationRecentFiles.Source.EXTERNAL
            ? externalRecentName(entry.target())
            : entry.target();
        return name + " [" + entry.source().label() + "]";
    }

    private static String externalRecentName(String target) {
        try {
            var fileName = Path.of(target).getFileName();
            return fileName != null ? fileName.toString() : target;
        } catch (InvalidPathException ignored) {
            return target;
        }
    }

    private boolean openContextMenu(double mouseX, double mouseY) {
        if (menuOpener == null) {
            return true;
        }
        var state = AnimationEditorState.get();
        var row = rowAt(mouseX, mouseY);
        if (row != null && row.isAnimation()) {
            var key = row.key();
            if (!state.isAnimationSelected(key.documentId(), key.animationName())) {
                state.selectAnimation(key.documentId(), key.animationName());
                selectionAnchorAnimation = key;
            }
            menuOpener.open(
                new DropdownMenu(
                    (int) mouseX,
                    (int) mouseY,
                    List.of(
                        new DropdownMenu.Item("Rename", () -> beginRename(key)),
                        new DropdownMenu.Item("Duplicate", () -> state.duplicateAnimation(key.documentId(), key.animationName())),
                        new DropdownMenu.Item("Delete", () -> state.deleteAnimation(key.documentId(), key.animationName()))
                    )
                )
            );
            return true;
        }
        if (row != null && row.isFile()) {
            var documentId = row.documentId();
            var document = documentRef(documentId);
            var documentFolder = documentFolder(documentId);
            menuOpener.open(
                new DropdownMenu(
                    (int) mouseX,
                    (int) mouseY,
                    List.of(
                        new DropdownMenu.Item(
                            "Save",
                            () -> state.save(documentId),
                            state.canSave(documentId),
                            Component.literal("No animation changes or save target available.")
                        ),
                        new DropdownMenu.Item("Save As...", () -> saveAsFile(documentId), document != null),
                        new DropdownMenu.Item(
                            "Open in File Explorer",
                            () -> openDocumentFolder(documentId),
                            documentFolder != null,
                            Component.literal("This animation file does not have an available folder.")
                        ),
                        DropdownMenu.Item.divider(),
                        new DropdownMenu.Item("New Animation", this::newAnimation, state.hasDraft()),
                        DropdownMenu.Item.divider(),
                        new DropdownMenu.Item("Unload", () -> unloadDocument(documentId), document != null)
                    )
                )
            );
            return true;
        }
        if (mouseY >= rowsTopY && mouseY < rowsTopY + rowsViewportHeight) {
            menuOpener.open(
                new DropdownMenu(
                    (int) mouseX,
                    (int) mouseY,
                    List.of(new DropdownMenu.Item("New Animation", this::newAnimation, state.hasDraft()))
                )
            );
            return true;
        }
        return false;
    }

    private void openFromFile() {
        var picked = ModelerFilePicker.pickAnimationJson(initialOpenDirectory());
        if (picked != null) {
            openAnimationPath(picked, picked.toString());
        }
    }

    private void saveAsFile() {
        var picked = ModelerFilePicker.saveAnimationJson(defaultFileName(), initialSaveDirectory());
        if (picked != null) {
            AnimationEditorState.get().saveAsFile(picked);
        }
    }

    private void saveAsFile(int documentId) {
        var state = AnimationEditorState.get();
        var picked = ModelerFilePicker.saveAnimationJson(defaultFileName(documentId), initialSaveDirectory(documentId));
        if (picked != null) {
            state.saveAsFile(documentId, picked);
        }
    }

    private static void openDocumentFolder(int documentId) {
        var folder = documentFolder(documentId);
        if (folder != null) {
            Util.getPlatform().openUri(folder.toUri());
        }
    }

    private static @Nullable Path documentFolder(int documentId) {
        var path = AnimationEditorState.get().documentPath(documentId);
        if (path == null) {
            return null;
        }
        var folder = Files.isDirectory(path) ? path : path.getParent();
        return folder != null && Files.isDirectory(folder) ? folder : null;
    }

    private void unloadDocument(int documentId) {
        var removedCollapsed = collapsedDocuments.remove(documentId);
        if (AnimationEditorState.get().unloadDocument(documentId)) {
            if (removedCollapsed) {
                collapsedDocumentsRevision++;
            }
            if (renameTarget != null && renameTarget.documentId() == documentId) {
                cancelRename();
            }
            if (selectionAnchorAnimation != null && selectionAnchorAnimation.documentId() == documentId) {
                selectionAnchorAnimation = null;
            }
            if (lastClickedAnimation != null && lastClickedAnimation.documentId() == documentId) {
                lastClickedAnimation = null;
            }
        }
    }

    private static void openRecent(AnimationRecentFiles.Entry entry) {
        if (entry.source() != AnimationRecentFiles.Source.EXTERNAL) {
            return;
        }
        try {
            openAnimationPath(Path.of(entry.target()), entry.target());
        } catch (InvalidPathException ignored) {
            // Malformed legacy entries stay visible but do not open.
        }
    }

    private static void openAnimationPath(Path path, String recentTarget) {
        if (AnimationEditorState.get().openFromFile(path)) {
            var project = ProjectSession.activeProjectName();
            if (!project.isEmpty()) {
                AnimationRecentFiles.recordExternalOpen(project, recentTarget);
            }
        }
    }

    private void newAnimation() {
        AnimationEditorState.get().createAnimation(null);
    }

    private void beginRename(AnimationKey animationKey) {
        renameTarget = animationKey;
        renameInput.setContent(animationKey.animationName());
        renameInput.focus();
        renameInput.selectAll();
    }

    private void commitActiveRename() {
        commitRename(renameInput.content());
    }

    private void commitRename(String text) {
        var target = renameTarget;
        renameTarget = null;
        lastClickedAnimation = null;
        if (target != null) {
            AnimationEditorState.get().renameAnimation(target.documentId(), target.animationName(), text);
        }
    }

    private void cancelRename() {
        renameTarget = null;
        lastClickedAnimation = null;
    }

    private @Nullable Row rowAt(double mouseX, double mouseY) {
        var idx = rowIndexAt(mouseX, mouseY);
        return idx >= 0 && idx < rows.size() ? rows.get(idx) : null;
    }

    private void toggleDocument(int documentId) {
        if (!collapsedDocuments.remove(documentId)) {
            collapsedDocuments.add(documentId);
        }
        collapsedDocumentsRevision++;
    }

    private @Nullable AnimationDocumentRef documentRef(int documentId) {
        for (var document : cachedDocuments) {
            if (document.id() == documentId) {
                return document;
            }
        }
        return null;
    }

    private static int documentsSignature(List<AnimationDocumentRef> documents) {
        var hash = 1;
        for (var document : documents) {
            hash = 31 * hash + document.id();
            hash = 31 * hash + document.label().hashCode();
            hash = 31 * hash + (document.dirty() ? 1 : 0);
        }
        return hash;
    }

    private List<AnimationKey> visibleAnimationKeys() {
        var out = new ArrayList<AnimationKey>();
        for (var row : rows) {
            if (row.isAnimation()) {
                out.add(row.key());
            }
        }
        return out;
    }

    private static @Nullable AnimationKey selectedAnimationKey(AnimationEditorState state) {
        if (state.selectedDocumentId() == null || state.selectedAnimationName() == null) {
            return null;
        }
        return new AnimationKey(state.selectedDocumentId(), state.selectedAnimationName());
    }

    private int rowIndexAt(double mouseX, double mouseY) {
        if (mouseY < rowsTopY || mouseY >= rowsTopY + rowsViewportHeight) {
            return -1;
        }
        if (mouseX < rowsLeftX || mouseX >= rowsLeftX + rowsContentWidth) {
            return -1;
        }
        var contentY = (int) (mouseY - rowsTopY) + scroll.scrollY();
        return contentY < 0 ? -1 : contentY / ROW_HEIGHT;
    }

    private static @Nullable Path initialOpenDirectory() {
        var recentParent = recentExternalParentDirectory();
        if (recentParent != null) {
            return recentParent;
        }
        var state = AnimationEditorState.get();
        if (state.externalSavePath() != null) {
            var parent = state.externalSavePath().getParent();
            if (parent != null && Files.isDirectory(parent)) {
                return parent;
            }
        }
        return initialProjectAnimationDirectory();
    }

    private static @Nullable Path recentExternalParentDirectory() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        for (var recent : AnimationRecentFiles.list(project)) {
            if (recent.source() == AnimationRecentFiles.Source.EXTERNAL) {
                try {
                    return Path.of(recent.target()).getParent();
                } catch (InvalidPathException ignored) {
                    // Skip malformed legacy entries and keep looking for a usable external path.
                }
            }
        }
        return null;
    }

    private static @Nullable Path initialSaveDirectory() {
        var state = AnimationEditorState.get();
        if (state.externalSavePath() != null && state.externalSavePath().getParent() != null) {
            return state.externalSavePath().getParent();
        }
        return initialProjectAnimationDirectory();
    }

    private static @Nullable Path initialSaveDirectory(int documentId) {
        var path = AnimationEditorState.get().documentPath(documentId);
        if (path != null && path.getParent() != null) {
            return path.getParent();
        }
        return initialProjectAnimationDirectory();
    }

    private static @Nullable Path initialProjectAnimationDirectory() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        return EngineProjectIO.resourcepackRoot(project).resolve("assets").resolve(project).resolve("animations");
    }

    private static String defaultFileName() {
        var state = AnimationEditorState.get();
        if (state.externalSavePath() != null && state.externalSavePath().getFileName() != null) {
            return state.externalSavePath().getFileName().toString();
        }
        var selected = state.selectedAnimationName();
        return sanitizeFileName(selected == null ? "animations" : selected) + ".animation.json";
    }

    private static String defaultFileName(int documentId) {
        var path = AnimationEditorState.get().documentPath(documentId);
        if (path != null && path.getFileName() != null) {
            return path.getFileName().toString();
        }
        return "animations.animation.json";
    }

    private static String sanitizeFileName(@Nullable String value) {
        var raw = value == null || value.isBlank() ? "animations" : value.toLowerCase(Locale.ROOT);
        return raw.replaceAll("[^a-z0-9_./-]", "_").replace(':', '_');
    }

    private static void drawCaret(GuiGraphics graphics, int x, int y, boolean collapsed, int color) {
        if (collapsed) {
            for (var row = 0; row < 7; row++) {
                var width = row <= 3 ? row + 1 : 7 - row;
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
            return;
        }
        for (var row = 0; row < 4; row++) {
            graphics.fill(x + row, y + row + 1, x + 7 - row, y + row + 2, color);
        }
    }

    private record Row(
        int documentId,
        @Nullable String animationName
    ) {

        static Row file(int documentId) {
            return new Row(documentId, null);
        }

        static Row animation(int documentId, String animationName) {
            return new Row(documentId, animationName);
        }

        boolean isFile() {
            return animationName == null;
        }

        boolean isAnimation() {
            return animationName != null;
        }

        AnimationKey key() {
            return new AnimationKey(documentId, animationName == null ? "" : animationName);
        }

        boolean matches(@Nullable AnimationKey key) {
            return key != null && animationName != null && documentId == key.documentId() && animationName.equals(key.animationName());
        }
    }
}
