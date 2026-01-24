package com.blib.fabric.data.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

import com.blib.fabric.data.recipe.builder.RecipeBuilder;
import com.blib.fabric.data.recipe.impl.vanilla.VanillaIronLikeRecipeProvider;
import com.blib.fabric.data.recipe.impl.vanilla.VanillaMiscellaneousRecipeProvider;
import com.blib.mod.BLib;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        var builder = RecipeBuilder.with(BLib.MOD, recipeOutput, this::withConditions);

        VanillaIronLikeRecipeProvider.provide(builder);
        VanillaMiscellaneousRecipeProvider.provide(builder);
    }

}
