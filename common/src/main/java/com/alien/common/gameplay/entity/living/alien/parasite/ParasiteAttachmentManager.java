package com.alien.common.gameplay.entity.living.alien.parasite;

import com.alien.common.model.alien.FreeMob;
import com.alien.common.model.alien.Host;
import com.lib.common.network.DataAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.avp.common.registry.key.AVPDamageTypeKeys;
import com.avp.common.util.AVPPredicates;

public class ParasiteAttachmentManager {

    private final Parasite parasite;

    private final DataAccessor<Integer> ticksAttachedToHost;

    public ParasiteAttachmentManager(Parasite parasite) {
        this.parasite = parasite;
        this.ticksAttachedToHost = parasite.getDataContainer()
            .<Integer>builder("ticksAttachedToHost")
            .persistent(Codec.INT)
            .build(0);
    }

    public void tick() {
        if (parasite.level().isClientSide) {
            return;
        }

        var host = getHost();

        if (!isAttachedToHost()) {
            ticksAttachedToHost.reset();

            if (host instanceof Mob mob) {
                ((FreeMob) mob).restoreFreedom();
            }

            return;
        }

        Objects.requireNonNull(host);

        if (!AVPPredicates.isHost(host)) {
            parasite.unRide();

            if (host instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetPassengersPacket(host));
            }
            return;
        }

        if (parasite.isDeadOrDying()) {
            parasite.unRide();
            return;
        }

        var effectTimeInTicks = 20 * 4;
        host.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, effectTimeInTicks, 3, true, false, true));
        host.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectTimeInTicks, 3, true, false, true));
        host.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, effectTimeInTicks, 3, true, false, true));

        var falloffTimeInTicks = (host instanceof ServerPlayer ? 1.5 : 2.5) * 20 * 60;

        // TODO: Make time configurable
        if (ticksAttachedToHost() < 20 * 10) {
            host.hurt(parasite.damageSources().source(AVPDamageTypeKeys.SMOTHERING), 0.01F);
        } else if (ticksAttachedToHost() > falloffTimeInTicks) {
            parasite.stopRiding();

            if (host instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetPassengersPacket(host));
            }
        } else {
            host.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectTimeInTicks, 3, true, false, true));

            if (host instanceof Mob mob) {
                ((FreeMob) mob).removeFreedom();
            }

            // TODO: Make time configurable
            if (ticksAttachedToHost() >= 20 * 20) {

                if (parasite.isFertile.get()) {
                    ((Host) host).implantEmbryo(parasite);
                    parasite.isFertile.set(false);
                    // TODO: Play nasty toob sound
                }
            }
        }

        host.setAirSupply(host.getMaxAirSupply());
        ticksAttachedToHost.set(ticksAttachedToHost.get() + 1);
    }

    public void restore() {
        parasite.isFertile.set(true);
    }

    public @Nullable LivingEntity getHost() {
        return parasite.getVehicle() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    public boolean isAttachedToHost() {
        return getHost() != null && parasite.isAlive();
    }

    public int ticksAttachedToHost() {
        return ticksAttachedToHost.get();
    }
}
