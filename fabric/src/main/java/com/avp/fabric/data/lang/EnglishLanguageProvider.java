package com.avp.fabric.data.lang;

import com.alien.common.gameplay.hive.HiveBossBarManager;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import com.avp.fabric.data.lang.en_us.EnUsAdvancementProvider;
import com.avp.fabric.data.lang.en_us.EnUsBiomeTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsBlockProvider;
import com.avp.fabric.data.lang.en_us.EnUsBlockTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsConfigProvider;
import com.avp.fabric.data.lang.en_us.EnUsCreativeModeTabProvider;
import com.avp.fabric.data.lang.en_us.EnUsDamageTypeTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsEnchantmentTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsEntityProvider;
import com.avp.fabric.data.lang.en_us.EnUsEntityTypeTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsItemProvider;
import com.avp.fabric.data.lang.en_us.EnUsItemTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsKeybindProvider;
import com.avp.fabric.data.lang.en_us.EnUsMobEffectTagProvider;
import com.avp.fabric.data.lang.en_us.EnUsSoundEventProvider;
import com.avp.fabric.data.lang.en_us.EnUsTooltipProvider;

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

        // Items
        EnUsItemProvider.CONSUMER.accept(builder);

        // Sounds
        EnUsSoundEventProvider.CONSUMER.accept(builder);

        // Jukebox Sounds
        builder.add("jukebox_song.avp.alien_music_1", "Rotch Gwylt - Silver Smile");
        builder.add("jukebox_song.avp.predator_music_1", "Rotch Gwylt - Hunter");

        // Tooltips
        EnUsTooltipProvider.CONSUMER.accept(builder);

        // Keybinds
        EnUsKeybindProvider.CONSUMER.accept(builder);

        // Containers
        builder.add("container.lead_chest", "Lead Chest");
        builder.add("container.ammo_chest", "Ammo Chest");

        // Death messages
        builder.add("death.attack.acid", "%1$s vaporized in acid");
        builder.add("death.attack.radiation", "%1$s surrendered to radiation");
        builder.add("death.attack.razor_wire", "%1$s was struck by razor wire");
        builder.add("death.attack.smothering", "%1$s was smothered to death");

        // Advancements
        EnUsAdvancementProvider.CONSUMER.accept(builder);

        // Hive boss bars
        HiveBossBarManager.ALIEN_VARIANT_TO_TRANSLATABLE_STRING_MAP.forEach((alienVariant, translationKey) -> {
            var prefix = switch (alienVariant) {
                case ABERRANT -> "Aberrant ";
                case IRRADIATED -> "Irradiated ";
                case NETHER -> "Nether ";
                case NORMAL -> "";
            };

            builder.add(translationKey, prefix + "Hive");
        });

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
