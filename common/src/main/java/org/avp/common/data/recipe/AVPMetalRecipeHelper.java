package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.avp.api.Holder;
import org.avp.api.block.BlockHolderSet;
import org.avp.common.block.AVPBlocks;

public class AVPMetalRecipeHelper {

    public static void addFullMetalSetRecipes(
        RecipeOutput recipeOutput,
        Holder<Item> ingotHolder,
        AVPBlocks.FullBlockHolderMetalSet fullBlockHolderMetalSet
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
        addStandardCutterRecipes(recipeOutput, baseBlock, fullBlockHolderMetalSet.cutSet());

        // Base -> Extended Set
        var extendedSet = fullBlockHolderMetalSet.extendedSet();
        addMetalSetRecipes(recipeOutput, baseBlock, extendedSet);
    }

    public static void addMetalSetRecipes(RecipeOutput recipeOutput, Block baseBlock, AVPBlocks.BlockHolderMetalSet blockHolderMetalSet) {
        // Base -> Plated
        addStandardCutterRecipes(recipeOutput, baseBlock, blockHolderMetalSet.platedSet());
        // Base -> Plated Chevron
        addStandardCutterRecipes(recipeOutput, baseBlock, blockHolderMetalSet.platedChevronSet());
        // Base -> Plated Stack
        addStandardCutterRecipes(recipeOutput, baseBlock, blockHolderMetalSet.platedStackSet());
        // Base -> Grate
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, blockHolderMetalSet.grate().get(), 2);
        // Base -> Vent
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, blockHolderMetalSet.vent().get());
    }

    private static void addStandardCutterRecipes(RecipeOutput recipeOutput, Block baseBlock, BlockHolderSet blockHolderSet) {
        var setBaseBlock = blockHolderSet.base().get();

        // Base
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, setBaseBlock, 4);

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
