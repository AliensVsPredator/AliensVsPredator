package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPWeaponPartItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPWeaponPartRecipes {

    public static void addWeaponPartRecipes(RecipeOutput recipeOutput) {
        // Generic Barrel
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .pattern("AAA")
            .unlockedBy("has_steel", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        // Smart Barrel
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_SMART.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get())
            .requires(Items.OBSERVER)
            .unlockedBy("has_barrel", AVPRecipeProvider.has(AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get()))
            .save(recipeOutput);

        // Generic Grip
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get())
            .define('A', AVPItems.POLYMER.get())
            .define('B', AVPItems.INGOT_STEEL.get())
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .unlockedBy("has_steel", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        // Minigun Barrel
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_MINIGUN.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get())
            .pattern("BBB")
            .pattern("A A")
            .pattern("ABB")
            .unlockedBy("has_barrel", AVPRecipeProvider.has(AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get()))
            .save(recipeOutput);

        // Rocket Barrel
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_ROCKET.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .unlockedBy("has_steel", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        // Generic Receiver
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC.get())
            .define('A', AVPItems.INGOT_STEEL.get())
            .define('B', AVPItems.POLYMER.get())
            .define('C', Items.TRIPWIRE_HOOK)
            .define('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .unlockedBy("has_steel", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);

        // Smart Receiver
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART.get())
            .define('A', AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC.get())
            .define('B', AVPElectronicItems.CPU.get())
            .define('C', AVPElectronicItems.LED_DISPLAY.get())
            .define('D', AVPElectronicItems.POWER_SUPPLY.get())
            .define('E', AVPItems.POLYMER.get())
            .define('F', AVPElectronicItems.CAPACITOR.get())
            .pattern(" A ")
            .pattern("BCD")
            .pattern("EFE")
            .unlockedBy("has_receiver", AVPRecipeProvider.has(AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC.get()))
            .save(recipeOutput);

        // Generic Stock
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_STOCK_GENERIC.get())
            .define('A', AVPItems.POLYMER.get())
            .define('B', AVPItems.INGOT_STEEL.get())
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .unlockedBy("has_steel", AVPRecipeProvider.has(AVPItems.INGOT_STEEL.get()))
            .save(recipeOutput);
    }

    private AVPWeaponPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
