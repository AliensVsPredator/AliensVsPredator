package com.blib.engine.render.volume;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.MoveBlocksGizmo;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders the Move Blocks gizmo — three axis-colored arrows at AABB center, identical geometry to
 * {@link BlockSelectionTranslateGizmoRenderer}. Mode-gated on {@link BlockSelection.GizmoMode#MOVE_BLOCKS}; when the
 * user drags one of these arrows, {@link MoveBlocksGhostRenderer} paints the destination preview at the offset.
 * <p>
 * Visually identical to the translate gizmo on purpose — both belong to the same "axis-arrow" gizmo family. The
 * disambiguation comes from (1) the active toolbar button and (2) the ghost preview that only appears in MOVE_BLOCKS
 * mode after the user starts dragging.
 */
@ApiStatus.Internal
public final class MoveBlocksGizmoRenderer {

    private static final float[] DRAG_COLOR = { 1.00f, 0.85f, 0.20f };

    private static final float HOVER_BRIGHTEN = 0.55f;

    private static final float ARROW_ALPHA = 0.85f;

    private MoveBlocksGizmoRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        // Gating mirrors {@code GizmoHoverPass.tick()}; hover-state writes have moved there so this method is
        // read-only.
        if (!EngineMode.get().isActive()) {
            return;
        }
        if (
            SelectionManager.current()
                .single() instanceof EntitySelectable
        ) {
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        if (BlockSelection.gizmoMode() != BlockSelection.GizmoMode.MOVE_BLOCKS) {
            return;
        }
        if (BlockSelection.picking() != BlockSelection.PickingState.NONE) {
            return;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        var hoveredAxis = MoveBlocksGizmo.hoveredAxis();
        var draggingAxis = MoveBlocksGizmo.draggingAxis();

        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var center = MoveBlocksGizmo.aabbCenter(minX, minY, minZ, maxX, maxY, maxZ);
        var scale = BlockSelectionScaleGizmo.scaleForCamera(new Vec3(cameraX, cameraY, cameraZ), center);

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (var axis : MoveBlocksGizmo.Axis.values()) {
            var color = colorFor(axis, axis == draggingAxis, axis == hoveredAxis);
            addArrowQuads(buffer, matrix, axis, center, scale, cameraX, cameraY, cameraZ, color[0], color[1], color[2], ARROW_ALPHA);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float[] colorFor(MoveBlocksGizmo.Axis axis, boolean dragging, boolean hovered) {
        if (dragging) {
            return DRAG_COLOR;
        }
        var base = axis.color();
        if (!hovered) {
            return base;
        }
        return new float[] {
            base[0] + (1f - base[0]) * HOVER_BRIGHTEN,
            base[1] + (1f - base[1]) * HOVER_BRIGHTEN,
            base[2] + (1f - base[2]) * HOVER_BRIGHTEN
        };
    }

    private static void addArrowQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        MoveBlocksGizmo.Axis axis,
        Vec3 center,
        double scale,
        double camX,
        double camY,
        double camZ,
        float r,
        float g,
        float b,
        float a
    ) {
        var shaftLen = MoveBlocksGizmo.ARROW_LENGTH * scale;
        var shaftHalf = MoveBlocksGizmo.SHAFT_HALF_WIDTH * scale;
        var tipHalf = MoveBlocksGizmo.TIP_HALF_WIDTH * scale;

        var shaftStart = center;
        var dir = axis.direction();
        var shaftEnd = center.add(dir.x * shaftLen, dir.y * shaftLen, dir.z * shaftLen);
        var shaftMin = perpendicularSubtract(shaftStart, shaftEnd, axis, shaftHalf, true);
        var shaftMax = perpendicularSubtract(shaftStart, shaftEnd, axis, shaftHalf, false);
        addBoxQuads(
            buffer,
            matrix,
            (float) (shaftMin[0] - camX),
            (float) (shaftMin[1] - camY),
            (float) (shaftMin[2] - camZ),
            (float) (shaftMax[0] - camX),
            (float) (shaftMax[1] - camY),
            (float) (shaftMax[2] - camZ),
            r,
            g,
            b,
            a
        );

        var tx = shaftEnd.x;
        var ty = shaftEnd.y;
        var tz = shaftEnd.z;
        addBoxQuads(
            buffer,
            matrix,
            (float) (tx - tipHalf - camX),
            (float) (ty - tipHalf - camY),
            (float) (tz - tipHalf - camZ),
            (float) (tx + tipHalf - camX),
            (float) (ty + tipHalf - camY),
            (float) (tz + tipHalf - camZ),
            r,
            g,
            b,
            a
        );
    }

    private static double[] perpendicularSubtract(
        Vec3 start,
        Vec3 end,
        MoveBlocksGizmo.Axis axis,
        double half,
        boolean min
    ) {
        var ax = axis.vanillaAxis();
        var x = chooseEnd(start.x, end.x, min) + (ax == Direction.Axis.X ? 0 : (min ? -half : half));
        var y = chooseEnd(start.y, end.y, min) + (ax == Direction.Axis.Y ? 0 : (min ? -half : half));
        var z = chooseEnd(start.z, end.z, min) + (ax == Direction.Axis.Z ? 0 : (min ? -half : half));
        return new double[] { x, y, z };
    }

    private static double chooseEnd(double a, double b, boolean min) {
        return min ? Math.min(a, b) : Math.max(a, b);
    }

    private static void addBoxQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ,
        float r,
        float g,
        float b,
        float a
    ) {
        // -Z
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        // +Z
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        // -X
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        // +X
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        // -Y
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        // +Y
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
    }
}
