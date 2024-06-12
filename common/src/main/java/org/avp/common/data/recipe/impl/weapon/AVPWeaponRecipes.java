package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.registry.item.AVPWeaponBlueprintItemRegistry;
import org.avp.common.registry.item.AVPWeaponItemRegistry;
import org.avp.common.registry.item.AVPWeaponPartItemRegistry;

public final class AVPWeaponRecipes {

    public static void addWeaponRecipes(RecipeOutput recipeOutput) {
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprint3712Shotgun,
            AVPWeaponItemRegistry.INSTANCE.weapon3712Shotgun,
            true
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintAk47, AVPWeaponItemRegistry.INSTANCE.weaponAk47, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintF90Rifle,
            AVPWeaponItemRegistry.INSTANCE.weaponF90Rifle,
            true
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintFlamethrowerSevastopol,
            AVPWeaponItemRegistry.INSTANCE.weaponFlamethrowerSevastopol,
            false
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM4Carbine,
            AVPWeaponItemRegistry.INSTANCE.weaponM4Carbine,
            true
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM88Mod4CombatPistol,
            AVPWeaponItemRegistry.INSTANCE.weaponM88Mod4CombatPistol,
            false
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintSniperRifle,
            AVPWeaponItemRegistry.INSTANCE.weaponSniperRifle,
            true
        );

        // Pulse Rifle
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.weaponM41APulseRifle)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM41APulseRifle)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelGeneric)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverSmart) // Different receiver
                                                                                              // here.
            .requiresAndUnlockIfHas('E', AVPWeaponPartItemRegistry.INSTANCE.weaponPartStockGeneric)
            .save(recipeOutput);

        // Smartgun
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.weaponM56Smartgun)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM56Smartgun)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelSmart)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverSmart) // Different receiver
                                                                                              // here.
            .save(recipeOutput);

        // Old Painless
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.weaponOldPainless)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintOldPainless)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelMinigun)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric, 2) // Two grips for a big
                                                                                               // gun.
            .requiresAndUnlockIfHas('D', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverGeneric)
            .save(recipeOutput);

        // SADAR
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItemRegistry.INSTANCE.weaponM83A2Sadar)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItemRegistry.INSTANCE.blueprintM83A2Sadar)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelRocket)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverSmart)
            .save(recipeOutput);
    }

    private static void addGenericWeaponRecipe(
        RecipeOutput recipeOutput,
        BLHolder<Item> blueprintHolder,
        BLHolder<Item> weaponHolder,
        boolean hasStock
    ) {
        var builder = AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, weaponHolder)
            .requiresAndUnlockIfHas('A', blueprintHolder)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItemRegistry.INSTANCE.weaponPartBarrelGeneric)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItemRegistry.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItemRegistry.INSTANCE.weaponPartReceiverGeneric);

        if (hasStock) {
            builder.requiresAndUnlockIfHas('E', AVPWeaponPartItemRegistry.INSTANCE.weaponPartStockGeneric);
        }

        builder.save(recipeOutput);
    }

    private AVPWeaponRecipes() {
        throw new UnsupportedOperationException();
    }
}
