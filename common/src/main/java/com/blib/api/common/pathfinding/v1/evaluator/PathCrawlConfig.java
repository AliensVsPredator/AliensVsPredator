package com.blib.api.common.pathfinding.v1.evaluator;

/**
 * Optional pathfinding posture configuration for entities that can lower their hitbox while crawling.
 */
public record PathCrawlConfig(
    boolean enabled,
    int crawlHeight,
    float crawlCostMalus,
    int postureLookaheadNodes
) {

    public static final PathCrawlConfig DISABLED = new PathCrawlConfig(false, 1, 0.0f, 0);

    public static PathCrawlConfig enabled(int crawlHeight) {
        return new PathCrawlConfig(true, crawlHeight, 1.2f, 1);
    }

    public PathCrawlConfig {
        crawlHeight = Math.max(1, crawlHeight);
        crawlCostMalus = Math.max(0.0f, crawlCostMalus);
        postureLookaheadNodes = Math.max(0, postureLookaheadNodes);
    }
}
