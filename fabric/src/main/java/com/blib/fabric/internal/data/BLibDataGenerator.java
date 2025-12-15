package com.blib.fabric.internal.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.ApiStatus;

import com.blib.fabric.data.recipe.RecipeProvider;
import com.blib.fabric.internal.data.lang.en_us.EnglishLanguageProvider;
import com.blib.fabric.internal.data.tag.BLibEntityTypeTagProvider;
import com.blib.fabric.internal.data.tag.BLibItemTagProvider;

@ApiStatus.Internal
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
