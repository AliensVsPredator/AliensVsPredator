package org.avp.common.data.recipe.impl.block;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.RecipeUtils;
import org.avp.common.registry.block.AVPTempleBlockRegistry;

public final class AVPTempleBlockRecipes {

    public static void addTempleBlockRecipes(RecipeOutput recipeOutput) {
        addTempleStoneRecipes(recipeOutput);
        addTempleBrickSingleRecipes(recipeOutput);
        addTempleBrickRecipes(recipeOutput);
        addTempleTileRecipes(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.wallBase
        );

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPTempleBlockRegistry.INSTANCE.skulls)
            .defineAndUnlockIfHas('A', Items.SKELETON_SKULL)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);
    }

    private static void addTempleBrickSingleRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.brickSingle.base()
        );

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brickSingle.base(),
            AVPTempleBlockRegistry.INSTANCE.brickSingle.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brickSingle.base(),
            AVPTempleBlockRegistry.INSTANCE.brickSingle.stairs()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brickSingle.base(),
            AVPTempleBlockRegistry.INSTANCE.brickSingle.wall()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brickSingle.base(),
            AVPTempleBlockRegistry.INSTANCE.wallBase
        );
    }

    private static void addTempleBrickRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.brick.base()
        );

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brick.base(),
            AVPTempleBlockRegistry.INSTANCE.brick.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brick.base(),
            AVPTempleBlockRegistry.INSTANCE.brick.stairs()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brick.base(),
            AVPTempleBlockRegistry.INSTANCE.brick.wall()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.brick.base(),
            AVPTempleBlockRegistry.INSTANCE.wallBase
        );
    }

    private static void addTempleTileRecipes(RecipeOutput recipeOutput) {
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.tile.base()
        );

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.tile.base(),
            AVPTempleBlockRegistry.INSTANCE.tile.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.tile.base(),
            AVPTempleBlockRegistry.INSTANCE.tile.stairs()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.tile.base(),
            AVPTempleBlockRegistry.INSTANCE.tile.wall()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.tile.base(),
            AVPTempleBlockRegistry.INSTANCE.wallBase
        );
    }

    private static void addTempleStoneRecipes(RecipeOutput recipeOutput) {
        var templeStoneOutput = AVPTempleBlockRegistry.INSTANCE.floor.base().get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.DEEPSLATE), RecipeCategory.MISC, templeStoneOutput, 0, 100)
            .unlockedBy("has_deepslate", AVPRecipeProvider.has(Items.DEEPSLATE))
            .save(recipeOutput, AVPConstants.MOD_ID + ":temple_stone_from_blasting_deepslate");

        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.floor.slab()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.floor.stairs()
        );
        RecipeUtils.stonecutterBuildingBlock(
            recipeOutput,
            AVPTempleBlockRegistry.INSTANCE.floor.base(),
            AVPTempleBlockRegistry.INSTANCE.floor.wall()
        );
    }

    private AVPTempleBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
