package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPTempleBlocks;

public final class AVPTempleBlockRecipes {

    public static void addTempleBlockRecipes(RecipeOutput recipeOutput) {
        addTempleStoneRecipes(recipeOutput);
        addTempleBrickSingleRecipes(recipeOutput);
        addTempleBrickRecipes(recipeOutput);
        addTempleTileRecipes(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.WALL_BASE);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPTempleBlocks.SKULLS)
            .defineAndUnlockIfHas('A', Items.SKELETON_SKULL)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private static void addTempleBrickSingleRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.BRICK_SINGLE);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK_SINGLE, AVPTempleBlocks.BRICK_SINGLE_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK_SINGLE, AVPTempleBlocks.BRICK_SINGLE_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK_SINGLE, AVPTempleBlocks.BRICK_SINGLE_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK_SINGLE, AVPTempleBlocks.WALL_BASE);
    }

    private static void addTempleBrickRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.BRICK);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK, AVPTempleBlocks.BRICK_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK, AVPTempleBlocks.BRICK_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK, AVPTempleBlocks.BRICK_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.BRICK, AVPTempleBlocks.WALL_BASE);
    }

    private static void addTempleTileRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.TILE);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.TILE, AVPTempleBlocks.TILE_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.TILE, AVPTempleBlocks.TILE_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.TILE, AVPTempleBlocks.TILE_WALL);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.TILE, AVPTempleBlocks.WALL_BASE);
    }

    private static void addTempleStoneRecipes(RecipeOutput recipeOutput) {
        var templeStoneOutput = AVPTempleBlocks.FLOOR.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.DEEPSLATE), RecipeCategory.MISC, templeStoneOutput, 0.0F, 100)
            .unlockedBy("has_deepslate", AVPRecipeProvider.has(Items.DEEPSLATE))
            .save(recipeOutput, AVPConstants.MOD_ID + ":temple_stone_from_blasting_deepslate");

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.FLOOR_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.FLOOR_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPTempleBlocks.FLOOR, AVPTempleBlocks.FLOOR_WALL);
    }

    private AVPTempleBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
