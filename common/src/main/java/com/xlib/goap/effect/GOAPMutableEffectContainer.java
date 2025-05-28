package com.xlib.goap.effect;

import java.util.ArrayList;

public class GOAPMutableEffectContainer extends GOAPEffectContainer {

    public GOAPMutableEffectContainer() {
        super(new ArrayList<>());
    }

    public void addEffect(GOAPEffect<?> effect) {
        effects.add(effect);
    }
}
