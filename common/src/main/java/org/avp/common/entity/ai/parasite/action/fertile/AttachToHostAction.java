package org.avp.common.entity.ai.parasite.action.fertile;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;

import java.util.Objects;

public class AttachToHostAction extends Action {

    private final AVPAbstractParasite parasite;

    public AttachToHostAction(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.getTarget() != null && parasite.getTarget().getPassengers().isEmpty();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(parasite.getTarget());
        parasite.startRiding(target);
        // TODO: Send out a packet to tell the clients that the target entity is now being ridden.
    }
}
