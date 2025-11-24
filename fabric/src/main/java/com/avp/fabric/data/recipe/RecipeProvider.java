package com.avp.fabric.data.recipe;

import com.avp.fabric.data.recipe.builder.IndustrialFurnaceRecipeBuilder;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.impl.ArmorRecipeProvider;
import com.avp.fabric.data.recipe.impl.ElectronicItemRecipeProvider;
import com.avp.fabric.data.recipe.impl.GlassRecipeProvider;
import com.avp.fabric.data.recipe.impl.GunRecipeProvider;
import com.avp.fabric.data.recipe.impl.IndustrialConcreteRecipeProvider;
import com.avp.fabric.data.recipe.impl.MetalRecipeProvider;
import com.avp.fabric.data.recipe.impl.MiscellaneousRecipeProvider;
import com.avp.fabric.data.recipe.impl.PaddingRecipeProvider;
import com.avp.fabric.data.recipe.impl.PlasticRecipeProvider;
import com.avp.fabric.data.recipe.impl.ToolRecipeProvider;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaChestRecipeProvider;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaConcreteRecipeProvider;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaIronLikeRecipeProvider;
import com.avp.fabric.data.recipe.impl.vanilla.VanillaMiscellaneousRecipeProvider;
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
        ToolRecipeProvider.provide(builder);

        VanillaChestRecipeProvider.provide(builder);
        VanillaConcreteRecipeProvider.provide(builder);
        VanillaIronLikeRecipeProvider.provide(builder);
        VanillaMiscellaneousRecipeProvider.provide(builder);
    }

}
