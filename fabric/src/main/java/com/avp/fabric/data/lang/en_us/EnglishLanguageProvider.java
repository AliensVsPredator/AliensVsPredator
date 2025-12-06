package com.avp.fabric.data.lang.en_us;

import com.avp.fabric.data.lang.en_us.provider.EnUsBlockTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsConfigProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsEntityTypeTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsItemTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsTooltipProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Tooltips
        EnUsTooltipProvider.CONSUMER.accept(builder);

        // Configs
        EnUsConfigProvider.CONSUMER.accept(builder);

        // Tags
        EnUsBlockTagProvider.CONSUMER.accept(builder);
        EnUsItemTagProvider.CONSUMER.accept(builder);
        EnUsEntityTypeTagProvider.CONSUMER.accept(builder);
    }
}
