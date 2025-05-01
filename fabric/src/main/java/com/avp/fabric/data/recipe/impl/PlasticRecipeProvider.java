package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class PlasticRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPlasticBlockRecipes(builder);
    }

    private static void createPlasticBlockRecipes(RecipeBuilder builder) {
        var base = TempAVPBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.GREEN).get();

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('A', TempAVPItems.POLYMER)
            .pattern("AA")
            .pattern("AA")
            .into(1, base);

        TempAVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(((dyeColor, blockSupplier) -> {
            var block = blockSupplier.get();
            var dyeItem = DyeItem.byColor(dyeColor);

            var ingredient = Ingredient.of(
                TempAVPBlocks.DYE_COLOR_TO_PLASTIC.values()
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

            var slabBlock = TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slabBlock);

            var stairBlock = TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get();
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stairBlock);

            var stonecut = builder.stonecut(block);

            var cutBlock = TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get();
            stonecut.into(4, cutBlock);

            builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(block))
                .into(4, cutBlock);

            var cutSlabBlock = TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, cutBlock, cutSlabBlock);
            stonecut.into(8, cutSlabBlock);

            var cutStairBlock = TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get();
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, cutBlock, cutStairBlock);
            stonecut.into(4, cutStairBlock);
        }));
    }
}
