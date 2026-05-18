package com.blib.mod.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathDebugBlockPos;
import com.blib.api.common.pathfinding.v1.debug.PathSearchOutcome;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

/**
 * Client-side debug renderer that visualizes A* search exploration. Renders explored nodes with terrain-type coloring,
 * path nodes connected by lines, and section corridor wireframes. Snapshots are replaced immediately on update — no
 * fade.
 */
public final class PathfindingSearchDebugRenderer {

    public static final PathfindingSearchDebugRenderer INSTANCE = new PathfindingSearchDebugRenderer();

    private static final float NODE_SIZE = 0.2f;

    private static final float NODE_HALF = NODE_SIZE / 2.0f;

    private static final float PATH_NODE_SIZE = 0.35f;

    private static final float PATH_NODE_HALF = PATH_NODE_SIZE / 2.0f;

    private static final int SECTION_SIZE = 16;

    private static final float[] PATH_PLANNED_COLOR = { 0.56f, 0.58f, 0.64f };

    private static final float[] PATH_COMPLETED_COLOR = { 0.35f, 0.78f, 0.48f };

    private static final float[] PATH_IN_PROGRESS_COLOR = { 0.90f, 0.76f, 0.42f };

    private static final float[] PATH_OUTPUT_COLOR = { 0.43f, 0.55f, 1.0f };

    private static final float[] PATH_LINE_COLOR = PATH_IN_PROGRESS_COLOR;

    private static final float[] BEST_NODE_COLOR = PATH_IN_PROGRESS_COLOR;

    private static final float[] REJECTION_SAMPLE_COLOR = { 0.88f, 0.55f, 0.36f };

    private final Map<Integer, S2CPathfindingSearchDebugPayload> snapshots = new ConcurrentHashMap<>();

    public void update(S2CPathfindingSearchDebugPayload payload) {
        if (payload.nodes().isEmpty() && payload.corridorKeys().isEmpty()) {
            snapshots.remove(payload.entityId());
        } else {
            snapshots.put(payload.entityId(), payload);
        }
    }

    /**
     * Removes the debug snapshot for the given entity, clearing its rendering immediately.
     */
    public void clear(int entityId) {
        snapshots.remove(entityId);
    }

    public void clearAll() {
        snapshots.clear();
    }

    public void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        for (var entry : snapshots.entrySet()) {
            renderSnapshot(poseStack, bufferSource, cameraX, cameraY, cameraZ, entry.getValue());
        }
    }

    private void renderSnapshot(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
    ) {
        renderCorridorSections(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload);
        renderRejectionSamples(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload);
        renderNodes(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload);
        renderPathLines(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload);
        renderBestNode(poseStack, bufferSource, cameraX, cameraY, cameraZ, payload);
    }

    private void renderNodes(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
    ) {
        for (var node : payload.nodes()) {
            if (node.onPath()) {
                renderPathNode(poseStack, bufferSource, cameraX, cameraY, cameraZ, node, payload.entityId());
            } else {
                renderExploredNode(poseStack, bufferSource, cameraX, cameraY, cameraZ, node);
            }
        }
    }

    private void renderPathLines(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
    ) {
        // Collect path nodes sorted by path index.
        var nodes = payload.nodes();
        var maxPathIndex = -1;

        for (var node : nodes) {
            if (node.onPath() && node.pathIndex() > maxPathIndex) {
                maxPathIndex = node.pathIndex();
            }
        }

        if (maxPathIndex < 1) {
            return;
        }

        // Build ordered array of path nodes by index.
        var pathNodes = new DebugNodeEntry[maxPathIndex + 1];

        for (var node : nodes) {
            if (node.onPath() && node.pathIndex() >= 0 && node.pathIndex() <= maxPathIndex) {
                pathNodes[node.pathIndex()] = node;
            }
        }

        // Draw lines between consecutive path nodes.
        var buffer = bufferSource.getBuffer(RenderType.lines());

        for (int i = 0; i < maxPathIndex; i++) {
            var from = pathNodes[i];
            var to = pathNodes[i + 1];

            if (from == null || to == null) {
                continue;
            }

            var fromX = (float) (from.x() + 0.5 - cameraX);
            var fromY = (float) (from.y() + 0.5 - cameraY);
            var fromZ = (float) (from.z() + 0.5 - cameraZ);
            var toX = (float) (to.x() + 0.5 - cameraX);
            var toY = (float) (to.y() + 0.5 - cameraY);
            var toZ = (float) (to.z() + 0.5 - cameraZ);

            var dx = toX - fromX;
            var dy = toY - fromY;
            var dz = toZ - fromZ;
            var length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (length < 0.001f) {
                continue;
            }

            // Normalized direction for the line's normal parameter.
            var nx = dx / length;
            var ny = dy / length;
            var nz = dz / length;

            var matrix = poseStack.last().pose();

            buffer
                .addVertex(matrix, fromX, fromY, fromZ)
                .setColor(PATH_LINE_COLOR[0], PATH_LINE_COLOR[1], PATH_LINE_COLOR[2], 1.0f)
                .setNormal(poseStack.last(), nx, ny, nz);

            buffer
                .addVertex(matrix, toX, toY, toZ)
                .setColor(PATH_LINE_COLOR[0], PATH_LINE_COLOR[1], PATH_LINE_COLOR[2], 1.0f)
                .setNormal(poseStack.last(), nx, ny, nz);
        }
    }

    private void renderExploredNode(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        DebugNodeEntry node
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
            0.6f
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
        int entityId
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
            1.0f
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
            0.6f
        );

        poseStack.popPose();
    }

    private static float[] getPathNodeColor(int pathIndex, int entityId) {
        var navPayload = PathfindingDebugState.INSTANCE.latestPayload();

        if (navPayload == null || navPayload.entityId() != entityId) {
            return PATH_PLANNED_COLOR;
        }

        var totalNodes = navPayload.totalNodes();
        var currentIndex = navPayload.currentNodeIndex();

        if (navPayload.reached() && pathIndex <= Math.max(currentIndex, totalNodes - 1)) {
            return PATH_COMPLETED_COLOR;
        }

        if (pathIndex == currentIndex) {
            return PATH_IN_PROGRESS_COLOR;
        }

        if (pathIndex < currentIndex) {
            return PATH_COMPLETED_COLOR;
        }

        if (totalNodes > 0 && pathIndex == totalNodes - 1) {
            return PATH_OUTPUT_COLOR;
        }

        return PATH_PLANNED_COLOR;
    }

    private void renderCorridorSections(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
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
                0.3f
            );

            poseStack.popPose();
        }
    }

    private void renderBestNode(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
    ) {
        var diagnostics = payload.diagnostics();
        if (diagnostics.outcome() == PathSearchOutcome.COMPLETE || !diagnostics.bestNode().present()) {
            return;
        }

        renderMarkerBox(poseStack, bufferSource, cameraX, cameraY, cameraZ, diagnostics.bestNode(), 0.55f, BEST_NODE_COLOR, 1.0f);
    }

    private void renderRejectionSamples(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        S2CPathfindingSearchDebugPayload payload
    ) {
        for (var rejection : payload.diagnostics().rejections()) {
            for (var sample : rejection.samples()) {
                renderMarkerBox(poseStack, bufferSource, cameraX, cameraY, cameraZ, sample, 0.28f, REJECTION_SAMPLE_COLOR, 0.75f);
            }
        }
    }

    private void renderMarkerBox(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        PathDebugBlockPos pos,
        float size,
        float[] color,
        float alpha
    ) {
        if (!pos.present()) {
            return;
        }

        var half = size / 2.0f;
        poseStack.pushPose();
        poseStack.translate(pos.x() + 0.5 - cameraX, pos.y() + 0.5 - cameraY, pos.z() + 0.5 - cameraZ);

        LevelRenderer.renderLineBox(
            poseStack,
            bufferSource.getBuffer(RenderType.lines()),
            -half,
            -half,
            -half,
            half,
            half,
            half,
            color[0],
            color[1],
            color[2],
            alpha
        );

        poseStack.popPose();
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

    private PathfindingSearchDebugRenderer() {}
}
