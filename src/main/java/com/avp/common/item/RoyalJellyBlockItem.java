package com.avp.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.avp.common.util.AVPPredicates;
import org.jetbrains.annotations.NotNull;

public class RoyalJellyBlockItem extends BlockItem {

    public RoyalJellyBlockItem(Block block) {
        super(block, new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player player,
        LivingEntity livingEntity,
        InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Praetorian praetorian) {
            praetorian.getEntityData().set(Xenomorph.JELLY_COUNT, 10);
        }

        if (!AVPPredicates.IS_IMMORTAL.test(player)) {
            itemStack.shrink(1);
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
