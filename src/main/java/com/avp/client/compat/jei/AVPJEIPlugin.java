package com.avp.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.recipe.AVPRecipes;

@JeiPlugin
public class AVPJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = AVPResources.location("avp_jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new IndustrialCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var recipes = Minecraft.getInstance().level.getRecipeManager()
            .getAllRecipesFor(AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE)
            .stream()
            .map(RecipeHolder::value)
            .toList();
        AVP.LOGGER.info("Found {} Industrial Furnace recipes", recipes.size());
        registration.addRecipes(IndustrialCategory.RECIPE_TYPE, recipes);
    }
}
