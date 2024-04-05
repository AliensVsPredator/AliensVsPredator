package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPWeaponBlueprintItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.item.AVPWeaponPartItems;

/**
 * @author Boston Vanseghi
 */
public final class AVPWeaponRecipes {

    public static void addWeaponRecipes(RecipeOutput recipeOutput) {
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_37_12_SHOTGUN, AVPWeaponItems.WEAPON_37_12_SHOTGUN);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_AK_47, AVPWeaponItems.WEAPON_AK_47);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_F90_RIFLE, AVPWeaponItems.WEAPON_F90_RIFLE);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, AVPWeaponItems.WEAPON_FLAMETHROWER_SEVASTOPOL);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_M4_CARBINE, AVPWeaponItems.WEAPON_M4_CARBINE);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, AVPWeaponItems.WEAPON_M88MOD4_COMBAT_PISTOL);
        addGenericWeaponRecipe(recipeOutput, AVPWeaponBlueprintItems.BLUEPRINT_SNIPER_RIFLE, AVPWeaponItems.WEAPON_SNIPER_RIFLE);

        // Pulse Rifle
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M41A_PULSE_RIFLE.get())
            .requires(AVPWeaponBlueprintItems.BLUEPRINT_M41A_PULSE_RIFLE.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART.get()) // Different receiver here.
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(AVPWeaponBlueprintItems.BLUEPRINT_M41A_PULSE_RIFLE.get()))
            .save(recipeOutput);

        // Smartgun
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M56_SMARTGUN.get())
            .requires(AVPWeaponBlueprintItems.BLUEPRINT_M56_SMARTGUN.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_SMART.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART.get()) // Different receiver here.
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(AVPWeaponBlueprintItems.BLUEPRINT_M56_SMARTGUN.get()))
            .save(recipeOutput);

        // Old Painless
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_OLD_PAINLESS.get())
            .requires(AVPWeaponBlueprintItems.BLUEPRINT_OLD_PAINLESS.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_MINIGUN.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get(), 2) // Two grips for a big gun.
            .requires(AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC.get())
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(AVPWeaponBlueprintItems.BLUEPRINT_OLD_PAINLESS.get()))
            .save(recipeOutput);

        // SADAR
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, AVPWeaponItems.WEAPON_M83A2_SADAR.get())
            .requires(AVPWeaponBlueprintItems.BLUEPRINT_M83A2_SADAR.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_ROCKET.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_RECEIVER_SMART.get())
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(AVPWeaponBlueprintItems.BLUEPRINT_M83A2_SADAR.get()))
            .save(recipeOutput);
    }

    private static void addGenericWeaponRecipe(RecipeOutput recipeOutput, GameObject<Item> blueprintGameObject, GameObject<Item> weaponGameObject) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, weaponGameObject.get())
            .requires(blueprintGameObject.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_BARREL_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_GRIP_GENERIC.get())
            .requires(AVPWeaponPartItems.WEAPON_PART_RECEIVER_GENERIC.get())
            .unlockedBy("has_blueprint", AVPRecipeProvider.has(blueprintGameObject.get()))
            .save(recipeOutput);
    }

    private AVPWeaponRecipes() {
        throw new UnsupportedOperationException();
    }
}
