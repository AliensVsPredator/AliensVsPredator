package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPIndustrialBlocks;
import org.avp.common.item.AVPItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPIndustrialBlockRecipes {

    public static void addIndustrialBlockRecipes(RecipeOutput recipeOutput) {
        addBrickRecipes(recipeOutput);
        addMetalPanel0Recipes(recipeOutput);
        addMetalPanel1Recipes(recipeOutput);
        addMetalPanel2Recipes(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.LAMP)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', Items.REDSTONE_LAMP)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);

        var industrialGlassOutput = AVPIndustrialBlocks.GLASS.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GLASS), RecipeCategory.MISC, industrialGlassOutput, 0.7F, 100)
            .unlockedBy("has_glass", AVPRecipeProvider.has(Items.GLASS))
            .save(recipeOutput, AVPConstants.MOD_ID + ":industrial_glass_from_blasting_glass");
    }

    private static void addBrickRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.BRICK)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.BRICK, AVPIndustrialBlocks.BRICK_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.BRICK, AVPIndustrialBlocks.BRICK_STAIRS);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.BRICK, AVPIndustrialBlocks.FLOOR_GRILL, 4);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.BRICK, AVPIndustrialBlocks.VENT);
    }

    private static void addMetalPanel0Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_0)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("BA")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_0, AVPIndustrialBlocks.METAL_PANEL_0_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_0, AVPIndustrialBlocks.METAL_PANEL_0_STAIRS);
    }

    private static void addMetalPanel1Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_1)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("AA")
            .pattern("BB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_1, AVPIndustrialBlocks.METAL_PANEL_1_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_1, AVPIndustrialBlocks.METAL_PANEL_1_STAIRS);
    }

    private static void addMetalPanel2Recipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AVPIndustrialBlocks.METAL_PANEL_2)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', Items.IRON_INGOT)
            .pattern("BA")
            .pattern("AB")
            .save(recipeOutput);

        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_2, AVPIndustrialBlocks.METAL_PANEL_2_SLAB);
        RecipeUtils.stonecutterBuildingBlock(recipeOutput, AVPIndustrialBlocks.METAL_PANEL_2, AVPIndustrialBlocks.METAL_PANEL_2_STAIRS);
    }

    private AVPIndustrialBlockRecipes() {
        throw new UnsupportedOperationException();
    }
}
