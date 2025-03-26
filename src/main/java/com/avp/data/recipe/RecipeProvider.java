package com.avp.data.recipe;

import com.avp.data.recipe.builder.IndustrialFurnaceRecipeBuilder;
import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.impl.*;
import com.avp.data.recipe.impl.vanilla.VanillaChestRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaConcreteRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaIronLikeRecipeProvider;
import com.avp.data.recipe.impl.vanilla.VanillaMiscellaneousRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        var builder = RecipeBuilder.with(recipeOutput);
        IndustrialFurnaceRecipeBuilder.ensureRegistration(recipeOutput);
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

        VanillaChestRecipeProvider.provide(builder);
        VanillaConcreteRecipeProvider.provide(builder);
        VanillaIronLikeRecipeProvider.provide(builder);
        VanillaMiscellaneousRecipeProvider.provide(builder);
    }

}
