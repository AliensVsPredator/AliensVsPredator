package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPElectronicRecipes {

    public static void addElectronicRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.CAPACITOR, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.DUST_LITHIUM)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.DIODE, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.CARBON)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.SILICA)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('C', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.TRANSISTOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.RESISTOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.REGULATOR)
            .defineAndUnlockIfHas('G', AVPElectronicItems.INSTANCE.DIODE)
            .pattern("DBE")
            .pattern("CAC")
            .pattern("FBG")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.LED)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.DIODE)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('C', Items.REDSTONE)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.LED_DISPLAY)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.LED)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.DUST_LITHIUM)
            .pattern("BDB")
            .pattern("BCB")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.MOTHERBOARD)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.TRANSISTOR)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.CAPACITOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.REGULATOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.DIODE)
            .defineAndUnlockIfHas('G', AVPElectronicItems.INSTANCE.RESISTOR)
            .defineAndUnlockIfHas('H', AVPElectronicItems.INSTANCE.LED)
            .pattern("AEF")
            .pattern("GBC")
            .pattern("DBH")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.POWER_SUPPLY)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.DIODE)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.REGULATOR)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.CAPACITOR)
            .defineAndUnlockIfHas('E', Items.BEDROCK) // TODO: This should be a transformer block
            .pattern("ABC")
            .pattern("DEC")
            .pattern("ABC")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.CPU)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.SILICA)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.RAM)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.SILICA)
            .defineAndUnlockIfHas('D', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.TRANSISTOR)
            .pattern("AAA")
            .pattern("CEC")
            .pattern("DBD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.REGULATOR)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.DIODE)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.RESISTOR)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.RESISTOR, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.CARBON)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.SSD)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.RAM)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.REGULATOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.TRANSISTOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.CAPACITOR)
            .pattern("AAA")
            .pattern("ECE")
            .pattern("BDF")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.TRANSISTOR)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.POLYCARBONATE)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('C', Items.LEVER)
            .pattern("  A")
            .pattern("BC ")
            .pattern("  A")
            .save(recipeOutput);
    }

    private AVPElectronicRecipes() {
        throw new UnsupportedOperationException();
    }
}
