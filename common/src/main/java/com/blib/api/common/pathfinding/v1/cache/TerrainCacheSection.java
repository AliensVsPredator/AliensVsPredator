package com.blib.api.common.pathfinding.v1.cache;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Caches terrain classifications and region connectivity for a 16x16x16 block section. Lazily populated on first
 * access. Each position stores a TerrainType or null (impassable).
 * <p>
 * Uses union-find to identify connected regions of passable blocks in a single pass (merged with classification). For
 * each face, tracks which regions are present and at which Y-levels, enabling accurate boundary connectivity checks
 * between adjacent sections.
 * </p>
 */
final class TerrainCacheSection {

    private static final int SIZE = 16;

    private static final int VOLUME = SIZE * SIZE * SIZE;

    private static final byte UNCLASSIFIED = -1;

    private static final byte IMPASSABLE = -2;

    // Face indices.
    static final int FACE_WEST = 0;

    static final int FACE_EAST = 1;

    static final int FACE_NORTH = 2;

    static final int FACE_SOUTH = 3;

    static final int FACE_BOTTOM = 4;

    static final int FACE_TOP = 5;

    static final int FACE_COUNT = 6;

    private static final int NOT_IN_REGION = -1;

    private static final int MAX_FACE_REGIONS = 32;

    private final byte[] classifications;

    private final Set<TerrainType> containedTerrainTypes;

    // Union-find parent array. -1 = not passable, otherwise index of parent.
    private final int[] parent;

    private final byte[] rank;

    // Per-face: overall Y-level passability bitmask (fast early-exit check).
    private final short[] facePassableYLevels;

    // Per-face region data: parallel arrays of (root, yLevelBitmask) per face.
    private int[][] faceRegionRoots;

    private short[][] faceRegionYLevels;

    private boolean populated;

    private boolean hasPassableBlocks;

    TerrainCacheSection() {
        this.classifications = new byte[VOLUME];
        this.containedTerrainTypes = EnumSet.noneOf(TerrainType.class);
        this.parent = new int[VOLUME];
        this.rank = new byte[VOLUME];
        this.facePassableYLevels = new short[FACE_COUNT];
        clear();
    }

    void populate(LevelReader level, int sectionX, int sectionY, int sectionZ, TerrainClassifier classifier) {
        var baseX = sectionX << 4;
        var baseY = sectionY << 4;
        var baseZ = sectionZ << 4;
        var pos = new BlockPos.MutableBlockPos();

        containedTerrainTypes.clear();
        hasPassableBlocks = false;

        for (int i = 0; i < FACE_COUNT; i++) {
            facePassableYLevels[i] = 0;
        }

        // Single pass: classify + union-find + boundary tracking.
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    pos.set(baseX + x, baseY + y, baseZ + z);
                    var terrainType = classifier.classify(level, pos);
                    var index = packLocal(x, y, z);

                    if (terrainType != null) {
                        classifications[index] = (byte) terrainType.ordinal();
                        containedTerrainTypes.add(terrainType);
                        hasPassableBlocks = true;

                        // Initialize union-find node.
                        parent[index] = index;

                        // Union with already-classified backward neighbors.
                        if (x > 0) {
                            var neighbor = packLocal(x - 1, y, z);

                            if (parent[neighbor] >= 0) {
                                union(index, neighbor);
                            }
                        }

                        if (y > 0) {
                            var neighbor = packLocal(x, y - 1, z);

                            if (parent[neighbor] >= 0) {
                                union(index, neighbor);
                            }
                        }

                        if (z > 0) {
                            var neighbor = packLocal(x, y, z - 1);

                            if (parent[neighbor] >= 0) {
                                union(index, neighbor);
                            }
                        }

                        // Track overall face boundary passability.
                        var yBit = (short) (1 << y);

                        if (x == 0) {
                            facePassableYLevels[FACE_WEST] |= yBit;
                        }

                        if (x == SIZE - 1) {
                            facePassableYLevels[FACE_EAST] |= yBit;
                        }

                        if (z == 0) {
                            facePassableYLevels[FACE_NORTH] |= yBit;
                        }

                        if (z == SIZE - 1) {
                            facePassableYLevels[FACE_SOUTH] |= yBit;
                        }

                        if (y == 0) {
                            facePassableYLevels[FACE_BOTTOM] |= 1;
                        }

                        if (y == SIZE - 1) {
                            facePassableYLevels[FACE_TOP] |= 1;
                        }
                    } else {
                        classifications[index] = IMPASSABLE;
                    }
                }
            }
        }

        computeFaceRegions();
        this.populated = true;
    }

    // --- Union-find operations ---

    private int find(int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]]; // path splitting
            i = parent[i];
        }

        return i;
    }

    private void union(int a, int b) {
        var rootA = find(a);
        var rootB = find(b);

        if (rootA == rootB) {
            return;
        }

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
    }

    // --- Face region computation ---

    private void computeFaceRegions() {
        faceRegionRoots = new int[FACE_COUNT][];
        faceRegionYLevels = new short[FACE_COUNT][];

        var tempRoots = new int[MAX_FACE_REGIONS];
        var tempYLevels = new short[MAX_FACE_REGIONS];

        for (int face = 0; face < FACE_COUNT; face++) {
            var count = collectFaceRegions(face, tempRoots, tempYLevels);
            faceRegionRoots[face] = Arrays.copyOf(tempRoots, count);
            faceRegionYLevels[face] = Arrays.copyOf(tempYLevels, count);
        }
    }

    private int collectFaceRegions(int face, int[] roots, short[] yLevels) {
        var count = 0;

        for (int a = 0; a < SIZE; a++) {
            for (int b = 0; b < SIZE; b++) {
                int x, y, z;

                switch (face) {
                    case FACE_WEST -> { x = 0; y = a; z = b; }
                    case FACE_EAST -> { x = SIZE - 1; y = a; z = b; }
                    case FACE_NORTH -> { x = a; y = b; z = 0; }
                    case FACE_SOUTH -> { x = a; y = b; z = SIZE - 1; }
                    case FACE_BOTTOM -> { x = a; y = 0; z = b; }
                    case FACE_TOP -> { x = a; y = SIZE - 1; z = b; }
                    default -> { continue; }
                }

                var index = packLocal(x, y, z);

                if (parent[index] < 0) {
                    continue;
                }

                var root = find(index);
                var yBit = (short) (1 << y);

                count = addOrMergeRoot(roots, yLevels, count, root, yBit);
            }
        }

        return count;
    }

    private static int addOrMergeRoot(int[] roots, short[] yLevels, int count, int root, short yBit) {
        for (int i = 0; i < count; i++) {
            if (roots[i] == root) {
                yLevels[i] |= yBit;
                return count;
            }
        }

        if (count < roots.length) {
            roots[count] = root;
            yLevels[count] = yBit;
            return count + 1;
        }

        return count;
    }

    // --- Accessors ---

    @Nullable
    TerrainType get(int localX, int localY, int localZ) {
        var value = classifications[packLocal(localX, localY, localZ)];

        if (value == UNCLASSIFIED || value == IMPASSABLE) {
            return null;
        }

        return TerrainType.values()[value];
    }

    /**
     * Returns the region root for the given local position, or -1 if not passable.
     */
    int getRegionRoot(int localX, int localY, int localZ) {
        var index = packLocal(localX, localY, localZ);

        if (parent[index] < 0) {
            return NOT_IN_REGION;
        }

        return find(index);
    }

    boolean isPopulated() {
        return populated;
    }

    boolean hasPassableBlocks() {
        return hasPassableBlocks;
    }

    Set<TerrainType> getContainedTerrainTypes() {
        return containedTerrainTypes;
    }

    /**
     * Returns the overall Y-level passability bitmask for the given face (fast early-exit check).
     */
    short getFacePassableYLevels(int face) {
        return facePassableYLevels[face];
    }

    /**
     * Returns the number of distinct regions present on the given face.
     */
    int getFaceRegionCount(int face) {
        return faceRegionRoots[face].length;
    }

    /**
     * Returns the region root of the i-th region on the given face.
     */
    int getFaceRegionRoot(int face, int index) {
        return faceRegionRoots[face][index];
    }

    /**
     * Returns the Y-level bitmask of the i-th region on the given face.
     */
    short getFaceRegionYLevels(int face, int index) {
        return faceRegionYLevels[face][index];
    }

    void clear() {
        for (int i = 0; i < VOLUME; i++) {
            classifications[i] = UNCLASSIFIED;
        }

        Arrays.fill(parent, NOT_IN_REGION);
        Arrays.fill(rank, (byte) 0);

        containedTerrainTypes.clear();
        this.hasPassableBlocks = false;
        this.populated = false;

        for (int i = 0; i < FACE_COUNT; i++) {
            facePassableYLevels[i] = 0;
        }

        this.faceRegionRoots = null;
        this.faceRegionYLevels = null;
    }

    private static int packLocal(int x, int y, int z) {
        return (y << 8) | (z << 4) | x;
    }
}
