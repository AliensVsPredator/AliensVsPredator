package com.alien.common.gameplay.item;

import com.alien.common.gameplay.entity.living.alien.Alien;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PoisonJellyItem extends Item {

    public PoisonJellyItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack itemStack,
        @NotNull Player player,
        @NotNull LivingEntity livingEntity,
        @NotNull InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Alien alien && !alien.isPoisoned()) {
            alien.setPoisoned(true);
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
