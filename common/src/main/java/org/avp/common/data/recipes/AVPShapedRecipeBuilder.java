package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.avp.api.GameObject;

public class AVPShapedRecipeBuilder {

    public static AVPShapedRecipeBuilder shaped(RecipeCategory recipeCategory, ItemLike itemLike) {
        return new AVPShapedRecipeBuilder(ShapedRecipeBuilder.shaped(recipeCategory, itemLike));
    }

    public static AVPShapedRecipeBuilder shaped(RecipeCategory recipeCategory, GameObject<? extends ItemLike> itemLikeGameObject) {
        return new AVPShapedRecipeBuilder(ShapedRecipeBuilder.shaped(recipeCategory, itemLikeGameObject.get()));
    }

    public static AVPShapedRecipeBuilder shaped(RecipeCategory recipeCategory, ItemLike itemLike, int count) {
        return new AVPShapedRecipeBuilder(ShapedRecipeBuilder.shaped(recipeCategory, itemLike, count));
    }

    public static AVPShapedRecipeBuilder shaped(RecipeCategory recipeCategory, GameObject<? extends ItemLike> itemLikeGameObject, int count) {
        return new AVPShapedRecipeBuilder(ShapedRecipeBuilder.shaped(recipeCategory, itemLikeGameObject.get(), count));
    }

    private final ShapedRecipeBuilder shapedRecipeBuilder;

    private AVPShapedRecipeBuilder(ShapedRecipeBuilder shapedRecipeBuilder) {
        this.shapedRecipeBuilder = shapedRecipeBuilder;
    }

    public AVPShapedRecipeBuilder defineAndUnlockIfHas(char character, ItemLike itemLike) {
        shapedRecipeBuilder.define(character, itemLike);
        shapedRecipeBuilder.unlockedBy("has_" + character, AVPRecipeProvider.has(itemLike));
        return this;
    }

    public AVPShapedRecipeBuilder defineAndUnlockIfHas(char character, TagKey<Item> itemTag) {
        shapedRecipeBuilder.define(character, itemTag);
        shapedRecipeBuilder.unlockedBy("has_" + character, AVPRecipeProvider.has(itemTag));
        return this;
    }

    public AVPShapedRecipeBuilder defineAndUnlockIfHas(char character, GameObject<? extends ItemLike> itemLikeGameObject) {
        return defineAndUnlockIfHas(character, itemLikeGameObject.get());
    }

    public AVPShapedRecipeBuilder pattern(String pattern) {
        shapedRecipeBuilder.pattern(pattern);
        return this;
    }

    public void save(RecipeOutput recipeOutput) {
        shapedRecipeBuilder.save(recipeOutput);
    }
}
