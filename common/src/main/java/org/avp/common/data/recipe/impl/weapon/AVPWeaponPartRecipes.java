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
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
            .pattern("AAA")
            .save(recipeOutput);

        // Smart Barrel
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_SMART)
            .requiresAndUnlockIfHas('A', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('B', Items.OBSERVER)
            .save(recipeOutput);

        // Generic Grip
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.polymer)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotSteel)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .save(recipeOutput);

        // Minigun Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_MINIGUN)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .pattern("BBB")
            .pattern("A A")
            .pattern("ABB")
            .save(recipeOutput);

        // Rocket Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_ROCKET)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .save(recipeOutput);

        // Generic Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.polymer)
            .defineAndUnlockIfHas('C', Items.TRIPWIRE_HOOK)
            .defineAndUnlockIfHas('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .save(recipeOutput);

        // Smart Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_SMART)
            .defineAndUnlockIfHas('A', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC)
            .defineAndUnlockIfHas('B', AVPElectronicItems.INSTANCE.cpu)
            .defineAndUnlockIfHas('C', AVPElectronicItems.INSTANCE.ledDisplay)
            .defineAndUnlockIfHas('D', AVPElectronicItems.INSTANCE.powerSupply)
            .defineAndUnlockIfHas('E', AVPItems.INSTANCE.polymer)
            .defineAndUnlockIfHas('F', AVPElectronicItems.INSTANCE.capacitor)
            .pattern(" A ")
            .pattern("BCD")
            .pattern("EFE")
            .save(recipeOutput);

        // Generic Stock
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItems.INSTANCE.WEAPON_PART_STOCK_GENERIC)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.polymer)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.ingotSteel)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .save(recipeOutput);
    }

    private AVPWeaponPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
