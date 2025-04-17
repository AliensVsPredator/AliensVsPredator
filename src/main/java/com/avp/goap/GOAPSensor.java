package com.avp.goap;

import com.avp.goap.state.GOAPMutableWorldState;

@FunctionalInterface
public interface GOAPSensor<T> {

    void sense(T context, GOAPMutableWorldState worldState);
}
