package com.blib.engine.ui.panel.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.animation.AnimationEditorState;
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

    private final List<String> rows = new ArrayList<>();

    private @Nullable String renameTarget;

    private @Nullable String lastClickedAnimation;

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
        rows.clear();
        rows.addAll(state.animationNames());
        if (renameTarget != null && !rows.contains(renameTarget)) {
            cancelRename();
        }
        if (renameTarget != null && TextInput.getFocused() != renameInput) {
            commitActiveRename();
        }

        var metaY = y + MENU_BAR_HEIGHT + 4;
        var meta = state.hasDraft()
            ? (state.isDirty() ? "* " : "") + state.targetLabel()
            : "Open or create an animation JSON";
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
                var name = rows.get(i);
                var rowTop = contentY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= visibleTop || rowTop >= visibleBottom) {
                    continue;
                }
                var selected = name.equals(state.selectedAnimationName());
                var hovered = mouseX >= rowsLeftX && mouseX < rowsLeftX + rowsContentWidth && mouseY >= rowTop && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(rowsLeftX, rowTop, rowsLeftX + rowsContentWidth, rowBottom, ROW_HOVER_COLOR);
                }

                var textX = rowsLeftX + 4;
                if (name.equals(renameTarget)) {
                    renameInput.render(graphics, textX, rowTop + 1, Math.max(0, rowsContentWidth - 6), mouseX, mouseY);
                } else {
                    UiText.drawClipped(
                        graphics,
                        font,
                        name,
                        textX,
                        rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                        Math.max(0, rowsContentWidth - 8),
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
        var name = rows.get(idx);
        var now = System.currentTimeMillis();
        var doubleClick = name.equals(lastClickedAnimation) && now - lastClickMillis <= DOUBLE_CLICK_MS;
        AnimationEditorState.get().selectAnimation(name);
        if (doubleClick) {
            beginRename(name);
            lastClickedAnimation = null;
        } else {
            lastClickedAnimation = name;
            lastClickMillis = now;
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

    private DropdownMenu buildFileMenu() {
        var state = AnimationEditorState.get();
        var items = List
            .of(
                new DropdownMenu.Item("New File", state::newDraft),
                new DropdownMenu.Item("Open From File...", this::openFromFile),
                new DropdownMenu.Item(
                    "Save",
                    state::save,
                    state.canSave(),
                    Component.literal("No animation changes or save target available.")
                ),
                new DropdownMenu.Item("Save As File...", this::saveAsFile, state.hasDraft(), Component.literal("Open or create an animation file first.")),
                new DropdownMenu.Item(
                    "Save To Project...",
                    this::saveToProject,
                    state.hasDraft() && !ProjectSession.activeProjectName().isEmpty(),
                    Component.literal("Open a BLib project first.")
                ),
                DropdownMenu.Item.divider(),
                new DropdownMenu.Item("New Animation", this::newAnimation, state.hasDraft(), Component.literal("Open or create an animation file first."))
            );
        return new DropdownMenu(menuFileX, menuFileY + menuFileHeight + 1, items);
    }

    private boolean openContextMenu(double mouseX, double mouseY) {
        if (menuOpener == null) {
            return true;
        }
        var state = AnimationEditorState.get();
        var row = animationAt(mouseX, mouseY);
        if (row != null) {
            state.selectAnimation(row);
            menuOpener.open(
                new DropdownMenu(
                    (int) mouseX,
                    (int) mouseY,
                    List.of(
                        new DropdownMenu.Item("Rename", () -> beginRename(row)),
                        new DropdownMenu.Item("Duplicate", () -> state.duplicateAnimation(row)),
                        new DropdownMenu.Item("Delete", () -> state.deleteAnimation(row))
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
            AnimationEditorState.get().openFromFile(picked);
        }
    }

    private void saveAsFile() {
        var picked = ModelerFilePicker.saveAnimationJson(defaultFileName(), initialSaveDirectory());
        if (picked != null) {
            AnimationEditorState.get().saveAsFile(picked);
        }
    }

    private void saveToProject() {
        var state = AnimationEditorState.get();
        var suggested = state.projectResourceId() != null ? state.projectResourceId().toString() : suggestedProjectResourceId();
        var value = ModelerFilePicker.promptAnimationResourceId(suggested);
        if (value == null) {
            return;
        }
        var id = ResourceLocation.tryParse(value);
        if (id != null) {
            state.saveAsProject(AnimationEditorState.normalizeAnimationResourceId(id));
        }
    }

    private void newAnimation() {
        AnimationEditorState.get().createAnimation(null);
    }

    private void beginRename(String animationName) {
        renameTarget = animationName;
        renameInput.setContent(animationName);
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
            AnimationEditorState.get().renameAnimation(target, text);
        }
    }

    private void cancelRename() {
        renameTarget = null;
        lastClickedAnimation = null;
    }

    private @Nullable String animationAt(double mouseX, double mouseY) {
        var idx = rowIndexAt(mouseX, mouseY);
        return idx >= 0 && idx < rows.size() ? rows.get(idx) : null;
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
        var state = AnimationEditorState.get();
        if (state.externalSavePath() != null) {
            var parent = state.externalSavePath().getParent();
            if (parent != null && Files.isDirectory(parent)) {
                return parent;
            }
        }
        return initialProjectAnimationDirectory();
    }

    private static @Nullable Path initialSaveDirectory() {
        var state = AnimationEditorState.get();
        if (state.externalSavePath() != null && state.externalSavePath().getParent() != null) {
            return state.externalSavePath().getParent();
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

    private static String suggestedProjectResourceId() {
        var project = ProjectSession.activeProjectName();
        var namespace = project.isEmpty() ? "blib" : project;
        return namespace + ":animations/" + sanitizeFileName(AnimationEditorState.get().selectedAnimationName()) + ".animation.json";
    }

    private static String sanitizeFileName(@Nullable String value) {
        var raw = value == null || value.isBlank() ? "animations" : value.toLowerCase(Locale.ROOT);
        return raw.replaceAll("[^a-z0-9_./-]", "_").replace(':', '_');
    }
}
