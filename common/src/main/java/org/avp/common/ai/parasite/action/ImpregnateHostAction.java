package org.avp.common.ai.parasite.action;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.ai.CostConstraint;
import org.avp.api.ai.action.Action;
import org.avp.common.game.entity.AbstractParasite;
import org.avp.common.game.sound.AVPSoundEventRegistry;

import java.util.Objects;

public class ImpregnateHostAction extends Action {

    private static final int MAX_TICKS_UNTIL_TOOB = 2 * 20;

    private int ticksOnHost;

    private final AbstractParasite parasite;

    public ImpregnateHostAction(AbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
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
        parasite.playSound(AVPSoundEventRegistry.INSTANCE.entityParasiteImpregnate.get(), 0.2F, 1F);
        ticksOnHost = 0;
    }
}
