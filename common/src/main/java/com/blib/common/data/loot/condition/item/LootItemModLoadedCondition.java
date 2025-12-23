package com.blib.common.data.loot.condition.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import com.blib.BLib;

public record LootItemModLoadedCondition(String modId) implements LootItemCondition {

    public static final MapCodec<LootItemModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(
        (instance) -> instance.group(Codec.STRING.fieldOf("modId").forGetter(LootItemModLoadedCondition::modId))
            .apply(instance, LootItemModLoadedCondition::new)
    );

    public @NotNull LootItemConditionType getType() {
        return BLibLootItemConditionTypes.MOD_LOADED.get();
    }

    public boolean test(LootContext context) {
        return BLib.isModLoaded(modId);
    }
}
