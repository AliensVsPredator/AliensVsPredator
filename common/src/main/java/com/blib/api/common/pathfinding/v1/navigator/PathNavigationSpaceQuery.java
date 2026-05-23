package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Shared world-space collision, support, and terrain queries used by path navigation components.
 */
final class PathNavigationSpaceQuery {

    private static final double COLLISION_EPSILON = 1.0E-7;

    private final LevelReader level;

    private final PathNavigatorConfig config;

    private final Supplier<@Nullable Set<TerrainType>> excludedTerrainsSupplier;

    PathNavigationSpaceQuery(
        LevelReader level,
        PathNavigatorConfig config,
        Supplier<@Nullable Set<TerrainType>> excludedTerrainsSupplier
    ) {
        this.level = level;
        this.config = config;
        this.excludedTerrainsSupplier = excludedTerrainsSupplier;
    }

    boolean isTerrainAllowed(TerrainType terrainType) {
        var excludedTerrains = excludedTerrainsSupplier.get();

        return config.getEvaluatorConfig().getSupportedTerrains().contains(terrainType)
            && (excludedTerrains == null || !excludedTerrains.contains(terrainType));
    }

    boolean isEntityBoxClear(Vec3 feetCenter, float entityWidth, float entityHeight) {
        return isEntityBoxClear(feetCenter, entityWidth, entityHeight, false);
    }

    boolean isEntityBoxClear(Vec3 feetCenter, float entityWidth, float entityHeight, boolean allowLiquids) {
        var halfWidth = entityWidth / 2.0d;
        var entityBox = new AABB(
            feetCenter.x - halfWidth,
            feetCenter.y,
            feetCenter.z - halfWidth,
            feetCenter.x + halfWidth,
            feetCenter.y + entityHeight,
            feetCenter.z + halfWidth
        ).deflate(COLLISION_EPSILON, 0.0, COLLISION_EPSILON);

        var minX = (int) Math.floor(entityBox.minX);
        var minY = (int) Math.floor(entityBox.minY);
        var minZ = (int) Math.floor(entityBox.minZ);
        var maxX = (int) Math.floor(entityBox.maxX - COLLISION_EPSILON);
        var maxY = (int) Math.floor(entityBox.maxY - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(entityBox.maxZ - COLLISION_EPSILON);
        var cursor = new BlockPos.MutableBlockPos();

        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                for (var z = minZ; z <= maxZ; z++) {
                    cursor.set(x, y, z);

                    var state = level.getBlockState(cursor);

                    if (state.liquid() && (!allowLiquids || !state.getFluidState().is(FluidTags.WATER))) {
                        return false;
                    }

                    var shape = state.getCollisionShape(level, cursor, CollisionContext.empty());

                    if (shape.isEmpty()) {
                        continue;
                    }

                    for (var blockBox : shape.toAabbs()) {
                        if (blockBox.move(cursor).intersects(entityBox)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    boolean hasEntitySupport(Vec3 feetCenter, float entityWidth) {
        var halfWidth = entityWidth / 2.0d;
        var minX = (int) Math.floor(feetCenter.x - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(feetCenter.z - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(feetCenter.x + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(feetCenter.z + halfWidth - COLLISION_EPSILON);

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                if (!hasSupportAt(x, feetCenter.y, z)) {
                    return false;
                }
            }
        }

        return true;
    }

    boolean hasExpectedTerrain(Vec3 feetCenter, float entityWidth, TerrainType expectedTerrain) {
        var halfWidth = entityWidth / 2.0d;
        var minX = (int) Math.floor(feetCenter.x - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(feetCenter.z - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(feetCenter.x + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(feetCenter.z + halfWidth - COLLISION_EPSILON);
        var y = (int) Math.floor(feetCenter.y);
        var cursor = new BlockPos.MutableBlockPos();
        var terrainClassifier = config.getEvaluatorConfig().getTerrainClassifier();

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                cursor.set(x, y, z);

                var terrain = terrainClassifier.classify(level, cursor);

                if (terrain != expectedTerrain || !isTerrainAllowed(terrain)) {
                    return false;
                }
            }
        }

        return true;
    }

    boolean hasSupportAt(int x, double feetY, int z) {
        var supportY = (int) Math.floor(feetY) - 1;
        var cursor = new BlockPos.MutableBlockPos(x, supportY, z);
        var state = level.getBlockState(cursor);

        if (state.liquid()) {
            return false;
        }

        var shape = state.getCollisionShape(level, cursor, CollisionContext.empty());

        if (shape.isEmpty()) {
            return false;
        }

        var supportProbe = new AABB(
            x + COLLISION_EPSILON,
            feetY - COLLISION_EPSILON,
            z + COLLISION_EPSILON,
            x + 1.0d - COLLISION_EPSILON,
            feetY + COLLISION_EPSILON,
            z + 1.0d - COLLISION_EPSILON
        );

        for (var blockBox : shape.toAabbs()) {
            if (blockBox.move(cursor).intersects(supportProbe)) {
                return true;
            }
        }

        return false;
    }
}
