package org.avp.common.data.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.avp.common.AVPConstants;

public class AVPRecipeBuilder {

    public static AVPRecipeBuilder with(RecipeOutput recipeOutput) {
        return new AVPRecipeBuilder(recipeOutput);
    }

    private final RecipeOutput recipeOutput;

    private AVPRecipeBuilder(RecipeOutput recipeOutput) {
        this.recipeOutput = recipeOutput;
    }

    public AVPBlastingRecipeBuilder blast(ItemLike source) {
        return new AVPBlastingRecipeBuilder(this, source);
    }

    public AVPShapedRecipeBuilder shape() {
        return new AVPShapedRecipeBuilder(this);
    }

    public AVPShapelessRecipeBuilder shapeless() {
        return new AVPShapelessRecipeBuilder(this);
    }

    public AVPSmeltingRecipeBuilder smelt(ItemLike source) {
        return new AVPSmeltingRecipeBuilder(this, source);
    }

    public AVPStonecutterRecipeBuilder stonecut(ItemLike source) {
        return new AVPStonecutterRecipeBuilder(this, source);
    }

    public RecipeOutput getRecipeOutput() {
        return recipeOutput;
    }

    private static String getNameForItem(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }

    public static class AVPBlastingRecipeBuilder {

        private final AVPRecipeBuilder avpRecipeBuilder;

        private final ItemLike source;

        private RecipeCategory recipeCategory;

        private float experience;

        private int cookTime;

        private AVPBlastingRecipeBuilder(AVPRecipeBuilder avpRecipeBuilder, ItemLike source) {
            this.avpRecipeBuilder = avpRecipeBuilder;
            this.source = source;
            this.recipeCategory = RecipeCategory.MISC;
            this.cookTime = 100; // 5 seconds
        }

        public AVPBlastingRecipeBuilder withCategory(RecipeCategory recipeCategory) {
            this.recipeCategory = recipeCategory;
            return this;
        }

        public AVPBlastingRecipeBuilder withExperience(float experience) {
            this.experience = experience;
            return this;
        }

        public AVPBlastingRecipeBuilder withCookTime(int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        public void into(ItemLike destination) {
            var ingredient = Ingredient.of(source);
            var sourceName = BuiltInRegistries.ITEM.getKey(source.asItem()).getPath();
            var destinationName = BuiltInRegistries.ITEM.getKey(destination.asItem()).getPath();

            SimpleCookingRecipeBuilder.blasting(ingredient, recipeCategory, destination, experience, cookTime)
                .unlockedBy("has_ingredient", AVPRecipeProvider.has(source))
                .save(avpRecipeBuilder.getRecipeOutput(), AVPConstants.MOD_ID + ":" + sourceName + "_from_blasting_" + destinationName);
        }
    }

    public static class AVPShapedRecipeBuilder {

        private final AVPRecipeBuilder avpRecipeBuilder;

        private final List<UnaryOperator<ShapedRecipeBuilder>> transformations;

        private RecipeCategory recipeCategory;

        private AVPShapedRecipeBuilder(AVPRecipeBuilder avpRecipeBuilder) {
            this.avpRecipeBuilder = avpRecipeBuilder;
            this.transformations = new ArrayList<>();
            this.recipeCategory = RecipeCategory.MISC;
        }

        public AVPShapedRecipeBuilder withCategory(RecipeCategory recipeCategory) {
            this.recipeCategory = recipeCategory;
            return this;
        }

        public AVPShapedRecipeBuilder define(char key, ItemLike ingredient) {
            transformations.add((shapedRecipeBuilder -> {
                shapedRecipeBuilder.define(key, ingredient);
                shapedRecipeBuilder.unlockedBy("has_" + key, AVPRecipeProvider.has(ingredient));
                return shapedRecipeBuilder;
            }));
            return this;
        }

        public AVPShapedRecipeBuilder define(char key, TagKey<Item> itemTagKey) {
            transformations.add((shapedRecipeBuilder -> {
                shapedRecipeBuilder.define(key, itemTagKey);
                shapedRecipeBuilder.unlockedBy("has_" + key, AVPRecipeProvider.has(itemTagKey));
                return shapedRecipeBuilder;
            }));
            return this;
        }

        public AVPShapedRecipeBuilder pattern(String pattern) {
            this.transformations.add((shapedRecipeBuilder -> {
                shapedRecipeBuilder.pattern(pattern);
                return shapedRecipeBuilder;
            }));
            return this;
        }

        public void into(int count, ItemLike destination) {
            var builder = ShapedRecipeBuilder.shaped(recipeCategory, destination, count);

            for (var transformation : transformations) {
                builder = transformation.apply(builder);
            }

            builder.save(avpRecipeBuilder.getRecipeOutput());
        }
    }

    public static class AVPShapelessRecipeBuilder {

        private final AVPRecipeBuilder avpRecipeBuilder;

        private final List<UnaryOperator<ShapelessRecipeBuilder>> transformations;

        private RecipeCategory recipeCategory;

        private AVPShapelessRecipeBuilder(AVPRecipeBuilder avpRecipeBuilder) {
            this.avpRecipeBuilder = avpRecipeBuilder;
            this.transformations = new ArrayList<>();
            this.recipeCategory = RecipeCategory.MISC;
        }

        public AVPShapelessRecipeBuilder withCategory(RecipeCategory recipeCategory) {
            this.recipeCategory = recipeCategory;
            return this;
        }

        public AVPShapelessRecipeBuilder requires(char key, int count, Ingredient ingredient) {
            transformations.add((shapelessRecipeBuilder -> {
                shapelessRecipeBuilder.requires(ingredient, count);
                var ingredientItemStacks = ingredient.getItems();
                for (int i = 0; i < ingredientItemStacks.length; i++) {
                    var itemStack = ingredientItemStacks[0];
                    shapelessRecipeBuilder.unlockedBy("has_" + key + "_" + i, AVPRecipeProvider.has(itemStack.getItem()));
                }
                return shapelessRecipeBuilder;
            }));
            return this;
        }

        public AVPShapelessRecipeBuilder requires(char key, int count, ItemLike ingredient) {
            transformations.add((shapelessRecipeBuilder -> {
                shapelessRecipeBuilder.requires(ingredient, count);
                shapelessRecipeBuilder.unlockedBy("has_" + key, AVPRecipeProvider.has(ingredient));
                return shapelessRecipeBuilder;
            }));
            return this;
        }

        public AVPShapelessRecipeBuilder requires(char key, int count, TagKey<Item> itemTagKey) {
            transformations.add((shapelessRecipeBuilder -> {
                shapelessRecipeBuilder.requires(Ingredient.of(itemTagKey), count);
                shapelessRecipeBuilder.unlockedBy("has_" + key, AVPRecipeProvider.has(itemTagKey));
                return shapelessRecipeBuilder;
            }));
            return this;
        }

        public void into(int count, ItemLike destination) {
            var builder = ShapelessRecipeBuilder.shapeless(recipeCategory, destination, count);

            for (var transformation : transformations) {
                builder = transformation.apply(builder);
            }

            builder.save(avpRecipeBuilder.getRecipeOutput());
        }
    }

    public static class AVPSmeltingRecipeBuilder {

        private final AVPRecipeBuilder avpRecipeBuilder;

        private final ItemLike source;

        private RecipeCategory recipeCategory;

        private float experience;

        private int cookTime;

        private AVPSmeltingRecipeBuilder(AVPRecipeBuilder avpRecipeBuilder, ItemLike source) {
            this.avpRecipeBuilder = avpRecipeBuilder;
            this.source = source;
            this.recipeCategory = RecipeCategory.MISC;
            this.cookTime = 200; // 10 seconds
        }

        public AVPSmeltingRecipeBuilder withCategory(RecipeCategory recipeCategory) {
            this.recipeCategory = recipeCategory;
            return this;
        }

        public AVPSmeltingRecipeBuilder withExperience(float experience) {
            this.experience = experience;
            return this;
        }

        public AVPSmeltingRecipeBuilder withCookTime(int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        public void into(ItemLike destination) {
            var ingredient = Ingredient.of(source);
            var sourceName = getNameForItem(source);
            var destinationName = getNameForItem(destination);

            SimpleCookingRecipeBuilder.smelting(ingredient, recipeCategory, destination, experience, cookTime)
                .unlockedBy("has_ingredient", AVPRecipeProvider.has(source))
                .save(avpRecipeBuilder.getRecipeOutput(), AVPConstants.MOD_ID + ":" + sourceName + "_from_smelting_" + destinationName);
        }
    }

    public static class AVPStonecutterRecipeBuilder {

        private final AVPRecipeBuilder avpRecipeBuilder;

        private final ItemLike source;

        private RecipeCategory recipeCategory;

        private AVPStonecutterRecipeBuilder(AVPRecipeBuilder avpRecipeBuilder, ItemLike source) {
            this.avpRecipeBuilder = avpRecipeBuilder;
            this.source = source;
            this.recipeCategory = RecipeCategory.MISC;
        }

        public AVPStonecutterRecipeBuilder withCategory(RecipeCategory recipeCategory) {
            this.recipeCategory = recipeCategory;
            return this;
        }

        public void into(int count, ItemLike destination) {
            AVPRecipeProvider.stonecutterRecipeFromBase(
                avpRecipeBuilder.getRecipeOutput(),
                recipeCategory,
                destination.asItem(),
                source.asItem(),
                count
            );
        }
    }
}
