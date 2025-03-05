package com.avp.fabric.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.avp.fabric.data.recipe.util.RecipeProviderProxy;

public class ShapelessRecipeBuilder {

    private final RecipeBuilder baseBuilder;

    private final List<UnaryOperator<net.minecraft.data.recipes.ShapelessRecipeBuilder>> transformations;

    private RecipeCategory recipeCategory;

    private UnaryOperator<String> customNameOperator;

    ShapelessRecipeBuilder(RecipeBuilder baseBuilder) {
        this.baseBuilder = baseBuilder;
        this.transformations = new ArrayList<>();
        this.recipeCategory = RecipeCategory.MISC;
    }

    public ShapelessRecipeBuilder withCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public ShapelessRecipeBuilder withCustomName(UnaryOperator<String> customNameOperator) {
        this.customNameOperator = customNameOperator;
        return this;
    }

    public ShapelessRecipeBuilder requires(int count, Ingredient ingredient) {
        transformations.add((shapelessRecipeBuilder -> {
            shapelessRecipeBuilder.requires(ingredient, count);
            var ingredientItemStacks = Arrays.stream(ingredient.getItems())
                .sorted(Comparator.comparing(itemStackA -> RecipeProviderProxy.getNameForItem(itemStackA.getItem())))
                .toList();

            for (var itemStack : ingredientItemStacks) {
                var itemName = RecipeProviderProxy.getNameForItem(itemStack.getItem());
                shapelessRecipeBuilder.unlockedBy("has_" + itemName, RecipeProviderProxy.has(itemStack.getItem()));
            }

            return shapelessRecipeBuilder;
        }));
        return this;
    }

    public ShapelessRecipeBuilder requires(int count, Supplier<? extends ItemLike> supplier) {
        return requires(count, supplier.get());
    }

    public ShapelessRecipeBuilder requires(int count, ItemLike itemLike) {
        transformations.add((shapelessRecipeBuilder -> {
            var itemName = RecipeProviderProxy.getNameForItem(itemLike.asItem());
            shapelessRecipeBuilder.requires(itemLike, count);
            shapelessRecipeBuilder.unlockedBy("has_" + itemName, RecipeProviderProxy.has(itemLike));
            return shapelessRecipeBuilder;
        }));
        return this;
    }

    public ShapelessRecipeBuilder requires(int count, TagKey<Item> itemTagKey) {
        transformations.add((shapelessRecipeBuilder -> {
            shapelessRecipeBuilder.requires(Ingredient.of(itemTagKey), count);
            shapelessRecipeBuilder.unlockedBy("has_" + itemTagKey.location().getPath(), RecipeProviderProxy.has(itemTagKey));
            return shapelessRecipeBuilder;
        }));
        return this;
    }

    public void into(int count, Supplier<? extends ItemLike> supplier) {
        into(count, supplier.get());
    }

    public void into(int count, ItemLike destination) {
        var builder = net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless(recipeCategory, destination, count);

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
