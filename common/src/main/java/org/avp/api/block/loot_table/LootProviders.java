package org.avp.api.block.loot_table;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.avp.api.QuadFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LootProviders {

    public static final LootItemCondition.Builder HAS_SILK_TOUCH =
        MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    public static final BiFunction<Block, ItemLike, LootTable.Builder> OTHER = (block, other) ->
        LootTable.lootTable().withPool(
            LootPool.lootPool()
                .when(ExplosionCondition.survivesExplosion())
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(other))
        );

    public static final Function<Block, LootTable.Builder> SELF = block -> OTHER.apply(block, block);

    public static final BiFunction<Block, ItemLike, LootTable.Builder> ORE = (block, item) ->
        LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(block)
                        .when(HAS_SILK_TOUCH)
                        .otherwise(
                            LootItem.lootTableItem(item)
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                                .apply(ApplyExplosionDecay.explosionDecay())
                        )
                )
        );

    public static final QuadFunction<Block, ItemLike, Integer, Integer, LootTable.Builder> ORE_VARIABLE = (block, item, min, max) ->
        LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                    LootItem.lootTableItem(block)
                        .when(HAS_SILK_TOUCH)
                        .otherwise(
                            LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                                .apply(ApplyExplosionDecay.explosionDecay())
                        )
                )
        );

    private LootProviders() {
        throw new UnsupportedOperationException();
    }
}
