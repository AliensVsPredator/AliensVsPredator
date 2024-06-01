package org.avp.common.entity.ai.parasite.action.fertile;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;

import java.util.Objects;

public class MoveToHostAction extends Action {

    private final AVPAbstractParasite parasite;

    public MoveToHostAction(AVPAbstractParasite parasite) {
        this.parasite = parasite;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(parasite.getTarget());
        parasite.getNavigation().moveTo(target, 1);
    }
}
