package com.blib.api.common.pathfinding.v1.evaluator;

/**
 * Defines an entity posture for pathfinding. Each posture has different dimensions,
 * allowing the pathfinder to plan paths that switch between postures when beneficial.
 *
 * @param name   identifier for this posture (e.g., "default", "crawling")
 * @param width  entity width in blocks for this posture
 * @param height entity height in blocks for this posture
 */
public record Posture(String name, int width, int height) {

    public static final Posture DEFAULT = new Posture("default", 1, 2);
}
