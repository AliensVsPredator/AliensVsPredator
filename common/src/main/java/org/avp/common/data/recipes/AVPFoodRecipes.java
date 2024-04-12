package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.item.AVPFoodItems;

public final class AVPFoodRecipes {

    public static void addFoodRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.DORITOS)
            .defineAndUnlockIfHas('A', Items.WHEAT)
            .defineAndUnlockIfHas('B', Items.BAKED_POTATO)
            .pattern("AAA")
            .pattern("A B")
            .pattern("BBB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.DORITOS_COOL_RANCH)
            .defineAndUnlockIfHas('A', AVPFoodItems.DORITOS)
            .defineAndUnlockIfHas('B', Items.WHEAT)
            .pattern("AB")
            .pattern("B ")
            .save(recipeOutput);

        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(AVPFoodItems.RAW_TENTACLE.get()),
            RecipeCategory.FOOD,
            AVPFoodItems.TRILO_BITE.get(),
            0.35F,
            200
        )
            .unlockedBy("has_trilo_bite", AVPRecipeProvider.has(AVPFoodItems.RAW_TENTACLE.get()))
            .save(recipeOutput);
    }

    private AVPFoodRecipes() {
        throw new UnsupportedOperationException();
    }
}
