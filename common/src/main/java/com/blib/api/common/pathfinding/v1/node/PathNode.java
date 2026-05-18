package com.blib.api.common.pathfinding.v1.node;

import java.util.Objects;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A single position in pathfinding space with terrain classification and A* costs. Position and terrain type are fixed
 * at creation. Costs and parent are mutable for A* use.
 */
public final class PathNode implements Comparable<PathNode> {

    private final int x;

    private final int y;

    private final int z;

    private final TerrainType terrainType;

    private float gCost;

    private float hCost;

    private float costMalus;

    private float pendingCostMalus;

    private PathNode parent;

    private boolean closed;

    private boolean hasStableGround;

    private int stableGroundX;

    private int stableGroundY;

    private int stableGroundZ;

    private int stableGroundXSize;

    private int stableGroundZSize;

    public PathNode(int x, int y, int z, TerrainType terrainType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.terrainType = terrainType;
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

    public float getPendingCostMalus() {
        return pendingCostMalus;
    }

    public void setPendingTraversal(float costMalus) {
        this.pendingCostMalus = costMalus;
    }

    public void commitPendingTraversal() {
        this.costMalus = pendingCostMalus;
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

    public boolean hasStableGround() {
        return hasStableGround;
    }

    public int getStableGroundX() {
        return stableGroundX;
    }

    public int getStableGroundY() {
        return stableGroundY;
    }

    public int getStableGroundZ() {
        return stableGroundZ;
    }

    public int getStableGroundXSize() {
        return stableGroundXSize;
    }

    public int getStableGroundZSize() {
        return stableGroundZSize;
    }

    public void setStableGround(int x, int y, int z) {
        setStableGround(x, y, z, 1, 1);
    }

    public void setStableGround(int x, int y, int z, int xSize, int zSize) {
        this.hasStableGround = true;
        this.stableGroundX = x;
        this.stableGroundY = y;
        this.stableGroundZ = z;
        this.stableGroundXSize = Math.max(1, xSize);
        this.stableGroundZSize = Math.max(1, zSize);
    }

    public void copyStableGroundFrom(PathNode node) {
        if (!node.hasStableGround()) {
            this.hasStableGround = false;
            this.stableGroundXSize = 0;
            this.stableGroundZSize = 0;
            return;
        }

        setStableGround(
            node.getStableGroundX(),
            node.getStableGroundY(),
            node.getStableGroundZ(),
            node.getStableGroundXSize(),
            node.getStableGroundZSize()
        );
    }

    @Override
    public int compareTo(PathNode other) {
        return Float.compare(totalCost(), other.totalCost());
    }

    public void reset() {
        this.gCost = 0;
        this.hCost = 0;
        this.costMalus = 0;
        this.pendingCostMalus = 0;
        this.parent = null;
        this.closed = false;
        this.hasStableGround = false;
        this.stableGroundXSize = 0;
        this.stableGroundZSize = 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof PathNode other)) {
            return false;
        }

        return x == other.x && y == other.y && z == other.z && terrainType == other.terrainType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, terrainType);
    }

    @Override
    public String toString() {
        return "PathNode[x=" + x + ", y=" + y + ", z=" + z + ", terrain=" + terrainType + "]";
    }
}
