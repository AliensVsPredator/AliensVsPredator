package org.avp.common.entity.ai.parasite.action.infertile;

import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.util.GravityUtils;

public class DetachFromHostAction extends Action {

    private final int smotherTimeInTicks;

    private final AVPAbstractParasite parasite;

    public DetachFromHostAction(AVPAbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        this.smotherTimeInTicks = smotherTimeInTicks;
    }

    @Override
    public boolean isValid() {
        return parasite.getVehicle() instanceof Mob;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        if (parasite.getTicksAttachedToHost() >= smotherTimeInTicks) {
            if (parasite.getVehicle() instanceof Mob mob) {
                mob.setNoAi(false);
            }
            parasite.stopRiding();
        } else {
            // TODO: Move this to a cleanup function, otherwise the mob can be left in a broken state.
            if (parasite.getVehicle() instanceof Mob mob) {
                mob.setNoAi(true);
                GravityUtils.apply(mob);
            }
            parasite.incrementTicksAttachedToHost();
        }
    }
}
