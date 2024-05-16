package org.avp.common.data.recipe.impl.weapon;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.data.recipe.AVPShapelessRecipeBuilder;
import org.avp.common.item.AVPWeaponBlueprintItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.item.AVPWeaponPartItems;

public final class AVPWeaponRecipes {

    public static void addWeaponRecipes(RecipeOutput recipeOutput) {
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprint3712Shotgun,
            AVPWeaponItems.INSTANCE.weapon3712Shotgun,
            true
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.blueprintAk47, AVPWeaponItems.INSTANCE.weaponAk47, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprintF90Rifle,
            AVPWeaponItems.INSTANCE.weaponF90Rifle,
            true
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprintFlamethrowerSevastopol,
            AVPWeaponItems.INSTANCE.weaponFlamethrowerSevastopol,
            false
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprintM4Carbine,
            AVPWeaponItems.INSTANCE.weaponM4Carbine,
            true
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprintM88Mod4CombatPistol,
            AVPWeaponItems.INSTANCE.weaponM88Mod4CombatPistol,
            false
        );
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.blueprintSniperRifle,
            AVPWeaponItems.INSTANCE.weaponSniperRifle,
            true
        );

        // Pulse Rifle
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM41APulseRifle)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.blueprintM41APulseRifle)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.weaponPartBarrelGeneric)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.weaponPartReceiverSmart) // Different receiver
                                                                                                 // here.
            .requiresAndUnlockIfHas('E', AVPWeaponPartItems.INSTANCE.weaponPartStockGeneric)
            .save(recipeOutput);

        // Smartgun
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM56Smartgun)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.blueprintM56Smartgun)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.weaponPartBarrelSmart)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.weaponPartReceiverSmart) // Different receiver
                                                                                                 // here.
            .save(recipeOutput);

        // Old Painless
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponOldPainless)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.blueprintOldPainless)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.weaponPartBarrelMinigun)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.weaponPartGripGeneric, 2) // Two grips for a big
                                                                                                  // gun.
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.weaponPartReceiverGeneric)
            .save(recipeOutput);

        // SADAR
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM83A2Sadar)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.blueprintM83A2Sadar)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.weaponPartBarrelRocket)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.weaponPartReceiverSmart)
            .save(recipeOutput);
    }

    private static void addGenericWeaponRecipe(
        RecipeOutput recipeOutput,
        Holder<Item> blueprintHolder,
        Holder<Item> weaponHolder,
        boolean hasStock
    ) {
        var builder = AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, weaponHolder)
            .requiresAndUnlockIfHas('A', blueprintHolder)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.weaponPartBarrelGeneric)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.weaponPartGripGeneric)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.weaponPartReceiverGeneric);

        if (hasStock) {
            builder.requiresAndUnlockIfHas('E', AVPWeaponPartItems.INSTANCE.weaponPartStockGeneric);
        }

        builder.save(recipeOutput);
    }

    private AVPWeaponRecipes() {
        throw new UnsupportedOperationException();
    }
}
