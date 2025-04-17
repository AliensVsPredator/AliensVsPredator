package com.avp.goap;

@FunctionalInterface
public interface GOAPSensor<T> {

    void sense(T context, GOAPWorldState worldState);
}
