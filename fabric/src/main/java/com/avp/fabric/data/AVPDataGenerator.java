package com.avp.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import com.avp.fabric.data.advancement.AdvancementProvider;
import com.avp.fabric.data.damage_type.DamageTypeBootstrapper;
import com.avp.fabric.data.damage_type.DamageTypeProvider;
import com.avp.fabric.data.jukebox_song.AVPJukeboxSongsProvider;
import com.avp.fabric.data.lang.en_us.EnglishLanguageProvider;
import com.avp.fabric.data.loot.BlockLootTableProvider;
import com.avp.fabric.data.loot.ChestLootTableProvider;
import com.avp.fabric.data.loot.EntityLootTableProvider;
import com.avp.fabric.data.loot.VillagerLootTableProvider;
import com.avp.fabric.data.model.BlockModelProvider;
import com.avp.fabric.data.model.ItemModelProvider;
import com.avp.fabric.data.recipe.RecipeProvider;
import com.avp.fabric.data.tag.AVPBiomeTagProvider;
import com.avp.fabric.data.tag.AVPBlockTagProvider;
import com.avp.fabric.data.tag.AVPDamageTypeTagProvider;
import com.avp.fabric.data.tag.AVPEnchantmentTagProvider;
import com.avp.fabric.data.tag.AVPEntityTypeTagProvider;
import com.avp.fabric.data.tag.AVPItemTagProvider;
import com.avp.fabric.data.tag.AVPMobEffectTagProvider;
import com.avp.fabric.data.tag.AVPPoiTagProvider;
import com.avp.fabric.data.worldgen.AVPBiomeProvider;
import com.avp.fabric.data.worldgen.AVPCaveConfigurations;
import com.avp.fabric.data.worldgen.AVPCavePlacements;
import com.avp.fabric.data.worldgen.AVPOreConfigurations;
import com.avp.fabric.data.worldgen.AVPOrePlacements;
import com.avp.fabric.data.worldgen.AVPWorldGenProvider;

public class AVPDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        // Advancement providers
        pack.addProvider(AdvancementProvider::new);

        // Language providers
        pack.addProvider(EnglishLanguageProvider::new);

        // Model providers
        pack.addProvider(BlockModelProvider::new);
        pack.addProvider(ItemModelProvider::new);

        // Recipe providers
        pack.addProvider(RecipeProvider::new);

        // Tag providers
        pack.addProvider(AVPBlockTagProvider::new);
        pack.addProvider(AVPBiomeTagProvider::new);
        pack.addProvider(AVPDamageTypeTagProvider::new);
        pack.addProvider(AVPEnchantmentTagProvider::new);
        pack.addProvider(AVPEntityTypeTagProvider::new);
        pack.addProvider(AVPItemTagProvider::new);
        pack.addProvider(AVPMobEffectTagProvider::new);
        pack.addProvider(AVPPoiTagProvider::new);

        // Loot providers
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(EntityLootTableProvider::new);
        pack.addProvider(ChestLootTableProvider::new);
        pack.addProvider(VillagerLootTableProvider::new);

        // Worldgen providers
        pack.addProvider(AVPWorldGenProvider::new);
        pack.addProvider(AVPBiomeProvider::new);

        // Damage Type providers
        pack.addProvider(DamageTypeProvider::new);

        // Jukebox Song Providers
        pack.addProvider(AVPJukeboxSongsProvider::new);
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
