package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPWeaponPartItems;

public final class AVPWeaponPartRecipes {

    public static void addWeaponPartRecipes(RecipeOutput recipeOutput) {
        // Generic Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .pattern("AAA")
            .save(recipeOutput);

        // Smart Barrel
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_SMART)
            .requiresAndUnlockIfHas('A', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('B', Items.OBSERVER)
            .save(recipeOutput);

        // Generic Grip
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.POLYMER)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.INGOT_STEEL)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .save(recipeOutput);

        // Minigun Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_MINIGUN)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .pattern("BBB")
            .pattern("A A")
            .pattern("ABB")
            .save(recipeOutput);

        // Rocket Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_ROCKET)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .save(recipeOutput);

        // Generic Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_STEEL)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.POLYMER)
            .defineAndUnlockIfHas('C', Items.TRIPWIRE_HOOK)
            .defineAndUnlockIfHas('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .save(recipeOutput);

        // Smart Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_SMART)
            .defineAndUnlockIfHas('A', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.CPU)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.LED_DISPLAY)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.POWER_SUPPLY)
            .defineAndUnlockIfHas('E', AVPItems.INSTANCE.POLYMER)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.CAPACITOR)
            .pattern(" A ")
            .pattern("BCD")
            .pattern("EFE")
            .save(recipeOutput);

        // Generic Stock
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_STOCK_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.POLYMER)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.INGOT_STEEL)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .save(recipeOutput);
    }

    private AVPWeaponPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
