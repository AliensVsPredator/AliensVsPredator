package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.common.pathfinding.v1.path.BLibPath;

/**
 * Long-distance pathfinding planner. Computes a section-level corridor once, then repeatedly pathfinds toward the final
 * target with the corridor as a constraint. Each A* search naturally produces a partial path when the budget is
 * exhausted; the entity follows it, then the next segment continues from the entity's new position.
 * <p>
 * On flat terrain this produces straight-line paths since the A* always aims at the real destination. The corridor
 * prevents wasteful exploration in the wrong direction without forcing detours through section centers.
 * </p>
 */
public final class SegmentedPathPlanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentedPathPlanner.class);

    private static final int SHORT_DISTANCE_THRESHOLD = 48;

    private final BLibPathFinder pathFinder;

    // --- Route state ---
    private @Nullable CorridorResult activeRoute;

    private @Nullable BlockPos finalTarget;

    public SegmentedPathPlanner(BLibPathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    /**
     * Plans a route from start to target. Computes the corridor once, then runs block-level A* toward the actual target
     * (not an intermediate waypoint). If no corridor finder is available, falls back to direct pathfinding.
     *
     * @return a path (possibly partial if distance exceeds A* budget), or null if unreachable
     */
    public @Nullable BLibPath findPath(LevelReader level, BlockPos start, BlockPos target) {
        clear();
        this.finalTarget = target;

        LOGGER.info("[Planner] findPath: {} -> {} (dist={})", start, target, start.distManhattan(target));

        var result = pathFinder.computeCorridor(level, start, target);

        if (result == null) {
            LOGGER.info("[Planner] computeCorridor returned null — falling back to direct pathfinding");
            return pathFinder.findPath(level, start, target);
        }

        LOGGER.info(
            "[Planner] corridor found: {} sections in corridor, {} waypoints",
            result.corridor().size(),
            result.sectionWaypoints().size()
        );

        this.activeRoute = result;

        return computeSegment(level, start);
    }

    /**
     * Computes the next segment from the entity's current position toward the final target, constrained by the stored
     * corridor. Call this when the previous segment's path has been fully traversed but didn't reach the goal.
     *
     * @return the next path segment, or null if computation failed
     */
    public @Nullable BLibPath computeNextSegment(LevelReader level, BlockPos entityPos) {
        if (activeRoute == null || finalTarget == null) {
            LOGGER.info("[Planner] computeNextSegment: no active route");
            return null;
        }

        LOGGER.info("[Planner] computeNextSegment from {} toward {}", entityPos, finalTarget);
        return computeSegment(level, entityPos);
    }

    /**
     * Returns true if a corridor route is active. The navigator should check whether the current path reached the goal
     * to decide if more segments are needed.
     */
    public boolean hasActiveRoute() {
        return activeRoute != null;
    }

    /**
     * Returns the final target position of the current route, or null if no route is active.
     */
    public @Nullable BlockPos getFinalTarget() {
        return finalTarget;
    }

    /**
     * Clears all route state.
     */
    public void clear() {
        activeRoute = null;
        finalTarget = null;
    }

    private @Nullable BLibPath computeSegment(LevelReader level, BlockPos from) {
        var dist = from.distManhattan(finalTarget);
        var useCorridor = dist > SHORT_DISTANCE_THRESHOLD;
        var corridor = useCorridor ? activeRoute.corridor() : null;

        LOGGER.info("[Planner] computeSegment from {} (dist={}, useCorridor={})", from, dist, useCorridor);

        var path = pathFinder.findPathInCorridor(level, from, finalTarget, corridor);

        LOGGER.info(
            "[Planner] computeSegment result: path={}, reached={}, nodes={}",
            path != null ? "found" : "null",
            path != null ? path.isReached() : "n/a",
            path != null ? path.getNodeCount() : 0
        );

        return path;
    }
}
