package com.avp.mixin;

import com.alien.common.model.alien.Host;
import com.alien.common.registry.InfectionRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.util.AVPPredicates;

@Mixin(ChorusFruitItem.class)
public class MixinItem_ChorusEmbryo {

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void removeEmbryo(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (
            // Entity eating chorus fruit is NOT a host...
            !(livingEntity instanceof Host host)
                // OR level is client side...
                || level.isClientSide()
                // OR the host doesn't have an embryo...
                || !AVPPredicates.hasEmbryo(livingEntity)
        ) {
            // Then return, there's no embryo to teleport out of the host. Returning here will let the normal
            // chorus fruit behavior play out.
            return;
        }

        var parasiteType = host.getParasiteType();

        if (parasiteType == null) {
            // Parasite type is null, despite our hasEmbryo check earlier above. Not much we can do in this case.
            return;
        }

        var alienInfection = InfectionRegistry.get(livingEntity.getType(), parasiteType).unwrapOr(null);

        if (alienInfection == null) {
            return;
        }

        var embryoType = alienInfection.embryoType();

        var embryo = embryoType.create(level);

        if (!(embryo instanceof LivingEntity livingEmbryo)) {
            // Embryo is null or not a living entity, not much we can do in this case.
            // We need the embryo to be a living entity in order to do potion effect / teleportation behavior.
            return;
        }

        // before attempting to teleport, we set the embryo's position to the host's position so that if the
        // teleportation
        // fails, then the embryo will at the very least be at the host's feet.
        livingEmbryo.setPos(livingEntity.position());
        // Attempt teleportation. We don't need a result from this since it wouldn't help anyway.
        tryTeleportingEntity(livingEmbryo);
        // Add the embryo to the world after we've moved it.
        level.addFreshEntity(livingEmbryo);

        // Copies effects from the host to the embryo.
        // TODO: This logic is duplicated elsewhere, need to unify this with other embryo ejection behavior.
        for (var effect : livingEntity.getActiveEffects()) {
            livingEmbryo.addEffect(new MobEffectInstance(effect));
        }

        if (livingEntity instanceof Player player) {
            player.resetCurrentImpulseContext();
            player.getCooldowns().addCooldown(stack.getItem(), 20);
        }

        // Removes the embryo without any side effects.
        host.clearParasiteType();

        // Return here so that the rest of the chorus fruit behavior to teleport the host entity doesn't happen.
        cir.setReturnValue(stack);
    }

    // This function is largely based on the logic in ChorusFruitItem#finishUsingItem (mojang mappings).
    @Unique
    private static void tryTeleportingEntity(LivingEntity livingEntity) {
        var level = livingEntity.level();

        // Sanity check for if the level is server-side, in case this function is ever moved or publicized.
        if (level.isClientSide) {
            return;
        }

        if (livingEntity.isPassenger()) {
            livingEntity.stopRiding();
        }

        var entityPos = livingEntity.position();

        for (var i = 0; i < 16; i++) {
            var xOffset = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
            var yOffset = Mth.clamp(
                livingEntity.getY() + (double) (livingEntity.getRandom().nextInt(16) - 8),
                level.getMinBuildHeight(),
                level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() - 1
            );
            var zOffset = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;

            if (!livingEntity.randomTeleport(xOffset, yOffset, zOffset, true)) {
                // Failed to teleport, continue to the next attempt.
                continue;
            }

            // Teleported successfully, run events/sound effects and reset the entity's fall distance.
            level.gameEvent(GameEvent.TELEPORT, entityPos, GameEvent.Context.of(livingEntity));
            level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.PLAYERS
            );
            livingEntity.resetFallDistance();
        }
    }
}
