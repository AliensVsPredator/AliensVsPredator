package com.blib.engine.render.modeler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerTransforms;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;

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

        // Selection targets: a CubeSelection outlines just that cube; a MultiCubeSelection outlines every cube in the
        // group so the viewport mirrors the UV map / outliner multi-highlight; a BoneSelection cascades to every cube
        // in the bone's subtree so the user sees the whole group lit up.
        var selectionTargets = new HashSet<ModelerCube>();
        if (selection instanceof Selection.CubeSelection cs) {
            selectionTargets.add(cs.cube());
        } else if (selection instanceof Selection.MultiCubeSelection ms) {
            for (var cs : ms.cubes()) {
                selectionTargets.add(cs.cube());
            }
        } else if (selection instanceof Selection.BoneSelection bs) {
            collectCubesInSubtree(bs.bone(), selectionTargets);
        }

        // Hover pass — drawn before the selection pass and skipped when the hovered cube is already in the
        // selection set so the selection's yellow always wins on the cube the user has actually picked.
        var hovered = ModelerScene.get().hoveredCube;
        if (hovered != null && !selectionTargets.contains(hovered)) {
            renderOutlines(pose, root, Set.of(hovered), HOVER_COLOR);
        }

        if (!selectionTargets.isEmpty()) {
            renderOutlines(pose, root, selectionTargets, SELECTION_COLOR);
        }

        // Drag ghost pass — when a TRANSLATE / ROTATE / RESIZE drag is in flight, emit a second outline using the
        // baseline (drag-start) field values so the user sees a "phantom" at the original position alongside the
        // live geometry. PIVOT mode is skipped: its drag compensates the body so the ghost would coincide with the
        // live cube and add no visual information.
        var drag = ModelerGizmoState.drag();
        if (drag != null) {
            var mode = drag.mode();
            if (mode == ModelerGizmoMode.TRANSLATE || mode == ModelerGizmoMode.ROTATE || mode == ModelerGizmoMode.RESIZE) {
                renderGhost(pose, root, drag);
            }
        }
    }

    /**
     * Render the drag-ghost outline for the in-flight drag. For a cube drag, this emits one outline for the dragged
     * cube at its baseline pivot/rotation/origin/size/inflate. For a bone drag, this walks the dragged bone's subtree
     * with the bone's baseline transform substituted in, emitting outlines for every cube in the subtree.
     */
    private static void renderGhost(PoseStack pose, ModelerBone root, ModelerGizmoState.DragState drag) {
        var snapshot = drag.startSnapshot();
        if (drag.isBoneDrag()) {
            var bone = snapshot.bone();
            var baseline = drag.startBone();
            if (bone == null || baseline == null) {
                return;
            }
            walkAndEmitBoneGhost(pose, root, bone, baseline);
        } else {
            var cube = snapshot.cube();
            var owner = snapshot.owner();
            var baseline = drag.startCube();
            if (cube == null || owner == null || baseline == null) {
                return;
            }
            walkAndEmitCubeGhost(pose, root, owner, cube, baseline);
        }
    }

    /**
     * Walk the bone tree to {@code owner} (applying transforms onto pose as usual), then emit the ghost outline for
     * {@code target} using {@code baseline} values. Other cubes are skipped — ghost is per-drag-target, not whole-
     * subtree.
     */
    private static boolean walkAndEmitCubeGhost(
        PoseStack pose,
        ModelerBone current,
        ModelerBone owner,
        ModelerCube target,
        ModelerGizmoState.CubeBaseline baseline
    ) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, current);

        if (current == owner) {
            // We're at the owner bone's local frame. Emit the baseline outline for the dragged cube.
            if (current.cubes.contains(target)) {
                emitCubeEdgesFromBaseline(pose, baseline, SELECTION_COLOR);
            }
            pose.popPose();
            return true;
        }
        for (var child : current.children) {
            if (walkAndEmitCubeGhost(pose, child, owner, target, baseline)) {
                pose.popPose();
                return true;
            }
        }

        pose.popPose();
        return false;
    }

    /**
     * Walk the bone tree to {@code target} (applying transforms onto pose as usual), then at the target apply the
     * BASELINE bone transform and recurse into the subtree emitting outlines for every cube. Cubes outside the target's
     * subtree are not ghosted.
     */
    private static boolean walkAndEmitBoneGhost(
        PoseStack pose,
        ModelerBone current,
        ModelerBone target,
        ModelerGizmoState.BoneBaseline baseline
    ) {
        if (current == target) {
            pose.pushPose();
            ModelerTransforms.applyBone(pose, baseline);
            emitSubtreeOutlines(pose, current);
            pose.popPose();
            return true;
        }
        pose.pushPose();
        ModelerTransforms.applyBone(pose, current);
        for (var child : current.children) {
            if (walkAndEmitBoneGhost(pose, child, target, baseline)) {
                pose.popPose();
                return true;
            }
        }
        pose.popPose();
        return false;
    }

    /**
     * Emit selection-color outlines for every cube in {@code bone}'s subtree. The caller is responsible for having the
     * pose stack positioned at the bone's local frame (with the bone's own transform applied — possibly from baseline
     * values for the topmost call); descendant bones get their CURRENT transforms applied recursively since only the
     * target bone's fields change during a bone drag.
     */
    private static void emitSubtreeOutlines(PoseStack pose, ModelerBone bone) {
        for (var cube : bone.cubes) {
            emitCubeEdges(pose, cube, SELECTION_COLOR);
        }
        for (var child : bone.children) {
            pose.pushPose();
            ModelerTransforms.applyBone(pose, child);
            emitSubtreeOutlines(pose, child);
            pose.popPose();
        }
    }

    /** Recursively collects every cube reachable from {@code bone} into {@code out}. */
    private static void collectCubesInSubtree(ModelerBone bone, Set<ModelerCube> out) {
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
            var scene = ModelerScene.get();
            var active = scene.activeTexture;
            if (active == null) {
                renderFlatCubes(pose, bone.cubes);
            } else {
                // Split: hasPerFaceUv cubes can't use box-UV math, so they keep the flat render until v2 wires up
                // per-face UV authoring. Textured cubes go through the position+tex+color path.
                List<ModelerCube> textured = null;
                List<ModelerCube> flat = null;
                for (var cube : bone.cubes) {
                    if (cube.hasPerFaceUv) {
                        if (flat == null) {
                            flat = new ArrayList<>();
                        }
                        flat.add(cube);
                    } else {
                        if (textured == null) {
                            textured = new ArrayList<>();
                        }
                        textured.add(cube);
                    }
                }
                if (textured != null) {
                    renderTexturedCubes(pose, textured, active.textureId(), (float) scene.textureWidth, (float) scene.textureHeight);
                }
                if (flat != null) {
                    renderFlatCubes(pose, flat);
                }
            }
        }

        for (var child : bone.children) {
            renderBoneFilled(pose, child);
        }

        pose.popPose();
    }

    /** Emits the flat-color filled pass for {@code cubes}, all in the caller's current bone-local pose. */
    private static void renderFlatCubes(PoseStack pose, List<ModelerCube> cubes) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (var cube : cubes) {
            emitCubeFaces(buffer, pose, cube);
        }
        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
    }

    /**
     * Emits the textured filled pass for {@code cubes}. Binds {@code textureId}, switches to the position+tex+color
     * shader/format, and writes per-vertex UVs derived from each cube's box-UV origin + size, matching
     * {@code AzBakedModelFactory.buildQuad}'s direction-keyed unwrap so the model in the viewport reads from the same
     * texture region the UV map panel highlights for each face. {@code FACE_SHADE} is preserved as a grayscale vertex
     * tint multiplied into the sampled texture color.
     */
    private static void renderTexturedCubes(
        PoseStack pose,
        List<ModelerCube> cubes,
        ResourceLocation textureId,
        float texW,
        float texH
    ) {
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (var cube : cubes) {
            emitCubeFacesTextured(buffer, pose, cube, texW, texH);
        }
        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
    }

    private static void emitCubeFaces(BufferBuilder buffer, PoseStack pose, ModelerCube cube) {
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
        BufferBuilder buffer,
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

    /**
     * Textured analog of {@link #emitCubeFaces}. Emits 6 box-UV-mapped quads for {@code cube}. UV pixel rects per face
     * mirror {@code AzBakedModelFactory.buildQuad}'s direction switch: EAST/NORTH/WEST/SOUTH unwrap left-to-right on
     * the V-band at {@code v+sz}, UP/DOWN occupy the top row at {@code v}, and the DOWN face uses a negative vSize so
     * its texture sample is V-flipped (Bedrock convention so the bottom of the cube reads right-side-up when viewed
     * from below). Mirrored box-UV cubes follow the baked renderer's convention: WEST/EAST texture islands are applied
     * to the opposite X face, and all faces skip {@code GeoQuad.build}'s non-mirror U swap so the island reads
     * horizontally mirrored. Mapping into this renderer's vertex order is consistent across faces —
     * {@code my[0..3] = vert3, vert0, vert1, vert2}.
     */
    private static void emitCubeFacesTextured(BufferBuilder buffer, PoseStack pose, ModelerCube cube, float texW, float texH) {
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

        var u = (float) cube.uvOriginU;
        var v = (float) cube.uvOriginV;
        // Box-UV uses floor'd integer cube sizes to derive the unwrap rectangle (so a 7.5-pixel-wide cube unwraps as
        // 7).
        var sx = (float) Math.floor(cube.size.x);
        var sy = (float) Math.floor(cube.size.y);
        var sz = (float) Math.floor(cube.size.z);
        var mirror = cube.mirrorUv;
        var eastU = mirror ? u + sz + sx : u;
        var westU = mirror ? u : u + sz + sx;

        // +X (EAST): u_pix=u, v_pix=v+sz, uSize=sz, vSize=sy
        emitTexturedFace(
            buffer,
            matrix,
            x1,
            y0,
            z0,
            x1,
            y1,
            z0,
            x1,
            y1,
            z1,
            x1,
            y0,
            z1,
            eastU,
            v + sz,
            sz,
            sy,
            texW,
            texH,
            mirror,
            FACE_SHADE[0]
        );
        // -X (WEST): u_pix=u+sz+sx, v_pix=v+sz, uSize=sz, vSize=sy
        emitTexturedFace(
            buffer,
            matrix,
            x0,
            y0,
            z1,
            x0,
            y1,
            z1,
            x0,
            y1,
            z0,
            x0,
            y0,
            z0,
            westU,
            v + sz,
            sz,
            sy,
            texW,
            texH,
            mirror,
            FACE_SHADE[1]
        );
        // +Y (UP): u_pix=u+sz, v_pix=v, uSize=sx, vSize=sz
        emitTexturedFace(
            buffer,
            matrix,
            x0,
            y1,
            z0,
            x0,
            y1,
            z1,
            x1,
            y1,
            z1,
            x1,
            y1,
            z0,
            u + sz,
            v,
            sx,
            sz,
            texW,
            texH,
            mirror,
            FACE_SHADE[2]
        );
        // -Y (DOWN): u_pix=u+sz+sx, v_pix=v+sz, uSize=sx, vSize=-sz (V-flipped intentionally)
        emitTexturedFace(
            buffer,
            matrix,
            x0,
            y0,
            z1,
            x0,
            y0,
            z0,
            x1,
            y0,
            z0,
            x1,
            y0,
            z1,
            u + sz + sx,
            v + sz,
            sx,
            -sz,
            texW,
            texH,
            mirror,
            FACE_SHADE[3]
        );
        // +Z (SOUTH): u_pix=u+2sz+sx, v_pix=v+sz, uSize=sx, vSize=sy
        emitTexturedFace(
            buffer,
            matrix,
            x1,
            y0,
            z1,
            x1,
            y1,
            z1,
            x0,
            y1,
            z1,
            x0,
            y0,
            z1,
            u + 2 * sz + sx,
            v + sz,
            sx,
            sy,
            texW,
            texH,
            mirror,
            FACE_SHADE[4]
        );
        // -Z (NORTH): u_pix=u+sz, v_pix=v+sz, uSize=sx, vSize=sy
        emitTexturedFace(
            buffer,
            matrix,
            x0,
            y0,
            z0,
            x0,
            y1,
            z0,
            x1,
            y1,
            z0,
            x1,
            y0,
            z0,
            u + sz,
            v + sz,
            sx,
            sy,
            texW,
            texH,
            mirror,
            FACE_SHADE[5]
        );

        pose.popPose();
    }

    private static void emitTexturedFace(
        BufferBuilder buffer,
        Matrix4f matrix,
        float vx0,
        float vy0,
        float vz0,
        float vx1,
        float vy1,
        float vz1,
        float vx2,
        float vy2,
        float vz2,
        float vx3,
        float vy3,
        float vz3,
        float uPix,
        float vPix,
        float uSize,
        float vSize,
        float texW,
        float texH,
        boolean mirror,
        float shade
    ) {
        // Our vertices arrive in GeoQuad order (vert3, vert0, vert1, vert2). Non-mirror follows GeoQuad.build's U
        // swap; mirror leaves U in the natural left-to-right order.
        var uA = mirror ? uPix / texW : (uPix + uSize) / texW;
        var uB = mirror ? (uPix + uSize) / texW : uPix / texW;
        var vT = vPix / texH;
        var vB = (vPix + vSize) / texH;
        buffer.addVertex(matrix, vx0, vy0, vz0).setUv(uA, vB).setColor(shade, shade, shade, 1f);
        buffer.addVertex(matrix, vx1, vy1, vz1).setUv(uA, vT).setColor(shade, shade, shade, 1f);
        buffer.addVertex(matrix, vx2, vy2, vz2).setUv(uB, vT).setColor(shade, shade, shade, 1f);
        buffer.addVertex(matrix, vx3, vy3, vz3).setUv(uB, vB).setColor(shade, shade, shade, 1f);
    }

    // === Outline pass ===

    /**
     * Walks the full bone tree, emitting outline edges in {@code color} for any cube that's in {@code targets}.
     * Set-based so a bone selection (many cubes) and a cube selection / hover (one cube) share the same render path;
     * the color parameter lets the caller distinguish hover (white, semi-transparent) from selection (yellow).
     */
    private static void renderOutlines(PoseStack pose, ModelerBone bone, Set<ModelerCube> targets, int color) {
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
        emitCubeEdgeQuads(
            pose,
            (float) cube.origin.x,
            (float) cube.origin.y,
            (float) cube.origin.z,
            (float) cube.size.x,
            (float) cube.size.y,
            (float) cube.size.z,
            (float) cube.inflate,
            color
        );
        pose.popPose();
    }

    /**
     * Same as {@link #emitCubeEdges(PoseStack, ModelerCube, int)} but reads from a drag-start baseline — used by the
     * ghost-outline pass to draw at the cube's baseline transform without touching the live cube fields.
     */
    private static void emitCubeEdgesFromBaseline(PoseStack pose, ModelerGizmoState.CubeBaseline baseline, int color) {
        pose.pushPose();
        ModelerTransforms.applyCube(pose, baseline);
        emitCubeEdgeQuads(
            pose,
            (float) baseline.origin().x,
            (float) baseline.origin().y,
            (float) baseline.origin().z,
            (float) baseline.size().x,
            (float) baseline.size().y,
            (float) baseline.size().z,
            (float) baseline.inflate(),
            color
        );
        pose.popPose();
    }

    /** Shared geometry emission for {@link #emitCubeEdges} and {@link #emitCubeEdgesFromBaseline}. */
    private static void emitCubeEdgeQuads(
        PoseStack pose,
        float originX,
        float originY,
        float originZ,
        float sizeX,
        float sizeY,
        float sizeZ,
        float inflate,
        int color
    ) {
        var matrix = pose.last().pose();
        var x0 = originX - inflate;
        var y0 = originY - inflate;
        var z0 = originZ - inflate;
        var x1 = x0 + sizeX + 2 * inflate;
        var y1 = y0 + sizeY + 2 * inflate;
        var z1 = z0 + sizeZ + 2 * inflate;
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
    }

    /** Thin rectangular prism along the X axis from {@code (xa, y, z)} to {@code (xb, y, z)}. */
    private static void emitPrismX(
        BufferBuilder buffer,
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
        BufferBuilder buffer,
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
        BufferBuilder buffer,
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
        BufferBuilder buffer,
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
