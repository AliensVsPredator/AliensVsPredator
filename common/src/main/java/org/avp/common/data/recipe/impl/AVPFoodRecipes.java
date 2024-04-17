package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPFoodItems;

public final class AVPFoodRecipes {

    public static void addFoodRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.INSTANCE.doritos)
            .defineAndUnlockIfHas('A', Items.WHEAT)
            .defineAndUnlockIfHas('B', Items.BAKED_POTATO)
            .pattern("AAA")
            .pattern("A B")
            .pattern("BBB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.INSTANCE.doritosCoolRanch)
            .defineAndUnlockIfHas('A', AVPFoodItems.INSTANCE.doritos)
            .defineAndUnlockIfHas('B', Items.WHEAT)
            .pattern("AB")
            .pattern("B ")
            .save(recipeOutput);

        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(AVPFoodItems.INSTANCE.rawTentacle.get()),
            RecipeCategory.FOOD,
            AVPFoodItems.INSTANCE.triloBite.get(),
            0.35F,
            200
        )
            .unlockedBy("has_trilo_bite", AVPRecipeProvider.has(AVPFoodItems.INSTANCE.rawTentacle.get()))
            .save(recipeOutput);
    }

    private AVPFoodRecipes() {
        throw new UnsupportedOperationException();
    }
}
