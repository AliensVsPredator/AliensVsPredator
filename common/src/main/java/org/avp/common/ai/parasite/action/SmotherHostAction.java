package org.avp.common.ai.parasite.action;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.ai.CostConstraint;
import org.avp.api.ai.action.Action;
import org.avp.common.game.entity.AbstractParasite;

public class SmotherHostAction extends Action {

    private final int smotherTimeInTicks;

    private final AbstractParasite parasite;

    public SmotherHostAction(AbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        this.smotherTimeInTicks = smotherTimeInTicks;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
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
        // Tick counter.
        parasite.incrementTicksAttachedToHost();
        parasite.smotherHost();

        if (parasite.getTicksAttachedToHost() >= smotherTimeInTicks) {
            // Reset state.
            parasite.restoreHost();
            parasite.stopRiding();
        }
    }
}
