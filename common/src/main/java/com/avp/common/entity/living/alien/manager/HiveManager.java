package com.avp.common.entity.living.alien.manager;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
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
                .map(
                    hiveLevelData -> new Tuple<>(
                        hiveLevelData,
                        hiveLevelData.findNearestHive(
                            alien.blockPosition(),
                            // Find the nearest hive for this alien's variant type.
                            hive -> Objects.equals(alien.getVariant(), hive.getVariant())
                        )
                    )
                )
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

        hiveOption.ifSome(hive -> {
            if (
                // If hive is no longer alive...
            !hive.isAlive()
                // OR alien variant no longer matches the hive's variant...
                || !Objects.equals(alien.getVariant(), hive.getVariant())
            ) {
                // Then remove the alien from the hive.
                hive.removeHiveMember(alien);
                // And also assign this alien's hive to nothing.
                this.hiveOption = Option.none();
                // Don't proceed any further.
                return;
            }

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

        if (nearestHive != null && nearestHive.getSpaceManager().isEntityWithinHiveBuffer(alien)) {
            return;
        }

        var newHive = hiveLevelData.createHive(alien);
        this.hiveOption = Option.some(newHive);
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
