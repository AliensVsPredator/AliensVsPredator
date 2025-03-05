package com.avp.common.level.saveddata;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.common.hive.Hive;
import com.avp.common.util.AVPPredicates;

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

    public Optional<Hive> findNearestHive(BlockPos blockPos) {
        return findNearestHive(blockPos, AVPPredicates.alwaysTrue());
    }

    public Optional<Hive> findNearestHive(BlockPos blockPos, Predicate<Hive> hivePredicate) {
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

        return Optional.ofNullable(closestHive);
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

    public @Nullable Hive hiveOrNull(UUID id) {
        return hiveByIdMap.get(id);
    }

    public boolean hasHive(Hive hive) {
        return hiveOrNull(hive.id()) != null;
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

    public static Optional<HiveLevelData> getOrCreate(Level level) {
        if (level.isClientSide) {
            return Optional.empty();
        }

        var manager = ((ServerLevel) level).getDataStorage();
        return Optional.of(manager.computeIfAbsent(HiveLevelData.factory(level), DATA_NAME));
    }

    public static Factory<HiveLevelData> factory(Level level) {
        return new Factory<>(
            () -> new HiveLevelData(level),
            (compoundTag, provider) -> HiveLevelData.load(level, compoundTag, provider),
            null
        );
    }
}
