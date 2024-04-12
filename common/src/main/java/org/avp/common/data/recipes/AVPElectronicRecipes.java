package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPElectronicRecipes {

    public static void addElectronicRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.CAPACITOR, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_LITHIUM)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.DIODE, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.CARBON)
            .defineAndUnlockIfHas('C', AVPItems.SILICA)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('A', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('C', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('D', AVPElectronicItems.TRANSISTOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.RESISTOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.REGULATOR)
            .defineAndUnlockIfHas('G', AVPElectronicItems.DIODE)
            .pattern("DBE")
            .pattern("CAC")
            .pattern("FBG")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.LED)
            .defineAndUnlockIfHas('A', AVPElectronicItems.DIODE)
            .defineAndUnlockIfHas('B', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('C', Items.REDSTONE)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.LED_DISPLAY)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPElectronicItems.LED)
            .defineAndUnlockIfHas('C', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('D', AVPItems.INGOT_LITHIUM)
            .pattern("BDB")
            .pattern("BCB")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.MOTHERBOARD)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPElectronicItems.TRANSISTOR)
            .defineAndUnlockIfHas('D', AVPElectronicItems.CAPACITOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.REGULATOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.DIODE)
            .defineAndUnlockIfHas('G', AVPElectronicItems.RESISTOR)
            .defineAndUnlockIfHas('H', AVPElectronicItems.LED)
            .pattern("AEF")
            .pattern("GBC")
            .pattern("DBH")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.POWER_SUPPLY)
            .defineAndUnlockIfHas('A', AVPElectronicItems.DIODE)
            .defineAndUnlockIfHas('B', AVPElectronicItems.REGULATOR)
            .defineAndUnlockIfHas('C', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('D', AVPElectronicItems.CAPACITOR)
            .defineAndUnlockIfHas('E', Items.BEDROCK) // TODO: This should be a transformer block
            .pattern("ABC")
            .pattern("DEC")
            .pattern("ABC")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.CPU)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.SILICA)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.RAM)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('B', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPItems.SILICA)
            .defineAndUnlockIfHas('D', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('E', AVPElectronicItems.TRANSISTOR)
            .pattern("AAA")
            .pattern("CEC")
            .pattern("DBD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.REGULATOR)
            .defineAndUnlockIfHas('A', AVPElectronicItems.DIODE)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPElectronicItems.RESISTOR)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.RESISTOR, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.CARBON)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.SSD)
            .defineAndUnlockIfHas('A', AVPElectronicItems.RAM)
            .defineAndUnlockIfHas('B', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INTEGRATED_CIRCUIT)
            .defineAndUnlockIfHas('D', AVPElectronicItems.REGULATOR)
            .defineAndUnlockIfHas('E', AVPElectronicItems.TRANSISTOR)
            .defineAndUnlockIfHas('F', AVPElectronicItems.CAPACITOR)
            .pattern("AAA")
            .pattern("ECE")
            .pattern("BDF")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.TRANSISTOR)
            .defineAndUnlockIfHas('A', AVPItems.POLYCARBONATE)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_ALUMINUM)
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
