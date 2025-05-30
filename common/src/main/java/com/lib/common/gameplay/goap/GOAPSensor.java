package com.lib.common.gameplay.goap;

import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;

@FunctionalInterface
public interface GOAPSensor<T> {

    void sense(T context, GOAPMutableWorldState worldState);
}
