package com.avp.common.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.avp.common.entity.living.FreeMob;
import com.avp.common.entity.living.Host;
import com.avp.common.entity.living.alien.parasite.Parasite;
import com.avp.common.util.AVPPredicates;

public class ParasiteAttachmentManager {

    private static final String IS_FERTILE_KEY = "isFertile";

    private static final String TICKS_ATTACHED_TO_HOST_KEY = "ticksAttachedToHost";

    private final Parasite parasite;

    private final EntityDataAccessor<Boolean> isFertileEDA;

    private int ticksAttachedToHost;

    public ParasiteAttachmentManager(Parasite parasite, EntityDataAccessor<Boolean> isFertileEDA) {
        this.parasite = parasite;
        this.isFertileEDA = isFertileEDA;
        this.ticksAttachedToHost = 0;
    }

    public void tick() {
        if (parasite.level().isClientSide) {
            return;
        }

        var host = getHost();

        if (!isAttachedToHost()) {
            this.ticksAttachedToHost = 0;

            if (host instanceof Mob mob) {
                ((FreeMob) mob).restoreFreedom();
            }

            return;
        }

        Objects.requireNonNull(host);

        if (host instanceof ServerPlayer player && AVPPredicates.IS_IMMORTAL.test(player)) {
            parasite.unRide();
            player.connection.send(new ClientboundSetPassengersPacket(host));
            return;
        }

        host.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 3, true, false, true));
        host.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, true, false, true));
        host.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 3, true, false, true));

        var falloffTimeInTicks = (host instanceof ServerPlayer ? 1.5 : 2.5) * 20 * 60;

        // FIXME: Make time configurable
        if (ticksAttachedToHost() < 20 * 10) {
            // FIXME: Use custom damage type here
            // FIXME: Make damage configurable
            host.hurt(parasite.damageSources().source(DamageTypes.IN_WALL), 0.01F);
        } else if (ticksAttachedToHost() > falloffTimeInTicks) {
            parasite.stopRiding();

            if (host instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetPassengersPacket(host));
            }
        } else {
            if (host instanceof Mob mob) {
                ((FreeMob) mob).removeFreedom();
            }

            // FIXME: Make time configurable
            if (ticksAttachedToHost() >= 20 * 20) {
                host.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, true, false, true));

                if (isFertile()) {
                    ((Host) host).injectEmbryo(parasite);
                    setIsFertile(false);
                    // TODO: Play nasty toob sound
                }
            }
        }

        ticksAttachedToHost++;
    }

    public void restore() {
        setIsFertile(true);
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(IS_FERTILE_KEY)) {
            setIsFertile(compoundTag.getBoolean(IS_FERTILE_KEY));
        }

        this.ticksAttachedToHost = compoundTag.getInt(TICKS_ATTACHED_TO_HOST_KEY);
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean(IS_FERTILE_KEY, isFertile());
        compoundTag.putInt(TICKS_ATTACHED_TO_HOST_KEY, ticksAttachedToHost);
    }

    public @Nullable LivingEntity getHost() {
        return parasite.getVehicle() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    public boolean isAttachedToHost() {
        return getHost() != null;
    }

    public boolean isFertile() {
        return parasite.getEntityData().get(isFertileEDA);
    }

    public void setIsFertile(boolean isFertile) {
        parasite.getEntityData().set(isFertileEDA, isFertile);

        if (!isFertile) {
            ((FreeMob) parasite).removeFreedom();
            parasite.removeAllGoals(AVPPredicates.alwaysTrue());
        } else {
            ((FreeMob) parasite).restoreFreedom();
            parasite.restoreAllGoals();
        }
    }

    public int ticksAttachedToHost() {
        return ticksAttachedToHost;
    }
}
