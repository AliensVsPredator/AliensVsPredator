package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;

/**
 * Property display for the currently-selected modeler bone or cube. v1 is read-only — shows the selection's fields as
 * formatted text. Editing (live-updating numeric inputs) is deferred; right now this exists so the user can verify the
 * selection state and (in phase J) confirm a loaded model's bone / cube data matches expectations field-by-field.
 */
@ApiStatus.Internal
public final class ModelerInspectorPanel implements Panel {

    private static final int BG_COLOR = 0xFF18181C;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int VALUE_COLOR = 0xFFD0D0D0;

    private static final int HEADER_COLOR = 0xFFE0E0E0;

    private static final int ROW_HEIGHT = 11;

    private static final int PADDING_X = 8;

    private static final int PADDING_Y = 8;

    private static final int LABEL_WIDTH = 60;

    @Override
    public String title() {
        return "Modeler Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BG_COLOR);

        var scene = ModelerScene.get();
        var selection = scene.selection;
        if (selection == null) {
            drawHeader(graphics, x, y, "Nothing selected");
            return;
        }

        if (selection instanceof Selection.BoneSelection bs) {
            renderBone(graphics, x, y, bs.bone());
        } else if (selection instanceof Selection.CubeSelection cs) {
            renderCube(graphics, x, y, cs.cube());
        }
    }

    private void renderBone(GuiGraphics graphics, int x, int y, ModelerBone bone) {
        drawHeader(graphics, x, y, "Bone: " + bone.name);
        var row = 0;
        row = drawVec3Row(graphics, x, y, row, "Position", bone.position);
        row = drawVec3Row(graphics, x, y, row, "Rotation", bone.rotation);
        row = drawVec3Row(graphics, x, y, row, "Scale", bone.scale);
        row = drawVec3Row(graphics, x, y, row, "Pivot", bone.pivot);
        drawCountRow(graphics, x, y, row + 1, "Children", bone.children.size(), "Cubes", bone.cubes.size());
    }

    private void renderCube(GuiGraphics graphics, int x, int y, ModelerCube cube) {
        drawHeader(graphics, x, y, "Cube: " + cube.name);
        var row = 0;
        row = drawVec3Row(graphics, x, y, row, "Origin", cube.origin);
        row = drawVec3Row(graphics, x, y, row, "Size", cube.size);
        row = drawVec3Row(graphics, x, y, row, "Rotation", cube.rotation);
        row = drawVec3Row(graphics, x, y, row, "Pivot", cube.pivot);
        drawScalarRow(graphics, x, y, row, "Inflate", cube.inflate);
    }

    private void drawHeader(GuiGraphics graphics, int x, int y, String text) {
        var font = EngineFont.get();
        graphics.drawString(font, Component.literal(text), x + PADDING_X, y + PADDING_Y, HEADER_COLOR, false);
    }

    private int drawVec3Row(GuiGraphics graphics, int x, int y, int row, String label, Vec3 value) {
        var font = EngineFont.get();
        var rowY = y + PADDING_Y + ROW_HEIGHT * 2 + row * ROW_HEIGHT;
        graphics.drawString(font, Component.literal(label), x + PADDING_X, rowY, LABEL_COLOR, false);
        graphics.drawString(
            font,
            Component.literal(String.format("%.3f, %.3f, %.3f", value.x, value.y, value.z)),
            x + PADDING_X + LABEL_WIDTH,
            rowY,
            VALUE_COLOR,
            false
        );
        return row + 1;
    }

    private int drawScalarRow(GuiGraphics graphics, int x, int y, int row, String label, double value) {
        var font = EngineFont.get();
        var rowY = y + PADDING_Y + ROW_HEIGHT * 2 + row * ROW_HEIGHT;
        graphics.drawString(font, Component.literal(label), x + PADDING_X, rowY, LABEL_COLOR, false);
        graphics.drawString(
            font,
            Component.literal(String.format("%.3f", value)),
            x + PADDING_X + LABEL_WIDTH,
            rowY,
            VALUE_COLOR,
            false
        );
        return row + 1;
    }

    private void drawCountRow(GuiGraphics graphics, int x, int y, int row, String label1, int count1, String label2, int count2) {
        var font = EngineFont.get();
        var rowY = y + PADDING_Y + ROW_HEIGHT * 2 + row * ROW_HEIGHT;
        graphics.drawString(font, Component.literal(label1 + ": " + count1), x + PADDING_X, rowY, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(label2 + ": " + count2), x + PADDING_X + LABEL_WIDTH, rowY, LABEL_COLOR, false);
    }
}
