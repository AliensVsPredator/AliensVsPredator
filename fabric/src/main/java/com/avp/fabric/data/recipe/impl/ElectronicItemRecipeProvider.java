package com.avp.fabric.data.recipe.impl;

import com.compat.CommonConstants;
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
            .define('A', CommonConstants.INGOTS_ALUMINUM)
            .define('N', AVPItems.NEODYMIUM_MAGNET)
            .define('R', AVPItems.REGULATOR)
            .define('P', AVPItems.POLYMER)
            .pattern("PAP")
            .pattern("ANA")
            .pattern("PRP")
            .into(8, AVPItems.SPEAKER);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('B', AVPItems.BRASS_NUGGET)
            .define('C', Items.COPPER_INGOT)
            .define('N', AVPItems.NEODYMIUM_MAGNET)
            .define('I', AVPItems.INTEGRATED_CIRCUIT)
            .pattern("BCB")
            .pattern("NIN")
            .pattern("BCB")
            .into(8, AVPItems.SERVO);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', AVPItems.SILICON)
            .pattern("AB")
            .pattern(" C")
            .pattern("AB")
            .into(8, AVPItems.RESISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', AVPItems.SILICON)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" A ")
            .into(8, AVPItems.DIODE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', Items.LEVER)
            .define('R', Items.REDSTONE)
            .define('S', AVPItems.SILICON)
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
            .define('S', AVPItems.SILICON)
            .pattern(" D ")
            .pattern("SLS")
            .pattern(" R ")
            .into(2, AVPItems.REGULATOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', AVPItems.LITHIUM_DUST)
            .define('R', Items.REDSTONE)
            .define('S', AVPItems.SILICON)
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
            .define('S', AVPItems.SILICON)
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
            .define('S', AVPItems.SILICON)
            .pattern("SCS")
            .pattern("CRC")
            .pattern("SCS")
            .into(1, AVPItems.CPU);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonConstants.INGOTS_ALUMINUM)
            .define('C', AVPItems.CAPACITOR)
            .define('L', AVPItems.LITHIUM_DUST)
            .pattern("ACA")
            .pattern("LLL")
            .pattern("ACL")
            .into(1, AVPItems.BATTERY_PACK);
    }
}
