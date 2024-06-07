package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.avp.api.Holder;

import java.util.Arrays;

public class AVPShapelessRecipeBuilder {

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike itemLike) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike));
    }

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, Holder<? extends ItemLike> itemLikeHolder) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLikeHolder.get()));
    }

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike itemLike, int count) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike, count));
    }

    public static AVPShapelessRecipeBuilder shapeless(
        RecipeCategory recipeCategory,
        Holder<? extends ItemLike> itemLikeHolder,
        int count
    ) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLikeHolder.get(), count));
    }

    private final ShapelessRecipeBuilder shapelessRecipeBuilder;

    private AVPShapelessRecipeBuilder(ShapelessRecipeBuilder shapelessRecipeBuilder) {
        this.shapelessRecipeBuilder = shapelessRecipeBuilder;
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, ItemLike itemLike) {
        return requiresAndUnlockIfHas(character, itemLike, 1);
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, TagKey<Item> itemTag) {
        shapelessRecipeBuilder.requires(itemTag);
        shapelessRecipeBuilder.unlockedBy("has_" + character, AVPRecipeProvider.has(itemTag));
        return this;
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, Ingredient ingredient) {
        shapelessRecipeBuilder.requires(ingredient);
        var ingredientItemStacks = ingredient.getItems();
        for (int i = 0; i < ingredientItemStacks.length; i++) {
            var itemStack = ingredientItemStacks[0];
            shapelessRecipeBuilder.unlockedBy("has_" + character + "_" + i, AVPRecipeProvider.has(itemStack.getItem()));
        }
        return this;
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, Holder<? extends ItemLike> itemLikeHolder) {
        return requiresAndUnlockIfHas(character, itemLikeHolder.get());
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, ItemLike itemLike, int count) {
        shapelessRecipeBuilder.requires(itemLike, count);
        shapelessRecipeBuilder.unlockedBy("has_" + character, AVPRecipeProvider.has(itemLike));
        return this;
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, Holder<? extends ItemLike> itemLikeHolder, int count) {
        return requiresAndUnlockIfHas(character, itemLikeHolder.get(), count);
    }

    public void save(RecipeOutput recipeOutput) {
        shapelessRecipeBuilder.save(recipeOutput);
    }

    public void save(RecipeOutput recipeOutput, String name) {
        shapelessRecipeBuilder.save(recipeOutput, name);
    }
}
