package com.blib.engine.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.entity.EntityScaleGizmo;
import com.blib.engine.domain.selection.entity.EntityTranslateGizmo;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Translucent wireframe / volume preview drawn at the entity's pending pose during a translate or scale drag. Lets the
 * user see exactly where the entity will end up before the C2S commit fires on release. Translucent yellow to match the
 * scale handle / drag-active translate arrows so the preview visually ties to the active gizmo.
 * <p>
 * No-op when no entity gizmo is mid-drag; the existing {@link com.blib.engine.render.hud.EngineSelectionRenderer}
 * continues to paint the entity's true-pose highlight underneath, so during a drag the user sees both the original
 * (orange highlight) and the destination (yellow ghost).
 */
@ApiStatus.Internal
public final class EntityGhostRenderer {

    private static final float COLOR_R = 1.00f;

    private static final float COLOR_G = 0.85f;

    private static final float COLOR_B = 0.20f;

    private static final float COLOR_A = 0.30f;

    private static final double AABB_INFLATE = 0.04;

    private EntityGhostRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            return;
        }
        // Pick whichever gizmo is mid-drag — at most one, since they share the LMB capture.
        var translateEntity = EntityTranslateGizmo.draggingEntity();
        var scaleEntity = EntityScaleGizmo.draggingEntity();
        if (translateEntity == null && scaleEntity == null) {
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        var entity = translateEntity != null ? translateEntity : scaleEntity;
        if (entity == null || !entity.isAlive()) {
            return;
        }

        // Compose the ghost AABB: original bounds + translate offset, then scaled by the ghost-scale ratio about the
        // entity's feet (centre of bottom face) so growing the entity grows it upward and outward like a vanilla scale
        // attribute would actually appear. Scale ratio is relative to the current attribute scale, not absolute, since
        // the AABB already reflects the entity's current scale.
        var box = entity.getBoundingBox();
        var translateOffset = EntityTranslateGizmo.ghostOffset();
        box = box.move(translateOffset.x, translateOffset.y, translateOffset.z);

        if (scaleEntity != null) {
            var attr = entity.getAttribute(Attributes.SCALE);
            var current = attr == null ? 1.0 : attr.getValue();
            if (current > 0.0) {
                var ratio = EntityScaleGizmo.ghostScale() / current;
                var cx = (box.minX + box.maxX) * 0.5;
                var cz = (box.minZ + box.maxZ) * 0.5;
                var halfX = (box.maxX - box.minX) * 0.5 * ratio;
                var halfZ = (box.maxZ - box.minZ) * 0.5 * ratio;
                var height = (box.maxY - box.minY) * ratio;
                box = new AABB(cx - halfX, box.minY, cz - halfZ, cx + halfX, box.minY + height, cz + halfZ);
            }
        }

        box = box.inflate(AABB_INFLATE);

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var minX = (float) (box.minX - cameraX);
        var minY = (float) (box.minY - cameraY);
        var minZ = (float) (box.minZ - cameraZ);
        var maxX = (float) (box.maxX - cameraX);
        var maxY = (float) (box.maxY - cameraY);
        var maxZ = (float) (box.maxZ - cameraZ);

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, COLOR_R, COLOR_G, COLOR_B, COLOR_A);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
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
