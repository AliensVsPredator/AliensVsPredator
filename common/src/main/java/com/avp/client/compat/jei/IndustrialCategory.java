package com.avp.client.compat.jei;

import com.human.common.gameplay.recipe.IndustrialFurnaceRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.registry.init.block.AVPBlocks;

public class IndustrialCategory implements IRecipeCategory<IndustrialFurnaceRecipe> {

    public IGuiHelper guiHelper;

    public IndustrialCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public @NotNull RecipeType<IndustrialFurnaceRecipe> getRecipeType() {
        return AVPJEIPlugin.INDUSTRIAL_FURNACE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return AVPBlocks.INDUSTRIAL_FURNACE.get().getName();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AVPBlocks.INDUSTRIAL_FURNACE.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IndustrialFurnaceRecipe recipe, @NotNull IFocusGroup focuses) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        builder.addInputSlot(1, 19)
            .setStandardSlotBackground()
            .addIngredients(recipe.getIngredients().getFirst());

        builder.addOutputSlot(61, 19)
            .setOutputSlotBackground()
            .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, @NotNull IndustrialFurnaceRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(recipe.getCookingTime()).setPosition(26, 17);
    }

    @Override
    public boolean isHandled(@NotNull IndustrialFurnaceRecipe recipe) {
        return IRecipeCategory.super.isHandled(recipe);
    }
}
