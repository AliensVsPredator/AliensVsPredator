package com.avp.common.hive.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.AVP;
import com.avp.common.hive.Hive;
import com.avp.common.util.spatial.Vec3Like;
import com.avp.common.util.spatial.block.BlockPosVec3;
import com.avp.common.util.spatial.sphere.layer.LayeredSphere;
import com.avp.common.util.spatial.sphere.layer.SphereLayer;
import com.avp.common.util.spatial.sphere.layer.SphereLayerDistanceTarget;
import com.avp.common.util.spatial.sphere.layer.impl.PercentileSphereLayer;
import com.avp.common.util.spatial.sphere.layer.impl.SupplyingPercentileSphereLayer;

public class HiveSpaceManager implements LayeredSphere {

    // Resin layers

    // The layer at which the Queen resides.
    private static final SphereLayer CENTER_LAYER = new PercentileSphereLayer(0, 0.15F);

    // The layer at which Praetorians naturally spawn in and reside.
    private static final SphereLayer PRAETORIAN_LAYER = new PercentileSphereLayer(0.15F, 0.4F);

    // The layer at which Drones naturally spawn in and reside. Ovomorphs are also in this layer.
    private static final SphereLayer DRONE_LAYER = new PercentileSphereLayer(0.4F, 0.7F);

    // The layer at which Warriors naturally spawn in and reside.
    private static final SphereLayer WARRIOR_LAYER = new PercentileSphereLayer(0.7F, 0.95F);

    // The layer at where nothing really spawns or resides in.
    private static final SphereLayer EDGE_LAYER = new PercentileSphereLayer(0.95F, 1F);

    // Beyond resin layers

    private static final Supplier<Float> MAX_NORMALIZED_LEASH_RANGE_SUPPLIER = () -> {
        var hiveRadiusBlocks = AVP.config.hiveConfigs.HIVE_RADIUS_IN_BLOCKS;
        var hiveLeashRadiusInBlocks = AVP.config.hiveConfigs.HIVE_LEASH_RADIUS_IN_BLOCKS;

        return 1F + (hiveLeashRadiusInBlocks / hiveRadiusBlocks);
    };

    private static final Supplier<Float> MAX_NORMALIZED_BUFFER_SUPPLIER = () -> MAX_NORMALIZED_LEASH_RANGE_SUPPLIER.get() * 2;

    private static final SphereLayer LEASH_LAYER = new SupplyingPercentileSphereLayer(() -> 1F, MAX_NORMALIZED_LEASH_RANGE_SUPPLIER);

    private static final SphereLayer BUFFER_LAYER = new SupplyingPercentileSphereLayer(
        MAX_NORMALIZED_LEASH_RANGE_SUPPLIER,
        MAX_NORMALIZED_BUFFER_SUPPLIER
    );

    public static final List<SphereLayer> HIVE_LAYERS = List.of(
        // Resin layers
        CENTER_LAYER,
        PRAETORIAN_LAYER,
        DRONE_LAYER,
        WARRIOR_LAYER,
        EDGE_LAYER,
        // Beyond resin layers
        LEASH_LAYER,
        BUFFER_LAYER
    );

    private final Hive hive;

    public HiveSpaceManager(Hive hive) {
        this.hive = hive;
    }

    // Query util functions

    public boolean isEntityWithinHive(Entity entity) {
        return isBlockPosWithinHive(entity.blockPosition());
    }

    public boolean isBlockPosWithinHive(BlockPos blockPos) {
        return isWithinLayerOrBelow(EDGE_LAYER, new BlockPosVec3(blockPos));
    }

    public boolean isEntityLeashedToHive(Entity entity) {
        return isBlockPosLeashedToHive(entity.blockPosition());
    }

    public boolean isBlockPosLeashedToHive(BlockPos blockPos) {
        return isWithinLayerOrBelow(LEASH_LAYER, new BlockPosVec3(blockPos));
    }

    public boolean isEntityWithinHiveBuffer(Entity entity) {
        return isBlockPosWithinHiveBuffer(entity.blockPosition());
    }

    public boolean isBlockPosWithinHiveBuffer(BlockPos blockPos) {
        return isWithinLayerOrBelow(BUFFER_LAYER, new BlockPosVec3(blockPos));
    }

    // Distance functions

    public double distanceToCenterSqr(Entity entity) {
        return distanceToCenterSqr(entity.blockPosition());
    }

    public double distanceToCenterSqr(BlockPos blockPos) {
        return distanceToSqr(HiveLayer.CENTER, blockPos, SphereLayerDistanceTarget.INNER_BOUNDARY);
    }

    public double distanceToOuterBoundarySqr(HiveLayer hiveLayer, Entity entity) {
        return distanceToOuterBoundarySqr(hiveLayer, entity.blockPosition());
    }

    public double distanceToOuterBoundarySqr(HiveLayer hiveLayer, BlockPos blockPos) {
        return distanceToSqr(hiveLayer, blockPos, SphereLayerDistanceTarget.OUTER_BOUNDARY);
    }

    public double distanceToSqr(HiveLayer hiveLayer, Entity entity, SphereLayerDistanceTarget sphereLayerDistanceTarget) {
        return distanceToSqr(hiveLayer, entity.blockPosition(), sphereLayerDistanceTarget);
    }

    public double distanceToSqr(HiveLayer hiveLayer, BlockPos blockPos, SphereLayerDistanceTarget sphereLayerDistanceTarget) {
        return hiveLayer.sphereLayer.distanceSquaredTo(this, new BlockPosVec3(blockPos), sphereLayerDistanceTarget);
    }

    // Util functions
    public @Nullable SphereLayer getLayerOrNull(BlockPos blockPos) {
        return LayeredSphere.super.getLayerOrNull(new BlockPosVec3(blockPos));
    }

    public @Nullable HiveLayer getHiveLayerOrNull(BlockPos blockPos) {
        var sphereLayer = getLayerOrNull(blockPos);

        return sphereLayer == null
            ? null
            : HiveLayer.HIVE_LAYER_BY_SPHERE_LAYER_MAP.get(sphereLayer);
    }

    @Override
    public Vec3Like getCenter() {
        return new BlockPosVec3(hive.centerPosition());
    }

    @Override
    public float getRadius() {
        return AVP.config.hiveConfigs.HIVE_RADIUS_IN_BLOCKS;
    }

    @Override
    public List<SphereLayer> getAllLayers() {
        return HIVE_LAYERS;
    }

    public enum HiveLayer {

        // Resin layers
        CENTER(CENTER_LAYER),
        PRAETORIAN(PRAETORIAN_LAYER),
        DRONE(DRONE_LAYER),
        WARRIOR(WARRIOR_LAYER),
        EDGE(EDGE_LAYER),
        // Beyond resin layers
        LEASH(LEASH_LAYER),
        BUFFER(BUFFER_LAYER);

        private static final Map<SphereLayer, HiveLayer> HIVE_LAYER_BY_SPHERE_LAYER_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(hiveLayer -> hiveLayer.sphereLayer, Function.identity()));

        private final SphereLayer sphereLayer;

        HiveLayer(SphereLayer sphereLayer) {
            this.sphereLayer = sphereLayer;
        }

        public SphereLayer getSphereLayer() {
            return sphereLayer;
        }
    }
}
