package com.avp.common.item;

import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PoisionJellyItem extends Item {

    public PoisionJellyItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof Xenomorph xenomorph) {
            xenomorph.getEntityData().set(Xenomorph.IS_POISONED, true);
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
