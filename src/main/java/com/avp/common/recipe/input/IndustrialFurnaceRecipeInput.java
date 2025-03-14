package com.avp.common.recipe.input;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;
import java.util.Set;

public record IndustrialFurnaceRecipeInput(ItemStack stack) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }
}
