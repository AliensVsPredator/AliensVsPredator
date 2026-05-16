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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerTransforms;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.animation.AnimationCollisionState;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.texture.ModelerFaceTextureMapping;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TextureTool;

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

    private static final int COLLISION_COLOR = 0xFFFF3030;

    private static final int FACE_SELECTION_FILL_COLOR = 0x66FFCC33;

    /** Hover outline color — semi-transparent white so it reads as "preview" against the yellow selection. */
    private static final int HOVER_COLOR = 0xAAFFFFFF;

    private static final int FACE_HOVER_FILL_COLOR = 0x40FFFFFF;

    private static final int TEXTURE_PIXEL_GRID_COLOR = 0x884F8FFF;

    private static final int TEXTURE_PIXEL_HOVER_COLOR = 0xEEFFCC33;

    private static final float TEXTURE_PIXEL_GRID_OFFSET = 0.035f;

    private static final float TEXTURE_PIXEL_HOVER_OFFSET = 0.065f;

    private static final float TEXTURE_PIXEL_GRID_THICKNESS = 0.018f;

    private static final float TEXTURE_PIXEL_HOVER_THICKNESS = 0.085f;

    private static final float GRID_AXIS_EPSILON = 1.0e-6f;

    /** Per-face brightness multiplier in face order (+X, -X, +Y, -Y, +Z, -Z) — fakes a top-lit room. */
    private static final float[] FACE_SHADE = { 0.82f, 0.82f, 1.00f, 0.55f, 0.72f, 0.72f };

    private ModelerCubeRenderer() {}

    public static void render(Matrix4f scenePose, ModelerBone root, @Nullable Selection selection) {
        var pose = new PoseStack();
        pose.last().pose().mul(scenePose);

        // Filled quads first, outline passes on top.
        renderBoneFilled(pose, root);
        if (shouldRenderTexturePixelGrid()) {
            renderTexturePixelGrid(pose, root);
        }

        // Selection targets: a CubeSelection outlines just that cube; a MultiCubeSelection outlines every cube in the
        // group so the viewport mirrors the UV map / outliner multi-highlight; a BoneSelection cascades to every cube
        // in the bone's subtree so the user sees the whole group lit up.
        var selectionTargets = new HashSet<ModelerCube>();
        Selection.FaceSelection selectedFace = null;
        if (selection instanceof Selection.CubeSelection cs) {
            selectionTargets.add(cs.cube());
        } else if (selection instanceof Selection.FaceSelection fs) {
            selectedFace = fs;
            selectionTargets.add(fs.cube());
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
        var collisionTargets = AnimationCollisionState.get().currentCollisionCubes();
        if (!collisionTargets.isEmpty()) {
            renderOutlines(pose, root, collisionTargets, COLLISION_COLOR);
        }
        var hoveredFace = ModelerScene.get().hoveredFace;
        if (hoveredFace != null && !sameFace(hoveredFace, selectedFace)) {
            renderFaceOverlay(pose, root, hoveredFace, FACE_HOVER_FILL_COLOR);
        }
        if (selectedFace != null) {
            renderFaceOverlay(pose, root, selectedFace, FACE_SELECTION_FILL_COLOR);
        }
        var hoveredTexturePixel = ModelerScene.get().hoveredTexturePixel;
        if (hoveredTexturePixel != null) {
            renderTexturePixelHover(pose, root, hoveredTexturePixel);
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
            // Split: imported per-face UV cubes can preview through their captured face rectangles; legacy per-face
            // cubes without rectangles stay flat because box-UV math would be misleading.
            List<ModelerCube> boxTextured = null;
            List<ModelerCube> perFaceTextured = null;
            List<ModelerCube> flat = null;
            for (var cube : bone.cubes) {
                if (cube.hasPerFaceUv && !cube.faceUvs.isEmpty()) {
                    if (perFaceTextured == null) {
                        perFaceTextured = new ArrayList<>();
                    }
                    perFaceTextured.add(cube);
                } else if (active != null && !cube.hasPerFaceUv) {
                    if (boxTextured == null) {
                        boxTextured = new ArrayList<>();
                    }
                    boxTextured.add(cube);
                } else {
                    if (flat == null) {
                        flat = new ArrayList<>();
                    }
                    flat.add(cube);
                }
            }
            if (boxTextured != null) {
                renderTexturedCubes(pose, boxTextured, active.textureId(), (float) scene.textureWidth, (float) scene.textureHeight);
            }
            if (perFaceTextured != null) {
                renderPerFaceTexturedCubes(pose, perFaceTextured, scene);
            }
            if (flat != null) {
                renderFlatCubes(pose, flat);
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

    private static void renderPerFaceTexturedCubes(PoseStack pose, List<ModelerCube> cubes, ModelerScene scene) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        ResourceLocation boundTexture = null;
        BufferBuilder buffer = null;
        var texW = (float) scene.textureWidth;
        var texH = (float) scene.textureHeight;

        for (var cube : cubes) {
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

            for (var face : ModelerCube.Face.values()) {
                var uv = cube.faceUv(face);
                if (uv == null) {
                    continue;
                }
                var textureId = textureIdForFace(scene, uv);
                if (textureId == null) {
                    continue;
                }
                if (!textureId.equals(boundTexture)) {
                    if (buffer != null) {
                        var built = buffer.build();
                        if (built != null) {
                            BufferUploader.drawWithShader(built);
                        }
                    }
                    boundTexture = textureId;
                    RenderSystem.setShaderTexture(0, boundTexture);
                    buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                }
                emitImportedFaceGeometry(buffer, matrix, cube, face, x0, y0, z0, x1, y1, z1, texW, texH);
            }
            pose.popPose();
        }

        if (buffer != null) {
            var built = buffer.build();
            if (built != null) {
                BufferUploader.drawWithShader(built);
            }
        }
    }

    private static @Nullable ResourceLocation textureIdForFace(ModelerScene scene, ModelerCube.FaceUv uv) {
        var source = uv.textureSource();
        if (source != null) {
            for (var loaded : scene.textures) {
                if (source.equals(loaded.sourceResource())) {
                    return loaded.textureId();
                }
            }
        }
        var active = scene.activeTexture;
        return active != null ? active.textureId() : null;
    }

    private static boolean shouldRenderTexturePixelGrid() {
        var tool = TextureEditorState.tool();
        return tool == TextureTool.PENCIL || tool == TextureTool.BUCKET;
    }

    private static void renderTexturePixelGrid(PoseStack pose, ModelerBone root) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        emitTexturePixelGrid(buffer, pose, root, ModelerScene.get());
        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
        RenderSystem.disableBlend();
    }

    private static void emitTexturePixelGrid(BufferBuilder buffer, PoseStack pose, ModelerBone bone, ModelerScene scene) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

        for (var cube : bone.cubes) {
            emitCubeTexturePixelGrid(buffer, pose, cube, scene);
        }
        for (var child : bone.children) {
            emitTexturePixelGrid(buffer, pose, child, scene);
        }

        pose.popPose();
    }

    private static void emitCubeTexturePixelGrid(BufferBuilder buffer, PoseStack pose, ModelerCube cube, ModelerScene scene) {
        pose.pushPose();
        ModelerTransforms.applyCube(pose, cube);
        var matrix = pose.last().pose();
        var color = unpackColor(TEXTURE_PIXEL_GRID_COLOR);

        for (var face : ModelerCube.Face.values()) {
            var texture = ModelerFaceTextureMapping.textureForFace(scene, cube, face);
            var uv = ModelerFaceTextureMapping.uvQuad(cube, face);
            if (texture == null || uv == null) {
                continue;
            }
            var pixels = texture.texture().getPixels();
            if (pixels == null) {
                continue;
            }
            var geometry = ModelerFaceTextureMapping.faceGeometry(cube, face);
            var uvSheetWidth = scene.textureWidth > 0.0 ? scene.textureWidth : pixels.getWidth();
            var uvSheetHeight = scene.textureHeight > 0.0 ? scene.textureHeight : pixels.getHeight();
            var aSteps = uv.cellsAlongA(uvSheetWidth, uvSheetHeight, pixels.getWidth(), pixels.getHeight());
            var bSteps = uv.cellsAlongB(uvSheetWidth, uvSheetHeight, pixels.getWidth(), pixels.getHeight());
            var aLength = geometry.aAxis().length();
            var bLength = geometry.bAxis().length();
            if (aLength < GRID_AXIS_EPSILON || bLength < GRID_AXIS_EPSILON) {
                continue;
            }
            var halfA = (TEXTURE_PIXEL_GRID_THICKNESS * 0.5f) / aLength;
            var halfB = (TEXTURE_PIXEL_GRID_THICKNESS * 0.5f) / bLength;
            for (var i = 0; i <= aSteps; i++) {
                emitGridStripAlongB(buffer, matrix, geometry, i / (float) aSteps, 0.0f, 1.0f, halfA, TEXTURE_PIXEL_GRID_OFFSET, color);
            }
            for (var i = 0; i <= bSteps; i++) {
                emitGridStripAlongA(buffer, matrix, geometry, i / (float) bSteps, 0.0f, 1.0f, halfB, TEXTURE_PIXEL_GRID_OFFSET, color);
            }
        }
        pose.popPose();
    }

    private static void renderTexturePixelHover(PoseStack pose, ModelerBone root, ModelerScene.TexturePixelHover hover) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        emitTexturePixelHover(buffer, pose, root, hover, ModelerScene.get());
        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
        RenderSystem.disableBlend();
    }

    private static void emitTexturePixelHover(
        BufferBuilder buffer,
        PoseStack pose,
        ModelerBone bone,
        ModelerScene.TexturePixelHover hover,
        ModelerScene scene
    ) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

        if (bone == hover.owner()) {
            for (var cube : bone.cubes) {
                if (cube == hover.cube()) {
                    emitTexturePixelHoverCell(buffer, pose, cube, hover.face(), hover.pixelX(), hover.pixelY(), scene);
                    break;
                }
            }
        }
        for (var child : bone.children) {
            emitTexturePixelHover(buffer, pose, child, hover, scene);
        }

        pose.popPose();
    }

    private static void emitTexturePixelHoverCell(
        BufferBuilder buffer,
        PoseStack pose,
        ModelerCube cube,
        ModelerCube.Face face,
        int pixelX,
        int pixelY,
        ModelerScene scene
    ) {
        var uv = ModelerFaceTextureMapping.uvQuad(cube, face);
        if (uv == null) {
            return;
        }
        var texture = ModelerFaceTextureMapping.textureForFace(scene, cube, face);
        var pixels = texture == null ? null : texture.texture().getPixels();
        if (pixels == null) {
            return;
        }
        var uvSheetWidth = scene.textureWidth > 0.0 ? scene.textureWidth : pixels.getWidth();
        var uvSheetHeight = scene.textureHeight > 0.0 ? scene.textureHeight : pixels.getHeight();
        var cell = uv.faceCellForPixel(
            new ModelerFaceTextureMapping.Pixel(pixelX, pixelY),
            uvSheetWidth,
            uvSheetHeight,
            pixels.getWidth(),
            pixels.getHeight()
        );
        if (cell == null) {
            return;
        }

        pose.pushPose();
        ModelerTransforms.applyCube(pose, cube);
        var matrix = pose.last().pose();
        var geometry = ModelerFaceTextureMapping.faceGeometry(cube, face);
        var aLength = geometry.aAxis().length();
        var bLength = geometry.bAxis().length();
        if (aLength < GRID_AXIS_EPSILON || bLength < GRID_AXIS_EPSILON) {
            pose.popPose();
            return;
        }

        var color = unpackColor(TEXTURE_PIXEL_HOVER_COLOR);
        var halfA = (TEXTURE_PIXEL_HOVER_THICKNESS * 0.5f) / aLength;
        var halfB = (TEXTURE_PIXEL_HOVER_THICKNESS * 0.5f) / bLength;
        emitGridStripAlongB(buffer, matrix, geometry, cell.a0(), cell.b0(), cell.b1(), halfA, TEXTURE_PIXEL_HOVER_OFFSET, color);
        emitGridStripAlongB(buffer, matrix, geometry, cell.a1(), cell.b0(), cell.b1(), halfA, TEXTURE_PIXEL_HOVER_OFFSET, color);
        emitGridStripAlongA(buffer, matrix, geometry, cell.b0(), cell.a0(), cell.a1(), halfB, TEXTURE_PIXEL_HOVER_OFFSET, color);
        emitGridStripAlongA(buffer, matrix, geometry, cell.b1(), cell.a0(), cell.a1(), halfB, TEXTURE_PIXEL_HOVER_OFFSET, color);
        pose.popPose();
    }

    private static void emitGridStripAlongB(
        BufferBuilder buffer,
        Matrix4f matrix,
        ModelerFaceTextureMapping.FaceGeometry geometry,
        float a,
        float b0,
        float b1,
        float halfA,
        float offset,
        float[] color
    ) {
        var a0 = clamp01(a - halfA);
        var a1 = clamp01(a + halfA);
        var stripB0 = clamp01(Math.min(b0, b1));
        var stripB1 = clamp01(Math.max(b0, b1));
        addQuadColor(
            buffer,
            matrix,
            geometry.point(a0, stripB0, offset),
            geometry.point(a0, stripB1, offset),
            geometry.point(a1, stripB1, offset),
            geometry.point(a1, stripB0, offset),
            color
        );
    }

    private static void emitGridStripAlongA(
        BufferBuilder buffer,
        Matrix4f matrix,
        ModelerFaceTextureMapping.FaceGeometry geometry,
        float b,
        float a0,
        float a1,
        float halfB,
        float offset,
        float[] color
    ) {
        var b0 = clamp01(b - halfB);
        var b1 = clamp01(b + halfB);
        var stripA0 = clamp01(Math.min(a0, a1));
        var stripA1 = clamp01(Math.max(a0, a1));
        addQuadColor(
            buffer,
            matrix,
            geometry.point(stripA0, b0, offset),
            geometry.point(stripA0, b1, offset),
            geometry.point(stripA1, b1, offset),
            geometry.point(stripA1, b0, offset),
            color
        );
    }

    private static float[] unpackColor(int color) {
        return new float[] {
            ((color >> 16) & 0xFF) / 255f,
            ((color >> 8) & 0xFF) / 255f,
            (color & 0xFF) / 255f,
            ((color >> 24) & 0xFF) / 255f
        };
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
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

        if (cube.hasPerFaceUv && !cube.faceUvs.isEmpty()) {
            emitPerFaceUvs(buffer, matrix, cube, x0, y0, z0, x1, y1, z1, texW, texH);
            pose.popPose();
            return;
        }

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

    private static void emitPerFaceUvs(
        BufferBuilder buffer,
        Matrix4f matrix,
        ModelerCube cube,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1,
        float texW,
        float texH
    ) {
        for (var face : ModelerCube.Face.values()) {
            emitImportedFaceGeometry(buffer, matrix, cube, face, x0, y0, z0, x1, y1, z1, texW, texH);
        }
    }

    private static void emitImportedFaceGeometry(
        BufferBuilder buffer,
        Matrix4f matrix,
        ModelerCube cube,
        ModelerCube.Face face,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1,
        float texW,
        float texH
    ) {
        switch (face) {
            case EAST -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[0]
            );
            case WEST -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[1]
            );
            case UP -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[2]
            );
            case DOWN -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[3]
            );
            case SOUTH -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[4]
            );
            case NORTH -> emitImportedFace(
                buffer,
                matrix,
                cube,
                face,
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
                texW,
                texH,
                FACE_SHADE[5]
            );
        }
    }

    private static void emitImportedFace(
        BufferBuilder buffer,
        Matrix4f matrix,
        ModelerCube cube,
        ModelerCube.Face face,
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
        float texW,
        float texH,
        float shade
    ) {
        var uv = cube.faceUv(face);
        if (uv == null) {
            return;
        }
        emitImportedUvFace(
            buffer,
            matrix,
            vx0,
            vy0,
            vz0,
            vx1,
            vy1,
            vz1,
            vx2,
            vy2,
            vz2,
            vx3,
            vy3,
            vz3,
            (float) uv.u(),
            (float) uv.v(),
            (float) (uv.u() + uv.width()),
            (float) (uv.v() + uv.height()),
            uv.rotation(),
            texW,
            texH,
            shade
        );
    }

    private static void emitImportedUvFace(
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
        float u0,
        float v0,
        float u1,
        float v1,
        int rotation,
        float texW,
        float texH,
        float shade
    ) {
        // Our geometry vertices correspond to BlockFaceUV corner indices 1, 0, 3, 2. Applying the same
        // rotation-index shift as vanilla preserves 90/180/270-degree face UVs used by non-cube blocks like anvils.
        addImportedUvVertex(buffer, matrix, vx0, vy0, vz0, u0, v0, u1, v1, rotation, 1, texW, texH, shade);
        addImportedUvVertex(buffer, matrix, vx1, vy1, vz1, u0, v0, u1, v1, rotation, 0, texW, texH, shade);
        addImportedUvVertex(buffer, matrix, vx2, vy2, vz2, u0, v0, u1, v1, rotation, 3, texW, texH, shade);
        addImportedUvVertex(buffer, matrix, vx3, vy3, vz3, u0, v0, u1, v1, rotation, 2, texW, texH, shade);
    }

    private static void addImportedUvVertex(
        BufferBuilder buffer,
        Matrix4f matrix,
        float vx,
        float vy,
        float vz,
        float u0,
        float v0,
        float u1,
        float v1,
        int rotation,
        int index,
        float texW,
        float texH,
        float shade
    ) {
        var shifted = Math.floorMod(index + rotation / 90, 4);
        var u = shifted == 0 || shifted == 1 ? u0 : u1;
        var v = shifted == 0 || shifted == 3 ? v0 : v1;
        buffer.addVertex(matrix, vx, vy, vz).setUv(u / texW, v / texH).setColor(shade, shade, shade, 1f);
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

    private static boolean sameFace(@Nullable Selection.FaceSelection a, @Nullable Selection.FaceSelection b) {
        return a != null && b != null && a.owner() == b.owner() && a.cube() == b.cube() && a.face() == b.face();
    }

    private static void renderFaceOverlay(PoseStack pose, ModelerBone bone, Selection.FaceSelection target, int color) {
        pose.pushPose();
        ModelerTransforms.applyBone(pose, bone);

        if (bone == target.owner() && bone.cubes.contains(target.cube())) {
            emitFaceOverlay(pose, target.cube(), target.face(), color);
            pose.popPose();
            return;
        }
        for (var child : bone.children) {
            renderFaceOverlay(pose, child, target, color);
        }

        pose.popPose();
    }

    private static void emitFaceOverlay(PoseStack pose, ModelerCube cube, ModelerCube.Face face, int color) {
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
        var offset = 0.025f;

        var a = ((color >> 24) & 0xFF) / 255f;
        var r = ((color >> 16) & 0xFF) / 255f;
        var g = ((color >> 8) & 0xFF) / 255f;
        var b = (color & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        switch (face) {
            case EAST -> addQuadColor(buffer, matrix, x1 + offset, y0, z0, x1 + offset, y1, z0, x1 + offset, y1, z1, x1 + offset, y0, z1, r, g, b, a);
            case WEST -> addQuadColor(buffer, matrix, x0 - offset, y0, z1, x0 - offset, y1, z1, x0 - offset, y1, z0, x0 - offset, y0, z0, r, g, b, a);
            case UP -> addQuadColor(buffer, matrix, x0, y1 + offset, z0, x0, y1 + offset, z1, x1, y1 + offset, z1, x1, y1 + offset, z0, r, g, b, a);
            case DOWN -> addQuadColor(buffer, matrix, x0, y0 - offset, z1, x0, y0 - offset, z0, x1, y0 - offset, z0, x1, y0 - offset, z1, r, g, b, a);
            case SOUTH -> addQuadColor(buffer, matrix, x1, y0, z1 + offset, x1, y1, z1 + offset, x0, y1, z1 + offset, x0, y0, z1 + offset, r, g, b, a);
            case NORTH -> addQuadColor(buffer, matrix, x0, y0, z0 - offset, x0, y1, z0 - offset, x1, y1, z0 - offset, x1, y0, z0 - offset, r, g, b, a);
        }

        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
        RenderSystem.disableBlend();
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
        Vector3f v0,
        Vector3f v1,
        Vector3f v2,
        Vector3f v3,
        float[] color
    ) {
        addQuadColor(
            buffer,
            matrix,
            v0.x,
            v0.y,
            v0.z,
            v1.x,
            v1.y,
            v1.z,
            v2.x,
            v2.y,
            v2.z,
            v3.x,
            v3.y,
            v3.z,
            color[0],
            color[1],
            color[2],
            color[3]
        );
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
