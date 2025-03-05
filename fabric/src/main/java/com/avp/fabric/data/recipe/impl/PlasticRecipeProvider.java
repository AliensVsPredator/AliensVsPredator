package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

import java.util.function.Supplier;

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

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(((dyeColor, blockSupplier) -> {
            var dyeItem = DyeItem.byColor(dyeColor);

            var ingredient = Ingredient.of(
                AVPBlocks.DYE_COLOR_TO_PLASTIC.values()
                    .stream()
                    .filter(plasticBlock -> !plasticBlock.equals(blockSupplier))
                    .map(Supplier::get)
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
                .into(8, blockSupplier);

            var slabBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor);
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, blockSupplier, slabBlock);

            var stairBlock = AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, blockSupplier, stairBlock);

            var stonecut = builder.stonecut(blockSupplier);

            var cutBlock = AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor);
            stonecut.into(4, cutBlock);

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(blockSupplier.get()))
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
