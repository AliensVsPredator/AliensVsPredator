package com.lib.common.gameplay.goap.effect;

import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import com.lib.common.gameplay.goap.state.GOAPWorldState;

import java.util.Arrays;
import java.util.List;

public class GOAPEffectContainer {

    public static GOAPEffectContainer of(GOAPEffect<?>... effects) {
        return new GOAPEffectContainer(Arrays.stream(effects).toList());
    }

    protected final List<GOAPEffect<?>> effects;

    protected GOAPEffectContainer(List<GOAPEffect<?>> effects) {
        this.effects = effects;
    }

    public List<GOAPEffect<?>> getEffects() {
        return effects;
    }

    public GOAPWorldState toWorldState() {
        var worldState = new GOAPMutableWorldState();
        worldState.apply(this);
        return worldState;
    }
}
