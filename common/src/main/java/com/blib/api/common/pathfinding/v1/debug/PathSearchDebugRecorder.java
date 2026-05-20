package com.blib.api.common.pathfinding.v1.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.node.PathNode;

/**
 * Collects lightweight path search diagnostics while debug capture is enabled.
 */
public final class PathSearchDebugRecorder {

    private static final int MAX_SAMPLES_PER_REASON = 6;

    private static final int MAX_OPEN_NODES = 512;

    private static final int MAX_EDGE_ATTEMPTS = 1024;

    private static final int MAX_CLEARANCE_BOXES = 256;

    private static final int MAX_SUPPORT_CELLS = 512;

    private static final int MAX_BLOCKING_BLOCKS = 256;

    private final EnumMap<PathRejectionReason, MutableSummary> rejections = new EnumMap<>(PathRejectionReason.class);

    private final LinkedHashMap<String, PathOpenNodeDebugEntry> openNodes = new LinkedHashMap<>();

    private final Set<String> closedNodes = new HashSet<>();

    private final List<PathEdgeDebugEntry> edgeAttempts = new ArrayList<>();

    private final List<PathAabbDebugEntry> clearanceBoxes = new ArrayList<>();

    private final List<PathSupportDebugEntry> supportFootprint = new ArrayList<>();

    private final List<PathBlockDebugEntry> blockingBlocks = new ArrayList<>();

    private EdgeAttempt activeEdgeAttempt;

    private long featureUsageMask;

    public void markFeatureUsed(PathfindingFeature feature) {
        featureUsageMask |= feature.mask();
    }

    public void reject(PathRejectionReason reason, BlockPos pos) {
        reject(reason, pos.getX(), pos.getY(), pos.getZ());
    }

    public void reject(PathRejectionReason reason, int x, int y, int z) {
        var summary = rejections.computeIfAbsent(reason, ignored -> new MutableSummary());
        summary.count++;
        recordActiveEdgeRejection(reason);

        if (summary.samples.size() < MAX_SAMPLES_PER_REASON) {
            summary.samples.add(new PathDebugBlockPos(x, y, z));
        }
    }

    public void beginEdgeAttempt(PathNode from, int toX, int toY, int toZ, PathEdgeDebugType type) {
        beginEdgeAttempt(from, toX, toY, toZ, type, false);
    }

    public void beginEdgeAttempt(
        PathNode from,
        int toX,
        int toY,
        int toZ,
        PathEdgeDebugType type,
        boolean backward
    ) {
        activeEdgeAttempt = new EdgeAttempt(
            new PathDebugBlockPos(from.getX(), from.getY(), from.getZ()),
            new PathDebugBlockPos(toX, toY, toZ),
            type,
            backward
        );
    }

    public void finishEdgeAttempt(boolean accepted) {
        if (activeEdgeAttempt == null) {
            return;
        }

        addEdgeAttempt(
            new PathEdgeDebugEntry(
                activeEdgeAttempt.from,
                activeEdgeAttempt.to,
                activeEdgeAttempt.type,
                accepted,
                accepted ? PathEdgeDebugEntry.NO_REJECTION : activeEdgeAttempt.rejectionReason,
                activeEdgeAttempt.backward
            )
        );
        if (!accepted) {
            addAllLimited(clearanceBoxes, activeEdgeAttempt.clearanceBoxes, MAX_CLEARANCE_BOXES);
            addAllLimited(supportFootprint, activeEdgeAttempt.supportFootprint, MAX_SUPPORT_CELLS);
            addAllLimited(blockingBlocks, activeEdgeAttempt.blockingBlocks, MAX_BLOCKING_BLOCKS);
        }
        activeEdgeAttempt = null;
    }

    public void recordAcceptedEdge(PathNode from, PathNode to, PathEdgeDebugType type, boolean backward) {
        addEdgeAttempt(new PathEdgeDebugEntry(
            pos(from),
            pos(to),
            type,
            true,
            PathEdgeDebugEntry.NO_REJECTION,
            backward
        ));
    }

    public void recordRejectedEdge(
        PathNode from,
        PathNode to,
        PathEdgeDebugType type,
        PathRejectionReason reason,
        boolean backward
    ) {
        addEdgeAttempt(new PathEdgeDebugEntry(
            pos(from),
            pos(to),
            type,
            false,
            reason.ordinal(),
            backward
        ));
    }

    public void recordOpenNode(PathNode node, PathNode parent, float gCost, float hCost, boolean backward) {
        var nodePos = pos(node);
        var key = openKey(nodePos, backward);

        if (closedNodes.contains(key)) {
            return;
        }

        if (!openNodes.containsKey(key) && openNodes.size() >= MAX_OPEN_NODES) {
            return;
        }

        openNodes.put(
            key,
            new PathOpenNodeDebugEntry(
                nodePos,
                pos(parent),
                node.getTerrainType().ordinal(),
                gCost,
                hCost,
                backward
            )
        );
    }

    public void recordClosedNode(PathNode node, boolean backward) {
        var key = openKey(pos(node), backward);
        closedNodes.add(key);
        openNodes.remove(key);
    }

    public void recordClearanceBox(AABB box, PathRejectionReason reason) {
        if (activeEdgeAttempt == null) {
            return;
        }

        activeEdgeAttempt.clearanceBoxes.add(new PathAabbDebugEntry(
            activeEdgeAttempt.to,
            box.minX,
            box.minY,
            box.minZ,
            box.maxX,
            box.maxY,
            box.maxZ,
            activeEdgeAttempt.type,
            reason.ordinal()
        ));
    }

    public void recordSupportCell(int x, int y, int z, boolean supported) {
        if (activeEdgeAttempt == null) {
            return;
        }

        activeEdgeAttempt.supportFootprint.add(new PathSupportDebugEntry(
            activeEdgeAttempt.to,
            new PathDebugBlockPos(x, y, z),
            supported,
            activeEdgeAttempt.type
        ));
    }

    public void recordBlockingBlock(int x, int y, int z, PathRejectionReason reason) {
        if (activeEdgeAttempt == null) {
            return;
        }

        activeEdgeAttempt.blockingBlocks.add(new PathBlockDebugEntry(
            activeEdgeAttempt.to,
            new PathDebugBlockPos(x, y, z),
            activeEdgeAttempt.type,
            reason.ordinal()
        ));
    }

    public List<PathRejectionDebugData> rejectionSummary() {
        if (rejections.isEmpty()) {
            return List.of();
        }

        var out = new ArrayList<PathRejectionDebugData>(rejections.size());
        for (var entry : rejections.entrySet()) {
            out.add(new PathRejectionDebugData(entry.getKey(), entry.getValue().count, entry.getValue().samples));
        }
        return out;
    }

    public List<PathOpenNodeDebugEntry> openNodeEntries() {
        return List.copyOf(openNodes.values());
    }

    public List<PathEdgeDebugEntry> edgeAttemptEntries() {
        return List.copyOf(edgeAttempts);
    }

    public List<PathAabbDebugEntry> clearanceBoxEntries() {
        return List.copyOf(clearanceBoxes);
    }

    public List<PathSupportDebugEntry> supportFootprintEntries() {
        return List.copyOf(supportFootprint);
    }

    public List<PathBlockDebugEntry> blockingBlockEntries() {
        return List.copyOf(blockingBlocks);
    }

    public long featureUsageMask() {
        return featureUsageMask;
    }

    private void recordActiveEdgeRejection(PathRejectionReason reason) {
        if (
            activeEdgeAttempt != null
                && activeEdgeAttempt.rejectionReason == PathEdgeDebugEntry.NO_REJECTION
        ) {
            activeEdgeAttempt.rejectionReason = reason.ordinal();
        }
    }

    private void addEdgeAttempt(PathEdgeDebugEntry entry) {
        if (edgeAttempts.size() < MAX_EDGE_ATTEMPTS) {
            edgeAttempts.add(entry);
        }
    }

    private static PathDebugBlockPos pos(PathNode node) {
        return new PathDebugBlockPos(node.getX(), node.getY(), node.getZ());
    }

    private static String openKey(PathDebugBlockPos pos, boolean backward) {
        return pos.x() + "," + pos.y() + "," + pos.z() + "," + backward;
    }

    private static <T> void addAllLimited(List<T> target, List<T> source, int limit) {
        for (var value : source) {
            if (target.size() >= limit) {
                return;
            }
            target.add(value);
        }
    }

    private static final class MutableSummary {

        int count;

        final List<PathDebugBlockPos> samples = new ArrayList<>(MAX_SAMPLES_PER_REASON);
    }

    private static final class EdgeAttempt {

        final PathDebugBlockPos from;

        final PathDebugBlockPos to;

        final PathEdgeDebugType type;

        final boolean backward;

        final List<PathAabbDebugEntry> clearanceBoxes = new ArrayList<>();

        final List<PathSupportDebugEntry> supportFootprint = new ArrayList<>();

        final List<PathBlockDebugEntry> blockingBlocks = new ArrayList<>();

        int rejectionReason = PathEdgeDebugEntry.NO_REJECTION;

        EdgeAttempt(PathDebugBlockPos from, PathDebugBlockPos to, PathEdgeDebugType type, boolean backward) {
            this.from = from;
            this.to = to;
            this.type = type;
            this.backward = backward;
        }
    }
}
