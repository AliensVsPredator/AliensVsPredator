package com.blib.engine.render.selection;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.domain.selection.picking.EngineHoverProbe;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Thin wireframe outline of whatever {@link EngineHoverProbe} says is under the cursor. The outline is the visual cue
 * for "this is what selecting would pick" — distinct from the filled-volume styling that the selection itself uses, so
 * users can tell hover from selected at a glance.
 * <p>
 * Suppressed when the hover target equals the current selection (no point highlighting what's already selected), when
 * engine mode is off, or when the probe has nothing.
 */
@ApiStatus.Internal
public final class EngineHoverRenderer {

    /** Edge thickness in blocks — thin enough to read as an outline, thick enough to remain visible at distance. */
    private static final float EDGE_THICKNESS = 0.025f;

    /** Inflate the target AABB by this amount before drawing edges so the outline sits proud of the surface. */
    private static final double AABB_INFLATE = 0.02;

    /** Outline color — neutral off-white reads as "informational" rather than "selected." */
    private static final float COLOR_R = 0.95f;

    private static final float COLOR_G = 0.95f;

    private static final float COLOR_B = 1.00f;

    private static final float COLOR_A = 0.75f;

    private EngineHoverRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            return;
        }
        var target = EngineHoverProbe.current();
        if (target == null) {
            return;
        }
        if (matchesSelection(target)) {
            return;
        }

        var aabb = aabbFor(target);
        if (aabb == null) {
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addEdgeWireframe(buffer, matrix, aabb.inflate(AABB_INFLATE), cameraX, cameraY, cameraZ);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static boolean matchesSelection(EngineHoverProbe.Target target) {
        var single = SelectionManager.current().single();
        if (single == null) {
            return false;
        }
        return switch (target) {
            case EngineHoverProbe.Target.Entity et -> single instanceof EntitySelectable es && es.entity() == et.entity();
            case EngineHoverProbe.Target.Block bt -> (single instanceof BlockSelectable bs && bs.pos().equals(bt.pos()))
                || (single instanceof BlockVolumeSelectable && BlockSelection.cornerA() != null
                    && bt.pos().equals(BlockSelection.cornerA())
                    && bt.pos().equals(BlockSelection.cornerB()));
            case EngineHoverProbe.Target.Piece pt -> single instanceof PlacedJigsawPieceSelectable ps && ps.id().equals(pt.id());
        };
    }

    private static @Nullable AABB aabbFor(EngineHoverProbe.Target target) {
        return switch (target) {
            case EngineHoverProbe.Target.Entity et -> et.entity().getBoundingBox();
            case EngineHoverProbe.Target.Block bt -> new AABB(bt.pos());
            case EngineHoverProbe.Target.Piece pt -> {
                var piece = ClientPlacedPieceRegistry.get(pt.id());
                yield piece == null ? null : piece.worldAabb();
            }
        };
    }

    /** Draw the 12 edges of an AABB as thin filled rectangles. Each edge is a {@code EDGE_THICKNESS}-thick slab. */
    private static void addEdgeWireframe(
        BufferBuilder buffer,
        Matrix4f matrix,
        AABB aabb,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        var minX = (float) (aabb.minX - cameraX);
        var minY = (float) (aabb.minY - cameraY);
        var minZ = (float) (aabb.minZ - cameraZ);
        var maxX = (float) (aabb.maxX - cameraX);
        var maxY = (float) (aabb.maxY - cameraY);
        var maxZ = (float) (aabb.maxZ - cameraZ);
        var t = EDGE_THICKNESS;

        // 4 horizontal edges along X at min/max Y, min/max Z.
        addBox(buffer, matrix, minX, minY - t / 2, minZ - t / 2, maxX, minY + t / 2, minZ + t / 2);
        addBox(buffer, matrix, minX, minY - t / 2, maxZ - t / 2, maxX, minY + t / 2, maxZ + t / 2);
        addBox(buffer, matrix, minX, maxY - t / 2, minZ - t / 2, maxX, maxY + t / 2, minZ + t / 2);
        addBox(buffer, matrix, minX, maxY - t / 2, maxZ - t / 2, maxX, maxY + t / 2, maxZ + t / 2);
        // 4 horizontal edges along Z at min/max X, min/max Y.
        addBox(buffer, matrix, minX - t / 2, minY - t / 2, minZ, minX + t / 2, minY + t / 2, maxZ);
        addBox(buffer, matrix, maxX - t / 2, minY - t / 2, minZ, maxX + t / 2, minY + t / 2, maxZ);
        addBox(buffer, matrix, minX - t / 2, maxY - t / 2, minZ, minX + t / 2, maxY + t / 2, maxZ);
        addBox(buffer, matrix, maxX - t / 2, maxY - t / 2, minZ, maxX + t / 2, maxY + t / 2, maxZ);
        // 4 vertical edges along Y at the four (X, Z) corners.
        addBox(buffer, matrix, minX - t / 2, minY, minZ - t / 2, minX + t / 2, maxY, minZ + t / 2);
        addBox(buffer, matrix, maxX - t / 2, minY, minZ - t / 2, maxX + t / 2, maxY, minZ + t / 2);
        addBox(buffer, matrix, minX - t / 2, minY, maxZ - t / 2, minX + t / 2, maxY, maxZ + t / 2);
        addBox(buffer, matrix, maxX - t / 2, minY, maxZ - t / 2, maxX + t / 2, maxY, maxZ + t / 2);
    }

    private static void addBox(
        BufferBuilder buffer,
        Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ
    ) {
        var r = COLOR_R;
        var g = COLOR_G;
        var b = COLOR_B;
        var a = COLOR_A;
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
