package com.blib.common.data.loot.condition.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import com.blib.BLib;
import com.blib.common.registry.BLibHolder;
import com.blib.common.registry.BLibRegistry;

public class BLibLootItemConditionTypes {

    private static final BLibRegistry<LootItemConditionType> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.LOOT_CONDITION_TYPE);

    public static final BLibHolder<LootItemConditionType> MOD_LOADED = create("mod_loaded", LootItemModLoadedCondition.CODEC);

    private static BLibHolder<LootItemConditionType> create(String path, MapCodec<? extends LootItemCondition> codec) {
        return REGISTRY.createHolder(path, () -> new LootItemConditionType(codec));
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
