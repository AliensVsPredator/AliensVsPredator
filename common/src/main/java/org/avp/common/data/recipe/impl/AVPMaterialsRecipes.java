package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.item.AVPItems;

public final class AVPMaterialsRecipes {

    public static void addMaterialRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItems.INSTANCE.CARBON, 8)
            .defineAndUnlockIfHas('A', ItemTags.COALS)
            .pattern("AA")
            .pattern("AA")
            .save(recipeOutput);

        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AVPItems.INSTANCE.NEODYMIUM_MAGNET, 4)
            .requiresAndUnlockIfHas('A', AVPItems.INSTANCE.NEODYMIUM)
            .requiresAndUnlockIfHas('B', AVPItems.INSTANCE.COBALT, 2)
            .save(recipeOutput);

        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItems.INSTANCE.POLYCARBONATE, 4)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.SILICA)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.CARBON)
            .pattern("AAA")
            .pattern("BBB")
            .pattern("BBB")
            .save(recipeOutput);

        var poylmerOutput = AVPItems.INSTANCE.POLYMER.get();
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SLIME_BALL), RecipeCategory.MISC, poylmerOutput, 0.0F, 200)
            .unlockedBy("has_slime_ball", AVPRecipeProvider.has(Items.SLIME_BALL))
            .save(recipeOutput, AVPConstants.MOD_ID + ":polymer_from_smelting_slime_ball");

        var steelOutput = AVPItems.INSTANCE.INGOT_STEEL.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_INGOT), RecipeCategory.MISC, steelOutput, 0.7F, 100)
            .unlockedBy("has_iron_ingot", AVPRecipeProvider.has(Items.IRON_INGOT))
            .save(recipeOutput, AVPConstants.MOD_ID + ":ingot_steel_from_blasting_iron_ingot");
    }

    private AVPMaterialsRecipes() {
        throw new UnsupportedOperationException();
    }
}
