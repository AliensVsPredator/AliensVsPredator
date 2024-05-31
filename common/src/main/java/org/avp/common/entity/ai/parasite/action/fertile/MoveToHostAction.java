package org.avp.common.entity.ai.parasite.action.fertile;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.ai.action.Action;

import java.util.Objects;

public class MoveToHostAction extends Action {

    private final Monster parasite;

    public MoveToHostAction(Monster parasite) {
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
