package com.blib.fabric.data.recipe;

import com.compat.CommonItemTags;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.blib.fabric.data.recipe.builder.ShapedRecipeBuilder;

public class RecipeTemplates {

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> AXE =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemTagKey)
            .define('B', CommonItemTags.RODS_WOODEN)
            .pattern("AA")
            .pattern("AB")
            .pattern(" B");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> BARS_BLOCK =
        itemTagKey -> builder -> builder
            .define('A', itemTagKey)
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> BUTTON_BLOCK =
        itemTagKey -> builder -> builder
            .define('A', itemTagKey)
            .pattern("A")
            .pattern("A");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> BOOTS = itemLike -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemLike)
        .pattern("A A")
        .pattern("A A");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> BOOTS_TAG_FRIENDLY = itemTagKey -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemTagKey)
        .pattern("A A")
        .pattern("A A");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> CHESTPLATE =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemLike)
            .pattern("A A")
            .pattern("AAA")
            .pattern("AAA");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> CHESTPLATE_TAG_FRIENDLY =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemTagKey)
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

    public static final BiFunction<ItemLike, TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> COMPRESSED_BLOCK_3x3_TAG_FRIENDLY =
        (discriminator, itemTagKey) -> builder -> builder
            .define('A', discriminator)
            .define('B', itemTagKey)
            .pattern("BBB")
            .pattern("BAB")
            .pattern("BBB");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> DOOR_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AA")
            .pattern("AA")
            .pattern("AA");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> DOOR_BLOCK_TAG_FRIENDLY =
        itemTagKey -> builder -> builder
            .define('A', itemTagKey)
            .pattern("AA")
            .pattern("AA")
            .pattern("AA");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> HELMET = itemLike -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemLike)
        .pattern("AAA")
        .pattern("A A");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> HELMET_TAG_FRIENDLY = itemTagKey -> builder -> builder
        .withCategory(RecipeCategory.COMBAT)
        .define('A', itemTagKey)
        .pattern("AAA")
        .pattern("A A");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> HOE =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemTagKey)
            .define('B', CommonItemTags.RODS_WOODEN)
            .pattern("AA")
            .pattern(" B")
            .pattern(" B");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> LEGGINGS =
        itemLike -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemLike)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> LEGGINGS_TAG_FRIENDLY =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.COMBAT)
            .define('A', itemTagKey)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> PICKAXE =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemTagKey)
            .define('B', CommonItemTags.RODS_WOODEN)
            .pattern("AAA")
            .pattern(" B ")
            .pattern(" B ");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> PLUS_CROSS =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern(" A ")
            .pattern("AAA")
            .pattern(" A ");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> PRESSURE_PLATE_BLOCK =
        itemTagKey -> builder -> builder
            .define('A', itemTagKey)
            .pattern("AA");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> SHOVEL =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemTagKey)
            .define('B', CommonItemTags.RODS_WOODEN)
            .pattern("A")
            .pattern("B")
            .pattern("B");

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

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> SWORD =
        itemTagKey -> builder -> builder
            .withCategory(RecipeCategory.TOOLS)
            .define('A', itemTagKey)
            .define('B', CommonItemTags.RODS_WOODEN)
            .pattern("A")
            .pattern("A")
            .pattern("B");

    public static final Function<ItemLike, UnaryOperator<ShapedRecipeBuilder>> TRAP_DOOR_BLOCK =
        itemLike -> builder -> builder
            .define('A', itemLike)
            .pattern("AA")
            .pattern("AA");

    public static final Function<TagKey<Item>, UnaryOperator<ShapedRecipeBuilder>> TRAP_DOOR_BLOCK_TAG_FRIENDLY =
        itemTagKey -> builder -> builder
            .define('A', itemTagKey)
            .pattern("AA")
            .pattern("AA");

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
