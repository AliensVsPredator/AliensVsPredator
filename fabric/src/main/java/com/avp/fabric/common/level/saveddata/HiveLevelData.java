package com.avp.fabric.common.level.saveddata;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import com.avp.fabric.AVP;
import com.avp.fabric.common.hive.Hive;
import com.avp.fabric.common.util.AVPPredicates;

public class HiveLevelData extends SavedData {

    private static final String DATA_NAME = "hive_data";

    private static final String HIVES_KEY = "hives";

    private final Level level;

    private final Map<UUID, Hive> hiveByIdMap;

    private HiveLevelData(Level level) {
        this(level, new HashMap<>());
    }

    private HiveLevelData(Level level, Map<UUID, Hive> hiveByIdMap) {
        this.hiveByIdMap = hiveByIdMap;
        this.level = level;
    }

    public void tick() {
        hiveByIdMap.values().removeIf(hive -> {
            hive.tick();

            var shouldRemove = !hive.isAlive();

            if (shouldRemove) {
                hive.onRemove();
            }

            return shouldRemove;
        });

        setDirty();
    }

    public Option<Hive> findNearestHive(BlockPos blockPos) {
        return findNearestHive(blockPos, AVPPredicates.alwaysTrue());
    }

    public Option<Hive> findNearestHive(BlockPos blockPos, Predicate<Hive> hivePredicate) {
        var distanceSquared = Double.MAX_VALUE;
        Hive closestHive = null;

        for (var hive : hiveByIdMap.values()) {
            var centerPos = hive.centerPosition();
            var relativeDistanceSquared = blockPos.distToCenterSqr(centerPos.getX(), centerPos.getY(), centerPos.getZ());

            if (relativeDistanceSquared < distanceSquared && hivePredicate.test(hive)) {
                distanceSquared = relativeDistanceSquared;
                closestHive = hive;
            }
        }

        return Option.ofNullable(closestHive);
    }

    public Hive createHive() {
        var id = UUID.randomUUID();
        var hive = new Hive(level, id);
        hiveByIdMap.put(id, hive);
        AVP.LOGGER.debug("Created hive: {}", id);
        return hive;
    }

    public Collection<Hive> allHives() {
        return hiveByIdMap.values();
    }

    public Option<Hive> hive(@NotNull UUID id) {
        return Option.ofNullable(hiveByIdMap.get(id));
    }

    public boolean hasHive(Hive hive) {
        return hive(hive.id()).isSome();
    }

    public static HiveLevelData load(Level level, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var map = new HashMap<UUID, Hive>();
        var hivesTag = compoundTag.getCompound(HIVES_KEY);

        for (var key : hivesTag.getAllKeys()) {
            var id = UUID.fromString(key);
            var hiveTag = hivesTag.getCompound(key);
            var hive = new Hive(level, id);

            hive.load(hiveTag);

            map.put(hive.id(), hive);
        }

        return new HiveLevelData(level, map);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var hivesTag = new CompoundTag();

        hiveByIdMap.forEach((hiveId, hive) -> {
            var hiveTag = new CompoundTag();
            hive.save(hiveTag);
            hivesTag.put(hiveId.toString(), hiveTag);
        });

        compoundTag.put(HIVES_KEY, hivesTag);

        return compoundTag;
    }

    public static Option<HiveLevelData> getOrCreate(Level level) {
        return level.isClientSide
            ? Option.none()
            : Option.some(((ServerLevel) level).getDataStorage().computeIfAbsent(HiveLevelData.factory(level), DATA_NAME));
    }

    public static Factory<HiveLevelData> factory(Level level) {
        return new Factory<>(
            () -> new HiveLevelData(level),
            (compoundTag, provider) -> HiveLevelData.load(level, compoundTag, provider),
            null
        );
    }
}
