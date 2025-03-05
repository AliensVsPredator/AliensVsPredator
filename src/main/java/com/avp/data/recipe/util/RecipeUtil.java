package com.avp.data.recipe.util;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.avp.data.recipe.RecipeTemplates;
import com.avp.data.recipe.builder.RecipeBuilder;

public class RecipeUtil {

    public static void createSlabBlockManualAndStonecutterRecipes(RecipeBuilder builder, Block block, Block slabBlock) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.SLAB_BLOCK.apply(block))
            .into(6, slabBlock);

        builder.stonecut(block)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(2, slabBlock);
    }

    public static void createStairBlockManualAndStonecutterRecipes(RecipeBuilder builder, Block block, Block stairBlock) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.STAIR_BLOCK.apply(block))
            .into(4, stairBlock);

        builder.stonecut(block)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(1, stairBlock);
    }

    public static void createWallBlockManualAndStonecutterRecipes(RecipeBuilder builder, Block block, Block wallBlock) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.WALL_BLOCK.apply(block))
            .into(6, wallBlock);

        builder.stonecut(block)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(1, wallBlock);
    }

    public static void createCompressedBlockRecipes2x2(RecipeBuilder builder, Item item, Block block) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(item))
            .into(1, block);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, block)
            .withCustomName(name -> name + "_from_block")
            .into(4, item);
    }

    public static void createCompressedBlockRecipes3x3(RecipeBuilder builder, Item item, Block block) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.COMPRESSED_BLOCK_3x3.apply(item))
            .into(1, block);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, block)
            .withCustomName(name -> name + "_from_block")
            .into(9, item);
    }
}
