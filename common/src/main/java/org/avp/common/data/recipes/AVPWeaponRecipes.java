package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;

import org.avp.api.GameObject;
import org.avp.common.item.AVPWeaponBlueprintItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.item.AVPWeaponPartItems;

public final class AVPWeaponRecipes {

    public static void addWeaponRecipes(RecipeOutput recipeOutput) {
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_37_12_SHOTGUN, AVPWeaponItems.WEAPON_37_12_SHOTGUN, true);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_AK_47, AVPWeaponItems.WEAPON_AK_47, true);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_F90_RIFLE, AVPWeaponItems.WEAPON_F90_RIFLE, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL,
            AVPWeaponItems.WEAPON_FLAMETHROWER_SEVASTOPOL,
            false
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_M4_CARBINE, AVPWeaponItems.WEAPON_M4_CARBINE, true);
        addGenericWeaponRecipe(
            recipeOutput,
            AVPWeaponBlueprintItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL,
            AVPWeaponItems.WEAPON_M88MOD4_COMBAT_PISTOL,
            false
        );
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_SNIPER_RIFLE, AVPWeaponItems.WEAPON_SNIPER_RIFLE, true);

        // Pulse Rifle
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M41A_PULSE_RIFLE)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.BLUEPRINT_M41A_PULSE_RIFLE)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART) // Different receiver here.
            .requiresAndUnlockIfHas('E', AVPWeaponPartItems.WEAPON_PART_STOCK_GENERIC)
            .save(recipeOutput);

        // Smartgun
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M56_SMARTGUN)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.BLUEPRINT_M56_SMARTGUN)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_SMART)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART) // Different receiver here.
            .save(recipeOutput);

        // Old Painless
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_OLD_PAINLESS)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.BLUEPRINT_OLD_PAINLESS)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_MINIGUN)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC, 2) // Two grips for a big gun.
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC)
            .save(recipeOutput);

        // SADAR
        AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M83A2_SADAR)
            .requiresAndUnlockIfHas('A', AVPWeaponBlueprintItems.BLUEPRINT_M83A2_SADAR)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_ROCKET)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART)
            .save(recipeOutput);
    }

    private static void addGenericWeaponRecipe(
        RecipeOutput recipeOutput,
        GameObject<Item> blueprintGameObject,
        GameObject<Item> weaponGameObject,
        boolean hasStock
    ) {
        var builder = AVPShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, weaponGameObject)
            .requiresAndUnlockIfHas('A', blueprintGameObject)
            .requiresAndUnlockIfHas('B', AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC)
            .requiresAndUnlockIfHas('C', AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC)
            .requiresAndUnlockIfHas('D', AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC);

        if (hasStock) {
            builder.requiresAndUnlockIfHas('E', AVPWeaponPartItems.WEAPON_PART_STOCK_GENERIC);
        }

        builder.save(recipeOutput);
    }

    private AVPWeaponRecipes() {
        throw new UnsupportedOperationException();
    }
}
