package com.avp.fabric.data.recipe.impl;

import com.compat.CommonItemTags;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;

import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class PaddingRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createPaddingRecipes(builder);
    }

    private static void createPaddingRecipes(RecipeBuilder builder) {
        HumanPaddingBlocks.DYE_COLOR_TO_PADDING.forEach((dyeColor, blockSupplier) -> {
            var block = blockSupplier.get();

            builder.shapeless()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1, DyeItem.byColor(dyeColor))
                .requires(1, CommonItemTags.LEATHERS)
                .requires(1, ItemTags.WOOL)
                .into(4, block);

            var stonecutBuilder = builder.stonecut(block)
                .withCategory(RecipeCategory.BUILDING_BLOCKS);

            var slab = HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get();
            var stair = HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, block, slab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, block, stair);

            var panelBlock = HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get();
            stonecutBuilder.into(1, panelBlock);
            var panelSlab = HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get();
            var panelStair = HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, panelBlock, panelSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, panelBlock, panelStair);

            var pipeBlock = HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get();
            stonecutBuilder.into(1, pipeBlock);
            var pipeSlab = HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get();
            var pipeStair = HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, pipeBlock, pipeStair);

            var tileBlock = HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING.get(dyeColor).get();
            stonecutBuilder.into(1, tileBlock);
            var tileSlab = HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_SLAB.get(dyeColor).get();
            var tileStair = HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_STAIRS.get(dyeColor).get();
            RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, tileBlock, tileSlab);
            RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, tileBlock, tileStair);
        });
    }
}
