package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.avp.common.item.AVPItemTags;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class ElectronicItemRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createElectronicItemRecipes(builder);
    }

    private static void createElectronicItemRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', TempAVPItems.ALUMINUM_INGOT)
            .define('N', TempAVPItems.NEODYMIUM_MAGNET)
            .define('R', TempAVPItems.REGULATOR)
            .define('P', TempAVPItems.POLYMER)
            .pattern("PAP")
            .pattern("ANA")
            .pattern("PRP")
            .into(8, TempAVPItems.SPEAKER);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('B', TempAVPItems.BRASS_NUGGET)
            .define('C', Items.COPPER_INGOT)
            .define('N', TempAVPItems.NEODYMIUM_MAGNET)
            .define('I', TempAVPItems.INTEGRATED_CIRCUIT)
            .pattern("BCB")
            .pattern("NIN")
            .pattern("BCB")
            .into(8, TempAVPItems.SERVO);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', TempAVPItems.RAW_SILICA)
            .pattern("AB")
            .pattern(" C")
            .pattern("AB")
            .into(8, TempAVPItems.RESISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', Items.GOLD_NUGGET)
            .define('B', Items.REDSTONE)
            .define('C', TempAVPItems.RAW_SILICA)
            .pattern(" A ")
            .pattern("BCB")
            .pattern(" A ")
            .into(8, TempAVPItems.DIODE);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', Items.LEVER)
            .define('R', Items.REDSTONE)
            .define('S', TempAVPItems.RAW_SILICA)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .pattern("SSS")
            .pattern("ILI")
            .pattern("RGR")
            .into(2, TempAVPItems.TRANSISTOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', TempAVPItems.DIODE)
            .define('L', TempAVPItems.LEAD_INGOT)
            .define('R', TempAVPItems.RESISTOR)
            .define('S', TempAVPItems.RAW_SILICA)
            .pattern(" D ")
            .pattern("SLS")
            .pattern(" R ")
            .into(2, TempAVPItems.REGULATOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('G', Items.GOLD_NUGGET)
            .define('L', TempAVPItems.LITHIUM_DUST)
            .define('R', Items.REDSTONE)
            .define('S', TempAVPItems.RAW_SILICA)
            .pattern("GSS")
            .pattern("RLL")
            .pattern("GSS")
            .into(1, TempAVPItems.CAPACITOR);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', TempAVPItems.DIODE)
            .define('L', TempAVPItems.LEAD_INGOT)
            .define('G', TempAVPItems.REGULATOR)
            .define('O', Items.REDSTONE)
            .define('R', TempAVPItems.RESISTOR)
            .define('S', TempAVPItems.RAW_SILICA)
            .define('T', TempAVPItems.TRANSISTOR)
            .pattern("TSR")
            .pattern("LOL")
            .pattern("GSD")
            .into(1, TempAVPItems.INTEGRATED_CIRCUIT);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('D', TempAVPItems.DIODE)
            .define('G', Items.GOLD_NUGGET)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('R', Items.REDSTONE)
            .pattern("G  ")
            .pattern("RDI")
            .pattern("G  ")
            .into(2, TempAVPItems.LED);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', TempAVPItems.INTEGRATED_CIRCUIT)
            .define('I', AVPItemTags.INDUSTRIAL_GLASS_PANE)
            .define('L', TempAVPItems.LED)
            .define('T', TempAVPItems.LITHIUM_DUST)
            .pattern("LTL")
            .pattern("LIL")
            .pattern("LCL")
            .into(1, TempAVPItems.LED_DISPLAY);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('C', TempAVPItems.INTEGRATED_CIRCUIT)
            .define('R', Items.REDSTONE)
            .define('S', TempAVPItems.RAW_SILICA)
            .pattern("SCS")
            .pattern("CRC")
            .pattern("SCS")
            .into(1, TempAVPItems.CPU);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', TempAVPItems.ALUMINUM_INGOT)
            .define('C', TempAVPItems.CAPACITOR)
            .define('L', TempAVPItems.LITHIUM_DUST)
            .pattern("ACA")
            .pattern("LLL")
            .pattern("ACL")
            .into(1, TempAVPItems.BATTERY_PACK);
    }
}
