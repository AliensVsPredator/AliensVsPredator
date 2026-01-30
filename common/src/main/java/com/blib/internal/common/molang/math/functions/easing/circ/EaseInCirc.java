package com.blib.internal.common.molang.math.functions.easing.circ;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseInCirc extends EasingFunction {

    public EaseInCirc(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return 1 - Math.sqrt(1 - Math.pow(t, 2));
    }
}
