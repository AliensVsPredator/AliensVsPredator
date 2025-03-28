package com.avp.mixin;

import com.avp.common.block.AVPBlocks;
import com.avp.common.effect.AVPEffects;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPPredicates;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem_ApplyRads extends Item {
    @Shadow
    private Block block;

    public MixinBlockItem_ApplyRads(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (stack.is(AVPBlocks.LEAD_CHEST.asItem())) {
            return;
        }

        var container = stack.get(DataComponents.CONTAINER);
        if (container == null) {
            return;
        }

        if (containsRadiationItems(container)) {
            var armorCheck = livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                    livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                    livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                    livingEntity.getItemBySlot(EquipmentSlot.FEET).is(AVPItemTags.RADIATION_RESISTANT_ARMOR);
            if (!armorCheck || !AVPPredicates.IS_IMMORTAL.test(livingEntity)) {
                livingEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0));
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Unique
    private boolean containsRadiationItems(ItemContainerContents containerContents) {
        return containerContents.nonEmptyStream().anyMatch(itemStack ->
                !itemStack.isEmpty() && itemStack.is(AVPItemTags.RADIATION_ITEMS)
        );
    }

}
