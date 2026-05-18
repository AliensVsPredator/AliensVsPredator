package com.blib.api.common.pathfinding.v1.debug;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;

/**
 * Collects lightweight path search diagnostics while debug capture is enabled.
 */
public final class PathSearchDebugRecorder {

    private static final int MAX_SAMPLES_PER_REASON = 6;

    private final EnumMap<PathRejectionReason, MutableSummary> rejections = new EnumMap<>(PathRejectionReason.class);

    private int featureUsageMask;

    public void markFeatureUsed(PathfindingFeature feature) {
        featureUsageMask |= feature.mask();
    }

    public void reject(PathRejectionReason reason, BlockPos pos) {
        reject(reason, pos.getX(), pos.getY(), pos.getZ());
    }

    public void reject(PathRejectionReason reason, int x, int y, int z) {
        var summary = rejections.computeIfAbsent(reason, ignored -> new MutableSummary());
        summary.count++;

        if (summary.samples.size() < MAX_SAMPLES_PER_REASON) {
            summary.samples.add(new PathDebugBlockPos(x, y, z));
        }
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

    public int featureUsageMask() {
        return featureUsageMask;
    }

    private static final class MutableSummary {

        int count;

        final List<PathDebugBlockPos> samples = new ArrayList<>(MAX_SAMPLES_PER_REASON);
    }
}
