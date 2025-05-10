package com.avp.common.entity.living.alien.manager;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.avp.AVP;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.hive.Hive;
import com.avp.common.level.saveddata.HiveLevelData;
import com.avp.common.util.CompoundTagUtil;

public class HiveManager {

    private static final String HIVE_SIGNATURE_KEY = "HiveSignature";

    private final Alien alien;

    private Option<Hive> hiveOption;

    public HiveManager(Alien alien) {
        this.alien = alien;
        this.hiveOption = Option.none();
    }

    public void tick() {
        var level = alien.level();

        if (level.isClientSide) {
            return;
        }

        if (hiveOption.isNone() && alien.tickCount % (20 * 10) == 0) {
            HiveLevelData.getOrCreate(level)
                .map(hiveLevelData -> new Tuple<>(hiveLevelData, hiveLevelData.findNearestHive(alien.blockPosition())))
                .ifSome(tuple -> {
                    var hiveLevelData = tuple.getA();

                    tuple.getB()
                        .inspect(nearestHive -> {
                            var joinedHiveSuccessfully = nearestHive.requestToJoin(alien);

                            if (joinedHiveSuccessfully) {
                                this.hiveOption = Option.some(nearestHive);
                            } else {
                                tryCreateAndAssignHive(hiveLevelData, nearestHive);
                            }
                        })
                        .ifNone(() -> tryCreateAndAssignHive(hiveLevelData, null));
                });
        }

        hiveOption = hiveOption.filter(Hive::isAlive)
            .inspect(hive -> {
                if (alien.tickCount % (20 * 30) == 0) {
                    hive.ping(alien);
                }
            });
    }

    private void tryCreateAndAssignHive(HiveLevelData hiveLevelData, @Nullable Hive nearestHive) {
        if (!alien.getType().is(AVPEntityTypeTags.XENOMORPHS)) {
            // Non-xenomorphs cannot create their own hives.
            return;
        }

        if (alien instanceof Queen) {
            var newHive = hiveLevelData.createHive();
            newHive.moveCenter(alien.blockPosition());
            newHive.ping(alien);
            hiveOption = Option.some(newHive);
            return;
        }

        if (nearestHive != null) {
            var minimumDistance = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS;
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
        hiveOption = Option.some(newHive);
    }

    public void load(CompoundTag compoundTag) {
        if (!compoundTag.contains(HIVE_SIGNATURE_KEY)) {
            return;
        }

        this.hiveOption = HiveLevelData.getOrCreate(alien.level())
            .andThen(hiveLevelData -> {
                var hiveSignature = CompoundTagUtil.getUUIDOrNull(compoundTag, HIVE_SIGNATURE_KEY);
                return hiveSignature == null
                    ? Option.none()
                    : hiveLevelData.hive(hiveSignature);
            });
    }

    public void save(CompoundTag compoundTag) {
        hiveOption.ifSome(hive -> compoundTag.putUUID(HIVE_SIGNATURE_KEY, hive.id()));
    }

    public Option<Hive> hive() {
        return hiveOption;
    }

    public Option<UUID> signature() {
        return hiveOption.map(Hive::id);
    }
}
