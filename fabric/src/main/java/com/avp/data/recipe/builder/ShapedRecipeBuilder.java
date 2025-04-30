package com.avp.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import com.avp.data.recipe.util.RecipeProviderProxy;

public class ShapedRecipeBuilder {

    private final RecipeBuilder baseBuilder;

    private final List<UnaryOperator<net.minecraft.data.recipes.ShapedRecipeBuilder>> transformations;

    private RecipeCategory recipeCategory;

    private UnaryOperator<String> customNameOperator;

    ShapedRecipeBuilder(RecipeBuilder baseBuilder) {
        this.baseBuilder = baseBuilder;
        this.transformations = new ArrayList<>();
        this.recipeCategory = RecipeCategory.MISC;
    }

    public ShapedRecipeBuilder withCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public ShapedRecipeBuilder withCustomName(UnaryOperator<String> customNameOperator) {
        this.customNameOperator = customNameOperator;
        return this;
    }

    public ShapedRecipeBuilder apply(UnaryOperator<ShapedRecipeBuilder> unaryOperator) {
        return unaryOperator.apply(this);
    }

    public ShapedRecipeBuilder define(char key, ItemLike itemLike) {
        transformations.add((shapedRecipeBuilder -> {
            var itemName = RecipeProviderProxy.getNameForItem(itemLike.asItem());

            shapedRecipeBuilder.define(key, itemLike);
            shapedRecipeBuilder.unlockedBy("has_" + itemName, RecipeProviderProxy.has(itemLike));
            return shapedRecipeBuilder;
        }));
        return this;
    }

    public ShapedRecipeBuilder define(char key, TagKey<Item> itemTagKey) {
        transformations.add((shapedRecipeBuilder -> {
            shapedRecipeBuilder.define(key, itemTagKey);
            shapedRecipeBuilder.unlockedBy("has_" + itemTagKey.location().getPath(), RecipeProviderProxy.has(itemTagKey));
            return shapedRecipeBuilder;
        }));
        return this;
    }

    public ShapedRecipeBuilder define(char key, Ingredient ingredient) {
        transformations.add((shapedRecipeBuilder -> {
            shapedRecipeBuilder.define(key, ingredient);
            Arrays.stream(ingredient.getItems()).forEach(itemStack -> {
                var itemLike = itemStack.getItem();
                var itemName = RecipeProviderProxy.getNameForItem(itemLike.asItem());
                shapedRecipeBuilder.unlockedBy("has_" + itemName, RecipeProviderProxy.has(itemLike));
            });
            return shapedRecipeBuilder;
        }));
        return this;
    }

    public ShapedRecipeBuilder pattern(String pattern) {
        this.transformations.add((shapedRecipeBuilder -> {
            shapedRecipeBuilder.pattern(pattern);
            return shapedRecipeBuilder;
        }));
        return this;
    }

    public void into(int count, ItemLike destination) {
        var builder = net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(recipeCategory, destination, count);

        for (var transformation : transformations) {
            builder = transformation.apply(builder);
        }

        if (customNameOperator == null) {
            builder.save(baseBuilder.getRecipeOutput());
        } else {
            var destinationName = RecipeProviderProxy.getNameForItem(destination);
            builder.save(baseBuilder.getRecipeOutput(), customNameOperator.apply(destinationName));
        }
    }
}
