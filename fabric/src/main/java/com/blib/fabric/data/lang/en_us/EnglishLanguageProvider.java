package com.blib.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import com.blib.fabric.data.lang.en_us.provider.EnUsBlockTagProvider;
import com.blib.fabric.data.lang.en_us.provider.EnUsEntityTypeTagProvider;
import com.blib.fabric.data.lang.en_us.provider.EnUsItemTagProvider;
import com.blib.fabric.data.lang.en_us.provider.EnUsTooltipProvider;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Tooltips
        EnUsTooltipProvider.CONSUMER.accept(builder);

        // Tags
        EnUsBlockTagProvider.CONSUMER.accept(builder);
        EnUsItemTagProvider.CONSUMER.accept(builder);
        EnUsEntityTypeTagProvider.CONSUMER.accept(builder);
    }
}
