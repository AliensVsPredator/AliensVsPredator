package com.blib.engine.render.volume;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders three axis-colored arrows extending from the capture AABB center for translation. Each arrow is a thin
 * rectangular prism (the shaft) ending in a small cube (the tip) — recognizably "arrows" without the cost of proper
 * conical tip geometry. Hover and drag color changes mirror the scale gizmo's pattern (lerp toward white on hover,
 * accent yellow while dragging) so both gizmos feel like the same family.
 * <p>
 * Both shaft and tip scale with camera distance via {@link BlockSelectionScaleGizmo#scaleForCamera}, so the arrows stay
 * legible at any zoom level. Drawn in the same MixinDebugRenderer pass as the wireframe + scale handles, with
 * depth-test off so arrows aren't occluded by terrain in front of the AABB.
 */
@ApiStatus.Internal
public final class BlockSelectionTranslateGizmoRenderer {

    /** Drag-active highlight (yellow). Brighter than hover so the user knows they have a grab. */
    private static final float[] DRAG_COLOR = { 1.00f, 0.85f, 0.20f };

    /** Hover highlight: lerp the axis color toward white. Same factor as {@link BlockSelectionScaleGizmoRenderer}. */
    private static final float HOVER_BRIGHTEN = 0.55f;

    /** Arrow alpha. Slightly less than the scale-handle alpha so the wireframe behind it stays visible. */
    private static final float ARROW_ALPHA = 0.85f;

    private BlockSelectionTranslateGizmoRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        // Entity selection takes over the gizmo surface — block gizmo hides while an entity is selected. Hover state
        // is also cleared so a stale hovered axis doesn't render on the next non-entity frame.
        if (
            com.blib.engine.domain.selection.picking.SelectionManager.current()
                .single() instanceof com.blib.engine.domain.selection.picking.EntitySelectable
        ) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        // Only render when Translate is the active tool — exclusive with the scale gizmo.
        if (BlockSelection.gizmoMode() != BlockSelection.GizmoMode.TRANSLATE_VOLUME) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        if (BlockSelection.picking() != BlockSelection.PickingState.NONE) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        // Hover detection: lock to the dragged axis if a drag is in progress, else re-pick under the cursor.
        var draggingAxis = BlockSelectionTranslateGizmo.draggingAxis();
        if (draggingAxis != null) {
            BlockSelectionTranslateGizmo.setHoveredAxis(draggingAxis);
        } else {
            BlockSelectionTranslateGizmo.setHoveredAxis(BlockSelectionTranslateGizmo.pickUnderCursor(session));
        }
        var hoveredAxis = BlockSelectionTranslateGizmo.hoveredAxis();

        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var center = BlockSelectionTranslateGizmo.aabbCenter(minX, minY, minZ, maxX, maxY, maxZ);
        var scale = BlockSelectionScaleGizmo.scaleForCamera(new net.minecraft.world.phys.Vec3(cameraX, cameraY, cameraZ), center);

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (var axis : BlockSelectionTranslateGizmo.Axis.values()) {
            var color = colorFor(axis, axis == draggingAxis, axis == hoveredAxis);
            addArrowQuads(buffer, matrix, axis, center, scale, cameraX, cameraY, cameraZ, color[0], color[1], color[2], ARROW_ALPHA);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float[] colorFor(BlockSelectionTranslateGizmo.Axis axis, boolean dragging, boolean hovered) {
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

    /**
     * Render an arrow as a shaft (thin rectangular prism along the axis) plus a tip cube. Both pieces are camera-
     * relative and use the same color/alpha. Geometry is built per-axis to keep the perpendicular extents on the
     * correct two axes (e.g. for an X arrow, perpendicular extents are on Y and Z).
     */
    private static void addArrowQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        BlockSelectionTranslateGizmo.Axis axis,
        net.minecraft.world.phys.Vec3 center,
        double scale,
        double camX,
        double camY,
        double camZ,
        float r,
        float g,
        float b,
        float a
    ) {
        var shaftLen = BlockSelectionTranslateGizmo.ARROW_LENGTH * scale;
        var shaftHalf = BlockSelectionTranslateGizmo.SHAFT_HALF_WIDTH * scale;
        var tipHalf = BlockSelectionTranslateGizmo.TIP_HALF_WIDTH * scale;

        // Shaft: from `center` to `center + axis*shaftLen`, with thickness `shaftHalf` perpendicular.
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

        // Tip cube: centered on shaftEnd, with half-extent `tipHalf` in all axes.
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

    /**
     * Compute the min or max of the shaft's bounding box. The shaft extends along {@code axis} between two world
     * points; perpendicular extents (the other two axes) get inflated by {@code half}. Returns a 3-element array
     * {@code (x, y, z)}.
     */
    private static double[] perpendicularSubtract(
        net.minecraft.world.phys.Vec3 start,
        net.minecraft.world.phys.Vec3 end,
        BlockSelectionTranslateGizmo.Axis axis,
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
