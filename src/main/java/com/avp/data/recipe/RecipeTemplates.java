package com.avp.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.avp.data.recipe.builder.ShapedRecipeBuilder;

public class RecipeTemplates {

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> AXE =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemLike)
            .define('B', Items.STICK)
            .pattern("AA ")
            .pattern("AB ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> BARS_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> BOOTS = itemLike -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemLike)
        .pattern("A A")
        .pattern("A A");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> CHESTPLATE =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemLike)
            .pattern("A A")
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> COMPRESSED_BLOCK_2x2 =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AA")
            .pattern("AA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> COMPRESSED_BLOCK_3x3 =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> HELMET = itemLike -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemLike)
        .pattern("AAA")
        .pattern("A A");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> HOE =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemLike)
            .define('B', Items.STICK)
            .pattern("AA ")
            .pattern(" B ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> LEGGINGS =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemLike)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> PICKAXE =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemLike)
            .define('B', Items.STICK)
            .pattern("AAA")
            .pattern(" B ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> PLUS_CROSS =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern(" A ")
            .pattern("AAA")
            .pattern(" A ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> SHOVEL =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemLike)
            .define('B', Items.STICK)
            .pattern(" A ")
            .pattern(" B ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> SLAB_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> STAIR_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("A  ")
            .pattern("AA ")
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> SWORD =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemLike)
            .define('B', Items.STICK)
            .pattern(" A ")
            .pattern(" A ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> WALL_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> X_CROSS =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("A A")
            .pattern(" A ")
            .pattern("A A");
}
