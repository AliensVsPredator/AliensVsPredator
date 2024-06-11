package org.avp.common.ai.ovamorph.action;

import org.avp.api.ai.CostConstraint;
import org.avp.api.ai.action.Action;
import org.avp.common.game.entity.AbstractOvamorph;
import org.avp.common.game.sound.AVPSoundEventRegistry;

public class OpenAction extends Action {

    private final AbstractOvamorph ovamorph;

    public OpenAction(AbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
    }

    @Override
    public boolean isValid() {
        return !ovamorph.isOpen.get();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        ovamorph.playSound(AVPSoundEventRegistry.INSTANCE.entityOvamorphOpen.get());
        ovamorph.isOpening.set(true);
    }
}
