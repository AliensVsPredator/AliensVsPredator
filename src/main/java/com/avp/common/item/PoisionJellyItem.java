package com.avp.common.item;

import com.avp.common.entity.living.alien.Alien;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PoisionJellyItem extends Item {

    public PoisionJellyItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof Alien xenomorph) {
            xenomorph.getEntityData().set(Alien.IS_POISONED, true);
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
