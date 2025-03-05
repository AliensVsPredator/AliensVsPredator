package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.avp.common.item.AVPItemTags;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.builder.RecipeBuilder;

public class ElectronicItemRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createElectronicItemRecipes(builder);
    }

    private static void createElectronicItemRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', AVPItems.RAW_SILICA)
            .pattern("AB")
            .pattern(" C")
            .pattern("AB")
            .into(8, AVPItems.RESISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', AVPItems.RAW_SILICA)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" A ")
            .into(8, AVPItems.DIODE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', Items.LEVER)
            .define('R', Items.REDSTONE)
            .define('S', AVPItems.RAW_SILICA)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .pattern("SSS")
            .pattern("ILI")
            .pattern("RGR")
            .into(2, AVPItems.TRANSISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('L', AVPItems.LEAD_INGOT)
            .define('R', AVPItems.RESISTOR)
            .define('S', AVPItems.RAW_SILICA)
            .pattern(" D ")
            .pattern("SLS")
            .pattern(" R ")
            .into(2, AVPItems.REGULATOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', AVPItems.LITHIUM_DUST)
            .define('R', Items.REDSTONE)
            .define('S', AVPItems.RAW_SILICA)
            .pattern("GSS")
            .pattern("RLL")
            .pattern("GSS")
            .into(1, AVPItems.CAPACITOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('L', AVPItems.LEAD_INGOT)
            .define('G', AVPItems.REGULATOR)
            .define('O', Items.REDSTONE)
            .define('R', AVPItems.RESISTOR)
            .define('S', AVPItems.RAW_SILICA)
            .define('T', AVPItems.TRANSISTOR)
            .pattern("TSR")
            .pattern("LOL")
            .pattern("GSD")
            .into(1, AVPItems.INTEGRATED_CIRCUIT);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', AVPItems.DIODE)
            .define('G', Items.GOLD_NUGGET)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('R', Items.REDSTONE)
            .pattern("G  ")
            .pattern("RDI")
            .pattern("G  ")
            .into(2, AVPItems.LED);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', AVPItems.INTEGRATED_CIRCUIT)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('L', AVPItems.LED)
            .define('T', AVPItems.LITHIUM_DUST)
            .pattern("LTL")
            .pattern("LIL")
            .pattern("LCL")
            .into(1, AVPItems.LED_DISPLAY);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', AVPItems.INTEGRATED_CIRCUIT)
            .define('R', Items.REDSTONE)
            .define('S', AVPItems.RAW_SILICA)
            .pattern("SCS")
            .pattern("CRC")
            .pattern("SCS")
            .into(1, AVPItems.CPU);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('C', AVPItems.CAPACITOR)
            .define('L', AVPItems.LITHIUM_DUST)
            .pattern("ACA")
            .pattern("LLL")
            .pattern("ACL")
            .into(1, AVPItems.BATTERY_PACK);
    }
}
