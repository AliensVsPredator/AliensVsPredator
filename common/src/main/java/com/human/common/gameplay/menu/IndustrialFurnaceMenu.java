package com.human.common.gameplay.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;

import com.avp.common.registry.init.AVPMenuTypes;
import com.avp.common.registry.init.AVPRecipes;

public class IndustrialFurnaceMenu extends AbstractFurnaceMenu {

    public IndustrialFurnaceMenu(int containerId, Inventory inventory) {
        super(
            AVPMenuTypes.INDUSTRIAL_FURNACE_MENU.get(),
            AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE.get(),
            // TODO: Look into if we need to change this.
            RecipeBookType.BLAST_FURNACE,
            containerId,
            inventory
        );
    }

    public IndustrialFurnaceMenu(
        int containerId,
        Inventory inventory,
        Container industrialFurnaceContainer,
        ContainerData industrialFurnaceData
    ) {
        super(
            AVPMenuTypes.INDUSTRIAL_FURNACE_MENU.get(),
            AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE.get(),
            // TODO: Look into if we need to change this.
            RecipeBookType.BLAST_FURNACE,
            containerId,
            inventory,
            industrialFurnaceContainer,
            industrialFurnaceData
        );
    }
}
