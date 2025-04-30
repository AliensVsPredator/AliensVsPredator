package com.avp.fabric.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;

public class IndustrialFurnaceMenu extends AbstractFurnaceMenu {

    public IndustrialFurnaceMenu(int i, Inventory inventory) {
        super(MenuTypes.INDUSTRIAL_FURNACE_MENU, RecipeType.SMELTING, RecipeBookType.FURNACE, i, inventory);
    }

}
