package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerSceneLoader;
import com.blib.engine.modeler.Selection;

/**
 * Hierarchical tree view of the modeler scene: root → bones → cubes. Each row is selectable; the click event sets
 * {@link ModelerScene#selection} so the viewport (outline) and inspector (editor) both pick it up.
 * <p>
 * v1 layout: flat scrollable list with per-level indent, no folding (every bone is always expanded). Folding can layer
 * on later when models with deep hierarchies stress the readability.
 */
@ApiStatus.Internal
public final class ModelerOutlinerPanel implements Panel {

    private static final int ROW_HEIGHT = 12;

    private static final int INDENT_PX = 10;

    private static final int PADDING_X = 6;

    private static final int PADDING_Y = 6;

    private static final int BG_COLOR = 0xFF18181C;

    private static final int ROW_HOVER_COLOR = 0xFF24242A;

    private static final int ROW_SELECTED_COLOR = 0xFF3A3A48;

    private static final int BONE_COLOR = 0xFFD6D6E0;

    private static final int CUBE_COLOR = 0xFFA6C8FF;

    /** Per-frame snapshot of (label, depth, kind, owner-bone, cube?) rows for hit-testing. */
    private final List<Row> rows = new ArrayList<>();

    /**
     * Text input at the top of the panel. The user types a resource location (e.g. {@code blib:default_model}) and
     * presses Enter to load the model into the scene via {@link ModelerSceneLoader}.
     */
    private final TextInput loadInput = new TextInput("Load model (e.g. blib:default_model)", value -> {
        if (value != null && !value.isBlank()) {
            ModelerSceneLoader.loadByString(value);
        }
    });

    /** Panel rect captured at render time so {@link #mouseClicked} can hit-test against the rows. */
    private int panelX, panelY, panelWidth, panelHeight;

    private static final int LOAD_INPUT_HEIGHT = TextInput.HEIGHT;

    private static final int LOAD_INPUT_GAP_BELOW = 4;

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

        // Load-model text input at the top.
        loadInput.render(graphics, x + PADDING_X, y + PADDING_Y, width - 2 * PADDING_X, mouseX, mouseY);

        rows.clear();
        var scene = ModelerScene.get();
        buildRows(scene.root, 0);

        var font = EngineFont.get();
        var rowsTop = y + PADDING_Y + LOAD_INPUT_HEIGHT + LOAD_INPUT_GAP_BELOW;
        for (var i = 0; i < rows.size(); i++) {
            var row = rows.get(i);
            var rowTop = rowsTop + i * ROW_HEIGHT;
            var rowBottom = rowTop + ROW_HEIGHT;
            if (rowBottom > y + height)
                break;

            var selected = isSelected(row, scene.selection);
            var hovered = mouseX >= x && mouseX < x + width && mouseY >= rowTop && mouseY < rowBottom;
            if (selected) {
                graphics.fill(x, rowTop, x + width, rowBottom, ROW_SELECTED_COLOR);
            } else if (hovered) {
                graphics.fill(x, rowTop, x + width, rowBottom, ROW_HOVER_COLOR);
            }

            var labelX = x + PADDING_X + row.depth * INDENT_PX;
            var labelY = rowTop + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(
                font,
                Component.literal(row.label),
                labelX,
                labelY,
                row.cube != null ? CUBE_COLOR : BONE_COLOR,
                false
            );
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0)
            return false;
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }
        // Hand the click to the load-input first — if it lands inside the input rect, that takes precedence.
        if (loadInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var rowsTop = panelY + PADDING_Y + LOAD_INPUT_HEIGHT + LOAD_INPUT_GAP_BELOW;
        var localY = (int) mouseY - rowsTop;
        if (localY < 0)
            return false;
        var idx = localY / ROW_HEIGHT;
        if (idx < 0 || idx >= rows.size())
            return false;
        var row = rows.get(idx);
        var scene = ModelerScene.get();
        if (row.cube != null) {
            scene.selection = new Selection.CubeSelection(row.owner, row.cube);
        } else {
            scene.selection = new Selection.BoneSelection(row.owner);
        }
        return true;
    }

    private void buildRows(ModelerBone bone, int depth) {
        rows.add(new Row(bone.name, depth, bone, null));
        for (var cube : bone.cubes) {
            rows.add(new Row(cube.name, depth + 1, bone, cube));
        }
        for (var child : bone.children) {
            buildRows(child, depth + 1);
        }
    }

    private static boolean isSelected(Row row, Selection selection) {
        if (selection == null)
            return false;
        if (row.cube != null) {
            return selection instanceof Selection.CubeSelection cs && cs.cube() == row.cube;
        }
        return selection instanceof Selection.BoneSelection bs && bs.bone() == row.owner;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 'A' adds a default cube under the selected bone (or root); Delete removes the selected cube. Both only fire
        // when the outliner is the focused panel — the screen routes keyPressed only to the active tab.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_A) {
            ModelerScene.get().addDefaultCube();
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
            return ModelerScene.get().deleteSelectedCube();
        }
        return false;
    }

    private record Row(
        String label,
        int depth,
        ModelerBone owner,
        ModelerCube cube
    ) {}
}
