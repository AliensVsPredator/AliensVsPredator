package org.avp.common.ai.ovamorph.action;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;

import org.avp.api.common.ai.CostConstraint;
import org.avp.api.common.ai.action.Action;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerData;
import org.avp.common.game.entity.AbstractOvamorph;

public class ReleaseFacehuggerAction extends Action {

    private final AbstractOvamorph ovamorph;

    public ReleaseFacehuggerAction(AbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
    }

    @Override
    public boolean isValid() {
        return ovamorph.hasFacehugger();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        if (!ovamorph.isOpen.get()) {
            return;
        }

        var level = (ServerLevel) ovamorph.level();
        var ovamorphPos = ovamorph.blockPosition();

        FacehuggerData.INSTANCE.getHolder().get().spawn(level, ovamorphPos, MobSpawnType.TRIGGERED);

        ovamorph.setHasFacehugger(false);
    }
}
