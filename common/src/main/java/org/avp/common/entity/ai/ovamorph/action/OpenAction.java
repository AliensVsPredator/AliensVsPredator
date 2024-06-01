package org.avp.common.entity.ai.ovamorph.action;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.sound.AVPSoundEvents;

public class OpenAction extends Action {

    private final AVPAbstractOvamorph ovamorph;

    public OpenAction(AVPAbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
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
        ovamorph.playSound(AVPSoundEvents.INSTANCE.entityOvamorphOpen.get());
        ovamorph.isOpening.set(true);
    }
}
