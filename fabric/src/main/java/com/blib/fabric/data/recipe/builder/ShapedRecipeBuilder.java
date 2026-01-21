package com.blib.fabric.data.recipe.builder;

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.blib.fabric.data.recipe.util.RecipeProviderProxy;
import com.blib.internal.mixin.MixinShapedRecipeBuilder_Accessor;

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

    public ShapedRecipeBuilder define(char key, Supplier<? extends ItemLike> itemLikeSupplier) {
        return define(key, itemLikeSupplier.get());
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

    public void into(int count, Supplier<? extends ItemLike> destinationSupplier) {
        into(count, destinationSupplier.get());
    }

    public void into(int count, ItemLike destination) {
        into(new ItemStack(destination, count));
    }

    public void into(ItemStack destination) {
        var shapedRecipeBuilder = net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(
            recipeCategory,
            destination.getItem(),
            destination.getCount()
        );

        for (var transformation : transformations) {
            shapedRecipeBuilder = transformation.apply(shapedRecipeBuilder);
        }

        var destinationName = RecipeProviderProxy.getNameForItem(destination.getItem());

        if (customNameOperator != null) {
            destinationName = customNameOperator.apply(destinationName);
        }

        var resourceLocation = baseBuilder.getMod().resources().createLocation(destinationName);
        save(baseBuilder.getRecipeOutput(), shapedRecipeBuilder, destination, resourceLocation);
    }

    private void save(
        RecipeOutput recipeOutput,
        net.minecraft.data.recipes.ShapedRecipeBuilder shapedRecipeBuilder,
        ItemStack result,
        ResourceLocation id
    ) {
        var accessor = (MixinShapedRecipeBuilder_Accessor) shapedRecipeBuilder;
        var shapedRecipePattern = accessor.invokeEnsureValid(id);
        var builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        var criteria = accessor.getCriteria();

        Objects.requireNonNull(builder);
        criteria.forEach(builder::addCriterion);

        var shapedRecipe = new ShapedRecipe(
            Objects.requireNonNullElse(accessor.getGroup(), ""),
            net.minecraft.data.recipes.RecipeBuilder.determineBookCategory(accessor.getCategory()),
            shapedRecipePattern,
            result,
            accessor.getShowNotification()
        );

        recipeOutput.accept(
            id,
            shapedRecipe,
            builder.build(id.withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/"))
        );
    }
}
