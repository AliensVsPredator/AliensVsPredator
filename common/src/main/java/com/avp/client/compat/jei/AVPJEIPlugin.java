package com.avp.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;
import com.avp.common.recipe.AVPRecipes;
import com.avp.common.recipe.IndustrialFurnaceRecipe;

@JeiPlugin
public class AVPJEIPlugin implements IModPlugin {

    public static final RecipeType<IndustrialFurnaceRecipe> INDUSTRIAL_FURNACE_TYPE = RecipeType.create(
        AVP.MOD_ID,
        "smeltery",
        IndustrialFurnaceRecipe.class
    );

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return AVPResources.location("plugin_" + AVP.MOD_ID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new IndustrialCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(
            INDUSTRIAL_FURNACE_TYPE,
            recipeManager.getAllRecipesFor(AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList()
        );
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AVPBlocks.INDUSTRIAL_FURNACE.get()), INDUSTRIAL_FURNACE_TYPE);
    }

    @Override
    public void registerModInfo(IModInfoRegistration modAliasRegistration) {
        modAliasRegistration.addModAliases(AVP.MOD_ID, "avp");
    }
}
