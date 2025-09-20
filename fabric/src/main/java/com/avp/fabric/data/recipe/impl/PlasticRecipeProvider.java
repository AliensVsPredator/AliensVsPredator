package com.avp.fabric.data.recipe.impl;

import com.human.common.registry.init.block.HumanPlasticBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class PlasticRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPlasticBlockRecipes(builder);
    }

    private static void createPlasticBlockRecipes(RecipeBuilder builder) {
        var base = HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.GREEN).get();

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItems.POLYMER)
            .pattern("AA")
            .pattern("AA")
            .into(1, base);

        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.forEach(((dyeColor, blockSupplier) -> {
            var block = blockSupplier.get();
            var dyeItem = DyeItem.byColor(dyeColor);

            var ingredient = Ingredient.of(
                HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.values()
                    .stream()
                    .map(Supplier::get)
                    .filter(plasticBlock -> !plasticBlock.equals(block))
                    .map(ItemStack::new)
            );

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('A', dyeItem)
                .define('B', ingredient)
                .pattern("BBB")
                .pattern("BAB")
                .pattern("BBB")
                .withCustomName((outputItem) -> "dye_" + outputItem)
                .into(8, block);

            var slabBlock = HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slabBlock);

            var stairBlock = HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get();
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stairBlock);

            var stonecut = builder.stonecut(block);

            var cutBlock = HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get();
            stonecut.into(4, cutBlock);

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(block))
                .into(4, cutBlock);

            var framedBlock = HumanPlasticBlocks.DYE_COLOR_TO_FRAMED_PLASTIC.get(dyeColor).get();
            stonecut.into(2, framedBlock);

            var cutSlabBlock = HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, cutBlock, cutSlabBlock);
            stonecut.into(8, cutSlabBlock);

            var cutStairBlock = HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get();
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, cutBlock, cutStairBlock);
            stonecut.into(4, cutStairBlock);
        }));
    }
}
