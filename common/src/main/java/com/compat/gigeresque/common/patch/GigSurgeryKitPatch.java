package com.compat.gigeresque.common.patch;

import com.alien.common.model.alien.Host;
import com.alien.common.registry.InfectionRegistry;
import com.compat.gigeresque.GigResources;
import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.item.GigItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GigSurgeryKitPatch {

    public static void removeParasite(Player player, LivingEntity livingEntity, ItemStack itemStack) {
        if (!(livingEntity instanceof Host host)) {
            return;
        }

        if (!itemStack.is(GigItems.SURGERY_KIT.get())) {
            return;
        }

        var parasiteType = host.getEmbryoType();

        if (parasiteType == null) {
            return;
        }

        var alienInfection = InfectionRegistry.get(livingEntity.getType(), parasiteType).unwrapOr(null);

        if (alienInfection == null) {
            return;
        }

        var embryoType = alienInfection.embryoType();

        var embryo = embryoType.create(player.level());

        if (!(embryo instanceof LivingEntity livingEmbryo)) {
            return;
        }

        livingEmbryo.setPos(livingEntity.position());
        player.level().addFreshEntity(livingEmbryo);

        // TODO: This logic is duplicated elsewhere, need to unify this with other embryo ejection behavior.
        for (var effect : livingEntity.getActiveEffects()) {
            livingEmbryo.addEffect(new MobEffectInstance(effect));
        }

        host.removeEmbryo();

        applySurgeryKitBehavior(player, livingEntity, itemStack);
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
