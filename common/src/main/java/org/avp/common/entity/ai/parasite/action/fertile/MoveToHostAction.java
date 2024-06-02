package org.avp.common.entity.ai.parasite.action.fertile;

import net.minecraft.util.Mth;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;

import java.util.Objects;

public class MoveToHostAction extends Action {

    private final AVPAbstractParasite parasite;

    public MoveToHostAction(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.getTarget() != null && parasite.onGround();
    }

    @Override
    public int getCost() {
        var target = Objects.requireNonNull(parasite.getTarget());
        var distanceToHost = parasite.distanceTo(target);
        return (int) Mth.map(distanceToHost, 0F, 16F, 0F, 200F);
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(parasite.getTarget());
        parasite.getNavigation().moveTo(target, 1);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        parasite.getNavigation().stop();
    }
}
