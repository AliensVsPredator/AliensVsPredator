package com.blib.engine.blockselection;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders the six face arrows of the capture AABB scale gizmo (MagicaVoxel-style). Each arrow is an axis-colored shaft
 * + tip-cube extending outward from its face along the face normal — visually saying "drag me this direction to
 * grow/shrink this face". Hover and drag states bump the color toward white / accent yellow so the user has clear
 * feedback.
 * <p>
 * Hover detection runs once per frame: ray-cast the cursor against all six arrow pick boxes and stash the closest hit
 * on {@link BlockSelectionScaleGizmo#setHoveredFace}. The same picker is used by the input handler at click time so
 * "what I'm hovering" and "what I clicked" can never disagree.
 * <p>
 * Hooked into the same {@code MixinDebugRenderer} pass as {@link BlockSelectionWireframeRenderer}; the wireframe draws
 * the AABB volume and this draws the arrows on top. Skipped fast when engine mode is off, no AABB exists, or the user
 * is mid-corner-pick (arrows would visually compete with the picking flow).
 */
@ApiStatus.Internal
public final class BlockSelectionScaleGizmoRenderer {

    /** X-axis red. */
    private static final float[] X_COLOR = { 0.95f, 0.30f, 0.30f };

    /** Y-axis green. */
    private static final float[] Y_COLOR = { 0.30f, 0.95f, 0.30f };

    /** Z-axis blue. */
    private static final float[] Z_COLOR = { 0.30f, 0.55f, 0.95f };

    /** Drag-active highlight (yellow). Brighter than hover so the user knows they have a grab. */
    private static final float[] DRAG_COLOR = { 1.00f, 0.85f, 0.20f };

    /** Hover highlight: lerp the axis color toward white. */
    private static final float HOVER_BRIGHTEN = 0.55f;

    /** Arrow alpha. Solid-ish so arrows read clearly above the wireframe volume. */
    private static final float ARROW_ALPHA = 0.85f;

    private BlockSelectionScaleGizmoRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }
        if (com.blib.engine.selection.SelectionManager.current().single() instanceof com.blib.engine.selection.EntitySelectable) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }
        // Only render when Scale is the active tool. Tool switching is exclusive — only one of scale/translate/move
        // is visible at a time so the user always knows what their next click will do.
        if (BlockSelection.gizmoMode() != BlockSelection.GizmoMode.SCALE_VOLUME) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }
        // No handles while the user is mid-corner-pick — they'd visually compete with the corner-picking flow.
        if (BlockSelection.picking() != BlockSelection.PickingState.NONE) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        // Update hover state for this frame. The input handler reads this on click so hover and click target match.
        // While dragging we lock hover to the dragged face — picker is a no-op since the handles haven't moved by
        // the cursor's hit (they're attached to the AABB which the drag is editing).
        var draggingFace = BlockSelectionScaleGizmo.draggingFace();
        if (draggingFace != null) {
            BlockSelectionScaleGizmo.setHoveredFace(draggingFace);
        } else {
            BlockSelectionScaleGizmo.setHoveredFace(BlockSelectionScaleGizmo.pickUnderCursor(session));
        }
        var hoveredFace = BlockSelectionScaleGizmo.hoveredFace();

        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Depth-test off so handles aren't occluded by terrain in front of the AABB — same call the wireframe
        // makes; without this, indoor captures would have invisible handles on the far side of nearby walls.
        RenderSystem.disableDepthTest();

        var renderCamPos = new net.minecraft.world.phys.Vec3(cameraX, cameraY, cameraZ);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (var face : BlockSelectionScaleGizmo.Face.values()) {
            // Per-face scale: arrows farther from the camera grow proportionally so on-screen size stays roughly
            // constant. Anchor is the face center so the scale follows the geometry, not the camera-to-AABB-center
            // distance (which would give nearer faces too-small arrows when the volume is huge).
            var faceCenter = BlockSelectionScaleGizmo.aabbFaceCenter(face, minX, minY, minZ, maxX, maxY, maxZ);
            var scale = BlockSelectionScaleGizmo.scaleForCamera(renderCamPos, faceCenter);
            var color = colorFor(face, face == draggingFace, face == hoveredFace);
            addArrowQuads(buffer, matrix, face, faceCenter, scale, cameraX, cameraY, cameraZ, color[0], color[1], color[2], ARROW_ALPHA);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float[] colorFor(BlockSelectionScaleGizmo.Face face, boolean dragging, boolean hovered) {
        if (dragging) {
            return DRAG_COLOR;
        }
        var base = switch (face.axis()) {
            case X -> X_COLOR;
            case Y -> Y_COLOR;
            case Z -> Z_COLOR;
        };
        if (!hovered) {
            return base;
        }
        // Lerp toward white — gives the hovered handle a clear "you can grab this" pop without changing its hue.
        return new float[] {
            base[0] + (1f - base[0]) * HOVER_BRIGHTEN,
            base[1] + (1f - base[1]) * HOVER_BRIGHTEN,
            base[2] + (1f - base[2]) * HOVER_BRIGHTEN
        };
    }

    /**
     * Render one face arrow as a thin shaft along the face normal followed by a small cube "knob" at the tip. Both
     * pieces share the same color/alpha — hover and drag highlights apply to the whole arrow.
     */
    private static void addArrowQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        BlockSelectionScaleGizmo.Face face,
        net.minecraft.world.phys.Vec3 faceCenter,
        double scale,
        double camX,
        double camY,
        double camZ,
        float r,
        float g,
        float b,
        float a
    ) {
        var n = face.normal();
        var off = BlockSelectionScaleGizmo.ARROW_OFFSET * scale;
        var len = BlockSelectionScaleGizmo.ARROW_LENGTH * scale;
        var shaftHalf = BlockSelectionScaleGizmo.SHAFT_HALF_WIDTH * scale;
        var tipHalf = BlockSelectionScaleGizmo.TIP_HALF_WIDTH * scale;

        // Tail at face plane (with optional small offset); shaft end / tip-cube center at tail + n*len.
        var tailX = faceCenter.x + n.x * off;
        var tailY = faceCenter.y + n.y * off;
        var tailZ = faceCenter.z + n.z * off;
        var tipCx = tailX + n.x * len;
        var tipCy = tailY + n.y * len;
        var tipCz = tailZ + n.z * len;

        // Shaft: from tail to tipCenter, perpendicular thickness on the two non-axis dimensions.
        var sxMin = Math.min(tailX, tipCx) - (n.x == 0 ? shaftHalf : 0);
        var syMin = Math.min(tailY, tipCy) - (n.y == 0 ? shaftHalf : 0);
        var szMin = Math.min(tailZ, tipCz) - (n.z == 0 ? shaftHalf : 0);
        var sxMax = Math.max(tailX, tipCx) + (n.x == 0 ? shaftHalf : 0);
        var syMax = Math.max(tailY, tipCy) + (n.y == 0 ? shaftHalf : 0);
        var szMax = Math.max(tailZ, tipCz) + (n.z == 0 ? shaftHalf : 0);
        addBoxQuads(
            buffer,
            matrix,
            (float) (sxMin - camX),
            (float) (syMin - camY),
            (float) (szMin - camZ),
            (float) (sxMax - camX),
            (float) (syMax - camY),
            (float) (szMax - camZ),
            r,
            g,
            b,
            a
        );

        // Tip cube: centered at tipCenter, half-extent tipHalf in all axes.
        addBoxQuads(
            buffer,
            matrix,
            (float) (tipCx - tipHalf - camX),
            (float) (tipCy - tipHalf - camY),
            (float) (tipCz - tipHalf - camZ),
            (float) (tipCx + tipHalf - camX),
            (float) (tipCy + tipHalf - camY),
            (float) (tipCz + tipHalf - camZ),
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
