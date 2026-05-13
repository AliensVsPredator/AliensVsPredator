package com.blib.engine.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.entity.EntityScaleGizmo;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders the single Y-axis scale handle on top of a selected entity. Drawn as a yellow shaft + tip — distinct color
 * from the translate gizmo so the user can tell at a glance which mode is active. Anchors on top-center of the entity
 * AABB scaled by the live ghost scale, so the handle floats above the preview as the user drags.
 */
@ApiStatus.Internal
public final class EntityScaleGizmoRenderer {

    private static final float[] BASE_COLOR = { 0.95f, 0.85f, 0.30f };

    private static final float[] DRAG_COLOR = { 1.00f, 0.95f, 0.45f };

    private static final float HOVER_BRIGHTEN = 0.45f;

    private static final float ARROW_ALPHA = 0.85f;

    private EntityScaleGizmoRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        // Gating mirrors {@code GizmoHoverPass.tick()}; hover writes moved there so render is read-only.
        if (!EngineMode.get().isActive()) {
            return;
        }
        var sel = SelectionManager.current().single();
        if (!(sel instanceof EntitySelectable es)) {
            return;
        }
        if (EntityGizmoMode.get() != EntityGizmoMode.SCALE) {
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            return;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        var dragging = EntityScaleGizmo.isDragging();
        var hovered = EntityScaleGizmo.hovered();

        var anchor = EntityScaleGizmo.handleAnchor(entity, EntityScaleGizmo.ghostScale());
        var scale = BlockSelectionScaleGizmo.scaleForCamera(new Vec3(cameraX, cameraY, cameraZ), anchor);

        var color = colorFor(dragging, hovered);

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addArrowQuads(buffer, matrix, anchor, scale, cameraX, cameraY, cameraZ, color[0], color[1], color[2], ARROW_ALPHA);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float[] colorFor(boolean dragging, boolean hovered) {
        if (dragging) {
            return DRAG_COLOR;
        }
        if (!hovered) {
            return BASE_COLOR;
        }
        return new float[] {
            BASE_COLOR[0] + (1f - BASE_COLOR[0]) * HOVER_BRIGHTEN,
            BASE_COLOR[1] + (1f - BASE_COLOR[1]) * HOVER_BRIGHTEN,
            BASE_COLOR[2] + (1f - BASE_COLOR[2]) * HOVER_BRIGHTEN
        };
    }

    private static void addArrowQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        Vec3 anchor,
        double scale,
        double camX,
        double camY,
        double camZ,
        float r,
        float g,
        float b,
        float a
    ) {
        var shaftLen = BlockSelectionScaleGizmo.ARROW_LENGTH * scale;
        var shaftHalf = BlockSelectionScaleGizmo.SHAFT_HALF_WIDTH * scale;
        var tipHalf = BlockSelectionScaleGizmo.TIP_HALF_WIDTH * scale;

        // Shaft: thin rectangular prism along +Y from anchor.
        addBoxQuads(
            buffer,
            matrix,
            (float) (anchor.x - shaftHalf - camX),
            (float) (anchor.y - camY),
            (float) (anchor.z - shaftHalf - camZ),
            (float) (anchor.x + shaftHalf - camX),
            (float) (anchor.y + shaftLen - camY),
            (float) (anchor.z + shaftHalf - camZ),
            r,
            g,
            b,
            a
        );
        // Tip cube at top of shaft.
        var ty = anchor.y + shaftLen;
        addBoxQuads(
            buffer,
            matrix,
            (float) (anchor.x - tipHalf - camX),
            (float) (ty - tipHalf - camY),
            (float) (anchor.z - tipHalf - camZ),
            (float) (anchor.x + tipHalf - camX),
            (float) (ty + tipHalf - camY),
            (float) (anchor.z + tipHalf - camZ),
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
