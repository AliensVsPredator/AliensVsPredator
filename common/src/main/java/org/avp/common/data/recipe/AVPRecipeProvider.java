package org.avp.common.data.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.avp.common.data.recipe.impl.block.AVPIndustrialGlassBlockRecipes;
import org.avp.common.data.recipe.impl.material.AVPSteelRecipes;
import org.jetbrains.annotations.NotNull;

import org.avp.common.data.recipe.impl.AVPArmorRecipes;
import org.avp.common.data.recipe.impl.AVPElectronicRecipes;
import org.avp.common.data.recipe.impl.AVPFoodRecipes;
import org.avp.common.data.recipe.impl.AVPMaterialsRecipes;
import org.avp.common.data.recipe.impl.AVPToolRecipes;
import org.avp.common.data.recipe.impl.block.AVPAlienBlockRecipes;
import org.avp.common.data.recipe.impl.block.AVPIndustrialBlockRecipes;
import org.avp.common.data.recipe.impl.block.AVPPaddingBlockRecipes;
import org.avp.common.data.recipe.impl.block.AVPTempleBlockRecipes;
import org.avp.common.data.recipe.impl.material.AVPAluminumRecipes;
import org.avp.common.data.recipe.impl.material.AVPCobaltRecipes;
import org.avp.common.data.recipe.impl.material.AVPLithiumRecipes;
import org.avp.common.data.recipe.impl.material.AVPNeodymiumRecipes;
import org.avp.common.data.recipe.impl.material.AVPSilicaRecipes;
import org.avp.common.data.recipe.impl.material.AVPTitaniumRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPAmmunitionRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPAmmunitionPartRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPBulletRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPWeaponBlueprintRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPWeaponPartRecipes;
import org.avp.common.data.recipe.impl.weapon.AVPWeaponRecipes;

public class AVPRecipeProvider extends RecipeProvider {

    public AVPRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        AVPAlienBlockRecipes.addAlienBlockRecipes(recipeOutput);
        AVPAluminumRecipes.addAluminumRecipes(recipeOutput);

        AVPAmmunitionRecipes.addAmmoRecipes(recipeOutput);
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
        AVPSteelRecipes.addSteelRecipes(recipeOutput);
        AVPTitaniumRecipes.addTitaniumRecipes(recipeOutput);
        AVPToolRecipes.addToolRecipes(recipeOutput);

        AVPWeaponBlueprintRecipes.addWeaponBlueprintRecipes(recipeOutput);
        AVPWeaponPartRecipes.addWeaponPartRecipes(recipeOutput);
        AVPWeaponRecipes.addWeaponRecipes(recipeOutput);

        AVPIndustrialBlockRecipes.addIndustrialBlockRecipes(recipeOutput);
        AVPIndustrialGlassBlockRecipes.addIndustrialGlassBlockRecipes(recipeOutput);
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
