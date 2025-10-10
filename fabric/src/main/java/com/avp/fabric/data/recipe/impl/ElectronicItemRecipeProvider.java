package com.avp.fabric.data.recipe.impl;

import com.compat.CommonItemTags;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class ElectronicItemRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createElectronicItemRecipes(builder);
    }

    private static void createElectronicItemRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonItemTags.INGOTS_ALUMINUM)
            .define('N', AVPItems.NEODYMIUM_MAGNET)
            .define('R', AVPItems.REGULATOR)
            .define('P', AVPItems.POLYMER)
            .pattern("PAP")
            .pattern("ANA")
            .pattern("PRP")
            .into(8, AVPItems.SPEAKER);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('B', CommonItemTags.NUGGETS_BRASS)
            .define('C', CommonItemTags.INGOTS_COPPER)
            .define('N', AVPItems.NEODYMIUM_MAGNET)
            .define('I', AVPItems.INTEGRATED_CIRCUIT)
            .pattern("BCB")
            .pattern("NIN")
            .pattern("BCB")
            .into(8, AVPItems.SERVO);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonItemTags.NUGGETS_GOLD)
            .define('B', CommonItemTags.DUSTS_REDSTONE)
            .define('C', CommonItemTags.SILICON)
            .pattern("AB")
            .pattern(" C")
            .pattern("AB")
            .into(8, AVPItems.RESISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonItemTags.NUGGETS_GOLD)
            .define('B', CommonItemTags.DUSTS_REDSTONE)
            .define('C', CommonItemTags.SILICON)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" A ")
            .into(8, AVPItems.DIODE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', CommonItemTags.NUGGETS_GOLD)
            .define('L', Items.LEVER)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .define('S', CommonItemTags.SILICON)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .pattern("SSS")
            .pattern("ILI")
            .pattern("RGR")
            .into(2, AVPItems.TRANSISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('L', CommonItemTags.INGOTS_LEAD)
            .define('R', AVPItems.RESISTOR)
            .define('S', CommonItemTags.SILICON)
            .pattern(" D ")
            .pattern("SLS")
            .pattern(" R ")
            .into(2, AVPItems.REGULATOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', CommonItemTags.NUGGETS_GOLD)
            .define('L', CommonItemTags.DUSTS_LITHIUM)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .define('S', CommonItemTags.SILICON)
            .pattern("GSS")
            .pattern("RLL")
            .pattern("GSS")
            .into(1, AVPItems.CAPACITOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('L', CommonItemTags.INGOTS_LEAD)
            .define('G', AVPItems.REGULATOR)
            .define('O', CommonItemTags.DUSTS_REDSTONE)
            .define('R', AVPItems.RESISTOR)
            .define('S', CommonItemTags.SILICON)
            .define('T', AVPItems.TRANSISTOR)
            .pattern("TSR")
            .pattern("LOL")
            .pattern("GSD")
            .into(1, AVPItems.INTEGRATED_CIRCUIT);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('G', CommonItemTags.NUGGETS_GOLD)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .pattern("G  ")
            .pattern("RDI")
            .pattern("G  ")
            .into(2, AVPItems.LED);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', AVPItems.INTEGRATED_CIRCUIT)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('L', AVPItems.LED)
            .define('T', CommonItemTags.DUSTS_LITHIUM)
            .pattern("LTL")
            .pattern("LIL")
            .pattern("LCL")
            .into(1, AVPItems.LED_DISPLAY);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', AVPItems.INTEGRATED_CIRCUIT)
            .define('R', CommonItemTags.DUSTS_REDSTONE)
            .define('S', CommonItemTags.SILICON)
            .pattern("SCS")
            .pattern("CRC")
            .pattern("SCS")
            .into(1, AVPItems.CPU);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonItemTags.INGOTS_ALUMINUM)
            .define('C', AVPItems.CAPACITOR)
            .define('L', CommonItemTags.DUSTS_LITHIUM)
            .pattern("ACA")
            .pattern("LLL")
            .pattern("ACL")
            .into(1, AVPItems.BATTERY_PACK);
    }
}
