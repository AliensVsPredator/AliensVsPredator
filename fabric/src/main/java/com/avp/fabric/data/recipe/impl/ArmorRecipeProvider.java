package com.avp.fabric.data.recipe.impl;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.compat.CommonItemTags;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.builder.ShapedRecipeBuilder;

public class ArmorRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createMk50ArmorSetRecipes(builder);
        createPressureArmorSetRecipes(builder);
        createTacticalArmorSetRecipes(builder);
        createWYCommandoArmorSetRecipes(builder);
        createWYEliteArmorSetRecipes(builder);
        createPlatedChitinArmorSetRecipes(builder);
        createPlatedAberrantChitinArmorSetRecipes(builder);
        // TODO: Re-implement these at some point in the future.
        // createPlatedIrradiatedChitinArmorSetRecipes(builder);
        createPlatedNetherChitinArmorSetRecipes(builder);

        createStandardArmorSetRecipes(
            builder,
            AlienItems.ABERRANT_CHITIN.get(),
            AlienArmorItems.ABERRANT_CHITIN_HELMET.get(),
            AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
            AlienArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
            AlienArmorItems.ABERRANT_CHITIN_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            AlienItems.CHITIN.get(),
            AlienArmorItems.CHITIN_HELMET.get(),
            AlienArmorItems.CHITIN_CHESTPLATE.get(),
            AlienArmorItems.CHITIN_LEGGINGS.get(),
            AlienArmorItems.CHITIN_BOOTS.get()
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
            AlienItems.NETHER_CHITIN.get(),
            AlienArmorItems.NETHER_CHITIN_HELMET.get(),
            AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
            AlienArmorItems.NETHER_CHITIN_LEGGINGS.get(),
            AlienArmorItems.NETHER_CHITIN_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            CommonItemTags.INGOTS_STEEL,
            AVPArmorItems.STEEL_HELMET.get(),
            AVPArmorItems.STEEL_CHESTPLATE.get(),
            AVPArmorItems.STEEL_LEGGINGS.get(),
            AVPArmorItems.STEEL_BOOTS.get()
        );
        createStandardArmorSetRecipes(
            builder,
            CommonItemTags.INGOTS_TITANIUM,
            AVPArmorItems.TITANIUM_HELMET.get(),
            AVPArmorItems.TITANIUM_CHESTPLATE.get(),
            AVPArmorItems.TITANIUM_LEGGINGS.get(),
            AVPArmorItems.TITANIUM_BOOTS.get()
        );
    }

    private static void createPlatedAberrantChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.ABERRANT_CHITIN_HELMET)
            .requires(1, AlienItems.PLATED_ABERRANT_CHITIN)
            .into(1, AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE)
            .requires(1, AlienItems.PLATED_ABERRANT_CHITIN)
            .into(1, AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.ABERRANT_CHITIN_LEGGINGS)
            .requires(1, AlienItems.PLATED_ABERRANT_CHITIN)
            .into(1, AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.ABERRANT_CHITIN_BOOTS)
            .requires(1, AlienItems.PLATED_ABERRANT_CHITIN)
            .into(1, AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);
    }

    private static void createPlatedIrradiatedChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.IRRADIATED_CHITIN_HELMET)
            .requires(1, AlienItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE)
            .requires(1, AlienItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS)
            .requires(1, AlienItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.IRRADIATED_CHITIN_BOOTS)
            .requires(1, AlienItems.PLATED_IRRADIATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);
    }

    private static void createPlatedNetherChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.NETHER_CHITIN_HELMET)
            .requires(1, AlienItems.PLATED_NETHER_CHITIN)
            .into(1, AlienArmorItems.PLATED_NETHER_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.NETHER_CHITIN_CHESTPLATE)
            .requires(1, AlienItems.PLATED_NETHER_CHITIN)
            .into(1, AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.NETHER_CHITIN_LEGGINGS)
            .requires(1, AlienItems.PLATED_NETHER_CHITIN)
            .into(1, AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.NETHER_CHITIN_BOOTS)
            .requires(1, AlienItems.PLATED_NETHER_CHITIN)
            .into(1, AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS);
    }

    private static void createPlatedChitinArmorSetRecipes(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.CHITIN_HELMET)
            .requires(1, AlienItems.PLATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_CHITIN_HELMET);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.CHITIN_CHESTPLATE)
            .requires(1, AlienItems.PLATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_CHITIN_CHESTPLATE);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.CHITIN_LEGGINGS)
            .requires(1, AlienItems.PLATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_CHITIN_LEGGINGS);
        builder.shapeless()
            .withCategory(RecipeCategory.COMBAT)
            .requires(1, AlienArmorItems.CHITIN_BOOTS)
            .requires(1, AlienItems.PLATED_CHITIN)
            .into(1, AlienArmorItems.PLATED_CHITIN_BOOTS);
    }

    private static void createMk50ArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> mk50ArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_ALUMINUM)
            .define('C', CommonItemTags.INGOTS_LEAD);

        mk50ArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', CommonItemTags.DUSTS_COAL)
            .pattern("CAC")
            .pattern("B B")
            .pattern("AEA")
            .into(1, AVPArmorItems.MK50_HELMET);

        mk50ArmorBuilder.get()
            .define('D', CommonItemTags.LEATHERS)
            .pattern("A A")
            .pattern("CAC")
            .pattern("DAD")
            .into(1, AVPArmorItems.MK50_CHESTPLATE);

        mk50ArmorBuilder.get()
            .define('D', CommonItemTags.LEATHERS)
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
            .define('A', CommonItemTags.INGOTS_ALUMINUM);

        pressureArmorBuilder.get()
            .define('B', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('E', CommonItemTags.DUSTS_COAL)
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
            .define('A', CommonItemTags.INGOTS_STEEL)
            .define('B', ItemTags.WOOL);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("ABA")
            .pattern("C C")
            .into(1, AVPArmorItems.TACTICAL_HELMET);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("C C")
            .pattern("ABA")
            .pattern("CAC")
            .into(1, AVPArmorItems.TACTICAL_CHESTPLATE);

        tacticalArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("CBC")
            .pattern("A A")
            .pattern("C C")
            .into(1, AVPArmorItems.TACTICAL_LEGGINGS);

        tacticalArmorBuilder.get()
            .pattern("B B")
            .pattern("A A")
            .into(1, AVPArmorItems.TACTICAL_BOOTS);
    }

    private static void createWYCommandoArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> wyCommandoArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
            .define('B', AVPItemTags.PLASTIC);

        wyCommandoArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .define('D', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .pattern("ABA")
            .pattern("CDC")
            .into(1, AVPArmorItems.WY_COMMANDO_HELMET);

        wyCommandoArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("C C")
            .pattern("ABA")
            .pattern("CAC")
            .into(1, AVPArmorItems.WY_COMMANDO_CHESTPLATE);

        wyCommandoArmorBuilder.get()
            .define('C', AVPItems.POLYMER)
            .pattern("CBC")
            .pattern("A A")
            .pattern("C C")
            .into(1, AVPArmorItems.WY_COMMANDO_LEGGINGS);

        wyCommandoArmorBuilder.get()
            .pattern("B B")
            .pattern("A A")
            .into(1, AVPArmorItems.WY_COMMANDO_BOOTS);
    }

    private static void createWYEliteArmorSetRecipes(RecipeBuilder builder) {
        Supplier<ShapedRecipeBuilder> wyEliteArmorBuilder = () -> builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('A', CommonItemTags.INGOTS_STEEL)
            .define('B', AVPItemTags.PLASTIC);

        wyEliteArmorBuilder.get()
            .define('C', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .pattern("ABA")
            .pattern("BCB")
            .into(1, AVPArmorItems.WY_ELITE_HELMET);

        wyEliteArmorBuilder.get()
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .into(1, AVPArmorItems.WY_ELITE_CHESTPLATE);

        wyEliteArmorBuilder.get()
            .pattern("BBB")
            .pattern("A A")
            .pattern("B B")
            .into(1, AVPArmorItems.WY_ELITE_LEGGINGS);

        wyEliteArmorBuilder.get()
            .pattern("B B")
            .pattern("A A")
            .into(1, AVPArmorItems.WY_ELITE_BOOTS);
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

    private static void createStandardArmorSetRecipes(
        RecipeBuilder builder,
        TagKey<Item> base,
        Item helmet,
        Item chestplate,
        Item leggings,
        Item boots
    ) {
        builder.shaped()
            .apply(RecipeTemplates.HELMET_TAG_FRIENDLY.apply(base))
            .into(1, helmet);

        builder.shaped()
            .apply(RecipeTemplates.CHESTPLATE_TAG_FRIENDLY.apply(base))
            .into(1, chestplate);

        builder.shaped()
            .apply(RecipeTemplates.LEGGINGS_TAG_FRIENDLY.apply(base))
            .into(1, leggings);

        builder.shaped()
            .apply(RecipeTemplates.BOOTS_TAG_FRIENDLY.apply(base))
            .into(1, boots);
    }
}
