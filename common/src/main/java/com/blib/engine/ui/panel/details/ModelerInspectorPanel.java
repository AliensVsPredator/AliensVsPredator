package com.blib.engine.ui.panel.details;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerBlockElementRotation;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.layout.VerticalLayout;
import com.blib.engine.ui.widget.Checkbox;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.TextInput;

/**
 * Property editor for the currently-selected modeler bone or cube. Mirrors the block-volume inspector's surface in
 * {@code DetailsPanel}: a header with the selection's name, then a stack of section-headed rows of {@link TextInput}
 * fields (X / Y / Z for Vec3 fields, one field for scalars). Inputs commit on Enter or focus-loss and write directly
 * into the live {@link ModelerCube} / {@link ModelerBone} — the viewport picks up the change on its next render.
 * <p>
 * Inputs are constructed once and reused across cube/bone selection swaps; the per-frame {@code sync*} helpers skip
 * focused fields so the user's in-flight edit isn't clobbered.
 */
@ApiStatus.Internal
public final class ModelerInspectorPanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int HEADER_COLOR = 0xFFE0E0E0;

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int SECTION_HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int WARNING_COLOR = 0xFFE6C26B;

    /**
     * Axis tint colors painted as a small triangle in each input's top-right corner. Match the gizmo's red / green /
     * blue X/Y/Z palette so the inspector and viewport read the same axis affordance.
     */
    private static final int AXIS_RED = 0xFFFF3333;

    private static final int AXIS_GREEN = 0xFF33CC33;

    private static final int AXIS_BLUE = 0xFF3366FF;

    private static final int[] AXIS_COLORS = { AXIS_RED, AXIS_GREEN, AXIS_BLUE };

    /** Edge length (px) of the axis-color triangle drawn in each X/Y/Z input's top-right corner. */
    private static final int AXIS_CORNER_SIZE = 4;

    private static final int SECTION_HEADER_HEIGHT = 11;

    private static final int CONTENT_PADDING = 5;

    private static final int INPUT_GAP = 3;

    private static final int ROW_GAP = 4;

    /** Vertical offset from the panel top where the header text sits. */
    private static final int HEADER_TOP_PADDING = 8;

    /** Gap below the header before the first section starts. */
    private static final int HEADER_TO_SECTION_GAP = 6;

    /** Section break drawn between the item-config section and the selection inspector below it. */
    private static final int DIVIDER_COLOR = 0xFF353540;

    /** Vertical padding around the divider strip when an item is attached. */
    private static final int DIVIDER_GAP = 6;

    private final ModelerItemConfigSection itemConfig = new ModelerItemConfigSection();

    private final ScrollViewport scroll = new ScrollViewport();

    /** Which Vec3 field on the selected cube or bone an input commits into. */
    private enum VecField {
        ORIGIN,
        SIZE,
        ROTATION,
        PIVOT,
        POSITION,
        SCALE
    }

    // === Cube-only inputs ===

    private final TextInput originX = new TextInput("X", v -> commitVecAxis(VecField.ORIGIN, 0, v));

    private final TextInput originY = new TextInput("Y", v -> commitVecAxis(VecField.ORIGIN, 1, v));

    private final TextInput originZ = new TextInput("Z", v -> commitVecAxis(VecField.ORIGIN, 2, v));

    private final TextInput sizeX = new TextInput("X", v -> commitVecAxis(VecField.SIZE, 0, v));

    private final TextInput sizeY = new TextInput("Y", v -> commitVecAxis(VecField.SIZE, 1, v));

    private final TextInput sizeZ = new TextInput("Z", v -> commitVecAxis(VecField.SIZE, 2, v));

    private final TextInput inflateInput = new TextInput("Inflate", this::commitInflate);

    private final SearchableSelect<Integer> blockRotationAxisSelect = new SearchableSelect<>(
        ModelerInspectorPanel::blockRotationAxisItems,
        ModelerInspectorPanel::axisLabel,
        0,
        this::commitBlockRotationAxis
    );

    private final SearchableSelect<Double> blockRotationAngleSelect = new SearchableSelect<>(
        ModelerInspectorPanel::blockRotationAngleItems,
        ModelerInspectorPanel::angleLabel,
        0.0,
        this::commitBlockRotationAngle
    );

    private final Checkbox blockRotationRescale = new Checkbox(false, this::commitBlockRotationRescale);

    // === Bone-only inputs ===

    private final TextInput positionX = new TextInput("X", v -> commitVecAxis(VecField.POSITION, 0, v));

    private final TextInput positionY = new TextInput("Y", v -> commitVecAxis(VecField.POSITION, 1, v));

    private final TextInput positionZ = new TextInput("Z", v -> commitVecAxis(VecField.POSITION, 2, v));

    private final TextInput scaleX = new TextInput("X", v -> commitVecAxis(VecField.SCALE, 0, v));

    private final TextInput scaleY = new TextInput("Y", v -> commitVecAxis(VecField.SCALE, 1, v));

    private final TextInput scaleZ = new TextInput("Z", v -> commitVecAxis(VecField.SCALE, 2, v));

    // === Shared inputs (both cube and bone have rotation + pivot) ===

    private final TextInput rotationX = new TextInput("X", v -> commitVecAxis(VecField.ROTATION, 0, v));

    private final TextInput rotationY = new TextInput("Y", v -> commitVecAxis(VecField.ROTATION, 1, v));

    private final TextInput rotationZ = new TextInput("Z", v -> commitVecAxis(VecField.ROTATION, 2, v));

    private final TextInput pivotX = new TextInput("X", v -> commitVecAxis(VecField.PIVOT, 0, v));

    private final TextInput pivotY = new TextInput("Y", v -> commitVecAxis(VecField.PIVOT, 1, v));

    private final TextInput pivotZ = new TextInput("Z", v -> commitVecAxis(VecField.PIVOT, 2, v));

    /**
     * Inputs visible this frame, collected during {@link #render} so {@link #mouseClicked} only forwards clicks to
     * widgets that were actually drawn — keeps stale rects from the previous selection's inputs out of the hit-test.
     */
    private final List<TextInput> visibleInputs = new ArrayList<>();

    private boolean blockRotationControlsVisible;

    private int panelX, panelY, panelWidth, panelHeight;

    @Override
    public String title() {
        return "Modeler Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        graphics.fill(x, y, x + width, y + height, BG_COLOR);
        visibleInputs.clear();
        blockRotationControlsVisible = false;

        var scene = ModelerScene.get();
        var measuredWidth = Math.max(0, width - ScrollContainer.SCROLLBAR_GUTTER);
        var contentHeight = measureContentHeight(scene, measuredWidth);
        var frame = scroll.begin(graphics, UiRect.of(x, y, width, height), contentHeight);
        var contentX = frame.contentX();
        var contentY = frame.contentY() + CONTENT_PADDING;
        var contentW = frame.contentWidth();

        try {
            // Item-config mode is mutually exclusive with entity-model editing: when a session is attached, only the
            // Item Config section is shown; the cube/bone inspector below would be referencing a scene the user isn't
            // editing anyway.
            if (scene.itemSession != null) {
                itemConfig.render(graphics, contentX, contentY, contentW, mouseX, mouseY);
                return;
            }

            var selection = scene.selection;
            if (selection == null) {
                drawHeader(graphics, contentX, contentY, contentW, "Nothing selected");
                return;
            }

            if (selection instanceof Selection.BoneSelection bs) {
                renderBone(graphics, contentX, contentY, contentW, bs.bone(), mouseX, mouseY);
            } else if (selection instanceof Selection.CubeSelection cs) {
                renderCube(graphics, contentX, contentY, contentW, cs.cube(), null, mouseX, mouseY);
            } else if (selection instanceof Selection.FaceSelection fs) {
                renderCube(graphics, contentX, contentY, contentW, fs.cube(), fs.face(), mouseX, mouseY);
            } else if (selection instanceof Selection.MultiCubeSelection ms) {
                // Multi-cube: inspector edits the primary cube only — group edits via the UV map's drag/marquee path.
                renderCube(graphics, contentX, contentY, contentW, ms.primary().cube(), null, mouseX, mouseY);
            }
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    private void renderCube(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        ModelerCube cube,
        @Nullable ModelerCube.Face selectedFace,
        int mouseX,
        int mouseY
    ) {
        var font = EngineFont.get();
        drawHeader(graphics, x, y, width, cubeHeaderText(cube, selectedFace));

        // Sync inputs from the live cube state. Per-input skip-when-focused keeps the user's in-flight edit intact.
        // Size displays as whole numbers (no decimals) since the resize gizmo + commit path both snap to ints.
        var blockMode = ModelerScene.get().isJavaBlockModel();
        syncVec(originX, originY, originZ, cube.origin);
        syncVecInt(sizeX, sizeY, sizeZ, cube.size);
        syncVec(pivotX, pivotY, pivotZ, cube.pivot);
        if (blockMode) {
            syncBlockRotationControls(cube);
        } else {
            syncVec(rotationX, rotationY, rotationZ, cube.rotation);
            syncScalar(inflateInput, cube.inflate);
        }

        var rowY = y + HEADER_TOP_PADDING + font.lineHeight + HEADER_TO_SECTION_GAP;
        rowY = renderVecSection(graphics, font, x, rowY, width, "Position", originX, originY, originZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Size", sizeX, sizeY, sizeZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Pivot Point", pivotX, pivotY, pivotZ, mouseX, mouseY);
        if (blockMode) {
            renderBlockRotationSection(graphics, font, x, rowY, width, cube, mouseX, mouseY);
        } else {
            rowY = renderVecSection(graphics, font, x, rowY, width, "Rotation", rotationX, rotationY, rotationZ, mouseX, mouseY);
            renderScalarSection(graphics, font, x, rowY, width, "Inflate", inflateInput, mouseX, mouseY);
        }
    }

    private void renderBone(GuiGraphics graphics, int x, int y, int width, ModelerBone bone, int mouseX, int mouseY) {
        var font = EngineFont.get();
        drawHeader(graphics, x, y, width, "Bone: " + bone.name);

        // Bones only expose pivot + rotation in the inspector; position and size (scale) edits aren't meaningful for
        // a bone group and were dropped for the same reason Blockbench keeps the bone properties minimal.
        syncVec(rotationX, rotationY, rotationZ, bone.rotation);
        syncVec(pivotX, pivotY, pivotZ, bone.pivot);

        var rowY = y + HEADER_TOP_PADDING + font.lineHeight + HEADER_TO_SECTION_GAP;
        rowY = renderVecSection(graphics, font, x, rowY, width, "Pivot Point", pivotX, pivotY, pivotZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Rotation", rotationX, rotationY, rotationZ, mouseX, mouseY);
        drawCountRow(graphics, font, x, rowY + CONTENT_PADDING, width, "Children", bone.children.size(), "Cubes", bone.cubes.size());
    }

    private void drawHeader(GuiGraphics graphics, int x, int y, int width, String text) {
        var font = EngineFont.get();
        UiText.drawClipped(
            graphics,
            font,
            text,
            x + CONTENT_PADDING,
            y + HEADER_TOP_PADDING,
            Math.max(0, width - 2 * CONTENT_PADDING),
            HEADER_COLOR
        );
    }

    private static String cubeHeaderText(ModelerCube cube, @Nullable ModelerCube.Face selectedFace) {
        if (selectedFace == null) {
            return "Cube: " + cube.name;
        }
        var text = "Cube: " + cube.name + " / Face: " + selectedFace.name().toLowerCase(Locale.ROOT);
        var uv = cube.faceUv(selectedFace);
        if (uv == null) {
            return text;
        }
        var u0 = Math.min(uv.u(), uv.u() + uv.width());
        var v0 = Math.min(uv.v(), uv.v() + uv.height());
        var w = Math.abs(uv.width());
        var h = Math.abs(uv.height());
        return text + "  " + Math.round(w) + "x" + Math.round(h) + " @ " + Math.round(u0) + "," + Math.round(v0);
    }

    /**
     * Draws a section header + a row of three X/Y/Z {@link TextInput}s, splitting the row width evenly minus two
     * inter-input gaps. Returns the next-row Y so callers can chain sections without tracking layout themselves.
     */
    private int renderVecSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        TextInput xIn,
        TextInput yIn,
        TextInput zIn,
        int mouseX,
        int mouseY
    ) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, label);
        rowY += CONTENT_PADDING / 2;

        var inputsStart = x + CONTENT_PADDING;
        var available = Math.max(0, width - 2 * CONTENT_PADDING - 2 * INPUT_GAP);
        var perInput = available / 3;
        var xX = inputsStart;
        var yX = inputsStart + perInput + INPUT_GAP;
        var zX = inputsStart + 2 * (perInput + INPUT_GAP);
        xIn.render(graphics, xX, rowY, perInput, mouseX, mouseY);
        yIn.render(graphics, yX, rowY, perInput, mouseX, mouseY);
        zIn.render(graphics, zX, rowY, perInput, mouseX, mouseY);
        // Overlay the axis-color triangle in each input's top-right corner so the user can tell at a glance which
        // input drives which axis — same color convention as the gizmo handles in the viewport.
        drawAxisCorner(graphics, xX, rowY, perInput, AXIS_COLORS[0]);
        drawAxisCorner(graphics, yX, rowY, perInput, AXIS_COLORS[1]);
        drawAxisCorner(graphics, zX, rowY, perInput, AXIS_COLORS[2]);

        visibleInputs.add(xIn);
        visibleInputs.add(yIn);
        visibleInputs.add(zIn);

        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    /**
     * Right-triangle marker that fills the input's top-right corner — widest row at the top (sitting along the top
     * edge), narrowing down to a single pixel at the bottom-right. Drawn as a stack of 1-pixel-tall
     * {@link GuiGraphics#fill} rows whose left edge marches inward by 1px per row, giving a 4px triangle in the
     * standard pixel-stair style.
     */
    private static void drawAxisCorner(GuiGraphics graphics, int inputX, int inputY, int inputWidth, int color) {
        if (inputWidth <= 0) {
            return;
        }
        var rightEdge = inputX + inputWidth - 1;
        var topY = inputY;
        for (var i = 0; i < AXIS_CORNER_SIZE; i++) {
            var rowWidth = AXIS_CORNER_SIZE - i;
            var rowTop = topY + i;
            graphics.fill(rightEdge + 1 - rowWidth, rowTop, rightEdge + 1, rowTop + 1, color);
        }
    }

    /**
     * Section header + a single scalar input row. The input fills the same horizontal span the X/Y/Z trio would occupy
     * so the two row styles read consistently.
     */
    private int renderScalarSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        TextInput input,
        int mouseX,
        int mouseY
    ) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, label);
        rowY += CONTENT_PADDING / 2;
        var inputX = x + CONTENT_PADDING;
        var inputW = Math.max(0, width - 2 * CONTENT_PADDING);
        input.render(graphics, inputX, rowY, inputW, mouseX, mouseY);
        visibleInputs.add(input);
        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    private int renderBlockRotationSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        ModelerCube cube,
        int mouseX,
        int mouseY
    ) {
        blockRotationControlsVisible = true;
        var rowY = drawSectionHeader(graphics, font, x, y, width, "Block Rotation");
        rowY += CONTENT_PADDING / 2;

        var innerX = x + CONTENT_PADDING;
        var innerW = Math.max(0, width - 2 * CONTENT_PADDING);
        var colW = Math.max(0, (innerW - INPUT_GAP) / 2);
        renderLabeledSelect(graphics, font, innerX, rowY, colW, "Axis", blockRotationAxisSelect, mouseX, mouseY);
        renderLabeledSelect(
            graphics,
            font,
            innerX + colW + INPUT_GAP,
            rowY,
            Math.max(0, innerW - colW - INPUT_GAP),
            "Angle",
            blockRotationAngleSelect,
            mouseX,
            mouseY
        );
        rowY += SearchableSelect.HEIGHT + ROW_GAP;

        blockRotationRescale.render(graphics, innerX, rowY + 1, mouseX, mouseY);
        UiText.drawClipped(
            graphics,
            font,
            "Rescale",
            innerX + Checkbox.SIZE + INPUT_GAP,
            rowY + (Checkbox.SIZE - font.lineHeight + 2) / 2,
            Math.max(0, innerW - Checkbox.SIZE - INPUT_GAP),
            LABEL_COLOR
        );
        rowY += Checkbox.SIZE + ROW_GAP;

        if (!ModelerBlockElementRotation.isValid(cube.rotation)) {
            UiText.drawClipped(
                graphics,
                font,
                "Not valid for Java block JSON; adjust axis/angle to snap it.",
                innerX,
                rowY,
                innerW,
                WARNING_COLOR
            );
            rowY += font.lineHeight + ROW_GAP;
        }

        return rowY;
    }

    private static <T> void renderLabeledSelect(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        SearchableSelect<T> select,
        int mouseX,
        int mouseY
    ) {
        var labelW = Math.min(28, Math.max(0, width / 3));
        UiText.drawClipped(
            graphics,
            font,
            label,
            x,
            y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2,
            labelW,
            LABEL_COLOR
        );
        select.render(graphics, x + labelW + INPUT_GAP, y, Math.max(0, width - labelW - INPUT_GAP), mouseX, mouseY);
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        UiText.drawClipped(
            graphics,
            font,
            label,
            x + CONTENT_PADDING,
            // +2 compensates for MC font's descender padding so the section label visually centers.
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, width - 2 * CONTENT_PADDING),
            SECTION_HEADER_TEXT_COLOR
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static void drawCountRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label1,
        int count1,
        String label2,
        int count2
    ) {
        var row = UiRect.of(x + CONTENT_PADDING, y, Math.max(0, width - 2 * CONTENT_PADDING), font.lineHeight);
        var columns = VerticalLayout.columns(row, 2, INPUT_GAP);
        UiText.drawClipped(graphics, font, label1 + ": " + count1, columns[0].x(), columns[0].y(), columns[0].width(), LABEL_COLOR);
        UiText.drawClipped(graphics, font, label2 + ": " + count2, columns[1].x(), columns[1].y(), columns[1].width(), LABEL_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Item-config widgets first — its pickers, mode toggle, dump button, pivot-viz checkbox, and 12 transform
        // inputs all belong to the section.
        if (itemConfig.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Then forward to selection-inspector inputs that were actually rendered this frame; stale rects from the
        // other selection kind don't get a chance to spuriously claim focus.
        for (var input : visibleInputs) {
            if (input.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        if (blockRotationControlsVisible) {
            if (blockRotationAxisSelect.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (blockRotationAngleSelect.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (blockRotationRescale.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
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

    private int measureContentHeight(ModelerScene scene, int width) {
        var font = EngineFont.get();
        if (scene.itemSession != null) {
            return CONTENT_PADDING + itemConfig.measureHeight(width) + CONTENT_PADDING;
        }
        var headerHeight = HEADER_TOP_PADDING + font.lineHeight + HEADER_TO_SECTION_GAP;
        if (scene.selection == null) {
            return CONTENT_PADDING + headerHeight + CONTENT_PADDING;
        }
        if (scene.selection instanceof Selection.BoneSelection) {
            return CONTENT_PADDING + headerHeight + 2 * inspectorSectionHeight() + CONTENT_PADDING + font.lineHeight + CONTENT_PADDING;
        }
        if (scene.isJavaBlockModel()) {
            var cubeSel = primaryCubeSelection(scene.selection);
            var warning = cubeSel != null && !ModelerBlockElementRotation.isValid(cubeSel.cube().rotation);
            return CONTENT_PADDING + headerHeight + 3 * inspectorSectionHeight() + blockRotationSectionHeight(warning) + CONTENT_PADDING;
        }
        return CONTENT_PADDING + headerHeight + 5 * inspectorSectionHeight() + CONTENT_PADDING;
    }

    private static int inspectorSectionHeight() {
        return SECTION_HEADER_HEIGHT + CONTENT_PADDING / 2 + TextInput.HEIGHT + ROW_GAP;
    }

    private static int blockRotationSectionHeight(boolean warning) {
        var height = SECTION_HEADER_HEIGHT + CONTENT_PADDING / 2 + SearchableSelect.HEIGHT + ROW_GAP + Checkbox.SIZE + ROW_GAP;
        if (warning) {
            height += EngineFont.get().lineHeight + ROW_GAP;
        }
        return height;
    }

    // === Sync ===

    private static void syncVec(TextInput xIn, TextInput yIn, TextInput zIn, Vec3 v) {
        syncInput(xIn, formatDouble(v.x));
        syncInput(yIn, formatDouble(v.y));
        syncInput(zIn, formatDouble(v.z));
    }

    /**
     * Like {@link #syncVec} but formats values as plain integers — used for {@code size}, which is whole-number only.
     */
    private static void syncVecInt(TextInput xIn, TextInput yIn, TextInput zIn, Vec3 v) {
        syncInput(xIn, formatInt(v.x));
        syncInput(yIn, formatInt(v.y));
        syncInput(zIn, formatInt(v.z));
    }

    private static void syncScalar(TextInput input, double value) {
        syncInput(input, formatDouble(value));
    }

    private void syncBlockRotationControls(ModelerCube cube) {
        var value = ModelerBlockElementRotation.view(cube.rotation, blockRotationAxisSelect.currentValue());
        blockRotationAxisSelect.setCurrentValue(value.axis());
        blockRotationAngleSelect.setCurrentValue(value.angle());
        blockRotationRescale.setChecked(cube.blockElementRescale);
    }

    /** Set the input's text to {@code value} unless the user is mid-edit — same focus guard as the volume inspector. */
    private static void syncInput(TextInput input, String value) {
        if (input.isFocused()) {
            return;
        }
        if (!input.content().equals(value)) {
            input.setContent(value);
        }
    }

    private static String formatDouble(double v) {
        return String.format(Locale.ROOT, "%.2f", v);
    }

    private static String formatInt(double v) {
        return String.valueOf(Math.round(v));
    }

    // === Commit ===

    private void commitVecAxis(VecField field, int axis, String text) {
        var parsed = parseDouble(text);
        if (parsed == null) {
            // Bad input — leave the data alone, sync on the next frame will rewrite the input to a valid value.
            return;
        }

        var sel = ModelerScene.get().selection;
        var cubeSel = primaryCubeSelection(sel);
        if (cubeSel != null) {
            var cube = cubeSel.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            switch (field) {
                case ORIGIN -> cube.origin = withAxis(cube.origin, axis, parsed);
                case SIZE -> {
                    // Whole numbers only, clamped at zero — matches the resize gizmo's "no decimals, no negatives"
                    // contract so typed edits and gizmo drags converge to the same data.
                    var sized = (double) Math.max(0L, Math.round(parsed));
                    cube.size = withAxis(cube.size, axis, sized);
                }
                case ROTATION -> {
                    if (ModelerScene.get().isJavaBlockModel()) {
                        cube.rotation = ModelerBlockElementRotation.toRotation(axis, parsed);
                    } else {
                        cube.rotation = withAxis(cube.rotation, axis, parsed);
                    }
                }
                case PIVOT -> cube.pivot = withAxis(cube.pivot, axis, parsed);
                default -> {
                    /* not applicable to cube */
                }
            }
            pushCubeMemento(cube, before, cubeMementoDescription(field, cube.name));
        } else if (sel instanceof Selection.BoneSelection bs) {
            var bone = bs.bone();
            var before = ModelerAction.BoneMemento.of(bone);
            switch (field) {
                case POSITION -> bone.position = withAxis(bone.position, axis, parsed);
                case ROTATION -> bone.rotation = withAxis(bone.rotation, axis, parsed);
                case SCALE -> bone.scale = withAxis(bone.scale, axis, parsed);
                case PIVOT -> bone.pivot = withAxis(bone.pivot, axis, parsed);
                default -> {
                    /* not applicable to bone */
                }
            }
            pushBoneMemento(bone, before, boneMementoDescription(field, bone.name));
        }
    }

    private void commitInflate(String text) {
        var parsed = parseDouble(text);
        if (parsed == null) {
            return;
        }
        var inflateSel = ModelerScene.get().selection;
        var cubeSel = primaryCubeSelection(inflateSel);
        if (cubeSel != null) {
            var cube = cubeSel.cube();
            var before = ModelerAction.CubeMemento.of(cube);
            cube.inflate = parsed;
            pushCubeMemento(cube, before, "Edit cube " + cube.name + " (inflate)");
        }
    }

    private void commitBlockRotationAxis(int axis) {
        var cubeSel = primaryCubeSelection(ModelerScene.get().selection);
        if (cubeSel == null) {
            return;
        }
        var cube = cubeSel.cube();
        var before = ModelerAction.CubeMemento.of(cube);
        var current = ModelerBlockElementRotation.view(cube.rotation, axis);
        var angle = blockRotationAngleSelect.currentValue();
        ModelerBlockElementRotation
            .applyBakedRotation(cube, cube.origin, cube.size, cube.pivot, cube.hasPerFaceUv, cube.faceUvs, axis, angle == null ? current.angle() : angle);
        pushCubeMemento(cube, before, "Edit cube " + cube.name + " (block rotation)");
    }

    private void commitBlockRotationAngle(double angle) {
        var cubeSel = primaryCubeSelection(ModelerScene.get().selection);
        if (cubeSel == null) {
            return;
        }
        var cube = cubeSel.cube();
        var before = ModelerAction.CubeMemento.of(cube);
        var current = ModelerBlockElementRotation.view(cube.rotation, blockRotationAxisSelect.currentValue());
        var axis = blockRotationAxisSelect.currentValue();
        ModelerBlockElementRotation
            .applyBakedRotation(cube, cube.origin, cube.size, cube.pivot, cube.hasPerFaceUv, cube.faceUvs, axis == null ? current.axis() : axis, angle);
        pushCubeMemento(cube, before, "Edit cube " + cube.name + " (block rotation)");
    }

    private void commitBlockRotationRescale(boolean rescale) {
        var cubeSel = primaryCubeSelection(ModelerScene.get().selection);
        if (cubeSel == null) {
            return;
        }
        var cube = cubeSel.cube();
        var before = ModelerAction.CubeMemento.of(cube);
        cube.blockElementRescale = rescale;
        pushCubeMemento(cube, before, "Edit cube " + cube.name + " (block rotation rescale)");
    }

    /**
     * Push a memento action if the cube field changed. Inspector commits fire on Enter / focus-loss even when the typed
     * value matches what's already there (e.g. user clicks into the field, doesn't change anything, clicks out) —
     * skipping the no-op push keeps the action stack from filling with empty edits.
     */
    private static void pushCubeMemento(ModelerCube cube, ModelerAction.CubeMemento before, String description) {
        var after = ModelerAction.CubeMemento.of(cube);
        if (!after.differsFrom(before)) {
            return;
        }
        ModelerActionHistory.push(
            new ModelerAction.CubeMementoAction("cube_edit", description, System.currentTimeMillis(), cube, before, after)
        );
    }

    private static void pushBoneMemento(ModelerBone bone, ModelerAction.BoneMemento before, String description) {
        var after = ModelerAction.BoneMemento.of(bone);
        if (!after.differsFrom(before)) {
            return;
        }
        ModelerActionHistory.push(
            new ModelerAction.BoneMementoAction("bone_edit", description, System.currentTimeMillis(), bone, before, after)
        );
    }

    private static @Nullable Selection.CubeSelection primaryCubeSelection(@Nullable Selection selection) {
        if (selection instanceof Selection.CubeSelection cs) {
            return cs;
        }
        if (selection instanceof Selection.FaceSelection fs) {
            return new Selection.CubeSelection(fs.owner(), fs.cube());
        }
        if (selection instanceof Selection.MultiCubeSelection ms) {
            return ms.primary();
        }
        return null;
    }

    private static String cubeMementoDescription(VecField field, String cubeName) {
        var axisLabel = switch (field) {
            case ORIGIN -> "origin";
            case SIZE -> "size";
            case ROTATION -> "rotation";
            case PIVOT -> "pivot";
            default -> field.name().toLowerCase(Locale.ROOT);
        };
        return "Edit cube " + cubeName + " (" + axisLabel + ")";
    }

    private static String boneMementoDescription(VecField field, String boneName) {
        var axisLabel = switch (field) {
            case POSITION -> "position";
            case ROTATION -> "rotation";
            case SCALE -> "scale";
            case PIVOT -> "pivot";
            default -> field.name().toLowerCase(Locale.ROOT);
        };
        return "Edit bone " + boneName + " (" + axisLabel + ")";
    }

    private static Vec3 withAxis(Vec3 v, int axis, double newValue) {
        return switch (axis) {
            case 0 -> new Vec3(newValue, v.y, v.z);
            case 1 -> new Vec3(v.x, newValue, v.z);
            default -> new Vec3(v.x, v.y, newValue);
        };
    }

    private static @Nullable Double parseDouble(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<SearchableSelect.Item<Integer>> blockRotationAxisItems() {
        return List.of(
            new SearchableSelect.Item<>(0, "X"),
            new SearchableSelect.Item<>(1, "Y"),
            new SearchableSelect.Item<>(2, "Z")
        );
    }

    private static List<SearchableSelect.Item<Double>> blockRotationAngleItems() {
        var items = new ArrayList<SearchableSelect.Item<Double>>();
        for (var angle = -180.0; angle <= 180.0 + 1.0e-4; angle += 22.5) {
            items.add(new SearchableSelect.Item<>(angle, angleLabel(angle)));
        }
        return items;
    }

    private static String axisLabel(Integer axis) {
        return switch (axis) {
            case 0 -> "X";
            case 1 -> "Y";
            default -> "Z";
        };
    }

    private static String angleLabel(Double angle) {
        if (angle == null) {
            return "0";
        }
        if (Math.abs(angle - Math.rint(angle)) < 1.0e-4) {
            return String.valueOf((int) Math.rint(angle));
        }
        return String.format(Locale.ROOT, "%.1f", angle);
    }
}
