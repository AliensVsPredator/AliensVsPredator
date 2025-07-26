package com.alien.common.gameplay.item;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.registry.init.AlienBlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RoyalJellyBlockItem extends BlockItem {

    public RoyalJellyBlockItem() {
        super(AlienBlocks.ROYAL_JELLY_BLOCK.get(), new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack itemStack,
        @NotNull Player player,
        @NotNull LivingEntity livingEntity,
        @NotNull InteractionHand interactionHand
    ) {
        if (
            livingEntity instanceof Alien alien
                && !alien.isPoisoned()
                && alien.getMaxJellyToGrowth() != null
        ) {
            alien.setPersistenceRequired();
            alien.setJellyCount(alien.getJellyCount() + 9);
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
