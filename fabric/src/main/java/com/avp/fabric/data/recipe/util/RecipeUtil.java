package com.avp.fabric.data.recipe.util;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

import java.util.function.Supplier;

public class RecipeUtil {

    public static void createSlabBlockManualAndStonecutterRecipes(RecipeBuilder builder, Supplier<Block> blockSupplier, Supplier<Block> slabBlockSupplier) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.SLAB_BLOCK.apply(blockSupplier.get()))
            .into(6, slabBlockSupplier);

        builder.stonecut(blockSupplier)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(2, slabBlockSupplier);
    }

    public static void createStairBlockManualAndStonecutterRecipes(RecipeBuilder builder, Supplier<Block> blockSupplier, Supplier<Block> stairBlockSupplier) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.STAIR_BLOCK.apply(blockSupplier.get()))
            .into(4, stairBlockSupplier);

        builder.stonecut(blockSupplier)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(1, stairBlockSupplier);
    }

    public static void createWallBlockManualAndStonecutterRecipes(RecipeBuilder builder, Supplier<Block> blockSupplier, Supplier<Block> wallBlockSupplier) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.WALL_BLOCK.apply(blockSupplier.get()))
            .into(6, wallBlockSupplier);

        builder.stonecut(blockSupplier)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(1, wallBlockSupplier);
    }

    public static void createCompressedBlockRecipes2x2(RecipeBuilder builder, Supplier<Item> itemSupplier, Supplier<Block> blockSupplier) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.COMPRESSED_BLOCK_2x2.apply(itemSupplier.get()))
            .into(1, blockSupplier);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, blockSupplier)
            .withCustomName(name -> name + "_from_block")
            .into(4, itemSupplier);
    }

    public static void createCompressedBlockRecipes3x3(RecipeBuilder builder, Supplier<Item> itemSupplier, Supplier<Block> blockSupplier) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.COMPRESSED_BLOCK_3x3.apply(itemSupplier.get()))
            .into(1, blockSupplier);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, blockSupplier)
            .withCustomName(name -> name + "_from_block")
            .into(9, itemSupplier);
    }
}
