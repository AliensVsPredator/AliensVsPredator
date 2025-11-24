package com.avp.fabric.data.lang.en_us;

import com.avp.fabric.data.lang.en_us.provider.EnUsAdvancementProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsBiomeTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsBlockProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsBlockTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsConfigProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsCreativeModeTabProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsDamageTypeTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsEnchantmentTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsEntityProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsEntityTypeTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsGeneProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsItemProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsItemTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsKeybindProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsMobEffectTagProvider;
import com.avp.fabric.data.lang.en_us.provider.EnUsSoundEventProvider;
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
        // Villagers
        builder.add("entity.minecraft.villager.commissary", "Commissary Villager");
        builder.add("entity.minecraft.villager.avp.commissary", "Commissary Villager");

        // Blocks
        EnUsBlockProvider.CONSUMER.accept(builder);

        // Creative Mode Tabs
        EnUsCreativeModeTabProvider.CONSUMER.accept(builder);

        // Entities
        EnUsEntityProvider.CONSUMER.accept(builder);

        // Genes
        EnUsGeneProvider.CONSUMER.accept(builder);

        // Items
        EnUsItemProvider.CONSUMER.accept(builder);

        // Sounds
        EnUsSoundEventProvider.CONSUMER.accept(builder);

        // Tooltips
        EnUsTooltipProvider.CONSUMER.accept(builder);

        // Keybinds
        EnUsKeybindProvider.CONSUMER.accept(builder);

        // Containers
        builder.add("container.lead_chest", "Lead Chest");
        builder.add("container.ammo_chest", "Ammo Chest");

        // Death messages
        builder.add("death.attack.acid", "%1$s vaporized in acid");
        builder.add("death.attack.bullet", "%1$s was shot to death");
        builder.add("death.attack.chestbursting", "%1$s gave birth");
        builder.add("death.attack.radiation", "%1$s surrendered to radiation");
        builder.add("death.attack.razor_wire", "%1$s got tangled in razor wire");
        builder.add("death.attack.smothering", "%1$s was smothered to death");

        // Advancements
        EnUsAdvancementProvider.CONSUMER.accept(builder);

        builder.add("avp.industrialfurnace.displayName", "Industrial Furnace");
        builder.add("effect.avp.radiation", "Radiation");

        // Configs
        EnUsConfigProvider.CONSUMER.accept(builder);

        builder.add("display.avp.low_ammunition_warning", "Low Ammo");
        builder.add("display.avp.no_ammunition_warning", "Out of Ammo");

        // Tags
        EnUsBlockTagProvider.CONSUMER.accept(builder);
        EnUsItemTagProvider.CONSUMER.accept(builder);
        EnUsEnchantmentTagProvider.CONSUMER.accept(builder);
        EnUsEntityTypeTagProvider.CONSUMER.accept(builder);
        EnUsMobEffectTagProvider.CONSUMER.accept(builder);
        EnUsDamageTypeTagProvider.CONSUMER.accept(builder);
        EnUsBiomeTagProvider.CONSUMER.accept(builder);
    }
}
