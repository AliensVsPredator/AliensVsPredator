package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.common.registry.block.AVPBlockRegistry;

public class AVPMetalRecipeHelper {

    public static void addFullMetalSetRecipes(
        RecipeOutput recipeOutput,
        BLHolder<Item> ingotHolder,
        AVPBlockRegistry.FullBlockHolderMetalSet fullBlockHolderMetalSet
    ) {
        var baseBlockHolder = fullBlockHolderMetalSet.base();
        var baseBlock = baseBlockHolder.get();

        ingotHolder.getOptional().ifPresent(ingot -> {
            // Compressed
            AVPRecipeHelper.compressedBlockRecipe(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ingot, baseBlock);
            // Decompressed
            AVPRecipeHelper.decompressedItemRecipe(recipeOutput, RecipeCategory.MISC, baseBlock, ingot);
        });

        // Base -> Cut
        addStandardCutterRecipes(recipeOutput, baseBlockHolder, fullBlockHolderMetalSet.cutSet());
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fullBlockHolderMetalSet.cutSet().base(), 4)
            .defineAndUnlockIfHas('A', baseBlock)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        // Base -> Extended Set
        var extendedSet = fullBlockHolderMetalSet.extendedSet();
        addMetalSetRecipes(recipeOutput, baseBlockHolder, extendedSet);
    }

    public static void addMetalSetRecipes(
        RecipeOutput recipeOutput,
        BLHolder<Block> baseBlockHolder,
        AVPBlockRegistry.BlockHolderMetalSet blockHolderMetalSet
    ) {
        // Base -> Plated
        addStandardCutterRecipes(recipeOutput, baseBlockHolder, blockHolderMetalSet.platedSet());
        // Base -> Plated Chevron
        addStandardCutterRecipes(recipeOutput, baseBlockHolder, blockHolderMetalSet.platedChevronSet());
        // Base -> Plated Stack
        addStandardCutterRecipes(recipeOutput, baseBlockHolder, blockHolderMetalSet.platedStackSet());
        // Base -> Grate
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlockHolder, blockHolderMetalSet.grate(), 2);
        // Base -> Vent
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlockHolder, blockHolderMetalSet.vent());
    }

    public static void addStandardCutterRecipes(RecipeOutput recipeOutput, BLHolder<Block> baseBlockHolder, BlockHolderSet blockHolderSet) {
        var setBaseBlock = blockHolderSet.base().get();

        // Base
        baseBlockHolder.getOptional()
            .ifPresent(baseBlock -> RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, setBaseBlock, 4));

        // Slabs
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.slab().get(), 2);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.slab().get(), 6)
            .defineAndUnlockIfHas('A', setBaseBlock)
            .pattern("AAA")
            .save(recipeOutput);
        // Stairs
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.stairs().get());
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.stairs().get(), 4)
            .defineAndUnlockIfHas('A', setBaseBlock)
            .pattern("A  ")
            .pattern("AA ")
            .pattern("AAA")
            .save(recipeOutput);
        // Wall
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.wall().get());
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.wall().get(), 6)
            .defineAndUnlockIfHas('A', setBaseBlock)
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
    }

    private AVPMetalRecipeHelper() {
        throw new UnsupportedOperationException();
    }
}
