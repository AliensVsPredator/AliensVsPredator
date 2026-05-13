package com.blib.engine.render.territory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.territory.ContestedClaimAnimation;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.internal.client.shader.BLibShaders;
import com.blib.internal.client.territory.ClientTerritoryCache;

/**
 * Per-frame world-render hook that draws a thin colored floor plane over every claimed chunk in
 * {@link ClientTerritoryCache}. Inspected-faction planes render at higher alpha than other factions'; contested chunks
 * sit in between. When the {@link ClaimPaintTool} is active, the chunk under the cursor gets a bright yellow plane on
 * top so the user can see exactly where their next claim/unclaim click will land.
 * <p>
 * Earlier versions stacked a full-height translucent column + a ceiling cap; users found those visually obnoxious and
 * hard to see through. The current design is a single near-bedrock slab — visible from above, doesn't compete with
 * terrain when looking horizontally.
 * <p>
 * Visibility is gated by {@link ClaimPaintTool#isOverlayVisible} so the View menu can hide the overlay without
 * affecting paint-tool state. Plugged into the same debug-render pass as
 * {@link com.blib.engine.render.volume.BlockSelectionWireframeRenderer}.
 */
@ApiStatus.Internal
public final class ChunkClaimOverlayRenderer {

    /**
     * Floor-plane Y offset above world bedrock — keeps the plane out of the bedrock layer's z-fighting zone and makes
     * it visible to a player standing on the surface looking down. 1 block above min build height.
     */
    private static final float FLOOR_Y_OFFSET = 1.0f;

    /** Plane thickness in blocks. Thin enough to read as a slab, thick enough to remain visible at glancing angles. */
    private static final float PLANE_THICKNESS = 0.5f;

    /** Inspected faction's plane — bright so the user can spot their own claim at a glance. */
    private static final float OWN_ALPHA = 0.65f;

    /** Other factions' planes — visible but quiet so the inspected one dominates. */
    private static final float OTHER_ALPHA = 0.35f;

    /** Hover plane alpha (paint-mode cursor highlight). */
    private static final float HOVER_ALPHA = 0.75f;

    private static final float HOVER_R = 0.95f;

    private static final float HOVER_G = 0.78f;

    private static final float HOVER_B = 0.30f;

    // Animation timing + lerp math lives in ContestedClaimAnimation — both this renderer and the territory map call
    // it so the two surfaces stay in lockstep. Tuning constants are public on that class.

    private ChunkClaimOverlayRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive() || !ClaimPaintTool.isOverlayVisible()) {
            return;
        }
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        var claims = ClientTerritoryCache.INSTANCE.factionsByChunk();
        var hoveredChunk = ClaimPaintTool.isActive() ? ClaimPaintTool.hoveredChunk() : null;
        if (claims.isEmpty() && hoveredChunk == null) {
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        var matrix = poseStack.last().pose();
        var inspected = inspectedFactionId();
        var floorY = mc.level.getMinBuildHeight() + FLOOR_Y_OFFSET;
        // Cull to the player's loaded-chunk square — that's the region MC actually streams in. Earlier this used
        // Euclidean distance from the engine camera, which (a) shrunk to a circle inscribed in the square (corners
        // clipped) and (b) followed the camera away from the player, hiding still-loaded claims when the user flew
        // off. Player-chunk-grid distance matches what's rendered exactly.
        var renderDist = mc.options.renderDistance().get();
        var playerChunkX = mc.player.chunkPosition().x;
        var playerChunkZ = mc.player.chunkPosition().z;

        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var chunkSource = mc.level.getChunkSource();
        var nowMs = System.currentTimeMillis();

        for (var entry : claims.entrySet()) {
            var pos = entry.getKey();
            var ids = entry.getValue();
            if (ids.isEmpty()) {
                continue;
            }
            if (Math.abs(pos.x - playerChunkX) > renderDist || Math.abs(pos.z - playerChunkZ) > renderDist) {
                continue;
            }
            // Skip planes for chunks that aren't actually streamed in — without this, a faction with claims past the
            // unloaded boundary would still show floor planes hovering in unloaded space, which reads as broken.
            if (!chunkSource.hasChunk(pos.x, pos.z)) {
                continue;
            }

            var contested = ids.size() > 1;
            var isOwn = inspected != null && ids.contains(inspected);

            float[] color;
            float alpha;
            if (contested) {
                // Per-segment color + alpha. The inspected faction's segment uses OWN_ALPHA so that part of the
                // cycle reads as bright as a fully-owned chunk; other claimants get OTHER_ALPHA so they read at the
                // same muted brightness they'd have if THEY were the owner. ContestedClaimAnimation lerps both
                // smoothly through the transitions, so the cell visibly brightens when "your" color is showing.
                var rgbs = new int[ids.size()];
                var alphas = new double[ids.size()];
                for (var i = 0; i < ids.size(); i++) {
                    rgbs[i] = rgbForFaction(ids.get(i));
                    alphas[i] = ids.get(i).equals(inspected) ? OWN_ALPHA : OTHER_ALPHA;
                }
                var sample = ContestedClaimAnimation.sampleAt(rgbs, alphas, nowMs);
                color = unpackRgb(sample.rgb());
                alpha = (float) sample.alpha();
            } else {
                color = colorFromFaction(isOwn ? inspected : ids.get(0));
                alpha = isOwn ? OWN_ALPHA : OTHER_ALPHA;
            }

            addChunkFloorPlane(buffer, matrix, pos, cameraX, cameraY, cameraZ, floorY, color, alpha);
        }

        if (hoveredChunk != null) {
            addChunkFloorPlane(
                buffer,
                matrix,
                hoveredChunk,
                cameraX,
                cameraY,
                cameraZ,
                floorY,
                new float[] { HOVER_R, HOVER_G, HOVER_B },
                HOVER_ALPHA
            );
        }

        // build() returns null instead of throwing when no quads were added — happens when every claim was distance-
        // culled (e.g. user zoomed so far out that no chunk is within render distance). Avoids the buildOrThrow
        // crash; the empty-claims early-out at the top doesn't catch this because the cull happens inside the loop.
        var meshData = buffer.build();
        if (meshData != null) {
            BufferUploader.drawWithShader(meshData);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static @Nullable ResourceLocation inspectedFactionId() {
        var single = SelectionManager.current().single();
        return single instanceof FactionSelectable fs ? fs.factionId() : null;
    }

    /** RGB-only color (lower 24 bits) for a faction; falls back to grey when the directory cache hasn't synced. */
    private static int rgbForFaction(ResourceLocation factionId) {
        var entry = ClientFactionDirectoryCache.get(factionId);
        return entry == null ? 0x888888 : (entry.color() & 0xFFFFFF);
    }

    /** Convert a packed RGB int (lower 24 bits) to a {@code float[3]} normalized for the shader. */
    private static float[] unpackRgb(int rgb) {
        return new float[] {
            ((rgb >> 16) & 0xFF) / 255f,
            ((rgb >> 8) & 0xFF) / 255f,
            (rgb & 0xFF) / 255f
        };
    }

    /** Convert an ARGB color (or 0 if the faction directory hasn't synced yet) to a {r,g,b} float triple. */
    private static float[] colorFromFaction(ResourceLocation factionId) {
        var entry = ClientFactionDirectoryCache.get(factionId);
        var argb = entry == null ? 0xFF888888 : (entry.color() | 0xFF000000);
        return new float[] {
            ((argb >> 16) & 0xFF) / 255f,
            ((argb >> 8) & 0xFF) / 255f,
            (argb & 0xFF) / 255f
        };
    }

    /**
     * Thin horizontal slab at {@code yBase..yBase+PLANE_THICKNESS} spanning the chunk's full 16x16 footprint. This is
     * the only geometry the overlay draws now — earlier versions stacked a full-height column + ceiling cap on top but
     * users found those obscuring and hard to read against terrain.
     */
    private static void addChunkFloorPlane(
        BufferBuilder buffer,
        Matrix4f matrix,
        ChunkPos pos,
        double cameraX,
        double cameraY,
        double cameraZ,
        float yBase,
        float[] color,
        float alpha
    ) {
        var minX = (float) (pos.x * 16 - cameraX);
        var minZ = (float) (pos.z * 16 - cameraZ);
        var maxX = (float) (pos.x * 16 + 16 - cameraX);
        var maxZ = (float) (pos.z * 16 + 16 - cameraZ);
        var planeMin = yBase - (float) cameraY;
        var planeMax = planeMin + PLANE_THICKNESS;
        addBoxQuads(buffer, matrix, minX, planeMin, minZ, maxX, planeMax, maxZ, color[0], color[1], color[2], alpha);
    }

    /** Six-face AABB quad emit — identical pattern to {@code BlockSelectionWireframeRenderer.addBoxQuads}. */
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
