package org.avp.common.entity.ai.parasite.action.fertile;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.sound.AVPSoundEvents;

public class ImpregnateHostAction extends Action {

    private final Monster parasite;

    public ImpregnateHostAction(Monster parasite) {
        this.parasite = parasite;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        ((Parasite) parasite).setFertile(false);
        parasite.playSound(AVPSoundEvents.INSTANCE.entityParasiteImpregnate.get());
    }
}
