package com.compat.gigeresque.common.patch;

import com.alien.common.model.alien.Host;
import com.alien.common.util.AlienEmbryoUtil;
import com.compat.gigeresque.GigResources;
import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.item.GigItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.avp.common.util.AVPPredicates;

public class GigSurgeryKitPatch {

    public static void removeParasite(Player player, LivingEntity livingEntity, ItemStack itemStack) {
        if (
            // Entity is NOT a host...
            !(livingEntity instanceof Host host)
                // OR level is client side...
                || player.level().isClientSide()
                // OR the host doesn't have an embryo...
                || !AVPPredicates.hasEmbryo(livingEntity)
                // OR this item is not a Gigeresque surgery kit...
                || !itemStack.is(GigItems.SURGERY_KIT.get())
        ) {
            // Then return.
            return;
        }

        var embryos = AlienEmbryoUtil.birthEmbryos(livingEntity);

        if (!embryos.isEmpty()) {
            // Removes the embryo without any side effects.
            host.removeEmbryo();
            // Apply surgery kit behavior after embryo is removed.
            applySurgeryKitBehavior(player, livingEntity, itemStack);
        }
    }

    private static void applySurgeryKitBehavior(Player player, LivingEntity livingEntity, ItemStack itemStack) {
        player.getCooldowns().addCooldown(itemStack.getItem(), CommonMod.config.surgeryKitCooldownTicks);

        if (!player.isCreative() || !player.isSpectator()) {
            itemStack.hurtAndBreak(1, player, livingEntity.getEquipmentSlotForItem(itemStack));
        }

        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.server.getAdvancements().get(GigResources.location("surgery_kit"));

            if (advancement != null && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                for (var s : serverPlayer.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) {
                    serverPlayer.getAdvancements().award(advancement, s);
                }
            }
        }
    }
}
