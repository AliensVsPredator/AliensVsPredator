package com.avp.fabric.client.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.recipe.AVPRecipes;
import com.avp.common.recipe.IndustrialFurnaceRecipe;
import com.avp.fabric.client.screen.IndustrialFurnaceScreen;

public class AVPREIClient implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new IndustrialCategory());

        registry.addWorkstations(IndustrialCategory.INDUSTRIAL_FURNACE, EntryStacks.of(TempAVPBlocks.INDUSTRIAL_FURNACE.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
            IndustrialFurnaceRecipe.class,
            AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE.get(),
            IndustrialDisplay::new
        );
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(
            screen -> new Rectangle(
                ((screen.width - 176) / 2) + 78,
                ((screen.height - 166) / 2) + 30,
                20,
                25
            ),
            IndustrialFurnaceScreen.class,
            IndustrialCategory.INDUSTRIAL_FURNACE
        );
    }
}
