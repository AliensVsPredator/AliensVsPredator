package com.blib.mod.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

/**
 * Client-side debug renderer that visualizes A* search exploration. Renders explored nodes with terrain-type coloring,
 * surface direction arrows, section corridor wireframes, and path highlighting. Entries fade over 10 seconds.
 */
public final class PathfindingSearchDebugRenderer {

    public static final PathfindingSearchDebugRenderer INSTANCE = new PathfindingSearchDebugRenderer();

    private static final long FADE_DURATION_MS = 10_000L;

    private static final float NODE_SIZE = 0.2f;

    private static final float NODE_HALF = NODE_SIZE / 2.0f;

    private static final float PATH_NODE_SIZE = 0.35f;

    private static final float PATH_NODE_HALF = PATH_NODE_SIZE / 2.0f;

    private static final int SECTION_SIZE = 16;

    private static final float[] DEFAULT_PATH_COLOR = { 1.0f, 1.0f, 1.0f };

    private static final float[] TARGET_NODE_COLOR = { 1.0f, 0.85f, 0.0f };

    private static final float[] CURRENT_NODE_COLOR = { 0.0f, 1.0f, 1.0f };

    private static final float[] NEXT_NODE_COLOR = { 1.0f, 0.3f, 1.0f };

    private static final float[] PREVIOUS_NODE_COLOR = { 1.0f, 0.5f, 0.0f };

    private final Map<Integer, TimestampedSnapshot> snapshots = new ConcurrentHashMap<>();

    public void update(S2CPathfindingSearchDebugPayload payload) {
        snapshots.put(payload.entityId(), new TimestampedSnapshot(payload, System.currentTimeMillis()));
    }

    public void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        var now = System.currentTimeMillis();
        var iterator = snapshots.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            var snapshot = entry.getValue();
            var age = now - snapshot.receivedTimeMillis;

            if (age > FADE_DURATION_MS) {
                iterator.remove();
                continue;
            }

            var alpha = 1.0f - (float) age / FADE_DURATION_MS;

            renderSnapshot(poseStack, bufferSource, cameraX, cameraY, cameraZ, snapshot.payload, alpha);
        }
    }

    private void renderSnapshot(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload,
        float alpha
    ) {
        renderCorridorSections(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload, alpha);
        renderNodes(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload, alpha);
    }

    private void renderNodes(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload,
        float alpha
    ) {
        for (var node : payload.nodes()) {
            if (node.onPath()) {
                renderPathNode(poseStack, bufferSource, cameraX, cameraY, cameraZ, node, payload.entityId(), alpha);
            } else {
                renderExploredNode(poseStack, bufferSource, cameraX, cameraY, cameraZ, node, alpha);
            }
        }
    }

    private void renderExploredNode(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        DebugNodeEntry node,
        float alpha
    ) {
        var color = getTerrainColor(node.terrainType());
        var centerX = node.x() + 0.5 - cameraX;
        var centerY = node.y() + 0.5 - cameraY;
        var centerZ = node.z() + 0.5 - cameraZ;

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);

        LevelRenderer.renderLineBox(
            poseStack,
            bufferSource.getBuffer(RenderType.lines()),
            -NODE_HALF,
            -NODE_HALF,
            -NODE_HALF,
            NODE_HALF,
            NODE_HALF,
            NODE_HALF,
            color[0],
            color[1],
            color[2],
            alpha * 0.6f
        );

        poseStack.popPose();
    }

    private void renderPathNode(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        DebugNodeEntry node,
        int entityId,
        float alpha
    ) {
        var pathColor = getPathNodeColor(node.pathIndex(), entityId);
        var terrainColor = getTerrainColor(node.terrainType());
        var centerX = node.x() + 0.5 - cameraX;
        var centerY = node.y() + 0.5 - cameraY;
        var centerZ = node.z() + 0.5 - cameraZ;

        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);

        var buffer = bufferSource.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(
            poseStack,
            buffer,
            -PATH_NODE_HALF,
            -PATH_NODE_HALF,
            -PATH_NODE_HALF,
            PATH_NODE_HALF,
            PATH_NODE_HALF,
            PATH_NODE_HALF,
            pathColor[0],
            pathColor[1],
            pathColor[2],
            alpha
        );

        LevelRenderer.renderLineBox(
            poseStack,
            buffer,
            -NODE_HALF,
            -NODE_HALF,
            -NODE_HALF,
            NODE_HALF,
            NODE_HALF,
            NODE_HALF,
            terrainColor[0],
            terrainColor[1],
            terrainColor[2],
            alpha * 0.6f
        );

        poseStack.popPose();
    }

    private static float[] getPathNodeColor(int pathIndex, int entityId) {
        var navPayload = PathfindingNavDebugHUD.INSTANCE.getLatestPayload();

        if (navPayload == null || navPayload.entityId() != entityId) {
            return DEFAULT_PATH_COLOR;
        }

        var totalNodes = navPayload.totalNodes();
        var currentIndex = navPayload.currentNodeIndex();

        if (totalNodes > 0 && pathIndex == totalNodes - 1) {
            return TARGET_NODE_COLOR;
        }

        if (pathIndex == currentIndex) {
            return CURRENT_NODE_COLOR;
        }

        if (pathIndex == currentIndex + 1) {
            return NEXT_NODE_COLOR;
        }

        if (pathIndex == currentIndex - 1) {
            return PREVIOUS_NODE_COLOR;
        }

        return DEFAULT_PATH_COLOR;
    }

    private void renderCorridorSections(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload,
        float alpha
    ) {
        for (var key : payload.corridorKeys()) {
            var sectionX = unpackSectionX(key);
            var sectionY = unpackSectionY(key);
            var sectionZ = unpackSectionZ(key);

            var minX = sectionX * SECTION_SIZE - cameraX;
            var minY = sectionY * SECTION_SIZE - cameraY;
            var minZ = sectionZ * SECTION_SIZE - cameraZ;

            poseStack.pushPose();
            poseStack.translate(minX, minY, minZ);

            LevelRenderer.renderLineBox(
                poseStack,
                bufferSource.getBuffer(RenderType.lines()),
                0,
                0,
                0,
                SECTION_SIZE,
                SECTION_SIZE,
                SECTION_SIZE,
                0.5f,
                0.5f,
                1.0f,
                alpha * 0.3f
            );

            poseStack.popPose();
        }
    }

    private static float[] getTerrainColor(int terrainTypeOrdinal) {
        var terrainTypes = TerrainType.values();

        if (terrainTypeOrdinal < 0 || terrainTypeOrdinal >= terrainTypes.length) {
            return new float[] { 0.5f, 0.5f, 0.5f };
        }

        return switch (terrainTypes[terrainTypeOrdinal]) {
            case GROUND -> new float[] { 0.0f, 1.0f, 0.0f };
            case WATER -> new float[] { 0.0f, 0.5f, 1.0f };
            case AIR -> new float[] { 0.8f, 0.8f, 1.0f };
            case BREAKABLE -> new float[] { 1.0f, 0.0f, 0.0f };
            case BURROWABLE -> new float[] { 0.6f, 0.3f, 0.0f };
        };
    }

    private static int unpackSectionX(long key) {
        var raw = (int) (key >> 38) & 0x3FFFFFF;

        if (raw >= 0x2000000) {
            raw -= 0x4000000;
        }

        return raw;
    }

    private static int unpackSectionY(long key) {
        var raw = (int) (key >> 26) & 0xFFF;

        if (raw >= 0x800) {
            raw -= 0x1000;
        }

        return raw;
    }

    private static int unpackSectionZ(long key) {
        var raw = (int) key & 0x3FFFFFF;

        if (raw >= 0x2000000) {
            raw -= 0x4000000;
        }

        return raw;
    }

    private record TimestampedSnapshot(
        S2CPathfindingSearchDebugPayload payload,
        long receivedTimeMillis
    ) {}

    private PathfindingSearchDebugRenderer() {}
}
