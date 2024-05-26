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
    }

    private static void addStandardCutterRecipes(RecipeOutput recipeOutput, Block baseBlock, BlockHolderSet fullBlockHolderMetalSet) {
        var setBaseBlock = fullBlockHolderMetalSet.base().get();

        // Stonecutter recipes
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, setBaseBlock, 4);
        // Cut -> Slabs, Stairs, Walls
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, fullBlockHolderMetalSet.slab().get(), 2);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, fullBlockHolderMetalSet.stairs().get());
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, fullBlockHolderMetalSet.wall().get());
    }

    private AVPMetalRecipeHelper() {
        throw new UnsupportedOperationException();
    }
}
