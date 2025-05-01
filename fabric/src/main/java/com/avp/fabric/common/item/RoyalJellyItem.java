package com.avp.fabric.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.common.util.TempAVPPredicates;
import com.avp.fabric.common.entity.living.alien.Alien;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.Praetorian;

public class RoyalJellyItem extends Item {

    public RoyalJellyItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player player,
        LivingEntity livingEntity,
        InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Alien xenomorph && !(xenomorph instanceof Praetorian)) {
            xenomorph.getEntityData().set(Alien.JELLY_COUNT, xenomorph.getEntityData().get(Alien.JELLY_COUNT) + 1);
        }

        if (!TempAVPPredicates.IS_IMMORTAL.test(player)) {
            itemStack.shrink(1);
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
