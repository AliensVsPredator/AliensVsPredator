package com.alien.common.gameplay.level.saveddata;

import com.alien.common.model.alien.variant.AlienVariant;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tracks strain leak progression for the level.
 */
public class StrainLeakData extends SavedData {

    private static final String DATA_NAME = "strain_leak_data";

    private static final String NBT_ALIEN_VARIANTS = "alienVariants";

    private final Map<AlienVariant, Integer> variantCounts;

    private StrainLeakData(Level level) {
        this.variantCounts = new HashMap<>();

        // Initialize with defaults based on dimension type.
        if (level.dimension() == Level.OVERWORLD) {
            variantCounts.put(AlienVariant.NORMAL, 1_000_000);
        } else if (level.dimension() == Level.NETHER) {
            variantCounts.put(AlienVariant.NETHER, 1_000_000);
        }
    }

    public boolean hasVariant(AlienVariant variant) {
        return variantCounts.getOrDefault(variant, 0) > 0;
    }

    public int getCount(AlienVariant variant) {
        return variantCounts.getOrDefault(variant, 0);
    }

    public Set<AlienVariant> getVariants() {
        return variantCounts.entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 0)
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableSet());
    }

    public void add(AlienVariant variant, int amount) {
        if (amount == 0) {
            return;
        }

        variantCounts.compute(variant, (key, oldValue) -> {
            var current = oldValue == null ? 0 : oldValue;
            var newCount = current + amount;

            if (newCount <= 0) {
                // remove entry if zero or negative
                return null;
            }

            return newCount;
        });

        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        var variantTag = new CompoundTag();

        for (var entry : variantCounts.entrySet()) {
            var id = entry.getKey().getId();
            var count = entry.getValue();

            if (count > 0) {
                variantTag.putInt(Integer.toString(id), count);
            }
        }

        compoundTag.put(NBT_ALIEN_VARIANTS, variantTag);

        return compoundTag;
    }

    public static StrainLeakData load(Level level, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new StrainLeakData(level);

        if (compoundTag.contains(NBT_ALIEN_VARIANTS, Tag.TAG_COMPOUND)) {
            var variantTag = compoundTag.getCompound(NBT_ALIEN_VARIANTS);

            for (var key : variantTag.getAllKeys()) {
                try {
                    var id = Integer.parseInt(key);
                    var count = variantTag.getInt(key);

                    AlienVariant.getById(id).ifSome(variant -> {
                        if (count > 0) {
                            data.variantCounts.put(variant, count);
                        }
                    });
                } catch (NumberFormatException ignored) {
                    // Skip any invalid keys that aren't integers
                }
            }
        }

        return data;
    }

    public static Option<StrainLeakData> getOrCreate(Level level) {
        return level.isClientSide
            ? Option.none()
            : Option.some(
                ((ServerLevel) level).getDataStorage()
                    .computeIfAbsent(factory(level), DATA_NAME)
            );
    }

    public static Factory<StrainLeakData> factory(Level level) {
        return new Factory<>(
            () -> new StrainLeakData(level),
            (compoundTag, provider) -> StrainLeakData.load(level, compoundTag, provider),
            null
        );
    }

}
