package com.xlib.goap;

import com.xlib.goap.state.GOAPMutableWorldState;

@FunctionalInterface
public interface GOAPSensor<T> {

    void sense(T context, GOAPMutableWorldState worldState);
}
