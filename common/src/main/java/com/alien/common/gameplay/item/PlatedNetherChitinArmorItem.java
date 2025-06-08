package com.alien.common.gameplay.item;

import com.alien.common.registry.init.AlienArmorMaterials;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.lib.common.data.TooltipHintBuilder;
import com.lib.common.model.TooltipCategoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.data.TooltipTranslationKeys;

public class PlatedNetherChitinArmorItem extends ArmorItem {

    private static final List<Component> TOOLTIP_COMPONENTS = new TooltipHintBuilder()
        .addCategory(TooltipCategoryType.WHEN_FULL_ARMOR_SET_EQUIPPED)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_FIRE_RESISTANCE)
        .build();

    public PlatedNetherChitinArmorItem(Type type) {
        super(
            AlienArmorMaterials.PLATED_NETHER_CHITIN.getHolder(),
            type,
            new Properties().durability(type.getDurability(AlienArmorItems.PLATED_CHITIN_DURABILITY_MULTIPLIER)).fireResistant()
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
