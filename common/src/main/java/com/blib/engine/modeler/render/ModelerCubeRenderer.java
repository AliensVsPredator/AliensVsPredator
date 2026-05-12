package com.blib.engine.modeler.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.Selection;

/**
 * Emits filled quads + selection outline for every cube in the scene. Walks the bone tree depth-first and applies the
 * same transform stack as {@code RenderUtil.prepMatrixForBone()} — translate to bone, translate to pivot, rotate
 * (Z-Y-X), scale, translate away from pivot — so the in-engine modeler matches how the entity is rendered in-world.
 * <p>
 * Per-cube transform layered on top: translate to cube pivot → rotate around it → translate away. Cube geometry itself
 * is six axis-aligned quads in cube-local coords spanning {@code [origin, origin+size]}, shaded with a hardcoded
 * "AO-style" per-face brightness so faces are readable without a light source.
 */
@ApiStatus.Internal
public final class ModelerCubeRenderer {

    private static final int CUBE_COLOR = 0xFFB8B8C0;

    private static final int SELECTION_COLOR = 0xFFFFCC33;

    /** Per-face brightness multiplier in face order (+X, -X, +Y, -Y, +Z, -Z) — fakes a top-lit room. */
    private static final float[] FACE_SHADE = { 0.82f, 0.82f, 1.00f, 0.55f, 0.72f, 0.72f };

    private ModelerCubeRenderer() {}

    public static void render(Matrix4f scenePose, ModelerBone root, @Nullable Selection selection) {
        var pose = new PoseStack();
        pose.last().pose().mul(scenePose);

        // Two passes so all filled quads emit first (one draw call) and the selection outline draws on top.
        renderBoneFilled(pose, root);

        if (selection instanceof Selection.CubeSelection cs) {
            renderSelectionOutline(pose, root, cs.cube());
        }
    }

    // === Filled-quads pass ===

    private static void renderBoneFilled(PoseStack pose, ModelerBone bone) {
        pose.pushPose();
        applyBoneTransform(pose, bone);

        if (!bone.cubes.isEmpty()) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (var cube : bone.cubes) {
                emitCubeFaces(buffer, pose, cube);
            }
            var built = buffer.build();
            if (built != null) {
                BufferUploader.drawWithShader(built);
            }
        }

        for (var child : bone.children) {
            renderBoneFilled(pose, child);
        }

        pose.popPose();
    }

    private static void applyBoneTransform(PoseStack pose, ModelerBone bone) {
        // Mirror RenderUtil.prepMatrixForBone (position → pivot → rotate → scale → un-pivot). Bedrock model space uses
        // pixel units; the viewport renders in those same units so we don't divide by 16 here.
        pose.translate(bone.position.x, bone.position.y, bone.position.z);
        pose.translate(bone.pivot.x, bone.pivot.y, bone.pivot.z);
        pose.mulPose(Axis.ZP.rotationDegrees((float) bone.rotation.z));
        pose.mulPose(Axis.YP.rotationDegrees((float) bone.rotation.y));
        pose.mulPose(Axis.XP.rotationDegrees((float) bone.rotation.x));
        pose.scale((float) bone.scale.x, (float) bone.scale.y, (float) bone.scale.z);
        pose.translate(-bone.pivot.x, -bone.pivot.y, -bone.pivot.z);
    }

    private static void applyCubeTransform(PoseStack pose, ModelerCube cube) {
        pose.translate(cube.pivot.x, cube.pivot.y, cube.pivot.z);
        pose.mulPose(Axis.ZP.rotationDegrees((float) cube.rotation.z));
        pose.mulPose(Axis.YP.rotationDegrees((float) cube.rotation.y));
        pose.mulPose(Axis.XP.rotationDegrees((float) cube.rotation.x));
        pose.translate(-cube.pivot.x, -cube.pivot.y, -cube.pivot.z);
    }

    private static void emitCubeFaces(com.mojang.blaze3d.vertex.BufferBuilder buffer, PoseStack pose, ModelerCube cube) {
        pose.pushPose();
        applyCubeTransform(pose, cube);
        var matrix = pose.last().pose();

        var inflate = (float) cube.inflate;
        var x0 = (float) cube.origin.x - inflate;
        var y0 = (float) cube.origin.y - inflate;
        var z0 = (float) cube.origin.z - inflate;
        var x1 = x0 + (float) cube.size.x + 2 * inflate;
        var y1 = y0 + (float) cube.size.y + 2 * inflate;
        var z1 = z0 + (float) cube.size.z + 2 * inflate;

        // +X
        addQuad(buffer, matrix, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, FACE_SHADE[0]);
        // -X
        addQuad(buffer, matrix, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, FACE_SHADE[1]);
        // +Y
        addQuad(buffer, matrix, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, FACE_SHADE[2]);
        // -Y
        addQuad(buffer, matrix, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, FACE_SHADE[3]);
        // +Z
        addQuad(buffer, matrix, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, FACE_SHADE[4]);
        // -Z
        addQuad(buffer, matrix, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, FACE_SHADE[5]);

        pose.popPose();
    }

    private static void addQuad(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        float x3,
        float y3,
        float z3,
        float shade
    ) {
        var r = ((CUBE_COLOR >> 16) & 0xFF) / 255f * shade;
        var g = ((CUBE_COLOR >> 8) & 0xFF) / 255f * shade;
        var b = (CUBE_COLOR & 0xFF) / 255f * shade;
        buffer.addVertex(matrix, x0, y0, z0).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, 1f);
        buffer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, 1f);
    }

    // === Selection outline pass ===

    private static void renderSelectionOutline(PoseStack pose, ModelerBone root, ModelerCube target) {
        // Walk the tree looking for the cube; render its 12 edges in the selection colour at the matching pose.
        renderOutlineRecursive(pose, root, target);
    }

    private static boolean renderOutlineRecursive(PoseStack pose, ModelerBone bone, ModelerCube target) {
        pose.pushPose();
        applyBoneTransform(pose, bone);

        var found = false;
        for (var cube : bone.cubes) {
            if (cube == target) {
                emitCubeEdges(pose, cube);
                found = true;
                break;
            }
        }

        if (!found) {
            for (var child : bone.children) {
                if (renderOutlineRecursive(pose, child, target)) {
                    found = true;
                    break;
                }
            }
        }

        pose.popPose();
        return found;
    }

    private static void emitCubeEdges(PoseStack pose, ModelerCube cube) {
        pose.pushPose();
        applyCubeTransform(pose, cube);
        var matrix = pose.last().pose();

        var inflate = (float) cube.inflate + 0.01f; // tiny outset so lines don't z-fight with the filled cube
        var x0 = (float) cube.origin.x - inflate;
        var y0 = (float) cube.origin.y - inflate;
        var z0 = (float) cube.origin.z - inflate;
        var x1 = x0 + (float) cube.size.x + 2 * inflate;
        var y1 = y0 + (float) cube.size.y + 2 * inflate;
        var z1 = z0 + (float) cube.size.z + 2 * inflate;

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(2.0f);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        // 12 edges of the AABB
        addEdge(buffer, matrix, x0, y0, z0, x1, y0, z0);
        addEdge(buffer, matrix, x0, y0, z1, x1, y0, z1);
        addEdge(buffer, matrix, x0, y1, z0, x1, y1, z0);
        addEdge(buffer, matrix, x0, y1, z1, x1, y1, z1);
        addEdge(buffer, matrix, x0, y0, z0, x0, y1, z0);
        addEdge(buffer, matrix, x1, y0, z0, x1, y1, z0);
        addEdge(buffer, matrix, x0, y0, z1, x0, y1, z1);
        addEdge(buffer, matrix, x1, y0, z1, x1, y1, z1);
        addEdge(buffer, matrix, x0, y0, z0, x0, y0, z1);
        addEdge(buffer, matrix, x1, y0, z0, x1, y0, z1);
        addEdge(buffer, matrix, x0, y1, z0, x0, y1, z1);
        addEdge(buffer, matrix, x1, y1, z0, x1, y1, z1);

        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }

        pose.popPose();
    }

    private static void addEdge(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1
    ) {
        var a = ((SELECTION_COLOR >> 24) & 0xFF) / 255f;
        var r = ((SELECTION_COLOR >> 16) & 0xFF) / 255f;
        var g = ((SELECTION_COLOR >> 8) & 0xFF) / 255f;
        var b = (SELECTION_COLOR & 0xFF) / 255f;
        var dx = x1 - x0;
        var dy = y1 - y0;
        var dz = z1 - z0;
        var len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        var nx = dx / len;
        var ny = dy / len;
        var nz = dz / len;
        buffer.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }
}
