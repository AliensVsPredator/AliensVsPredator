package com.avp.client.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;
import com.avp.common.recipe.IndustrialFurnaceRecipe;

public class IndustrialCategory implements IRecipeCategory<IndustrialFurnaceRecipe> {

    public static final RecipeType<IndustrialFurnaceRecipe> RECIPE_TYPE = RecipeType.create(
        AVP.MOD_ID,
        "industrial_furnace",
        IndustrialFurnaceRecipe.class
    );

    private final IDrawable icon;

    public IndustrialCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AVPBlocks.INDUSTRIAL_FURNACE));
    }

    @Override
    public @NotNull RecipeType<IndustrialFurnaceRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block.avp.industrial_furnace_block");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    // TODO: See what I need to do exactly here
    @Override
    public int getHeight() {
        return 128;
    }

    // TODO: See what I need to do exactly here
    @Override
    public int getWidth() {
        return 128;
    }

    // TODO: See what I need to do exactly here
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IndustrialFurnaceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 18).addIngredients(recipe.getIngredients().getFirst());
    }
}
