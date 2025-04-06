package com.avp.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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

import com.avp.common.block.AVPBlocks;
import com.avp.common.effect.AVPEffects;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPPredicates;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem_ApplyRads extends Item {

    @Shadow
    private Block block;

    public MixinBlockItem_ApplyRads(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof LivingEntity livingEntity) || stack.is(AVPBlocks.LEAD_CHEST.asItem())) {
            return;
        }

        var container = stack.get(DataComponents.CONTAINER);

        if (container == null) {
            return;
        }

        if (AVPPredicates.canBeIrradiated(livingEntity) && containsRadiationItems(container)) {
            var mobEffectInstance = new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0);
            livingEntity.addEffect(mobEffectInstance);
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Unique
    private boolean containsRadiationItems(ItemContainerContents containerContents) {
        return containerContents.nonEmptyStream()
            .anyMatch(
                itemStack -> !itemStack.isEmpty() && itemStack.is(AVPItemTags.RADIATION_ITEMS)
            );
    }

}
