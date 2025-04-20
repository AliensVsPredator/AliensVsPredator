package com.avp.goap.effect;

import java.util.Arrays;
import java.util.List;

import com.avp.goap.state.GOAPMutableWorldState;
import com.avp.goap.state.GOAPWorldState;

public class GOAPEffectContainer {

    public static GOAPEffectContainer of(GOAPEffect<?>... effects) {
        return new GOAPEffectContainer(Arrays.stream(effects).toList());
    }

    private final List<GOAPEffect<?>> effects;

    private GOAPEffectContainer(List<GOAPEffect<?>> effects) {
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
