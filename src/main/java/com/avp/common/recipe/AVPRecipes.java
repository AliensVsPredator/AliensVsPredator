package com.avp.common.recipe;

import com.avp.AVP;
import com.avp.AVPResources;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;

public class AVPRecipes {
    public static final RecipeSerializer<IndustrialFurnaceRecipe> INDUSTRIAL_FURNACE_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, AVPResources.location("industrial_furnace"),
            new SimpleCookingSerializer<>(IndustrialFurnaceRecipe::new,100));

    public static final RecipeType<IndustrialFurnaceRecipe> INDUSTRIAL_FURNACE_RECIPE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE, ResourceLocation.fromNamespaceAndPath(AVP.MOD_ID, "industrial_furnace"), new RecipeType<IndustrialFurnaceRecipe>() {
                @Override
                public String toString() {
                    return "industrial_furnace";
                }
            });

    private AVPRecipes() {}

    public static void initialize() {}
}
