package com.predator.common.gameplay.item;

import com.lib.common.data.TooltipHintBuilder;
import com.lib.common.model.TooltipCategoryType;
import com.predator.common.registry.init.PredatorArmorMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.data.TooltipTranslationKeys;

public class VeritaniumArmorItem extends ArmorItem {

    private static final int VERITANIUM_DURABILITY_MULTIPLIER = 40;

    private static final List<Component> TOOLTIP_COMPONENTS = new TooltipHintBuilder()
        .addCategory(TooltipCategoryType.WHEN_HELMET_EQUIPPED)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_PREVENTS_FACEHUGGING)
        .addCategory(TooltipCategoryType.WHEN_FULL_ARMOR_SET_EQUIPPED)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_JUMP_BOOST)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_STRENGTH)
        .build();

    public VeritaniumArmorItem(Type type) {
        super(
            PredatorArmorMaterials.VERITANIUM.getHolder(),
            type,
            new Properties().durability(type.getDurability(VERITANIUM_DURABILITY_MULTIPLIER)).fireResistant()
        );
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @NotNull TooltipContext context,
        @NotNull List<Component> tooltipComponents,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.addAll(TOOLTIP_COMPONENTS);
    }
}
