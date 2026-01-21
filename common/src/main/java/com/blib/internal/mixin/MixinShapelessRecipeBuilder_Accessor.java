package com.blib.internal.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapelessRecipeBuilder.class)
public interface MixinShapelessRecipeBuilder_Accessor {

    @Accessor(value = "category")
    RecipeCategory getCategory();

    @Accessor(value = "criteria")
    Map<String, Criterion<?>> getCriteria();

    @Accessor(value = "group")
    @Nullable
    String getGroup();

    @Accessor(value = "ingredients")
    NonNullList<Ingredient> getIngredients();

    @Invoker(value = "ensureValid")
    void invokeEnsureValid(ResourceLocation id);
}
