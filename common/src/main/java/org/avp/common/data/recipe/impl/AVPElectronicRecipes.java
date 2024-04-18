package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;

public final class AVPElectronicRecipes {

    public static void addElectronicRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.capacitor, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.dustLithium)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.diode, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.carbon)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.silica)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('C', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.transistor)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.resistor)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.regulator)
            .defineAndUnlockIfHas('G', AVPElectronicItems.INSTANCE.diode)
            .pattern("DBE")
            .pattern("CAC")
            .pattern("FBG")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.led)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.diode)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', Items.REDSTONE)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.ledDisplay)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.led)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.dustLithium)
            .pattern("BDB")
            .pattern("BCB")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.motherboard)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.transistor)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.capacitor)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.regulator)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.diode)
            .defineAndUnlockIfHas('G', AVPElectronicItems.INSTANCE.resistor)
            .defineAndUnlockIfHas('H', AVPElectronicItems.INSTANCE.led)
            .pattern("AEF")
            .pattern("GBC")
            .pattern("DBH")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.powerSupply)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.diode)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.regulator)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.capacitor)
            .defineAndUnlockIfHas('E', Items.BEDROCK) // TODO: This should be a transformer block
            .pattern("ABC")
            .pattern("DEC")
            .pattern("ABC")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.cpu)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.silica)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.ram)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.silica)
            .defineAndUnlockIfHas('D', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.transistor)
            .pattern("AAA")
            .pattern("CEC")
            .pattern("DBD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.regulator)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.diode)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.resistor)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.resistor, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.carbon)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.ssd)
            .defineAndUnlockIfHas('A', AVPElectronicItems.INSTANCE.ram)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.regulator)
            .defineAndUnlockIfHas('E', AVPElectronicItems.INSTANCE.transistor)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.capacitor)
            .pattern("AAA")
            .pattern("ECE")
            .pattern("BDF")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItems.INSTANCE.transistor)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotAluminum)
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
