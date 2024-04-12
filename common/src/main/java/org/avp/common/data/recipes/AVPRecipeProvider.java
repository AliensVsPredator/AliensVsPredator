package org.avp.common.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class AVPRecipeProvider extends RecipeProvider {

    public AVPRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        AVPAluminumRecipes.addAluminumRecipes(recipeOutput);
        AVPAmmoRecipes.addAmmoRecipes(recipeOutput);
        AVPAmmunitionPartRecipes.addAmmunitionPartRecipes(recipeOutput);
        AVPArmorRecipes.addArmorRecipes(recipeOutput);
        AVPBulletRecipes.addRecipes(recipeOutput);
        AVPCobaltRecipes.addCobaltRecipes(recipeOutput);
        AVPFoodRecipes.addFoodRecipes(recipeOutput);
        AVPElectronicRecipes.addElectronicRecipes(recipeOutput);
        AVPLithiumRecipes.addLithiumRecipes(recipeOutput);
        AVPMaterialsRecipes.addMaterialRecipes(recipeOutput);
        AVPNeodymiumRecipes.addNeodymiumRecipes(recipeOutput);
        AVPSilicaRecipes.addSilicaRecipes(recipeOutput);
        AVPToolRecipes.addToolRecipes(recipeOutput);
        AVPWeaponBlueprintRecipes.addWeaponBlueprintRecipes(recipeOutput);
        AVPWeaponPartRecipes.addWeaponPartRecipes(recipeOutput);
        AVPWeaponRecipes.addWeaponRecipes(recipeOutput);

        AVPIndustrialBlockRecipes.addIndustrialBlockRecipes(recipeOutput);
        AVPPaddingBlockRecipes.addPaddingBlockRecipes(recipeOutput);
        AVPTempleBlockRecipes.addTempleBlockRecipes(recipeOutput);
    }

    public static @NotNull Criterion<InventoryChangeTrigger.TriggerInstance> has(@NotNull ItemLike itemLike) {
        return RecipeProvider.has(itemLike);
    }

    public static @NotNull Criterion<InventoryChangeTrigger.TriggerInstance> has(@NotNull TagKey<Item> tag) {
        return RecipeProvider.has(tag);
    }

    public static void stonecutterRecipeFromBase(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        ItemLike output,
        ItemLike input,
        int count
    ) {
        RecipeProvider.stonecutterResultFromBase(recipeOutput, recipeCategory, output, input, count);
    }
}
