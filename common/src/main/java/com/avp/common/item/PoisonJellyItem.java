package com.avp.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.gene.GeneKeys;

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
        var wasConsumed = false;

        if (livingEntity instanceof Alien alien && !alien.isPoisoned()) {
            alien.setPoisoned(true);
            wasConsumed = true;

            // TODO: This should be moved to the ovomorph class, probably.
            if (
                livingEntity.getType().is(AVPEntityTypeTags.OVOMORPHS)
                    && alien.isRoyal()
                    && !alien.isAberrant()
                    && !alien.isNetherAfflicted()
            ) {
                alien.geneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
            }
        }

        if (wasConsumed) {
            itemStack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
