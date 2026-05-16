package com.blib.engine.ui.panel.animation;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.TextInput;

/**
 * Animation-specific model tree. It mirrors the Modeler Outliner bone traversal, but deliberately omits cube rows so
 * animation authoring stays scoped to group transforms.
 */
@ApiStatus.Internal
public final class AnimationsOutlinerPanel implements Panel {

    private static final int ROW_HEIGHT = TextInput.HEIGHT + 1;

    private static final long DOUBLE_CLICK_MS = 350L;

    private static final int INDENT_PX = 10;

    private static final int CARET_WIDTH = 8;

    private static final int PADDING_X = 6;

    private static final int PADDING_Y = 6;

    private static final int BOTTOM_SCROLL_PADDING = ROW_HEIGHT * 4;

    private static final int BG_COLOR = 0xFF18181C;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int BONE_COLOR = 0xFFD6D6E0;

    private static final int CARET_COLOR = 0xFF8A8A95;

    private final List<Row> rows = new ArrayList<>();

    private final Set<ModelerBone> collapsed = new HashSet<>();

    private final ScrollViewport scroll = new ScrollViewport();

    private @Nullable ModelerBone cachedRowsRoot;

    private long cachedRowsSceneRevision = Long.MIN_VALUE;

    private long cachedRowsCollapsedRevision = Long.MIN_VALUE;

    private long collapsedRevision;

    private int cachedRowsContentWidth;

    private final @Nullable PanelMenuOpener menuOpener;

    private final TextInput renameInput = new TextInput("Name", this::commitRename, this::cancelRename);

    private @Nullable ModelerBone renameTarget;

    private @Nullable ModelerBone lastClickedBone;

    private long lastClickMillis;

    private @Nullable ModelerBone lastSeenRoot;

    private @Nullable Selection lastSelection;

    private int panelX, panelY, panelWidth, panelHeight;

    private int rowsTopY, rowsLeftX, rowsViewportWidth, rowsViewportHeight, rowsContentX;

    public AnimationsOutlinerPanel(@Nullable PanelMenuOpener menuOpener) {
        this.menuOpener = menuOpener;
    }

    @Override
    public String title() {
        return "Animations Outliner";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var scene = ModelerScene.get();
        if (lastSeenRoot == null) {
            lastSeenRoot = scene.root;
        } else if (scene.root != lastSeenRoot) {
            collapsed.clear();
            for (var child : scene.root.children) {
                collectAllBones(child, collapsed);
            }
            scroll.reset();
            cancelRename();
            lastSeenRoot = scene.root;
            collapsedRevision++;
            cachedRowsRoot = null;
        }

        boolean selectionChanged = !Objects.equals(scene.selection, lastSelection);
        if (selectionChanged && scene.selection instanceof Selection.BoneSelection bs) {
            expandAncestorChainFrom(bs.bone().parent);
            AnimationEditorState.get().selectBone(bs.bone().name);
        }

        var font = EngineFont.get();
        ensureRows(scene, font);
        if (renameTarget != null && !hasRowFor(renameTarget)) {
            cancelRename();
        }
        if (renameTarget != null && TextInput.getFocused() != renameInput) {
            commitActiveRename();
        }

        var rowsTop = y + PADDING_Y;
        var rowsHeight = Math.max(0, (y + height) - rowsTop);
        var innerLeft = x + PADDING_X;
        var innerWidth = Math.max(0, width - 2 * PADDING_X);
        var contentWidth = cachedRowsContentWidth;
        var contentHeight = rows.size() * ROW_HEIGHT + BOTTOM_SCROLL_PADDING;
        var frame = scroll.begin(graphics, UiRect.of(innerLeft, rowsTop, innerWidth, rowsHeight), contentWidth, contentHeight);
        var visibleRows = frame.visibleContentRect();
        rowsTopY = visibleRows.y();
        rowsLeftX = visibleRows.x();
        rowsViewportWidth = visibleRows.width();
        rowsViewportHeight = visibleRows.height();
        rowsContentX = frame.contentX();

        if (selectionChanged && scene.selection instanceof Selection.BoneSelection bs) {
            scrollBoneIntoView(bs.bone());
        }
        lastSelection = scene.selection;

        Set<ModelerBone> selectedSubtree = new HashSet<>();
        if (scene.selection instanceof Selection.BoneSelection bs) {
            collectAllBones(bs.bone(), selectedSubtree);
        }

        try {
            var contentY = frame.contentY();
            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var rowTop = contentY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= visibleRows.y() || rowTop >= visibleRows.bottom()) {
                    continue;
                }
                var selected = selectedSubtree.contains(row.bone);
                var hovered = mouseX >= visibleRows.x()
                    && mouseX < visibleRows.right()
                    && mouseY >= rowTop
                    && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(visibleRows.x(), rowTop, visibleRows.right(), rowBottom, ROW_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(visibleRows.x(), rowTop, visibleRows.right(), rowBottom, ROW_HOVER_COLOR);
                }

                var indentX = frame.contentX() + row.depth * INDENT_PX;
                var labelY = rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2;
                if (isCollapsible(row.bone)) {
                    UiText.drawClipped(
                        graphics,
                        font,
                        collapsed.contains(row.bone) ? ">" : "v",
                        indentX,
                        labelY,
                        CARET_WIDTH,
                        CARET_COLOR
                    );
                }
                var labelX = indentX + CARET_WIDTH;
                if (row.bone == renameTarget) {
                    renameInput.render(graphics, labelX, rowTop, Math.max(0, visibleRows.right() - labelX), mouseX, mouseY);
                } else {
                    UiText.drawClipped(graphics, font, row.bone.name, labelX, labelY, Math.max(0, visibleRows.right() - labelX), BONE_COLOR);
                }
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 && button != 1) {
            return false;
        }
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        if (button == 1) {
            return openContextMenu(mouseX, mouseY);
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
        var row = rowAt(mouseX, mouseY);
        if (row == null) {
            return false;
        }
        if (isCollapsible(row.bone)) {
            var caretX = rowsContentX + row.depth * INDENT_PX;
            if (mouseX >= caretX && mouseX < caretX + CARET_WIDTH) {
                if (!collapsed.remove(row.bone)) {
                    collapsed.add(row.bone);
                }
                collapsedRevision++;
                lastClickedBone = null;
                return true;
            }
        }
        selectBone(row.bone);
        var now = System.currentTimeMillis();
        var doubleClick = lastClickedBone == row.bone && now - lastClickMillis <= DOUBLE_CLICK_MS && isLabelHit(row, mouseX);
        if (doubleClick) {
            beginRename(row.bone);
            lastClickedBone = null;
        } else {
            lastClickedBone = row.bone;
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
        return scroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean openContextMenu(double mouseX, double mouseY) {
        var row = rowAt(mouseX, mouseY);
        if (row == null) {
            return false;
        }
        selectBone(row.bone);
        if (menuOpener != null) {
            menuOpener.open(new DropdownMenu((int) mouseX, (int) mouseY, List.of(new DropdownMenu.Item("Rename", () -> beginRename(row.bone)))));
        }
        return true;
    }

    private void selectBone(ModelerBone bone) {
        ModelerScene.get().selection = new Selection.BoneSelection(bone);
        AnimationEditorState.get().selectBone(bone.name);
    }

    private void beginRename(ModelerBone bone) {
        renameTarget = bone;
        renameInput.setContent(bone.name);
        renameInput.focus();
        renameInput.selectAll();
    }

    private void commitActiveRename() {
        commitRename(renameInput.content());
    }

    private void commitRename(String text) {
        var target = renameTarget;
        renameTarget = null;
        lastClickedBone = null;
        if (target == null || text == null || text.isBlank() || text.equals(target.name)) {
            return;
        }
        var oldName = target.name;
        var before = ModelerAction.BoneMemento.of(target);
        target.name = text;
        var after = ModelerAction.BoneMemento.of(target);
        if (after.differsFrom(before)) {
            ModelerActionHistory.push(
                new ModelerAction.BoneMementoAction(
                    "bone_rename",
                    "Rename bone " + before.name() + " to " + after.name(),
                    System.currentTimeMillis(),
                    target,
                    before,
                    after
                )
            );
            AnimationEditorState.get().onBoneRenamed(target, oldName, text);
        }
    }

    private void cancelRename() {
        renameTarget = null;
        lastClickedBone = null;
    }

    private @Nullable Row rowAt(double mouseX, double mouseY) {
        if (mouseY < rowsTopY || mouseY >= rowsTopY + rowsViewportHeight) {
            return null;
        }
        if (mouseX < rowsLeftX || mouseX >= rowsLeftX + rowsViewportWidth) {
            return null;
        }
        var contentY = (int) (mouseY - rowsTopY) + scroll.scrollY();
        if (contentY < 0) {
            return null;
        }
        var idx = contentY / ROW_HEIGHT;
        return idx >= 0 && idx < rows.size() ? rows.get(idx) : null;
    }

    private void buildRows(ModelerBone bone, int depth) {
        rows.add(new Row(bone, depth));
        if (collapsed.contains(bone)) {
            return;
        }
        for (var child : bone.children) {
            buildRows(child, depth + 1);
        }
    }

    private void ensureRows(ModelerScene scene, Font font) {
        var sceneRevision = scene.revision();
        if (scene.root == cachedRowsRoot
            && sceneRevision == cachedRowsSceneRevision
            && collapsedRevision == cachedRowsCollapsedRevision) {
            return;
        }
        rows.clear();
        buildRows(scene.root, 0);
        cachedRowsContentWidth = measureRowsContentWidth(font);
        cachedRowsRoot = scene.root;
        cachedRowsSceneRevision = sceneRevision;
        cachedRowsCollapsedRevision = collapsedRevision;
    }

    private int measureRowsContentWidth(Font font) {
        var max = 0;
        for (var row : rows) {
            max = Math.max(max, row.depth * INDENT_PX + CARET_WIDTH + font.width(row.bone.name) + PADDING_X);
        }
        return max;
    }

    private boolean hasRowFor(ModelerBone bone) {
        for (var row : rows) {
            if (row.bone == bone) {
                return true;
            }
        }
        return false;
    }

    private boolean isLabelHit(Row row, double mouseX) {
        var labelX = rowsContentX + row.depth * INDENT_PX + CARET_WIDTH;
        var labelWidth = Math.max(24, EngineFont.get().width(row.bone.name) + PADDING_X);
        return mouseX >= labelX && mouseX < Math.min(rowsLeftX + rowsViewportWidth, labelX + labelWidth);
    }

    private void scrollBoneIntoView(ModelerBone bone) {
        var targetIdx = -1;
        for (var i = 0; i < rows.size(); i++) {
            if (rows.get(i).bone == bone) {
                targetIdx = i;
                break;
            }
        }
        if (targetIdx < 0) {
            return;
        }
        var targetTop = targetIdx * ROW_HEIGHT;
        var targetBottom = targetTop + ROW_HEIGHT;
        var viewTop = scroll.scrollY();
        var viewBottom = viewTop + rowsViewportHeight;
        if (targetTop < viewTop) {
            scroll.scrollBy(targetTop - viewTop);
        } else if (targetBottom > viewBottom) {
            scroll.scrollBy(targetBottom - viewBottom);
        }
    }

    private void expandAncestorChainFrom(@Nullable ModelerBone start) {
        var changed = false;
        while (start != null) {
            changed |= collapsed.remove(start);
            start = start.parent;
        }
        if (changed) {
            collapsedRevision++;
        }
    }

    private static boolean isCollapsible(ModelerBone bone) {
        return !bone.children.isEmpty();
    }

    private static void collectAllBones(ModelerBone bone, Set<ModelerBone> out) {
        out.add(bone);
        for (var child : bone.children) {
            collectAllBones(child, out);
        }
    }

    private record Row(
        ModelerBone bone,
        int depth
    ) {}
}
