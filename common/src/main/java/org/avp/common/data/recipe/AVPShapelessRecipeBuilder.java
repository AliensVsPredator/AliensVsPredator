package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.avp.api.GameObject;

public class AVPShapelessRecipeBuilder {

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike itemLike) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike));
    }

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, GameObject<? extends ItemLike> itemLikeGameObject) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLikeGameObject.get()));
    }

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, ItemLike itemLike, int count) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLike, count));
    }

    public static AVPShapelessRecipeBuilder shapeless(RecipeCategory recipeCategory, GameObject<? extends ItemLike> itemLikeGameObject, int count) {
        return new AVPShapelessRecipeBuilder(ShapelessRecipeBuilder.shapeless(recipeCategory, itemLikeGameObject.get(), count));
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

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, GameObject<? extends ItemLike> itemLikeGameObject) {
        return requiresAndUnlockIfHas(character, itemLikeGameObject.get());
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, ItemLike itemLike, int count) {
        shapelessRecipeBuilder.requires(itemLike, count);
        shapelessRecipeBuilder.unlockedBy("has_" + character, AVPRecipeProvider.has(itemLike));
        return this;
    }

    public AVPShapelessRecipeBuilder requiresAndUnlockIfHas(char character, GameObject<? extends ItemLike> itemLikeGameObject, int count) {
        return requiresAndUnlockIfHas(character, itemLikeGameObject.get(), count);
    }

    public void save(RecipeOutput recipeOutput) {
        shapelessRecipeBuilder.save(recipeOutput);
    }
}
