package com.avp.core.common.manager;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.AVPEntityTypeTags;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.hive.Hive;
import com.avp.core.common.level.saveddata.HiveLevelData;
import com.avp.core.common.util.CompoundTagUtil;

public class HiveManager {

    private static final String HIVE_SIGNATURE_KEY = "HiveSignature";

    private final Alien alien;

    private Hive hive;

    public HiveManager(Alien alien) {
        this.alien = alien;
    }

    public void tick() {
        var level = alien.level();

        if (level.isClientSide) {
            return;
        }

        if (hive == null && alien.tickCount % (20 * 10) == 0) {
            var hiveLevelDataOptional = HiveLevelData.getOrCreate(level);

            hiveLevelDataOptional.ifPresent(hiveLevelData -> {
                var nearestHiveOptional = hiveLevelData.findNearestHive(alien.blockPosition());

                if (nearestHiveOptional.isEmpty()) {
                    tryCreateAndAssignHive(hiveLevelData, null);
                    return;
                }

                var nearestHive = nearestHiveOptional.get();

                var joinedHiveSuccessfully = nearestHive.requestToJoin(alien);

                if (joinedHiveSuccessfully) {
                    hive = nearestHive;
                } else {
                    tryCreateAndAssignHive(hiveLevelData, nearestHive);
                }
            });
        }

        // TODO: hasHiveLeader should change once we have actual strength comparisons.
        if (hive != null) {

            if (!hive.isAlive()) {
                hive = null;
                return;
            }

            if (alien.tickCount % (20 * 30) == 0) {
                hive.ping(alien);
            }
        }
    }

    private void tryCreateAndAssignHive(HiveLevelData hiveLevelData, @Nullable Hive nearestHive) {
        if (!alien.getType().is(AVPEntityTypeTags.XENOMORPHS)) {
            return;
        }

        if (nearestHive != null) {
            var properties = AVP.HIVES_CONFIG.properties();
            var minimumDistance = properties.getOrThrow(ConfigProperties.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS);
            var minimumDistanceSquared = minimumDistance * minimumDistance;
            var hivePos = nearestHive.centerPosition();
            var distanceFromHiveCenterSquared = alien.distanceToSqr(hivePos.getX(), hivePos.getY(), hivePos.getZ());

            if (distanceFromHiveCenterSquared < minimumDistanceSquared) {
                // If the nearest hive is too close, don't create a new hive.
                return;
            }
        }

        var newHive = hiveLevelData.createHive();
        newHive.moveCenter(alien.blockPosition());
        newHive.ping(alien);
        hive = newHive;
    }

    public void load(CompoundTag compoundTag) {
        var hiveDataOptional = HiveLevelData.getOrCreate(alien.level());

        hiveDataOptional.ifPresent(hiveLevelData -> {
            if (!compoundTag.contains(HIVE_SIGNATURE_KEY)) {
                return;
            }

            var hiveSignature = CompoundTagUtil.getUUIDOrNull(compoundTag, HIVE_SIGNATURE_KEY);

            this.hive = hiveLevelData.hiveOrNull(hiveSignature);
        });
    }

    public void save(CompoundTag compoundTag) {
        if (hive != null) {
            compoundTag.putUUID(HIVE_SIGNATURE_KEY, hive.id());
        }
    }

    public @Nullable Hive hiveOrNull() {
        return hive;
    }

    public @Nullable UUID signatureOrNull() {
        return hive == null ? null : hive.id();
    }
}
