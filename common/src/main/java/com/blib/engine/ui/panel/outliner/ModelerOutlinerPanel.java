package com.blib.engine.ui.panel.outliner;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.blib.api.client.registry.v1.AzItemRendererRegistry;
import com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.item.ModelerItemSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;

/**
 * Hierarchical tree view of the modeler scene: root → bones → cubes. Each row is selectable; the click event sets
 * {@link ModelerScene#selection} so the viewport (outline) and inspector (editor) both pick it up.
 * <p>
 * Collapsible bones, scrollable list. On model load, every named bone is collapsed by default; the implicit wrapper
 * root stays expanded so the top-level bones are visible immediately (collapsing the root would hide the entire tree
 * behind a single caret, which is what made the panel feel empty). The caret to the left of each bone name toggles
 * collapse; the rest of the row selects.
 * <p>
 * Selecting a bone highlights the bone row and every descendant row (cubes + sub-bones) in this panel, AND outlines
 * every cube in the bone's subtree in the viewport — implemented on the viewport side via
 * {@code ModelerCubeRenderer.render}'s subtree expansion.
 */
@ApiStatus.Internal
public final class ModelerOutlinerPanel implements Panel {

    private static final int ROW_HEIGHT = 12;

    private static final int INDENT_PX = 10;

    /** Width of the caret-click hit box (and the visual glyph) to the left of bone names. */
    private static final int CARET_WIDTH = 8;

    private static final int PADDING_X = 6;

    private static final int PADDING_Y = 6;

    private static final int BG_COLOR = 0xFF18181C;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int BONE_COLOR = 0xFFD6D6E0;

    private static final int CUBE_COLOR = 0xFFA6C8FF;

    /** Color used to mark the bone that the attached item session is authoring — matches the inspector accent. */
    private static final int ITEM_BONE_COLOR = 0xFFE6C26B;

    private static final int CARET_COLOR = 0xFF8A8A95;

    /** Per-frame snapshot of (label, depth, kind, owner-bone, cube?) rows for hit-testing. */
    private final List<Row> rows = new ArrayList<>();

    /** Bones currently in the collapsed state. Children of these bones aren't included in {@link #rows}. */
    private final Set<ModelerBone> collapsed = new HashSet<>();

    private final ScrollViewport scroll = new ScrollViewport();

    /**
     * Tracks the scene root pointer so we can detect a new model being loaded and reset collapse / scroll state. Null
     * on the very first render (we adopt the current root without auto-collapsing — the seed scene stays open).
     */
    private @Nullable ModelerBone lastSeenRoot;

    /**
     * Tracks the selection pointer between frames so we can react exactly once when the user clicks something new:
     * auto-expand the selection's ancestors and scroll its row into view. Stored as {@link Selection} (records, so
     * equality compares the contained bone / cube references), so a click on the same cube twice is a no-op.
     */
    private @Nullable Selection lastSelection;

    /** Panel rect captured at render time so {@link #mouseClicked} can hit-test against the rows. */
    private int panelX, panelY, panelWidth, panelHeight;

    /** Rows region (the entire panel content area). Captured during render for hit-tests. */
    private int rowsTopY, rowsLeftX, rowsViewportHeight, rowsContentWidth;

    @Override
    public String title() {
        return "Modeler Outliner";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var scene = ModelerScene.get();

        // New root → collapse named bones (not the wrapper root) by default so the user sees the model's top-level
        // structure immediately. First render after construction: adopt without collapsing so the initial seed
        // scene stays fully expanded.
        if (lastSeenRoot == null) {
            lastSeenRoot = scene.root;
        } else if (scene.root != lastSeenRoot) {
            collapsed.clear();
            for (var child : scene.root.children) {
                collectAllBones(child, collapsed);
            }
            scroll.reset();
            lastSeenRoot = scene.root;
        }

        // Selection change → walk the ancestor chain of the new selection and remove each ancestor from `collapsed`
        // so the selected row is visible. Fires only on change so a manual collapse of the parent doesn't fight the
        // auto-expand on the next frame. Scroll-into-view is deferred until after rows are rebuilt below.
        boolean selectionChanged = !Objects.equals(scene.selection, lastSelection);
        if (selectionChanged && scene.selection != null) {
            expandAncestorsOf(scene.selection);
        }

        rows.clear();
        buildRows(scene.root, 0);

        // Compute rows region — inset by PADDING_X on each side so the scrollbar sits inside the panel padding
        // instead of flush against the right edge. ScrollViewport reserves the gutter for row content.
        var rowsTop = y + PADDING_Y;
        var rowsHeight = Math.max(0, (y + height) - rowsTop);
        var innerLeft = x + PADDING_X;
        var innerWidth = Math.max(0, width - 2 * PADDING_X);
        rowsTopY = rowsTop;
        rowsLeftX = innerLeft;
        rowsViewportHeight = rowsHeight;

        var contentHeight = rows.size() * ROW_HEIGHT;
        var frame = scroll.begin(graphics, UiRect.of(innerLeft, rowsTop, innerWidth, rowsHeight), contentHeight);
        rowsContentWidth = frame.contentWidth();

        // Scroll-into-view: now that rows have been rebuilt with the ancestors un-collapsed, the selected row exists
        // in `rows` at a known index. If it's outside the viewport, nudge the scroll position so it lands at the
        // nearest edge — minimal motion, doesn't jump the user away from where they were looking.
        if (selectionChanged && scene.selection != null) {
            scrollSelectionIntoView(scene.selection);
        }
        lastSelection = scene.selection;

        // Subtree set for the bone-selection cascade: when a bone is selected, every row whose owner is in this set
        // gets the selected highlight.
        Set<ModelerBone> selectedSubtree = new HashSet<>();
        if (scene.selection instanceof Selection.BoneSelection bs) {
            collectAllBones(bs.bone(), selectedSubtree);
        }

        // Pull the item-bone name from the attached session (if any) so the matching bone row is rendered in the
        // item-accent color. Lookup walks Item → AzItemRenderer → BLibGeoBoneItemRendererConfig.boneName(); null when
        // no session is attached, the registry doesn't know the item, or the registered renderer isn't a
        // BLibGeoBoneItemRenderer (e.g. some other AzItemRenderer subclass).
        var itemBoneName = resolveItemBoneName(scene.itemSession);

        var font = EngineFont.get();
        var scrollY = scroll.scrollY();

        try {
            for (var i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var rowTop = rowsTop - scrollY + i * ROW_HEIGHT;
                var rowBottom = rowTop + ROW_HEIGHT;
                if (rowBottom <= rowsTop || rowTop >= rowsTop + rowsHeight) {
                    // Off-screen vertically; skip rendering but keep iterating so indices stay aligned with the row
                    // list (the click handler uses content-Y to index directly).
                    continue;
                }

                var selected = isHighlighted(row, scene.selection, selectedSubtree);
                var hovered = mouseX >= innerLeft
                    && mouseX < innerLeft + rowsContentWidth
                    && mouseY >= rowTop
                    && mouseY < rowBottom;
                if (selected) {
                    graphics.fill(innerLeft, rowTop, innerLeft + rowsContentWidth, rowBottom, ROW_SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(innerLeft, rowTop, innerLeft + rowsContentWidth, rowBottom, ROW_HOVER_COLOR);
                }

                var indentX = innerLeft + row.depth * INDENT_PX;
                var labelY = rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2;
                if (row.cube == null && isCollapsible(row.owner)) {
                    var caret = collapsed.contains(row.owner) ? "▸" : "▾";
                    UiText.drawClipped(graphics, font, caret, indentX, labelY, CARET_WIDTH, CARET_COLOR);
                }
                var labelX = indentX + CARET_WIDTH;
                int labelColor;
                if (row.cube != null) {
                    labelColor = CUBE_COLOR;
                } else if (itemBoneName != null && itemBoneName.equals(row.owner.name)) {
                    labelColor = ITEM_BONE_COLOR;
                } else {
                    labelColor = BONE_COLOR;
                }
                UiText.drawClipped(graphics, font, row.label, labelX, labelY, Math.max(0, innerLeft + rowsContentWidth - labelX), labelColor);
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
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseY < rowsTopY || mouseY >= rowsTopY + rowsViewportHeight) {
            return false;
        }
        if (mouseX < rowsLeftX || mouseX >= rowsLeftX + rowsContentWidth) {
            return false;
        }

        // Convert cursor Y to a row index using scroll offset so off-screen / scrolled rows pick correctly.
        var contentY = (int) (mouseY - rowsTopY) + scroll.scrollY();
        if (contentY < 0) {
            return false;
        }
        var idx = contentY / ROW_HEIGHT;
        if (idx < 0 || idx >= rows.size()) {
            return false;
        }

        var row = rows.get(idx);
        var scene = ModelerScene.get();

        // Caret-region click on a collapsible bone toggles collapse without altering the selection. Otherwise the
        // whole row selects the bone / cube.
        if (row.cube == null && isCollapsible(row.owner)) {
            var caretX = rowsLeftX + row.depth * INDENT_PX;
            if (mouseX >= caretX && mouseX < caretX + CARET_WIDTH) {
                if (!collapsed.remove(row.owner)) {
                    collapsed.add(row.owner);
                }
                return true;
            }
        }

        if (row.cube != null) {
            scene.selection = new Selection.CubeSelection(row.owner, row.cube);
        } else {
            scene.selection = new Selection.BoneSelection(row.owner);
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 'A' adds a default cube under the selected bone (or root); Delete removes the current selection (cube or
        // bone, whole subtree on bone). The workspace doesn't currently route keyPressed to panels, so the layout-
        // level Delete handler in EngineWorkspaceScreen.keyPressed is what fires in practice — keeping this method
        // makes it work the moment per-panel key dispatch is wired up.
        if (keyCode == GLFW.GLFW_KEY_A) {
            ModelerScene.get().addDefaultCube();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            return ModelerScene.get().deleteSelection();
        }
        return false;
    }

    private void buildRows(ModelerBone bone, int depth) {
        rows.add(new Row(bone.name, depth, bone, null));
        if (collapsed.contains(bone)) {
            return;
        }
        for (var cube : bone.cubes) {
            rows.add(new Row(cube.name, depth + 1, bone, cube));
        }
        for (var child : bone.children) {
            buildRows(child, depth + 1);
        }
    }

    /** A bone is "collapsible" only if it has at least one descendant row to hide — child bones or cubes. */
    private static boolean isCollapsible(ModelerBone bone) {
        return !bone.children.isEmpty() || !bone.cubes.isEmpty();
    }

    /** Recursively collects {@code bone} and every descendant bone into {@code out}. */
    private static void collectAllBones(ModelerBone bone, Set<ModelerBone> out) {
        out.add(bone);
        for (var child : bone.children) {
            collectAllBones(child, out);
        }
    }

    /**
     * Remove every ancestor of the selection from {@link #collapsed} so the selected row is visible. For a cube
     * selection the ancestor chain starts at the cube's owner bone (the owner must be expanded for the cube row to
     * exist); for a bone selection it starts at the bone's parent (the bone itself doesn't need to be expanded — its
     * row is its OWN row, not a child row).
     */
    private void expandAncestorsOf(Selection sel) {
        if (sel instanceof Selection.MultiCubeSelection ms) {
            // Expand every selected cube's ancestor chain so all rows are visible at once. Calling per-cube is fine —
            // collapsed.remove is idempotent and the chains share most of their nodes anyway.
            for (var cs : ms.cubes()) {
                expandAncestorChainFrom(cs.owner());
            }
            return;
        }
        ModelerBone start = null;
        if (sel instanceof Selection.CubeSelection cs) {
            start = cs.owner();
        } else if (sel instanceof Selection.BoneSelection bs) {
            start = bs.bone().parent;
        }
        expandAncestorChainFrom(start);
    }

    private void expandAncestorChainFrom(@Nullable ModelerBone start) {
        while (start != null) {
            collapsed.remove(start);
            start = start.parent;
        }
    }

    /**
     * Scroll the panel so the selected row sits inside the viewport. Looks up the row index (post-expansion, so the row
     * definitely exists), then nudges the scroll position by exactly enough to land it at the closer edge — minimal
     * motion, doesn't yank the user's view if the row is already visible.
     */
    private void scrollSelectionIntoView(Selection sel) {
        // For multi-cube selections, scroll the primary into view — that's the one the inspector + gizmo operate on, so
        // it's the most informative target for the user.
        ModelerCube targetCube = null;
        ModelerBone targetBone = null;
        if (sel instanceof Selection.CubeSelection cs) {
            targetCube = cs.cube();
        } else if (sel instanceof Selection.MultiCubeSelection ms) {
            targetCube = ms.primary().cube();
        } else if (sel instanceof Selection.BoneSelection bs) {
            targetBone = bs.bone();
        }
        int targetIdx = -1;
        for (int i = 0; i < rows.size(); i++) {
            var row = rows.get(i);
            if (targetCube != null && row.cube == targetCube) {
                targetIdx = i;
                break;
            }
            if (targetBone != null && row.cube == null && row.owner == targetBone) {
                targetIdx = i;
                break;
            }
        }
        if (targetIdx < 0) {
            return;
        }

        int targetTop = targetIdx * ROW_HEIGHT;
        int targetBottom = targetTop + ROW_HEIGHT;
        float viewTop = scroll.scrollY();
        float viewBottom = viewTop + rowsViewportHeight;

        if (targetTop < viewTop) {
            scroll.scrollBy(targetTop - viewTop);
        } else if (targetBottom > viewBottom) {
            scroll.scrollBy(targetBottom - viewBottom);
        }
    }

    private static boolean isHighlighted(Row row, @Nullable Selection sel, Set<ModelerBone> selectedSubtree) {
        if (sel == null) {
            return false;
        }
        if (sel instanceof Selection.CubeSelection cs) {
            return row.cube != null && row.cube == cs.cube();
        }
        if (sel instanceof Selection.MultiCubeSelection ms) {
            // Every cube in the multi-selection lights up its row. Bone rows aren't highlighted by a multi-cube
            // selection — the user explicitly picked cubes, not their owning bones.
            return row.cube != null && ms.contains(row.cube);
        }
        if (sel instanceof Selection.BoneSelection) {
            // Bone selection cascades: the bone row itself, every descendant bone row, and every cube row whose
            // owner is in the subtree all light up. row.owner is the bone for bone rows / the parent bone for cube
            // rows, so a single subtree-contains check covers both kinds.
            return selectedSubtree.contains(row.owner);
        }
        return false;
    }

    /**
     * Resolve the bone name the attached item session is authoring, by walking Item → AzItemRenderer →
     * BLibGeoBoneItemRendererConfig. Returns {@code null} when there's no session, the item isn't registered, the
     * renderer isn't a {@link BLibGeoBoneItemRenderer} (different AzItemRenderer subclass), or the renderer hasn't been
     * instantiated yet by the registry's lazy supplier.
     */
    private static @Nullable String resolveItemBoneName(@Nullable ModelerItemSession session) {
        if (session == null) {
            return null;
        }
        var item = BuiltInRegistries.ITEM.get(session.itemId);
        if (item == null) {
            return null;
        }
        var renderer = AzItemRendererRegistry.getOrNull(item);
        if (renderer instanceof BLibGeoBoneItemRenderer geoRenderer) {
            return geoRenderer.geoBoneItemConfig().boneName();
        }
        return null;
    }

    private record Row(
        String label,
        int depth,
        ModelerBone owner,
        ModelerCube cube
    ) {}
}
