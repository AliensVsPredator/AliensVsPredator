package com.blib.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import com.blib.fabric.data.lang.en_us.EnglishLanguageProvider;
import com.blib.fabric.data.recipe.RecipeProvider;
import com.blib.fabric.data.tag.BLibEntityTypeTagProvider;
import com.blib.fabric.data.tag.BLibItemTagProvider;

public class BLibDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        // Language providers
        pack.addProvider(EnglishLanguageProvider::new);

        // Recipe providers
        pack.addProvider(RecipeProvider::new);

        // Tag providers
        pack.addProvider(BLibEntityTypeTagProvider::new);
        pack.addProvider(BLibItemTagProvider::new);
    }
}
