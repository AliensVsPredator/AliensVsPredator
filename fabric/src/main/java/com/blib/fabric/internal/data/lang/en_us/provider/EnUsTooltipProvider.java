package com.blib.fabric.internal.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

import com.blib.common.data.TooltipTranslationKeys;
import com.blib.common.gameplay.model.TooltipCategoryType;

@ApiStatus.Internal
public final class EnUsTooltipProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(TooltipCategoryType.REQUIREMENTS.getTranslationKey(), "Requires");
        builder.add(TooltipCategoryType.WHEN_FULL_ARMOR_SET_EQUIPPED.getTranslationKey(), "Full Set Bonus");
        builder.add(TooltipCategoryType.WHEN_HELMET_EQUIPPED.getTranslationKey(), "Helmet Bonus");
        builder.add(TooltipCategoryType.WHEN_IN_INVENTORY.getTranslationKey(), "When In Inventory");
        builder.add(TooltipCategoryType.WHEN_PLACED_IN_WORLD.getTranslationKey(), "When Placed");
        builder.add(TooltipCategoryType.WHEN_USED.getTranslationKey(), "When Used");
        builder.add(TooltipCategoryType.WHEN_USED_ON_ARMOR_STAND.getTranslationKey(), "When Used On Armor Stand");

        builder.add(TooltipTranslationKeys.EFFECT_FIRE_RESISTANCE, "Fire Resistance");
        builder.add(TooltipTranslationKeys.EFFECT_JUMP_BOOST, "Jump Boost");
        builder.add(TooltipTranslationKeys.EFFECT_WATER_BREATHING, "Water Breathing");
        builder.add(TooltipTranslationKeys.EFFECT_SLOWNESS, "Slowness");
        builder.add(TooltipTranslationKeys.EFFECT_STRENGTH, "Strength");
    };
}
