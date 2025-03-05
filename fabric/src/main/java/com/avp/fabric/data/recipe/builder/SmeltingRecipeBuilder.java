package com.avp.fabric.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.avp.core.AVP;
import com.avp.fabric.data.recipe.util.RecipeProviderProxy;

public class SmeltingRecipeBuilder {

    private final RecipeBuilder recipeBuilder;

    private final ItemLike source;

    private RecipeCategory recipeCategory;

    private float experience;

    private int cookTime;

    private UnaryOperator<String> customNameOperator;

    SmeltingRecipeBuilder(RecipeBuilder recipeBuilder, ItemLike source) {
        this.recipeBuilder = recipeBuilder;
        this.source = source;
        this.recipeCategory = RecipeCategory.MISC;
        this.cookTime = 200; // 10 seconds
    }

    public SmeltingRecipeBuilder withCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public SmeltingRecipeBuilder withExperience(float experience) {
        this.experience = experience;
        return this;
    }

    public SmeltingRecipeBuilder withCookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public SmeltingRecipeBuilder withCustomName(UnaryOperator<String> customNameOperator) {
        this.customNameOperator = customNameOperator;
        return this;
    }

    public void into(Supplier<? extends ItemLike> supplier) {
        into(supplier.get());
    }

    public void into(ItemLike destination) {
        var ingredient = Ingredient.of(source);
        var sourceName = RecipeProviderProxy.getNameForItem(source);
        var destinationName = RecipeProviderProxy.getNameForItem(destination);
        var customName = customNameOperator == null
            ? destinationName + "_from_smelting_" + sourceName
            : customNameOperator.apply(destinationName);

        SimpleCookingRecipeBuilder.smelting(ingredient, recipeCategory, destination, experience, cookTime)
            .unlockedBy("has_" + sourceName, RecipeProviderProxy.has(source))
            .save(recipeBuilder.getRecipeOutput(), AVP.MOD_ID + ":" + customName);
    }
}
