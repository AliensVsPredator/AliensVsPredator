package com.alien.common.gameplay.entity.living.alien.xenomorph;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class EggPickupManager implements GameEventListener.Provider<EggPickupRequestListener> {

    private final DynamicGameEventListener<EggPickupRequestListener> dynamicEggPickupRequestListener;

    private final EggPickupRequestListener eggPickupRequestListener;

    private final Xenomorph xenomorph;

    private Ovomorph targetOvomorph;

    public EggPickupManager(Xenomorph xenomorph) {
        this.eggPickupRequestListener = new EggPickupRequestListener(xenomorph, this::acknowledgePickupRequest);
        this.dynamicEggPickupRequestListener = new DynamicGameEventListener<>(eggPickupRequestListener);
        this.xenomorph = xenomorph;
    }

    @Override
    public @NotNull EggPickupRequestListener getListener() {
        return eggPickupRequestListener;
    }

    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (xenomorph.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(dynamicEggPickupRequestListener, serverLevel);
        }
    }

    public void tick() {
        if (xenomorph.level().isClientSide) {
            return;
        }

        // Drop all ovomorphs if they can't be held or if the xenomorph has an attack target.
        getPassengerOvomorphs()
            .stream()
            .filter(ovomorph -> xenomorph.getTarget() != null || !ovomorph.canBeHeld())
            .forEach(Entity::stopRiding);

        if (xenomorph.getTarget() != null || (targetOvomorph != null && !targetOvomorph.canBeHeld())) {
            // Set the target ovomorph to null.
            setTargetOvomorph(null);
        }
    }

    private List<Ovomorph> getPassengerOvomorphs() {
        return xenomorph.getPassengers()
            .stream()
            .filter(passenger -> passenger.getType().is(AVPEntityTypeTags.OVOMORPHS))
            .map(entity -> (Ovomorph) entity)
            .toList();
    }

    public @Nullable Ovomorph getTargetOvomorphOrNull() {
        return targetOvomorph;
    }

    public void setTargetOvomorph(Ovomorph targetOvomorph) {
        if (targetOvomorph == null) {
            if (this.targetOvomorph != null) {
                this.targetOvomorph.pickupRequestAcknowledged = false;
            }
        } else {
            targetOvomorph.pickupRequestAcknowledged = true;
        }

        this.targetOvomorph = targetOvomorph;
    }

    private void acknowledgePickupRequest(Ovomorph ovomorph) {
        if (targetOvomorph != null || !getPassengerOvomorphs().isEmpty()) {
            return;
        }

        ovomorph.pickupRequestAcknowledged = true;
        setTargetOvomorph(ovomorph);
    }
}
