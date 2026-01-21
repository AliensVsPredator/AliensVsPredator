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
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.blib.fabric.data.recipe.util.RecipeProviderProxy;
import com.blib.internal.mixin.MixinShapelessRecipeBuilder_Accessor;

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

    public ShapelessRecipeBuilder requires(int count, Supplier<? extends ItemLike> itemLikeSupplier) {
        return requires(count, itemLikeSupplier.get());
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

    public void into(int count, Supplier<? extends ItemLike> destinationSupplier) {
        into(count, destinationSupplier.get());
    }

    public void into(int count, ItemLike destination) {
        into(new ItemStack(destination, count));
    }

    public void into(ItemStack destination) {
        var shapelessRecipeBuilder = net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless(
            recipeCategory,
            destination.getItem(),
            destination.getCount()
        );

        for (var transformation : transformations) {
            shapelessRecipeBuilder = transformation.apply(shapelessRecipeBuilder);
        }

        var destinationName = RecipeProviderProxy.getNameForItem(destination.getItem());

        if (customNameOperator != null) {
            destinationName = customNameOperator.apply(destinationName);
        }

        var resourceLocation = baseBuilder.getMod().resources().createLocation(destinationName);
        save(baseBuilder.getRecipeOutput(), shapelessRecipeBuilder, destination, resourceLocation);
    }

    private void save(
        RecipeOutput recipeOutput,
        net.minecraft.data.recipes.ShapelessRecipeBuilder shapelessRecipeBuilder,
        ItemStack result,
        ResourceLocation id
    ) {
        var accessor = (MixinShapelessRecipeBuilder_Accessor) shapelessRecipeBuilder;

        accessor.invokeEnsureValid(id);

        var builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        var criteria = accessor.getCriteria();

        Objects.requireNonNull(builder);
        criteria.forEach(builder::addCriterion);

        var shapelessRecipe = new ShapelessRecipe(
            Objects.requireNonNullElse(accessor.getGroup(), ""),
            net.minecraft.data.recipes.RecipeBuilder.determineBookCategory(accessor.getCategory()),
            result,
            accessor.getIngredients()
        );

        recipeOutput.accept(
            id,
            shapelessRecipe,
            builder.build(id.withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/"))
        );
    }
}
