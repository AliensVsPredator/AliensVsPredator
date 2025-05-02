package com.avp.common.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import com.avp.service.Services;

public class AVPRecipes {

    public static final Supplier<? extends RecipeSerializer<IndustrialFurnaceRecipe>> INDUSTRIAL_FURNACE_RECIPE_SERIALIZER = register(
        "industrial_furnace",
        IndustrialFurnaceRecipe::new,
        100
    );

    private static @NotNull <T extends AbstractCookingRecipe> Supplier<SimpleCookingSerializer<T>> register(
        String id,
        AbstractCookingRecipe.Factory<T> factory,
        int cookingTime
    ) {
        return Services.REGISTRY.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            id,
            () -> new SimpleCookingSerializer<>(factory, cookingTime)
        );
    }

    public static final Supplier<RecipeType<IndustrialFurnaceRecipe>> INDUSTRIAL_FURNACE_RECIPE_TYPE = register("industrial_furnace");

    private static @NotNull <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
        return Services.REGISTRY.register(
            BuiltInRegistries.RECIPE_TYPE,
            id,
            () -> new RecipeType<T>() {

                @Override
                public String toString() {
                    return id;
                }
            }
        );
    }

    private AVPRecipes() {}

    public static void initialize() {}
}
