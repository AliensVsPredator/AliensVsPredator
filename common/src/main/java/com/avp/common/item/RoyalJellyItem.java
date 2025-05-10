package com.avp.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.xenomorph.praetorian.Praetorian;

public class RoyalJellyItem extends Item {

    public RoyalJellyItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack itemStack,
        @NotNull Player player,
        @NotNull LivingEntity livingEntity,
        @NotNull InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Alien xenomorph && !(xenomorph instanceof Praetorian)) {
            xenomorph.getEntityData().set(Alien.JELLY_COUNT, xenomorph.getEntityData().get(Alien.JELLY_COUNT) + 1);
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
