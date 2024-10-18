package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

import org.avp.common.data.tag.AVPBiomeTags;

public class AVPBiomeTagsProvider extends FabricTagProvider<Biome> {

    public AVPBiomeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPBiomeTags.IS_WET)
            .addOptionalTag(BiomeTags.IS_BEACH)
            .addOptionalTag(BiomeTags.IS_OCEAN)
            .addOptionalTag(BiomeTags.IS_RIVER);
    }
}
