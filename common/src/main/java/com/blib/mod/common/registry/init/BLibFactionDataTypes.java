package com.blib.mod.common.registry.init;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionDataType;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibFactionDataTypes {

    private static final BLibRegistry<FactionDataType<?>> REGISTRY = BLib.MOD.registries().create(BLibBuiltInRegistries.FACTION_DATA_TYPES);

    public static final BLibHolder<FactionDataType<EmptyFactionData>> EMPTY = register(
        "empty",
        () -> new FactionDataType<>(EmptyFactionData::new)
    );

    private static <T extends FactionData> BLibHolder<FactionDataType<T>> register(String path, Supplier<FactionDataType<T>> supplier) {
        return REGISTRY.createHolder(path, supplier);
    }

    public static class EmptyFactionData extends FactionData {

        @Override
        public void load(CompoundTag compoundTag) {}

        @Override
        public void save(CompoundTag compoundTag) {}
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
