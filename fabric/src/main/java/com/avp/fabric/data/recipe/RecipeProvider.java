package com.avp.fabric.data.recipe;

import com.blib.fabric.data.recipe.builder.RecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

import com.avp.AVP;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaIronLikeRecipeProvider;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaMiscellaneousRecipeProvider;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        var builder = RecipeBuilder.with(AVP.MOD, recipeOutput);

        VanillaIronLikeRecipeProvider.provide(builder);
        VanillaMiscellaneousRecipeProvider.provide(builder);
    }

}
