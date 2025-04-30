package com.avp.fabric.goap;

import com.avp.fabric.goap.state.GOAPMutableWorldState;

@FunctionalInterface
public interface GOAPSensor<T> {

    void sense(T context, GOAPMutableWorldState worldState);
}
