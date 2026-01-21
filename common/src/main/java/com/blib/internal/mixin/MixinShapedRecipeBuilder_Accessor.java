package com.blib.internal.mixin;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public interface MixinShapedRecipeBuilder_Accessor {

    @Accessor(value = "category")
    RecipeCategory getCategory();

    @Accessor(value = "criteria")
    Map<String, Criterion<?>> getCriteria();

    @Accessor(value = "group")
    @Nullable
    String getGroup();

    @Accessor(value = "showNotification")
    boolean getShowNotification();

    @Invoker(value = "ensureValid")
    ShapedRecipePattern invokeEnsureValid(ResourceLocation id);
}
