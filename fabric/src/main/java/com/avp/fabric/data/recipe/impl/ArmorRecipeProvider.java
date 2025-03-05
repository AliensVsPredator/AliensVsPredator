package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.core.common.item.AVPItemTags;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.builder.ShapedRecipeBuilder;

public class ArmorRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createMk50ArmorSetRecipes(builder);
        createPressureArmorSetRecipes(builder);
        createTacticalArmorSetRecipes(builder);
        createPlatedChitinArmorSetRecipes(builder);
        createPlatedNetherChitinArmorSetRecipes(builder);

        createStandardArmorSetRecipes(
            builder,
            AVPItems.CHITIN,
            ArmorItems.CHITIN_HELMET,
            ArmorItems.CHITIN_CHESTPLATE,
            ArmorItems.CHITIN_LEGGINGS,
            ArmorItems.CHITIN_BOOTS
        );
        createStandardArmorSetRecipes(
            builder,
            AVPItems.NETHER_CHITIN,
            ArmorItems.NETHER_CHITIN_HELMET,
            ArmorItems.NETHER_CHITIN_CHESTPLATE,
            ArmorItems.NETHER_CHITIN_LEGGINGS,
            ArmorItems.NETHER_CHITIN_BOOTS
        );
        createStandardArmorSetRecipes(
            builder,
            AVPItems.STEEL_INGOT,
            ArmorItems.STEEL_HELMET,
            ArmorItems.STEEL_CHESTPLATE,
            ArmorItems.STEEL_LEGGINGS,
            ArmorItems.STEEL_BOOTS
        );
        createStandardArmorSetRecipes(
            builder,
            AVPItems.TITANIUM_INGOT,
            ArmorItems.TITANIUM_HELMET,
            ArmorItems.TITANIUM_CHESTPLATE,
            ArmorItems.TITANIUM_LEGGINGS,
            ArmorItems.TITANIUM_BOOTS
        );
    }

    private static void createPlatedNetherChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.NETHER_CHITIN_HELMET)
            .requires(1, AVPItems.PLATED_NETHER_CHITIN)
            .into(1, ArmorItems.PLATED_NETHER_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.NETHER_CHITIN_CHESTPLATE)
            .requires(1, AVPItems.PLATED_NETHER_CHITIN)
            .into(1, ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.NETHER_CHITIN_LEGGINGS)
            .requires(1, AVPItems.PLATED_NETHER_CHITIN)
            .into(1, ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.NETHER_CHITIN_BOOTS)
            .requires(1, AVPItems.PLATED_NETHER_CHITIN)
            .into(1, ArmorItems.PLATED_NETHER_CHITIN_BOOTS);
    }

    private static void createPlatedChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.CHITIN_HELMET)
            .requires(1, AVPItems.PLATED_CHITIN)
            .into(1, ArmorItems.PLATED_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.CHITIN_CHESTPLATE)
            .requires(1, AVPItems.PLATED_CHITIN)
            .into(1, ArmorItems.PLATED_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.CHITIN_LEGGINGS)
            .requires(1, AVPItems.PLATED_CHITIN)
            .into(1, ArmorItems.PLATED_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, ArmorItems.CHITIN_BOOTS)
            .requires(1, AVPItems.PLATED_CHITIN)
            .into(1, ArmorItems.PLATED_CHITIN_BOOTS);
    }

    private static void createMk50ArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> mk50ArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('C', AVPItems.LEAD_INGOT);

        mk50ArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', AVPItems.CARBON_DUST)
            .pattern("CAC")
            .pattern("B B")
            .pattern("AEA")
            .into(1, ArmorItems.MK50_HELMET);

        mk50ArmorBuilder.get()
            .define('D', Items.LEATHER)
            .pattern("A A")
            .pattern("CAC")
            .pattern("DAD")
            .into(1, ArmorItems.MK50_CHESTPLATE);

        mk50ArmorBuilder.get()
            .define('D', Items.LEATHER)
            .pattern("CDC")
            .pattern("D D")
            .pattern("A A")
            .into(1, ArmorItems.MK50_LEGGINGS);

        mk50ArmorBuilder.get()
            .pattern("C C")
            .pattern("A A")
            .into(1, ArmorItems.MK50_BOOTS);
    }

    private static void createPressureArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> pressureArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.ALUMINUM_INGOT);

        pressureArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', AVPItems.CARBON_DUST)
            .pattern("AAA")
            .pattern("B B")
            .pattern("EEE")
            .into(1, ArmorItems.PRESSURE_HELMET);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("A A")
            .pattern("ADA")
            .pattern("DAD")
            .into(1, ArmorItems.PRESSURE_CHESTPLATE);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("ADA")
            .pattern("D D")
            .pattern("A A")
            .into(1, ArmorItems.PRESSURE_LEGGINGS);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("D D")
            .pattern("A A")
            .into(1, ArmorItems.PRESSURE_BOOTS);
    }

    private static void createTacticalArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> tacticalArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', AVPItems.STEEL_INGOT)
            .define('B', ItemTags.WOOL);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("ABA")
            .pattern("C C")
            .into(1, ArmorItems.TACTICAL_HELMET);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("C C")
            .pattern("ABA")
            .pattern("CAC")
            .into(1, ArmorItems.TACTICAL_CHESTPLATE);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("CBC")
            .pattern("A A")
            .pattern("C C")
            .into(1, ArmorItems.TACTICAL_LEGGINGS);

        tacticalArmorBuilder.get()
            .pattern("B B")
            .pattern("A A")
            .into(1, ArmorItems.TACTICAL_BOOTS);
    }

    private static void createStandardArmorSetRecipes(
        RecipeBuilder builder,
        Supplier<? extends ItemLike> baseSupplier,
        Supplier<Item> helmetSupplier,
        Supplier<Item> chestplateSupplier,
        Supplier<Item> leggingsSupplier,
        Supplier<Item> bootsSupplier
    ) {
        var base = baseSupplier.get();

        builder.shaped()
            .apply(RecipeTemplates.HELMET.apply(base))
            .into(1, helmetSupplier);

        builder.shaped()
            .apply(RecipeTemplates.CHESTPLATE.apply(base))
            .into(1, chestplateSupplier);

        builder.shaped()
            .apply(RecipeTemplates.LEGGINGS.apply(base))
            .into(1, leggingsSupplier);

        builder.shaped()
            .apply(RecipeTemplates.BOOTS.apply(base))
            .into(1, bootsSupplier);
    }
}
