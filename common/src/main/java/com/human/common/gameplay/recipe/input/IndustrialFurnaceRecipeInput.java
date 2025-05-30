package com.human.common.gameplay.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record IndustrialFurnaceRecipeInput(ItemStack stack) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int i) {
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }

}
