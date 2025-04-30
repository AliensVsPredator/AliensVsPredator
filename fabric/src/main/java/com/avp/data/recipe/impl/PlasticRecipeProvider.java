package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.RecipeTemplates;
import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.util.RecipeUtil;

public class PlasticRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPlasticBlockRecipes(builder);
    }

    private static void createPlasticBlockRecipes(RecipeBuilder builder) {
        var base = AVPBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.GREEN);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', AVPItems.POLYMER)
            .pattern("AA")
            .pattern("AA")
            .into(1, base);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(((dyeColor, block) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            var ingredient = Ingredient.of(
                AVPBlocks.DYE_COLOR_TO_PLASTIC.values()
                    .stream()
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

            var slabBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slabBlock);

            var stairBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stairBlock);

            var stonecut = builder.stonecut(block);

            var cutBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor);
            stonecut.into(4, cutBlock);

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(block))
                .into(4, cutBlock);

            var cutSlabBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, cutBlock, cutSlabBlock);
            stonecut.into(8, cutSlabBlock);

            var cutStairBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, cutBlock, cutStairBlock);
            stonecut.into(4, cutStairBlock);
        }));
    }
}
