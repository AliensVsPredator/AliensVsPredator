package com.blib.internal.common.molang.math.functions.easing.sine;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseInOutSine extends EasingFunction {

    public EaseInOutSine(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return -(Math.cos(Math.PI * t) - 1) / 2;
    }
}
