package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.item.AVPFoodItems;

public final class AVPFoodRecipes {

    public static void addFoodRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.DORITOS.get())
            .define('A', Items.WHEAT)
            .define('B', Items.BAKED_POTATO)
            .pattern("AAA")
            .pattern("A B")
            .pattern("BBB")
            .unlockedBy("has_wheat", AVPRecipeProvider.has(Items.WHEAT))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, AVPFoodItems.DORITOS_COOL_RANCH.get())
            .define('A', AVPFoodItems.DORITOS.get())
            .define('B', Items.WHEAT)
            .pattern("AB")
            .pattern("B ")
            .unlockedBy("has_doritos", AVPRecipeProvider.has(AVPFoodItems.DORITOS.get()))
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
