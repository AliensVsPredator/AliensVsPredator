package com.blib.mod.common.registry.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import com.blib.api.common.loot.v1.condition.item.BLibLootItemModLoadedCondition;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibLootItemConditionTypes {

    private static final BLibRegistry<LootItemConditionType> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.LOOT_CONDITION_TYPE);

    public static final BLibHolder<LootItemConditionType> MOD_LOADED = create("mod_loaded", BLibLootItemModLoadedCondition.CODEC);

    private static BLibHolder<LootItemConditionType> create(String path, MapCodec<? extends LootItemCondition> codec) {
        return REGISTRY.createHolder(path, () -> new LootItemConditionType(codec));
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
