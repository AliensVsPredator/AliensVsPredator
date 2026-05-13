package com.blib.engine.render.entity;

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

import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.entity.EntityTranslateGizmo;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders the three axis arrows around a selected entity for translation. Same look as
 * {@link com.blib.engine.render.volume.BlockSelectionTranslateGizmoRenderer} — same shader, same color scheme — so
 * users get consistent feedback whether they're translating a block volume or an entity.
 * <p>
 * Anchors on the entity AABB center plus the gizmo's live ghost offset, so the arrows track the preview AABB during a
 * drag. Gated on engine mode + an active {@link EntitySelectable} + {@link EntityGizmoMode#TRANSLATE} so the renderer
 * cleanly gives way to the scale gizmo / block gizmos / no-selection.
 */
@ApiStatus.Internal
public final class EntityTranslateGizmoRenderer {

    private static final float[] DRAG_COLOR = { 1.00f, 0.85f, 0.20f };

    private static final float HOVER_BRIGHTEN = 0.55f;

    private static final float ARROW_ALPHA = 0.85f;

    private EntityTranslateGizmoRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            EntityTranslateGizmo.setHoveredAxis(null);
            return;
        }
        var sel = SelectionManager.current().single();
        if (!(sel instanceof EntitySelectable es)) {
            EntityTranslateGizmo.setHoveredAxis(null);
            return;
        }
        if (EntityGizmoMode.get() != EntityGizmoMode.TRANSLATE) {
            EntityTranslateGizmo.setHoveredAxis(null);
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            EntityTranslateGizmo.setHoveredAxis(null);
            return;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            EntityTranslateGizmo.setHoveredAxis(null);
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        var draggingAxis = EntityTranslateGizmo.draggingAxis();
        if (draggingAxis != null) {
            EntityTranslateGizmo.setHoveredAxis(draggingAxis);
        } else {
            var pick = EntityTranslateGizmo.pickUnderCursorWithDistance(session, entity);
            EntityTranslateGizmo.setHoveredAxis(pick == null ? null : pick.axis());
        }
        var hoveredAxis = EntityTranslateGizmo.hoveredAxis();

        var center = EntityTranslateGizmo.entityCenter(entity, EntityTranslateGizmo.ghostOffset());
        var scale = BlockSelectionScaleGizmo.scaleForCamera(new Vec3(cameraX, cameraY, cameraZ), center);

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

    private static void addArrowQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        BlockSelectionTranslateGizmo.Axis axis,
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
        var shaftLen = BlockSelectionTranslateGizmo.ARROW_LENGTH * scale;
        var shaftHalf = BlockSelectionTranslateGizmo.SHAFT_HALF_WIDTH * scale;
        var tipHalf = BlockSelectionTranslateGizmo.TIP_HALF_WIDTH * scale;

        var dir = axis.direction();
        var shaftEnd = center.add(dir.x * shaftLen, dir.y * shaftLen, dir.z * shaftLen);
        var ax = axis.vanillaAxis();

        var shaftMinX = Math.min(center.x, shaftEnd.x) + (ax == Direction.Axis.X ? 0 : -shaftHalf);
        var shaftMinY = Math.min(center.y, shaftEnd.y) + (ax == Direction.Axis.Y ? 0 : -shaftHalf);
        var shaftMinZ = Math.min(center.z, shaftEnd.z) + (ax == Direction.Axis.Z ? 0 : -shaftHalf);
        var shaftMaxX = Math.max(center.x, shaftEnd.x) + (ax == Direction.Axis.X ? 0 : shaftHalf);
        var shaftMaxY = Math.max(center.y, shaftEnd.y) + (ax == Direction.Axis.Y ? 0 : shaftHalf);
        var shaftMaxZ = Math.max(center.z, shaftEnd.z) + (ax == Direction.Axis.Z ? 0 : shaftHalf);
        addBoxQuads(
            buffer,
            matrix,
            (float) (shaftMinX - camX),
            (float) (shaftMinY - camY),
            (float) (shaftMinZ - camZ),
            (float) (shaftMaxX - camX),
            (float) (shaftMaxY - camY),
            (float) (shaftMaxZ - camZ),
            r,
            g,
            b,
            a
        );

        // Tip cube
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
