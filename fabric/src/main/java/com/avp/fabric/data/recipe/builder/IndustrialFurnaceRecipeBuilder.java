package com.avp.fabric.data.recipe.builder;

import com.human.common.gameplay.recipe.IndustrialFurnaceRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.avp.AVP;

import static net.minecraft.data.recipes.RecipeProvider.has;

public class IndustrialFurnaceRecipeBuilder implements RecipeBuilder {

    private RecipeCategory category;

    private CookingBookCategory bookCategory;

    private Item result;

    private Ingredient ingredient;

    private float experience;

    private int cookingTime;

    private Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Nullable
    private String group;

    private IndustrialFurnaceRecipeBuilder(
        RecipeCategory recipeCategory,
        CookingBookCategory cookingBookCategory,
        ItemLike itemLike,
        Ingredient ingredient,
        float experience,
        int cookingTime
    ) {
        this.category = recipeCategory;
        this.bookCategory = cookingBookCategory;
        this.result = itemLike.asItem();
        this.ingredient = ingredient;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public static IndustrialFurnaceRecipeBuilder smelting(
        Ingredient ingredient,
        RecipeCategory recipeCategory,
        ItemLike itemLike,
        float experience,
        int cookingTime
    ) {
        return new IndustrialFurnaceRecipeBuilder(
            recipeCategory,
            determineSmeltingRecipeCategory(itemLike),
            itemLike,
            ingredient,
            experience,
            cookingTime
        );
    }

    public IndustrialFurnaceRecipeBuilder withCategory(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public IndustrialFurnaceRecipeBuilder into(ItemLike itemLike) {
        this.result = itemLike.asItem();
        return this;
    }

    public IndustrialFurnaceRecipeBuilder withExperience(float experience) {
        this.experience = experience;
        return this;
    }

    public IndustrialFurnaceRecipeBuilder withCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public IndustrialFurnaceRecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    public IndustrialFurnaceRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    public Item getResult() {
        return this.result;
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        Advancement.Builder builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
            .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
            .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        IndustrialFurnaceRecipe recipe = new IndustrialFurnaceRecipe(
            Objects.requireNonNullElse(this.group, ""),
            this.bookCategory,
            this.ingredient,
            new ItemStack(this.result),
            this.experience,
            this.cookingTime
        );
        recipeOutput.accept(
            resourceLocation,
            recipe,
            builder.build(resourceLocation.withPrefix("recipes/" + this.category.getFolderName() + "/"))
        );
    }

    public void saveWithSuffix(RecipeOutput recipeOutput, String suffix) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(
            AVP.MOD_ID,
            net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this.result).getPath() + "_from_" +
                ingredient.getItems()[0].getDescriptionId() + suffix
        );

        this.save(recipeOutput, resourceLocation);
    }

    private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike itemLike) {
        if (itemLike.asItem().components().has(DataComponents.FOOD)) {
            return CookingBookCategory.FOOD;
        } else {
            return itemLike.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
        }
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }

    public static void ensureRegistration(RecipeOutput recipeOutput) {
        for (Map.Entry<Item, Item> entry : IndustrialFurnaceRecipe.MELTING_RECIPES.entrySet()) {
            Item input = entry.getKey();
            Item output = entry.getValue();

            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                AVP.MOD_ID,
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output).getPath() +
                    "_from_" +
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input).getPath() +
                    "_industrial_furnace"
            );

            IndustrialFurnaceRecipe recipe = new IndustrialFurnaceRecipe(
                "",
                CookingBookCategory.BLOCKS,
                Ingredient.of(input),
                new ItemStack(output),
                0.1f,
                100
            );

            Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);

            advancementBuilder.addCriterion("has_item", has(input));

            recipeOutput.accept(
                recipeId,
                recipe,
                advancementBuilder.build(recipeId.withPrefix("recipes/building_blocks/"))
            );
        }
    }
}
