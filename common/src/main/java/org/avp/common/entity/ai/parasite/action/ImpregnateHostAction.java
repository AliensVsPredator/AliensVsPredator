package org.avp.common.entity.ai.parasite.action;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.sound.AVPSoundEvents;

import java.util.Objects;

public class ImpregnateHostAction extends Action {

    private static final int MAX_TICKS_UNTIL_TOOB = 2 * 20;

    private int ticksOnHost;

    private final AVPAbstractParasite parasite;

    public ImpregnateHostAction(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.isFertile() && parasite.getVehicle() != null;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        var vehicle = Objects.requireNonNull(parasite.getVehicle());

        ticksOnHost++;

        if(ticksOnHost <= MAX_TICKS_UNTIL_TOOB) {
            if (parasite.tickCount % 10 == 0 && vehicle instanceof LivingEntity livingEntity) {
                livingEntity.hurt(parasite.damageSources().inWall(), 0F);
            }

            return;
        }


        parasite.setFertile(false);
        parasite.playSound(AVPSoundEvents.INSTANCE.entityParasiteImpregnate.get(), 0.2F, 1F);
        ticksOnHost = 0;
    }
}
