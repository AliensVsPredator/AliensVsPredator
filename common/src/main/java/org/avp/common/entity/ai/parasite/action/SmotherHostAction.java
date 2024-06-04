package org.avp.common.entity.ai.parasite.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.util.GravityUtils;

import java.util.Objects;

public class SmotherHostAction extends Action {

    private final int smotherTimeInTicks;

    private final AVPAbstractParasite parasite;

    public SmotherHostAction(AVPAbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        this.smotherTimeInTicks = smotherTimeInTicks;
    }

    @Override
    public boolean isValid() {
        return parasite.getVehicle() instanceof LivingEntity;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        var vehicle = (LivingEntity) Objects.requireNonNull(parasite.getVehicle());

        // TODO: Move this to a cleanup function, otherwise the mob can be left in a broken state.
        if (parasite.getVehicle() instanceof Mob mob) {
            mob.setNoAi(true);
        }

        // Tick counter.
        parasite.incrementTicksAttachedToHost();
        // Apply gravity while mob's "AI" is off.
        GravityUtils.apply(vehicle);
        // Facehuggers provide oxygen to their host.
        vehicle.setAirSupply(vehicle.getMaxAirSupply());
        // Parasites silence host.
        vehicle.setSilent(true);

        if (parasite.getTicksAttachedToHost() >= smotherTimeInTicks) {
            // Reset state.
            if (vehicle instanceof Mob mob) {
                mob.setNoAi(false);
            }
            vehicle.setSilent(false);
            parasite.stopRiding();
        }
    }
}
