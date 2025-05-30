package com.human.common.gameplay.menu;

import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class IndustrialFurnaceRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {

    @Override
    protected @NotNull Set<Item> getFuelItems() {
        return Set.of();
    }
}
