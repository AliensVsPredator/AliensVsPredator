package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.AVPItemTags;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.builder.ShapedRecipeBuilder;

public class ArmorRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createMk50ArmorSetRecipes(builder);
        createPressureArmorSetRecipes(builder);
        createTacticalArmorSetRecipes(builder);
        createPlatedChitinArmorSetRecipes(builder);
        createPlatedAberrantChitinArmorSetRecipes(builder);
        // TODO: Re-implement these at some point in the future.
        // createPlatedIrradiatedChitinArmorSetRecipes(builder);
        createPlatedNetherChitinArmorSetRecipes(builder);

        createStandardArmorSetRecipes(
            builder,
            TempAVPItems.ABERRANT_CHITIN.get(),
            AVPArmorItems.ABERRANT_CHITIN_HELMET.get(),
            AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
            AVPArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
            AVPArmorItems.ABERRANT_CHITIN_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            TempAVPItems.CHITIN.get(),
            AVPArmorItems.CHITIN_HELMET.get(),
            AVPArmorItems.CHITIN_CHESTPLATE.get(),
            AVPArmorItems.CHITIN_LEGGINGS.get(),
            AVPArmorItems.CHITIN_BOOTS.get()
        );
        // TODO: Re-implement these at some point in the future.
        // createStandardArmorSetRecipes(
        // builder,
        // AVPItems.IRRADIATED_CHITIN,
        // ArmorItems.IRRADIATED_CHITIN_HELMET,
        // ArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
        // ArmorItems.IRRADIATED_CHITIN_LEGGINGS,
        // ArmorItems.IRRADIATED_CHITIN_BOOTS
        // );
        createStandardArmorSetRecipes(
            builder,
            TempAVPItems.NETHER_CHITIN.get(),
            AVPArmorItems.NETHER_CHITIN_HELMET.get(),
            AVPArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
            AVPArmorItems.NETHER_CHITIN_LEGGINGS.get(),
            AVPArmorItems.NETHER_CHITIN_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            TempAVPItems.STEEL_INGOT.get(),
            AVPArmorItems.STEEL_HELMET.get(),
            AVPArmorItems.STEEL_CHESTPLATE.get(),
            AVPArmorItems.STEEL_LEGGINGS.get(),
            AVPArmorItems.STEEL_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            TempAVPItems.TITANIUM_INGOT.get(),
            AVPArmorItems.TITANIUM_HELMET.get(),
            AVPArmorItems.TITANIUM_CHESTPLATE.get(),
            AVPArmorItems.TITANIUM_LEGGINGS.get(),
            AVPArmorItems.TITANIUM_BOOTS.get()
        );
    }

    private static void createPlatedAberrantChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.ABERRANT_CHITIN_HELMET)
            .requires(1, TempAVPItems.PLATED_ABERRANT_CHITIN)
            .into(1, AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE)
            .requires(1, TempAVPItems.PLATED_ABERRANT_CHITIN)
            .into(1, AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.ABERRANT_CHITIN_LEGGINGS)
            .requires(1, TempAVPItems.PLATED_ABERRANT_CHITIN)
            .into(1, AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.ABERRANT_CHITIN_BOOTS)
            .requires(1, TempAVPItems.PLATED_ABERRANT_CHITIN)
            .into(1, AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);
    }

    private static void createPlatedIrradiatedChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.IRRADIATED_CHITIN_HELMET)
            .requires(1, TempAVPItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE)
            .requires(1, TempAVPItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS)
            .requires(1, TempAVPItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.IRRADIATED_CHITIN_BOOTS)
            .requires(1, TempAVPItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);
    }

    private static void createPlatedNetherChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.NETHER_CHITIN_HELMET)
            .requires(1, TempAVPItems.PLATED_NETHER_CHITIN)
            .into(1, AVPArmorItems.PLATED_NETHER_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.NETHER_CHITIN_CHESTPLATE)
            .requires(1, TempAVPItems.PLATED_NETHER_CHITIN)
            .into(1, AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.NETHER_CHITIN_LEGGINGS)
            .requires(1, TempAVPItems.PLATED_NETHER_CHITIN)
            .into(1, AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.NETHER_CHITIN_BOOTS)
            .requires(1, TempAVPItems.PLATED_NETHER_CHITIN)
            .into(1, AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS);
    }

    private static void createPlatedChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.CHITIN_HELMET)
            .requires(1, TempAVPItems.PLATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.CHITIN_CHESTPLATE)
            .requires(1, TempAVPItems.PLATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.CHITIN_LEGGINGS)
            .requires(1, TempAVPItems.PLATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AVPArmorItems.CHITIN_BOOTS)
            .requires(1, TempAVPItems.PLATED_CHITIN)
            .into(1, AVPArmorItems.PLATED_CHITIN_BOOTS);
    }

    private static void createMk50ArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> mk50ArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.ALUMINUM_INGOT)
            .define('C', TempAVPItems.LEAD_INGOT);

        mk50ArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', TempAVPItems.CARBON_DUST)
            .pattern("CAC")
            .pattern("B B")
            .pattern("AEA")
            .into(1, AVPArmorItems.MK50_HELMET);

        mk50ArmorBuilder.get()
            .define('D', Items.LEATHER)
            .pattern("A A")
            .pattern("CAC")
            .pattern("DAD")
            .into(1, AVPArmorItems.MK50_CHESTPLATE);

        mk50ArmorBuilder.get()
            .define('D', Items.LEATHER)
            .pattern("CDC")
            .pattern("D D")
            .pattern("A A")
            .into(1, AVPArmorItems.MK50_LEGGINGS);

        mk50ArmorBuilder.get()
            .pattern("C C")
            .pattern("A A")
            .into(1, AVPArmorItems.MK50_BOOTS);
    }

    private static void createPressureArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> pressureArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.ALUMINUM_INGOT);

        pressureArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', TempAVPItems.CARBON_DUST)
            .pattern("AAA")
            .pattern("B B")
            .pattern("EEE")
            .into(1, AVPArmorItems.PRESSURE_HELMET);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("A A")
            .pattern("ADA")
            .pattern("DAD")
            .into(1, AVPArmorItems.PRESSURE_CHESTPLATE);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("ADA")
            .pattern("D D")
            .pattern("A A")
            .into(1, AVPArmorItems.PRESSURE_LEGGINGS);

        pressureArmorBuilder.get()
            .define('D', ItemTags.WOOL)
            .pattern("D D")
            .pattern("A A")
            .into(1, AVPArmorItems.PRESSURE_BOOTS);
    }

    private static void createTacticalArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> tacticalArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', TempAVPItems.STEEL_INGOT)
            .define('B', ItemTags.WOOL);

        tacticalArmorBuilder.get()
            .define('C', TempAVPItems.POLYMER)
            .pattern("ABA")
            .pattern("C C")
            .into(1, AVPArmorItems.TACTICAL_HELMET);

        tacticalArmorBuilder.get()
            .define('C', TempAVPItems.POLYMER)
            .pattern("C C")
            .pattern("ABA")
            .pattern("CAC")
            .into(1, AVPArmorItems.TACTICAL_CHESTPLATE);

        tacticalArmorBuilder.get()
            .define('C', TempAVPItems.POLYMER)
            .pattern("CBC")
            .pattern("A A")
            .pattern("C C")
            .into(1, AVPArmorItems.TACTICAL_LEGGINGS);

        tacticalArmorBuilder.get()
            .pattern("B B")
            .pattern("A A")
            .into(1, AVPArmorItems.TACTICAL_BOOTS);
    }

    private static void createStandardArmorSetRecipes(
        RecipeBuilder builder,
        ItemLike base,
        Item helmet,
        Item chestplate,
        Item leggings,
        Item boots
    ) {
        builder.shaped()
            .apply(RecipeTemplates.HELMET.apply(base))
            .into(1, helmet);

        builder.shaped()
            .apply(RecipeTemplates.CHESTPLATE.apply(base))
            .into(1, chestplate);

        builder.shaped()
            .apply(RecipeTemplates.LEGGINGS.apply(base))
            .into(1, leggings);

        builder.shaped()
            .apply(RecipeTemplates.BOOTS.apply(base))
            .into(1, boots);
    }
}
