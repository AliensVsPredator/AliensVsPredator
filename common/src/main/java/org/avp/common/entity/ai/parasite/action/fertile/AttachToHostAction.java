package org.avp.common.entity.ai.parasite.action.fertile;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.ai.action.Action;

import java.util.Objects;

public class AttachToHostAction extends Action {

    private final Monster parasite;

    public AttachToHostAction(Monster parasite) {
        this.parasite = parasite;
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
