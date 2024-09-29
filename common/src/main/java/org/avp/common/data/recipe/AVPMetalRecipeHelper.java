package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;

@Deprecated(forRemoval = true)
public class AVPMetalRecipeHelper {

    public static void addStandardCutterRecipes(RecipeOutput recipeOutput, BLHolder<Block> baseBlockHolder, BlockHolderSet blockHolderSet) {
        // var setBaseBlock = blockHolderSet.base().get();

        // Base
        // baseBlockHolder.getOptional()
        // .ifPresent(baseBlock -> RecipeUtils.stonecutterBuildingBlock(recipeOutput, baseBlock, setBaseBlock, 4));

        // Slabs
        // RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.slab().get(), 2);
        // AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.slab().get(), 6)
        // .defineAndUnlockIfHas('A', setBaseBlock)
        // .pattern("AAA")
        // .save(recipeOutput);
        // Stairs
        // RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.stairs().get());
        // AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.stairs().get(), 4)
        // .defineAndUnlockIfHas('A', setBaseBlock)
        // .pattern("A ")
        // .pattern("AA ")
        // .pattern("AAA")
        // .save(recipeOutput);
        // Wall
        // RecipeUtils.stonecutterBuildingBlock(recipeOutput, setBaseBlock, blockHolderSet.wall().get());
        // AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blockHolderSet.wall().get(), 6)
        // .defineAndUnlockIfHas('A', setBaseBlock)
        // .pattern("AAA")
        // .pattern("AAA")
        // .save(recipeOutput);
    }

    private AVPMetalRecipeHelper() {
        throw new UnsupportedOperationException();
    }
}
