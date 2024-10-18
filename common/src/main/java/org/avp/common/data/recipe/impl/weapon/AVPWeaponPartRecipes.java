package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.registry.item.AVPWeaponPartItemRegistry;

public final class AVPWeaponPartRecipes {

    public static void addWeaponPartRecipes(RecipeOutput recipeOutput) {
        // Generic Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelGeneric)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("AAA")
            .save(recipeOutput);

        // Smart Barrel
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelSmart)
            .requiresAndUnlockIfHas('A', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelGeneric)
            .requiresAndUnlockIfHas('B', Items.OBSERVER)
            .save(recipeOutput);

        // Generic Grip
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.polymer)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("B")
            .pattern("A")
            .pattern("A")
            .save(recipeOutput);

        // Minigun Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelMinigun)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelGeneric)
            .pattern("BBB")
            .pattern("A A")
            .pattern("BBB")
            .save(recipeOutput);

        // Rocket Barrel
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelRocket)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("AAA")
            .pattern("   ")
            .pattern("AAA")
            .save(recipeOutput);

        // Generic Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverGeneric)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotSteel)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.polymer)
            .defineAndUnlockIfHas('C', Items.TRIPWIRE_HOOK)
            .defineAndUnlockIfHas('D', Items.STONE_BUTTON)
            .pattern("AAA")
            .pattern("BAC")
            .pattern("BDB")
            .save(recipeOutput);

        // Smart Receiver
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverSmart)
            .defineAndUnlockIfHas('A', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverGeneric)
            .defineAndUnlockIfHas('B', AVPElectronicItemRegistry.INSTANCE.cpu)
            .defineAndUnlockIfHas('C', AVPElectronicItemRegistry.INSTANCE.ledDisplay)
            .defineAndUnlockIfHas('D', AVPElectronicItemRegistry.INSTANCE.battery)
            .defineAndUnlockIfHas('E', AVPItemRegistry.INSTANCE.polymer)
            .defineAndUnlockIfHas('F', AVPElectronicItemRegistry.INSTANCE.capacitor)
            .pattern(" A ")
            .pattern("BCD")
            .pattern("EFE")
            .save(recipeOutput);

        // Generic Stock
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPWeaponPartItemRegistry.INSTANCE.weaponPartStockGeneric)
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.polymer)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("  A")
            .pattern("BAA")
            .pattern("  A")
            .save(recipeOutput);
    }

    private AVPWeaponPartRecipes() {
        throw new UnsupportedOperationException();
    }
}
