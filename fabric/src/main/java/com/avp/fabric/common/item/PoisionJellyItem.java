package com.avp.fabric.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.fabric.common.entity.living.alien.Alien;
import com.avp.fabric.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.common.entity.gene.GeneKeys;
import com.avp.fabric.common.util.AVPPredicates;

public class PoisionJellyItem extends Item {

    public PoisionJellyItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player player,
        LivingEntity livingEntity,
        InteractionHand interactionHand
    ) {
        if (livingEntity instanceof Alien xenomorph) {
            xenomorph.getEntityData().set(Alien.IS_POISONED, true);
        }

        if (livingEntity instanceof Ovamorph ovamorph && ovamorph.isRoyal() && !ovamorph.isAberrant() && !ovamorph.isNetherAfflicted()) {
            ovamorph.geneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
        }

        if (!AVPPredicates.IS_IMMORTAL.test(player)) {
            itemStack.shrink(1);
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
