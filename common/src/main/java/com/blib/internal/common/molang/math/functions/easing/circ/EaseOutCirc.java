package com.blib.internal.common.molang.math.functions.easing.circ;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseOutCirc extends EasingFunction {

    public EaseOutCirc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return Math.sqrt(1 - Math.pow(t - 1, 2));
    }
}
