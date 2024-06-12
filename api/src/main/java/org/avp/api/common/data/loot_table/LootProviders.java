package org.avp.api.common.data.loot_table;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.avp.api.util.function.QuadFunction;
import org.avp.api.util.function.TriFunction;

public class LootProviders {

    public static final LootItemCondition.Builder HAS_SILK_TOUCH =
        MatchTool.toolMatches(
            net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))
        );

    public static final TriFunction<Block, ItemLike, Integer, LootTable.Builder> OTHER_VARIABLE = (block, other, count) -> LootTable
        .lootTable()
        .withPool(
            LootPool.lootPool()
                .when(ExplosionCondition.survivesExplosion())
                .setRolls(ConstantValue.exactly(count))
                .add(LootItem.lootTableItem(other))
        );

    public static final BiFunction<Block, ItemLike, LootTable.Builder> OTHER = (block, other) -> OTHER_VARIABLE.apply(block, other, 1);

    public static final BiFunction<Block, Integer, LootTable.Builder> SELF_VARIABLE = (block, count) -> OTHER_VARIABLE.apply(
        block,
        block,
        count
    );

    public static final Function<Block, LootTable.Builder> SELF = block -> OTHER.apply(block, block);

    public static final Function<Block, LootTable.Builder> SLAB = block -> LootTable.lootTable()
        .withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(
                    LootItem.lootTableItem(block)
                        .apply(
                            SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(
                                            StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE)
                                        )
                                )
                        )
                        .apply(ApplyExplosionDecay.explosionDecay())
                )
        );

    public static final BiFunction<Block, ItemLike, LootTable.Builder> ORE = (block, item) -> LootTable.lootTable()
        .withPool(
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

    public static final QuadFunction<Block, ItemLike, Integer, Integer, LootTable.Builder> ORE_VARIABLE = (
        block,
        item,
        min,
        max
    ) -> LootTable.lootTable()
        .withPool(
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
