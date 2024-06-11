package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPElectronicRecipes {

    public static void addElectronicRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.battery)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.dustLithium)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .pattern("ACA")
            .pattern("BBB")
            .pattern("ACA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.capacitor, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.dustLithium)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.diode, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.carbon)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.silica)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('C', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('D', AVPElectronicItemRegistry.INSTANCE.transistor)
            .defineAndUnlockIfHas('E', AVPElectronicItemRegistry.INSTANCE.resistor)
            .defineAndUnlockIfHas('F', AVPElectronicItemRegistry.INSTANCE.regulator)
            .defineAndUnlockIfHas('G', AVPElectronicItemRegistry.INSTANCE.diode)
            .pattern("DBE")
            .pattern("CAC")
            .pattern("FBG")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.led)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.diode)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', Items.REDSTONE)
            .pattern("C")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.ledDisplay)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPElectronicItemRegistry.INSTANCE.led)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('D', AVPItemRegistry.INSTANCE.dustLithium)
            .pattern("BDB")
            .pattern("BCB")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.motherboard)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.transistor)
            .defineAndUnlockIfHas('D', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .defineAndUnlockIfHas('E', AVPElectronicItemRegistry.INSTANCE.regulator)
            .defineAndUnlockIfHas('F', AVPElectronicItemRegistry.INSTANCE.diode)
            .defineAndUnlockIfHas('G', AVPElectronicItemRegistry.INSTANCE.resistor)
            .defineAndUnlockIfHas('H', AVPElectronicItemRegistry.INSTANCE.led)
            .pattern("AEF")
            .pattern("GBC")
            .pattern("DBH")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.powerSupply)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.diode)
            .defineAndUnlockIfHas('B', AVPElectronicItemRegistry.INSTANCE.regulator)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('D', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .defineAndUnlockIfHas('E', Items.BEDROCK) // TODO: This should be a transformer block
            .pattern("ABC")
            .pattern("DEC")
            .pattern("ABC")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.cpu)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.silica)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.ram)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.silica)
            .defineAndUnlockIfHas('D', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('E', AVPElectronicItemRegistry.INSTANCE.transistor)
            .pattern("AAA")
            .pattern("CEC")
            .pattern("DBD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.regulator)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.diode)
            .defineAndUnlockIfHas('B', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.resistor)
            .pattern("A")
            .pattern("B")
            .pattern("C")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.resistor, 8)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.carbon)
            .pattern("A")
            .pattern("B")
            .pattern("A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.ssd)
            .defineAndUnlockIfHas('A', AVPElectronicItemRegistry.INSTANCE.ram)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.integratedCircuit)
            .defineAndUnlockIfHas('D', AVPElectronicItemRegistry.INSTANCE.regulator)
            .defineAndUnlockIfHas('E', AVPElectronicItemRegistry.INSTANCE.transistor)
            .defineAndUnlockIfHas('F', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .pattern("AAA")
            .pattern("ECE")
            .pattern("BDF")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPElectronicItemRegistry.INSTANCE.transistor)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.polycarbonate)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotAluminum)
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
