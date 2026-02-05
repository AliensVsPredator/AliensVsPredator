package com.blib.mod.common.registry.init;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionType;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibFactionTypes {

    private static final BLibRegistry<FactionType<?>> REGISTRY = BLib.MOD.registries().create(BLibBuiltInRegistries.FACTION_TYPES);

    public static final BLibHolder<FactionType<PredatorClan>> PREDATOR_CLAN = register(
        "predator_clan",
        () -> new FactionType<>(PredatorClan::new)
    );

    private static <T extends FactionData> BLibHolder<FactionType<T>> register(String path, Supplier<FactionType<T>> supplier) {
        return REGISTRY.createHolder(path, supplier);
    }

    public static class PredatorClan extends FactionData {

        public static final String NBT_POPULATION = "population";

        private int population;

        @Override
        public void load(CompoundTag compoundTag) {
            if (compoundTag.contains(NBT_POPULATION)) {
                this.population = compoundTag.getInt(NBT_POPULATION);
            }
        }

        @Override
        public void save(CompoundTag compoundTag) {
            compoundTag.putInt(NBT_POPULATION, population);
        }

        public int getPopulation() {
            return population;
        }

        public void setPopulation(int population) {
            this.population = population;
            markDirty();
        }
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
