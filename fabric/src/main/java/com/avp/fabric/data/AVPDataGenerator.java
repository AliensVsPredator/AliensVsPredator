package com.avp.fabric.data;

import com.avp.fabric.data.gene_bonus_data.GeneBonusDataSubProvider;
import com.avp.fabric.data.lang.en_us.EnglishLanguageProvider;
import com.avp.fabric.data.recipe.RecipeProvider;
import com.avp.fabric.data.tag.AVPEntityTypeTagProvider;
import com.avp.fabric.data.tag.AVPItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class AVPDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        // Language providers
        pack.addProvider(EnglishLanguageProvider::new);

        // Recipe providers
        pack.addProvider(RecipeProvider::new);

        // Tag providers
        pack.addProvider(AVPEntityTypeTagProvider::new);
        pack.addProvider(AVPItemTagProvider::new);

        // Custom Providers
        pack.addProvider(GeneBonusDataSubProvider::new);
    }
}
