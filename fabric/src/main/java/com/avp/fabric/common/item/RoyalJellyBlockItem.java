package com.avp.fabric.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import com.avp.common.util.TempAVPPredicates;
import com.avp.fabric.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.Praetorian;

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

        if (!TempAVPPredicates.IS_IMMORTAL.test(player)) {
            itemStack.shrink(1);
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
