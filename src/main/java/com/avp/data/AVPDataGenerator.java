package com.avp.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import com.avp.data.advancement.AdvancementProvider;
import com.avp.data.damage_type.DamageTypeBootstrapper;
import com.avp.data.damage_type.DamageTypeProvider;
import com.avp.data.lang.EnglishLanguageProvider;
import com.avp.data.loot.BlockLootTableProvider;
import com.avp.data.loot.EntityLootTableProvider;
import com.avp.data.model.BlockModelProvider;
import com.avp.data.model.ItemModelProvider;
import com.avp.data.recipe.RecipeProvider;
import com.avp.data.tag.AVPBiomeTagProvider;
import com.avp.data.tag.AVPBlockTagProvider;
import com.avp.data.tag.AVPDamageTypeTagProvider;
import com.avp.data.tag.AVPEnchantmentTagProvider;
import com.avp.data.tag.AVPEntityTypeTagProvider;
import com.avp.data.tag.AVPItemTagProvider;
import com.avp.data.tag.AVPMobEffectTagProvider;
import com.avp.data.worldgen.AVPCaveConfigurations;
import com.avp.data.worldgen.AVPCavePlacements;
import com.avp.data.worldgen.AVPOreConfigurations;
import com.avp.data.worldgen.AVPOrePlacements;
import com.avp.data.worldgen.AVPWorldGenProvider;

public class AVPDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(AdvancementProvider::new);
        pack.addProvider(EnglishLanguageProvider::new);
        pack.addProvider(BlockModelProvider::new);
        pack.addProvider(ItemModelProvider::new);
        pack.addProvider(RecipeProvider::new);

        // Tag providers
        pack.addProvider(AVPBlockTagProvider::new);
        pack.addProvider(AVPBiomeTagProvider::new);
        pack.addProvider(AVPDamageTypeTagProvider::new);
        pack.addProvider(AVPEnchantmentTagProvider::new);
        pack.addProvider(AVPEntityTypeTagProvider::new);
        pack.addProvider(AVPItemTagProvider::new);
        pack.addProvider(AVPMobEffectTagProvider::new);

        // Loot providers
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(EntityLootTableProvider::new);

        // Worldgen providers
        pack.addProvider(AVPWorldGenProvider::new);

        pack.addProvider(DamageTypeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, AVPCaveConfigurations::bootstrap);
        registryBuilder.add(Registries.CONFIGURED_FEATURE, AVPOreConfigurations::bootstrap);
        registryBuilder.add(Registries.DAMAGE_TYPE, DamageTypeBootstrapper::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, AVPCavePlacements::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, AVPOrePlacements::bootstrap);
    }
}
