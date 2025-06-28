package com.alien.common.gameplay.entity.living.alien.xenomorph;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EggPickupRequestListener implements GameEventListener {

    private final PositionSource positionSource;

    private final Consumer<Ovomorph> onRequestReceived;

    public EggPickupRequestListener(Entity entity, Consumer<Ovomorph> onRequestReceived) {
        this.onRequestReceived = onRequestReceived;
        this.positionSource = new EntityPositionSource(entity, 0F);
    }

    @Override
    public @NotNull PositionSource getListenerSource() {
        return positionSource;
    }

    @Override
    public int getListenerRadius() {
        return 16;
    }

    @Override
    public boolean handleGameEvent(
        @NotNull ServerLevel serverLevel,
        @NotNull Holder<GameEvent> holder,
        GameEvent.@NotNull Context context,
        @NotNull Vec3 vec3
    ) {
        var sourceEntity = context.sourceEntity();

        if (!(sourceEntity instanceof Ovomorph ovomorph)) {
            return false;
        }

        var alienVariantType = AlienVariantTypes.getFor(ovomorph);
        var deferredHolder = alienVariantType.eggPickupRequestEvent();

        // If the deferred holder is null...
        if (
            deferredHolder == null
                // OR is not the correct event type for this listener...
                || !holder.is(deferredHolder.getHolder())
                // OR ovomorph pickup request is already acknowledged...
                || ovomorph.pickupRequestAcknowledged
                // OR the ovomorph doesn't want to be picked up...
                || !ovomorph.wantsPickup
        ) {
            // Then don't acknowledge the event, it either can't be or already has been acknowledged.
            return false;
        }

        onRequestReceived.accept(ovomorph);

        return true;
    }

    @Override
    public @NotNull GameEventListener.DeliveryMode getDeliveryMode() {
        return GameEventListener.DeliveryMode.BY_DISTANCE;
    }
}
