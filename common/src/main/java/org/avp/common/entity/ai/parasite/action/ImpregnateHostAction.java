package org.avp.common.entity.ai.parasite.action;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.sound.AVPSoundEvents;

public class ImpregnateHostAction extends Action {

    private final AVPAbstractParasite parasite;

    public ImpregnateHostAction(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.isFertile();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        if (parasite.getVehicle() == null) {
            return;
        }

        parasite.setFertile(false);
        parasite.playSound(AVPSoundEvents.INSTANCE.entityParasiteImpregnate.get());
    }
}
