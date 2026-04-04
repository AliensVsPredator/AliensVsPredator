package com.blib.api.common.pathfinding.v1.node;

import net.minecraft.core.Direction;

import java.util.Objects;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A single position in pathfinding space with terrain classification, posture, and A* costs. Position, terrain type,
 * and posture index are fixed at creation. Surface direction is mutable — assigned by TPO post-processing after path
 * reconstruction. Costs and parent are mutable for A* use.
 */
public final class PathNode {

    private final int x;

    private final int y;

    private final int z;

    private final TerrainType terrainType;

    private final int postureIndex;

    private int surfaceDirection;

    private int availableSurfaces;

    private float gCost;

    private float hCost;

    private float costMalus;

    private PathNode parent;

    private boolean closed;

    public PathNode(int x, int y, int z, TerrainType terrainType, int postureIndex) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.terrainType = terrainType;
        this.postureIndex = postureIndex;
    }

    public float totalCost() {
        return gCost + hCost;
    }

    public float distanceTo(PathNode other) {
        var dx = (float) (other.x - x);
        var dy = (float) (other.y - y);
        var dz = (float) (other.z - z);

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public float distanceSquaredTo(PathNode other) {
        var dx = (float) (other.x - x);
        var dy = (float) (other.y - y);
        var dz = (float) (other.z - z);

        return dx * dx + dy * dy + dz * dz;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public int getPostureIndex() {
        return postureIndex;
    }

    public int getSurfaceDirection() {
        return surfaceDirection;
    }

    public void setSurfaceDirection(int surfaceDirection) {
        this.surfaceDirection = surfaceDirection;
    }

    public int getAvailableSurfaces() {
        return availableSurfaces;
    }

    public void setAvailableSurfaces(int availableSurfaces) {
        this.availableSurfaces = availableSurfaces;
    }

    public void addAvailableSurface(Direction direction) {
        this.availableSurfaces |= (1 << direction.ordinal());
    }

    public boolean hasAvailableSurface(Direction direction) {
        return (availableSurfaces & (1 << direction.ordinal())) != 0;
    }

    public float getGCost() {
        return gCost;
    }

    public void setGCost(float gCost) {
        this.gCost = gCost;
    }

    public float getHCost() {
        return hCost;
    }

    public void setHCost(float hCost) {
        this.hCost = hCost;
    }

    public float getCostMalus() {
        return costMalus;
    }

    public void setCostMalus(float costMalus) {
        this.costMalus = costMalus;
    }

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void reset() {
        this.gCost = 0;
        this.hCost = 0;
        this.costMalus = 0;
        this.surfaceDirection = 0;
        this.availableSurfaces = 0;
        this.parent = null;
        this.closed = false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof PathNode other)) {
            return false;
        }

        return x == other.x && y == other.y && z == other.z
            && postureIndex == other.postureIndex && terrainType == other.terrainType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, postureIndex, terrainType);
    }

    @Override
    public String toString() {
        return "PathNode[x=" + x + ", y=" + y + ", z=" + z + ", terrain=" + terrainType
            + ", posture=" + postureIndex + ", surface=" + surfaceDirection + "]";
    }
}
