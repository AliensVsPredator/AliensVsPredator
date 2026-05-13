package com.blib.engine.render.modeler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerTransforms;
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

    /** Hover outline color — semi-transparent white so it reads as "preview" against the yellow selection. */
    private static final int HOVER_COLOR = 0xAAFFFFFF;

    /** Per-face brightness multiplier in face order (+X, -X, +Y, -Y, +Z, -Z) — fakes a top-lit room. */
    private static final float[] FACE_SHADE = { 0.82f, 0.82f, 1.00f, 0.55f, 0.72f, 0.72f };

    private ModelerCubeRenderer() {}

    public static void render(Matrix4f scenePose, ModelerBone root, @Nullable Selection selection) {
        var pose = new PoseStack();
        pose.last().pose().mul(scenePose);

        // Filled quads first, outline passes on top.
        renderBoneFilled(pose, root);

        // Selection targets: a CubeSelection outlines just that cube; a BoneSelection cascades to every cube in the
        // bone's subtree so the user sees the whole group lit up.
        var selectionTargets = new java.util.HashSet<ModelerCube>();
        if (selection instanceof Selection.CubeSelection cs) {
            selectionTargets.add(cs.cube());
        } else if (selection instanceof Selection.BoneSelection bs) {
            collectCubesInSubtree(bs.bone(), selectionTargets);
        }

        // Hover pass — drawn before the selection pass and skipped when the hovered cube is already in the
        // selection set so the selection's yellow always wins on the cube the user has actually picked.
        var hovered = ModelerScene.get().hoveredCube;
        if (hovered != null && !selectionTargets.contains(hovered)) {
            renderOutlines(pose, root, java.util.Set.of(hovered), HOVER_COLOR);
        }

        if (!selectionTargets.isEmpty()) {
            renderOutlines(pose, root, selectionTargets, SELECTION_COLOR);
        }
    }

    /** Recursively collects every cube reachable from {@code bone} into {@code out}. */
    private static void collectCubesInSubtree(ModelerBone bone, java.util.Set<ModelerCube> out) {
        out.addAll(bone.cubes);
        for (var child : bone.children) {
            collectCubesInSubtree(child, out);
        }
    }

    // === Filled-quads pass ===

    private static void renderBoneFilled(PoseStack pose, ModelerBone bone) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

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

    private static void emitCubeFaces(com.mojang.blaze3d.vertex.BufferBuilder buffer, PoseStack pose, ModelerCube cube) {
        pose.pushPose();
        ModelerTransforms.applyCube(pose, cube);
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

    // === Outline pass ===

    /**
     * Walks the full bone tree, emitting outline edges in {@code color} for any cube that's in {@code targets}.
     * Set-based so a bone selection (many cubes) and a cube selection / hover (one cube) share the same render path;
     * the color parameter lets the caller distinguish hover (white, semi-transparent) from selection (yellow).
     */
    private static void renderOutlines(PoseStack pose, ModelerBone bone, java.util.Set<ModelerCube> targets, int color) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

        for (var cube : bone.cubes) {
            if (targets.contains(cube)) {
                emitCubeEdges(pose, cube, color);
            }
        }
        for (var child : bone.children) {
            renderOutlines(pose, child, targets, color);
        }

        pose.popPose();
    }

    /**
     * Outline thickness in cube-local pixels. Real world-space geometry instead of {@link RenderSystem#lineWidth}
     * because GPU drivers cap line width and apply per-pixel anti-aliasing inconsistently across angled lines,
     * producing the "thin from certain angles" look. With a thin rectangular prism per edge, thickness is uniform
     * regardless of view direction.
     */
    private static final float OUTLINE_THICKNESS = 0.05f;

    /**
     * Outline pass for a single cube — emits 12 thin rectangular prisms (one per cube edge) using
     * {@code POSITION_COLOR} with alpha blending. Each prism is centered on its edge and extends
     * {@link #OUTLINE_THICKNESS} cube-local pixels into each perpendicular axis, so the outline reads as a
     * uniform-width cube wireframe from every camera angle.
     */
    private static void emitCubeEdges(PoseStack pose, ModelerCube cube, int color) {
        pose.pushPose();
        ModelerTransforms.applyCube(pose, cube);
        var matrix = pose.last().pose();

        var inflate = (float) cube.inflate;
        var x0 = (float) cube.origin.x - inflate;
        var y0 = (float) cube.origin.y - inflate;
        var z0 = (float) cube.origin.z - inflate;
        var x1 = x0 + (float) cube.size.x + 2 * inflate;
        var y1 = y0 + (float) cube.size.y + 2 * inflate;
        var z1 = z0 + (float) cube.size.z + 2 * inflate;
        var t = OUTLINE_THICKNESS;

        var a = ((color >> 24) & 0xFF) / 255f;
        var r = ((color >> 16) & 0xFF) / 255f;
        var g = ((color >> 8) & 0xFF) / 255f;
        var b = (color & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Four X-axis edges (vary by which Y/Z corner they're on).
        emitPrismX(buffer, matrix, x0, x1, y0, z0, t, r, g, b, a);
        emitPrismX(buffer, matrix, x0, x1, y1, z0, t, r, g, b, a);
        emitPrismX(buffer, matrix, x0, x1, y0, z1, t, r, g, b, a);
        emitPrismX(buffer, matrix, x0, x1, y1, z1, t, r, g, b, a);
        // Four Y-axis edges.
        emitPrismY(buffer, matrix, x0, y0, y1, z0, t, r, g, b, a);
        emitPrismY(buffer, matrix, x1, y0, y1, z0, t, r, g, b, a);
        emitPrismY(buffer, matrix, x0, y0, y1, z1, t, r, g, b, a);
        emitPrismY(buffer, matrix, x1, y0, y1, z1, t, r, g, b, a);
        // Four Z-axis edges.
        emitPrismZ(buffer, matrix, x0, y0, z0, z1, t, r, g, b, a);
        emitPrismZ(buffer, matrix, x1, y0, z0, z1, t, r, g, b, a);
        emitPrismZ(buffer, matrix, x0, y1, z0, z1, t, r, g, b, a);
        emitPrismZ(buffer, matrix, x1, y1, z0, z1, t, r, g, b, a);

        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
        RenderSystem.disableBlend();

        pose.popPose();
    }

    /** Thin rectangular prism along the X axis from {@code (xa, y, z)} to {@code (xb, y, z)}. */
    private static void emitPrismX(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float xa,
        float xb,
        float y,
        float z,
        float t,
        float r,
        float g,
        float b,
        float a
    ) {
        float ymin = y - t, ymax = y + t, zmin = z - t, zmax = z + t;
        addQuadColor(buffer, matrix, xa, ymin, zmin, xa, ymax, zmin, xb, ymax, zmin, xb, ymin, zmin, r, g, b, a); // -Z
        addQuadColor(buffer, matrix, xb, ymin, zmax, xb, ymax, zmax, xa, ymax, zmax, xa, ymin, zmax, r, g, b, a); // +Z
        addQuadColor(buffer, matrix, xa, ymax, zmin, xa, ymax, zmax, xb, ymax, zmax, xb, ymax, zmin, r, g, b, a); // +Y
        addQuadColor(buffer, matrix, xa, ymin, zmax, xa, ymin, zmin, xb, ymin, zmin, xb, ymin, zmax, r, g, b, a); // -Y
        addQuadColor(buffer, matrix, xa, ymin, zmax, xa, ymax, zmax, xa, ymax, zmin, xa, ymin, zmin, r, g, b, a); // -X
                                                                                                                  // cap
        addQuadColor(buffer, matrix, xb, ymin, zmin, xb, ymax, zmin, xb, ymax, zmax, xb, ymin, zmax, r, g, b, a); // +X
                                                                                                                  // cap
    }

    /** Thin rectangular prism along the Y axis from {@code (x, ya, z)} to {@code (x, yb, z)}. */
    private static void emitPrismY(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float x,
        float ya,
        float yb,
        float z,
        float t,
        float r,
        float g,
        float b,
        float a
    ) {
        float xmin = x - t, xmax = x + t, zmin = z - t, zmax = z + t;
        addQuadColor(buffer, matrix, xmin, ya, zmin, xmax, ya, zmin, xmax, yb, zmin, xmin, yb, zmin, r, g, b, a); // -Z
        addQuadColor(buffer, matrix, xmin, yb, zmax, xmax, yb, zmax, xmax, ya, zmax, xmin, ya, zmax, r, g, b, a); // +Z
        addQuadColor(buffer, matrix, xmax, ya, zmin, xmax, ya, zmax, xmax, yb, zmax, xmax, yb, zmin, r, g, b, a); // +X
        addQuadColor(buffer, matrix, xmin, yb, zmin, xmin, yb, zmax, xmin, ya, zmax, xmin, ya, zmin, r, g, b, a); // -X
        addQuadColor(buffer, matrix, xmin, ya, zmax, xmax, ya, zmax, xmax, ya, zmin, xmin, ya, zmin, r, g, b, a); // -Y
                                                                                                                  // cap
        addQuadColor(buffer, matrix, xmin, yb, zmin, xmax, yb, zmin, xmax, yb, zmax, xmin, yb, zmax, r, g, b, a); // +Y
                                                                                                                  // cap
    }

    /** Thin rectangular prism along the Z axis from {@code (x, y, za)} to {@code (x, y, zb)}. */
    private static void emitPrismZ(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float x,
        float y,
        float za,
        float zb,
        float t,
        float r,
        float g,
        float b,
        float a
    ) {
        float xmin = x - t, xmax = x + t, ymin = y - t, ymax = y + t;
        addQuadColor(buffer, matrix, xmin, ymin, za, xmin, ymax, za, xmax, ymax, za, xmax, ymin, za, r, g, b, a); // -Z
                                                                                                                  // cap
        addQuadColor(buffer, matrix, xmax, ymin, zb, xmax, ymax, zb, xmin, ymax, zb, xmin, ymin, zb, r, g, b, a); // +Z
                                                                                                                  // cap
        addQuadColor(buffer, matrix, xmin, ymax, za, xmin, ymax, zb, xmax, ymax, zb, xmax, ymax, za, r, g, b, a); // +Y
        addQuadColor(buffer, matrix, xmin, ymin, zb, xmin, ymin, za, xmax, ymin, za, xmax, ymin, zb, r, g, b, a); // -Y
        addQuadColor(buffer, matrix, xmax, ymin, za, xmax, ymax, za, xmax, ymax, zb, xmax, ymin, zb, r, g, b, a); // +X
        addQuadColor(buffer, matrix, xmin, ymin, zb, xmin, ymax, zb, xmin, ymax, za, xmin, ymin, za, r, g, b, a); // -X
    }

    private static void addQuadColor(
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
        float r,
        float g,
        float b,
        float a
    ) {
        buffer.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a);
        buffer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a);
    }
}
