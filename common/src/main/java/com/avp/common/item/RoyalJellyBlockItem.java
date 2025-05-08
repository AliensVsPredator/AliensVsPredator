package com.avp.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.AVPBlocks;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.living.alien.xenomorph.praetorian.Praetorian;

public class RoyalJellyBlockItem extends BlockItem {

    public RoyalJellyBlockItem() {
        super(AVPBlocks.ROYAL_JELLY_BLOCK.get(), new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack itemStack,
        @NotNull Player player,
        @NotNull LivingEntity livingEntity,
        @NotNull InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Praetorian praetorian) {
            praetorian.getEntityData().set(Xenomorph.JELLY_COUNT, 10);
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
