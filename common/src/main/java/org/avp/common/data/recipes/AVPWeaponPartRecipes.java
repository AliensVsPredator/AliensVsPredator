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
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_STEEL)
            .pattern("AAA")
            .save(recipeOutput);

        // Smart Barrel
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_SMART)
            .requiresAndUnlockIfHas('A', AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('B', Items.OBSERVER)
            .save(recipeOutput);

        // Generic Grip
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.POLYMER)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_STEEL)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .save(recipeOutput);

        // Minigun Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_MINIGUN)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_STEEL)
            .defineAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC)
            .pattern("BBB")
            .pattern("A A")
            .pattern("ABB")
            .save(recipeOutput);

        // Rocket Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_BARREL_ROCKET)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_STEEL)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .save(recipeOutput);

        // Generic Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_STEEL)
            .defineAndUnlockIfHas('B', AVPItems.POLYMER)
            .defineAndUnlockIfHas('C', Items.TRIPWIRE_HOOK)
            .defineAndUnlockIfHas('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .save(recipeOutput);

        // Smart Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART)
            .defineAndUnlockIfHas('A', AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('B', AVPElectronicItems.CPU)
            .defineAndUnlockIfHas('C', AVPElectronicItems.LED_DISPLAY)
            .defineAndUnlockIfHas('D', AVPElectronicItems.POWER_SUPPLY)
            .defineAndUnlockIfHas('E', AVPItems.POLYMER)
            .defineAndUnlockIfHas('F', AVPElectronicItems.CAPACITOR)
            .pattern(" A ")
            .pattern("BCD")
            .pattern("EFE")
            .save(recipeOutput);

        // Generic Stock
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.WEAPON_PART_STOCK_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.POLYMER)
            .defineAndUnlockIfHas('B', AVPItems.INGOT_STEEL)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .save(recipeOutput);
    }

    private AVPWeaponPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
