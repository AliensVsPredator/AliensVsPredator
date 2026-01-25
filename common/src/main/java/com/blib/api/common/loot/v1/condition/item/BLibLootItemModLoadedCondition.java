package com.blib.api.common.loot.v1.condition.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import com.blib.api.BLibAPI;
import com.blib.mod.common.registry.init.BLibLootItemConditionTypes;

public class BLibLootItemModLoadedCondition implements LootItemCondition {

    public static final MapCodec<BLibLootItemModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(
        (instance) -> instance.group(Codec.STRING.fieldOf("modId").forGetter(BLibLootItemModLoadedCondition::modId))
            .apply(instance, BLibLootItemModLoadedCondition::new)
    );

    private final String modId;

    private BLibLootItemModLoadedCondition(String modId) {
        this.modId = modId;
    }

    public String modId() {
        return modId;
    }

    public @NotNull LootItemConditionType getType() {
        return BLibLootItemConditionTypes.MOD_LOADED.get();
    }

    public boolean test(LootContext context) {
        return BLibAPI.isModLoaded(modId);
    }

    public static LootItemCondition.Builder isModLoaded(String modId) {
        return () -> new BLibLootItemModLoadedCondition(modId);
    }

}
