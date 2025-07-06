package com.alien.common.gameplay.level.saveddata;

import com.alien.common.model.alien.variant.AlienVariant;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks strain leak progression for the level.
 */
public class StrainLeakData extends SavedData {

    private static final String DATA_NAME = "strain_leak_data";

    private static final String NBT_ALIEN_VARIANTS = "alienVariants";

    private final Set<AlienVariant> activeVariants;

    private StrainLeakData(Level level) {
        this.activeVariants = new HashSet<>();

        // Initialize with defaults based on dimension type.
        if (level.dimension() == Level.OVERWORLD) {
            activeVariants.add(AlienVariant.NORMAL);
        } else if (level.dimension() == Level.NETHER) {
            activeVariants.add(AlienVariant.NETHER);
        }
    }

    public boolean hasVariant(AlienVariant variant) {
        return activeVariants.contains(variant);
    }

    public Set<AlienVariant> getVariants() {
        return Set.copyOf(activeVariants);
    }

    public boolean addVariant(AlienVariant variant) {
        var added = activeVariants.add(variant);

        if (added) {
            setDirty();
        }

        return added;
    }

    public boolean removeVariant(AlienVariant variant) {
        var removed = activeVariants.remove(variant);

        if (removed) {
            setDirty();
        }

        return removed;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        var listTag = new ListTag();

        for (var variant : activeVariants) {
            listTag.add(IntTag.valueOf(variant.getId()));
        }

        compoundTag.put(NBT_ALIEN_VARIANTS, listTag);

        return compoundTag;
    }

    public static StrainLeakData load(Level level, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new StrainLeakData(level);
        var list = compoundTag.getList(NBT_ALIEN_VARIANTS, Tag.TAG_INT);

        for (var element : list) {
            var id = ((IntTag) element).getAsInt();

            AlienVariant.getById(id)
                .ifSome(data.activeVariants::add);
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
