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
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_37_12_SHOTGUN, AVPWeaponItems.INSTANCE.weapon3712Shotgun, true);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_AK_47, AVPWeaponItems.INSTANCE.weaponAk47, true);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_F90_RIFLE, AVPWeaponItems.INSTANCE.weaponF90Rifle, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_FLAMETHROWER_SEVASTOPOL,
            AVPWeaponItems.INSTANCE.weaponFlamethrowerSevastopol,
            false
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M4_CARBINE, AVPWeaponItems.INSTANCE.weaponM4Carbine, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M88MOD4_COMBAT_PISTOL,
            AVPWeaponItems.INSTANCE.weaponM88Mod4CombatPistol,
            false
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_SNIPER_RIFLE, AVPWeaponItems.INSTANCE.weaponSniperRifle, true);

        // Pulse Rifle
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM41APulseRifle)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M41A_PULSE_RIFLE)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_SMART) // Different receiver here.
            .requiresAndUnlockIfHas('E', AVPWeaponPartItems.INSTANCE.WEAPON_PART_STOCK_GENERIC)
            .save(recipeOutput);

        // Smartgun
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM56Smartgun)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M56_SMARTGUN)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_SMART)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_SMART) // Different receiver here.
            .save(recipeOutput);

        // Old Painless
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponOldPainless)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_OLD_PAINLESS)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_MINIGUN)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC, 2) // Two grips for a big gun.
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC)
            .save(recipeOutput);

        // SADAR
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.INSTANCE.weaponM83A2Sadar)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.INSTANCE.BLUEPRINT_M83A2_SADAR)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_ROCKET)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_SMART)
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
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.INSTANCE.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.INSTANCE.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.INSTANCE.WEAPON_PART_RECEIVER_GENERIC);

        if (hasStock) {
            builder.requiresAndUnlockIfHas('E', AVPWeaponPartItems.INSTANCE.WEAPON_PART_STOCK_GENERIC);
        }

        builder.save(recipeOutput);
    }

    private AVPWeaponRecipes() {
        throw new UnsupportedOperationException();
    }
}
