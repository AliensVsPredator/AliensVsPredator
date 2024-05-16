package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPTempleBlocks;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;

public final class AVPTempleBlockRecipes {

    public static void addTempleBlockRecipes(RecipeOutput recipeOutput) {
        addTempleStoneRecipes(recipeOutput);
        addTempleBrickSingleRecipes(recipeOutput);
        addTempleBrickRecipes(recipeOutput);
        addTempleTileRecipes(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.wallBase);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPTempleBlocks.INSTANCE.skulls)
            .defineAndUnlockIfHas('A', Items.SKELETON_SKULL)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private static void addTempleBrickSingleRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.brickSingle);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brickSingle, AVPTempleBlocks.INSTANCE.brickSingleSlab);
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlocks.INSTANCE.brickSingle,
            AVPTempleBlocks.INSTANCE.brickSingleStairs
        );
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brickSingle, AVPTempleBlocks.INSTANCE.brickSingleWall);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brickSingle, AVPTempleBlocks.INSTANCE.wallBase);
    }

    private static void addTempleBrickRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.brick);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brick, AVPTempleBlocks.INSTANCE.brickSlab);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brick, AVPTempleBlocks.INSTANCE.brickStairs);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brick, AVPTempleBlocks.INSTANCE.brickWall);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.brick, AVPTempleBlocks.INSTANCE.wallBase);
    }

    private static void addTempleTileRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.tile);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.tile, AVPTempleBlocks.INSTANCE.tileSlab);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.tile, AVPTempleBlocks.INSTANCE.tileStairs);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.tile, AVPTempleBlocks.INSTANCE.tileWall);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.tile, AVPTempleBlocks.INSTANCE.wallBase);
    }

    private static void addTempleStoneRecipes(RecipeOutput recipeOutput) {
        var templeStoneOutput = AVPTempleBlocks.INSTANCE.floor.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.DEEPSLATE), RecipeCategory.MISC, templeStoneOutput, 0, 100)
            .unlockedBy("has_deepslate", AVPRecipeProvider.has(Items.DEEPSLATE))
            .save(recipeOutput, AVPConstants.MOD_ID + ":temple_stone_from_blasting_deepslate");

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.floorSlab);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.floorStairs);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.INSTANCE.floor, AVPTempleBlocks.INSTANCE.floorWall);
    }

    private AVPTempleBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
