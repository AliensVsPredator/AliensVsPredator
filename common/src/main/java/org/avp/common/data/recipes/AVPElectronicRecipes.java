package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPElectronicRecipes {

    public static void addElectronicRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.CAPACITOR.get(), 8)
            .define('A', Items.COPPER_INGOT)
            .define('B', AVPItems.INGOT_LITHIUM.get())
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .unlockedBy("has_lithium", AVPRecipeProvider.has(AVPItems.INGOT_LITHIUM.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.DIODE.get(), 8)
            .define('A', Items.COPPER_INGOT)
            .define('B', AVPItems.CARBON.get())
            .define('C', AVPItems.SILICA.get())
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .unlockedBy("has_carbon", AVPRecipeProvider.has(AVPItems.CARBON.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('A', AVPItems.POLYCARBONATE.get())
            .define('B', AVPItems.INGOT_ALUMINUM.get())
            .define('C', Items.COPPER_INGOT)
            .define('D', AVPElectronicItems.TRANSISTOR.get())
            .define('E', AVPElectronicItems.RESISTOR.get())
            .define('F', AVPElectronicItems.REGULATOR.get())
            .define('G', AVPElectronicItems.DIODE.get())
            .pattern("DBE")
            .pattern("CAC")
            .pattern("FBG")
            .unlockedBy("has_aluminum", AVPRecipeProvider.has(AVPItems.INGOT_ALUMINUM.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.LED.get())
            .define('A', AVPElectronicItems.DIODE.get())
            .define('B', AVPItems.POLYCARBONATE.get())
            .define('C', Items.REDSTONE)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .unlockedBy("has_diode", AVPRecipeProvider.has(AVPElectronicItems.DIODE.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.LED_DISPLAY.get())
            .define('A', AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('B', AVPElectronicItems.LED.get())
            .define('C', AVPItems.POLYCARBONATE.get())
            .define('D', AVPItems.INGOT_LITHIUM.get())
            .pattern("BDB")
            .pattern("BCB")
            .pattern("BAB")
            .unlockedBy("has_integrated_circuit", AVPRecipeProvider.has(AVPElectronicItems.INTEGRATED_CIRCUIT.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.MOTHERBOARD.get())
            .define('A', AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('B', AVPItems.POLYCARBONATE.get())
            .define('C', AVPElectronicItems.TRANSISTOR.get())
            .define('D', AVPElectronicItems.CAPACITOR.get())
            .define('E', AVPElectronicItems.REGULATOR.get())
            .define('F', AVPElectronicItems.DIODE.get())
            .define('G', AVPElectronicItems.RESISTOR.get())
            .define('H', AVPElectronicItems.LED.get())
            .pattern("AEF")
            .pattern("GBC")
            .pattern("DBH")
            .unlockedBy("has_integrated_circuit", AVPRecipeProvider.has(AVPElectronicItems.INTEGRATED_CIRCUIT.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.POWER_SUPPLY.get())
            .define('A', AVPElectronicItems.DIODE.get())
            .define('B', AVPElectronicItems.REGULATOR.get())
            .define('C', AVPItems.INGOT_ALUMINUM.get())
            .define('D', AVPElectronicItems.CAPACITOR.get())
            .define('E', Items.BEDROCK) // TODO: This should be a transformer block
            .pattern("ABC")
            .pattern("DEC")
            .pattern("ABC")
            .unlockedBy("has_diode", AVPRecipeProvider.has(AVPElectronicItems.DIODE.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.CPU.get())
            .define('A', AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('B', AVPItems.SILICA.get())
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .unlockedBy("has_integrated_circuit", AVPRecipeProvider.has(AVPElectronicItems.INTEGRATED_CIRCUIT.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.RAM.get())
            .define('A', AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('B', AVPItems.POLYCARBONATE.get())
            .define('C', AVPItems.SILICA.get())
            .define('D', Items.COPPER_INGOT)
            .define('E', AVPElectronicItems.TRANSISTOR.get())
            .pattern("AAA")
            .pattern("CEC")
            .pattern("DBD")
            .unlockedBy("has_integrated_circuit", AVPRecipeProvider.has(AVPElectronicItems.INTEGRATED_CIRCUIT.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.REGULATOR.get())
            .define('A', AVPElectronicItems.DIODE.get())
            .define('B', Items.COPPER_INGOT)
            .define('C', AVPElectronicItems.RESISTOR.get())
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .unlockedBy("has_diode", AVPRecipeProvider.has(AVPElectronicItems.DIODE.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.RESISTOR.get(), 8)
            .define('A', Items.COPPER_INGOT)
            .define('B', AVPItems.CARBON.get())
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .unlockedBy("has_diode", AVPRecipeProvider.has(AVPElectronicItems.DIODE.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.SSD.get())
            .define('A', AVPElectronicItems.RAM.get())
            .define('B', AVPItems.POLYCARBONATE.get())
            .define('C', AVPElectronicItems.INTEGRATED_CIRCUIT.get())
            .define('D', AVPElectronicItems.REGULATOR.get())
            .define('E', AVPElectronicItems.TRANSISTOR.get())
            .define('F', AVPElectronicItems.CAPACITOR.get())
            .pattern("AAA")
            .pattern("ECE")
            .pattern("BDF")
            .unlockedBy("has_integrated_circuit", AVPRecipeProvider.has(AVPElectronicItems.INTEGRATED_CIRCUIT.get()))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.TRANSISTOR.get())
            .define('A', AVPItems.POLYCARBONATE.get())
            .define('B', AVPItems.INGOT_ALUMINUM.get())
            .define('C', Items.LEVER)
            .pattern("  A")
            .pattern("BC ")
            .pattern("  A")
            .unlockedBy("has_polycarbonate", AVPRecipeProvider.has(AVPItems.POLYCARBONATE.get()))
            .save(recipeOutput);
    }

    private AVPElectronicRecipes() {
        throw new UnsupportedOperationException();
    }
}
