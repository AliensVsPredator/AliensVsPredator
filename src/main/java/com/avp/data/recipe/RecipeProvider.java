package com.avp.data.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.impl.ArmorRecipeProvider;
import com.avp.data.recipe.impl.ElectronicItemRecipeProvider;
import com.avp.data.recipe.impl.GlassRecipeProvider;
import com.avp.data.recipe.impl.GunRecipeProvider;
import com.avp.data.recipe.impl.IndustrialConcreteRecipeProvider;
import com.avp.data.recipe.impl.MetalRecipeProvider;
import com.avp.data.recipe.impl.MiscellaneousRecipeProvider;
import com.avp.data.recipe.impl.PaddingRecipeProvider;
import com.avp.data.recipe.impl.PlasticRecipeProvider;
import com.avp.data.recipe.impl.ResinRecipeProvider;
import com.avp.data.recipe.impl.ToolRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaConcreteRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaIronLikeRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaMiscellaneousRecipeProvider;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        var builder = RecipeBuilder.with(recipeOutput);

        ArmorRecipeProvider.provide(builder);
        ElectronicItemRecipeProvider.provide(builder);
        GlassRecipeProvider.provide(builder);
        GunRecipeProvider.provide(builder);
        IndustrialConcreteRecipeProvider.provide(builder);
        MetalRecipeProvider.provide(builder);
        MiscellaneousRecipeProvider.provide(builder);
        PaddingRecipeProvider.provide(builder);
        PlasticRecipeProvider.provide(builder);
        ResinRecipeProvider.provide(builder);
        ToolRecipeProvider.provide(builder);

        VanillaConcreteRecipeProvider.provide(builder);
        VanillaIronLikeRecipeProvider.provide(builder);
        VanillaMiscellaneousRecipeProvider.provide(builder);
    }
}
