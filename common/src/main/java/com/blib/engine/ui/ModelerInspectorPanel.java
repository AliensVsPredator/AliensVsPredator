package com.blib.engine.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;

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

    /**
     * Axis tint colors painted as a small triangle in each input's top-right corner. Match the gizmo's
     * red / green / blue X/Y/Z palette so the inspector and viewport read the same axis affordance.
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

    @Override
    public String title() {
        return "Modeler Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BG_COLOR);
        visibleInputs.clear();

        var scene = ModelerScene.get();
        var selection = scene.selection;
        if (selection == null) {
            drawHeader(graphics, x, y, "Nothing selected");
            return;
        }

        if (selection instanceof Selection.BoneSelection bs) {
            renderBone(graphics, x, y, width, bs.bone(), mouseX, mouseY);
        } else if (selection instanceof Selection.CubeSelection cs) {
            renderCube(graphics, x, y, width, cs.cube(), mouseX, mouseY);
        }
    }

    private void renderCube(GuiGraphics graphics, int x, int y, int width, ModelerCube cube, int mouseX, int mouseY) {
        var font = EngineFont.get();
        drawHeader(graphics, x, y, "Cube: " + cube.name);

        // Sync inputs from the live cube state. Per-input skip-when-focused keeps the user's in-flight edit intact.
        // Size displays as whole numbers (no decimals) since the resize gizmo + commit path both snap to ints.
        syncVec(originX, originY, originZ, cube.origin);
        syncVecInt(sizeX, sizeY, sizeZ, cube.size);
        syncVec(rotationX, rotationY, rotationZ, cube.rotation);
        syncVec(pivotX, pivotY, pivotZ, cube.pivot);
        syncScalar(inflateInput, cube.inflate);

        var rowY = y + HEADER_TOP_PADDING + font.lineHeight + HEADER_TO_SECTION_GAP;
        rowY = renderVecSection(graphics, font, x, rowY, width, "Position", originX, originY, originZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Size", sizeX, sizeY, sizeZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Pivot Point", pivotX, pivotY, pivotZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Rotation", rotationX, rotationY, rotationZ, mouseX, mouseY);
        renderScalarSection(graphics, font, x, rowY, width, "Inflate", inflateInput, mouseX, mouseY);
    }

    private void renderBone(GuiGraphics graphics, int x, int y, int width, ModelerBone bone, int mouseX, int mouseY) {
        var font = EngineFont.get();
        drawHeader(graphics, x, y, "Bone: " + bone.name);

        // Bones only expose pivot + rotation in the inspector; position and size (scale) edits aren't meaningful for
        // a bone group and were dropped for the same reason Blockbench keeps the bone properties minimal.
        syncVec(rotationX, rotationY, rotationZ, bone.rotation);
        syncVec(pivotX, pivotY, pivotZ, bone.pivot);

        var rowY = y + HEADER_TOP_PADDING + font.lineHeight + HEADER_TO_SECTION_GAP;
        rowY = renderVecSection(graphics, font, x, rowY, width, "Pivot Point", pivotX, pivotY, pivotZ, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Rotation", rotationX, rotationY, rotationZ, mouseX, mouseY);
        drawCountRow(graphics, font, x, rowY + CONTENT_PADDING, "Children", bone.children.size(), "Cubes", bone.cubes.size());
    }

    private void drawHeader(GuiGraphics graphics, int x, int y, String text) {
        var font = EngineFont.get();
        graphics.drawString(font, Component.literal(text), x + CONTENT_PADDING, y + HEADER_TOP_PADDING, HEADER_COLOR, false);
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
        var perInput = Math.max(24, available / 3);
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
        var inputW = Math.max(24, width - 2 * CONTENT_PADDING);
        input.render(graphics, inputX, rowY, inputW, mouseX, mouseY);
        visibleInputs.add(input);
        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        graphics.drawString(
            font,
            Component.literal(label),
            x + CONTENT_PADDING,
            // +2 compensates for MC font's descender padding so the section label visually centers.
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            SECTION_HEADER_TEXT_COLOR,
            false
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static void drawCountRow(GuiGraphics graphics, Font font, int x, int y, String label1, int count1, String label2, int count2) {
        graphics.drawString(font, Component.literal(label1 + ": " + count1), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(label2 + ": " + count2), x + CONTENT_PADDING + 80, y, LABEL_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Forward to inputs that were actually rendered this frame; stale rects from the other selection kind don't
        // get a chance to spuriously claim focus.
        for (var input : visibleInputs) {
            if (input.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    // === Sync ===

    private static void syncVec(TextInput xIn, TextInput yIn, TextInput zIn, Vec3 v) {
        syncInput(xIn, formatDouble(v.x));
        syncInput(yIn, formatDouble(v.y));
        syncInput(zIn, formatDouble(v.z));
    }

    /** Like {@link #syncVec} but formats values as plain integers — used for {@code size}, which is whole-number only. */
    private static void syncVecInt(TextInput xIn, TextInput yIn, TextInput zIn, Vec3 v) {
        syncInput(xIn, formatInt(v.x));
        syncInput(yIn, formatInt(v.y));
        syncInput(zIn, formatInt(v.z));
    }

    private static void syncScalar(TextInput input, double value) {
        syncInput(input, formatDouble(value));
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
        return String.format(java.util.Locale.ROOT, "%.2f", v);
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
        if (sel instanceof Selection.CubeSelection cs) {
            var cube = cs.cube();
            switch (field) {
                case ORIGIN -> cube.origin = withAxis(cube.origin, axis, parsed);
                case SIZE -> {
                    // Whole numbers only, clamped at zero — matches the resize gizmo's "no decimals, no negatives"
                    // contract so typed edits and gizmo drags converge to the same data.
                    var sized = (double) Math.max(0L, Math.round(parsed));
                    cube.size = withAxis(cube.size, axis, sized);
                }
                case ROTATION -> cube.rotation = withAxis(cube.rotation, axis, parsed);
                case PIVOT -> cube.pivot = withAxis(cube.pivot, axis, parsed);
                default -> {
                    /* not applicable to cube */
                }
            }
        } else if (sel instanceof Selection.BoneSelection bs) {
            var bone = bs.bone();
            switch (field) {
                case POSITION -> bone.position = withAxis(bone.position, axis, parsed);
                case ROTATION -> bone.rotation = withAxis(bone.rotation, axis, parsed);
                case SCALE -> bone.scale = withAxis(bone.scale, axis, parsed);
                case PIVOT -> bone.pivot = withAxis(bone.pivot, axis, parsed);
                default -> {
                    /* not applicable to bone */
                }
            }
        }
    }

    private void commitInflate(String text) {
        var parsed = parseDouble(text);
        if (parsed == null) {
            return;
        }
        if (ModelerScene.get().selection instanceof Selection.CubeSelection cs) {
            cs.cube().inflate = parsed;
        }
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
}
