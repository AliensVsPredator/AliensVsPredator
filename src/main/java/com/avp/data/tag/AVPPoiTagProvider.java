package com.avp.data.tag;

import com.avp.common.profession.AVPProfessions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class AVPPoiTagProvider extends FabricTagProvider<PoiType> {

    public AVPPoiTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(PoiTypeTags.ACQUIRABLE_JOB_SITE)
            .addOptional(
                AVPProfessions.COMMISAARY_KEY
            );
    }
}
