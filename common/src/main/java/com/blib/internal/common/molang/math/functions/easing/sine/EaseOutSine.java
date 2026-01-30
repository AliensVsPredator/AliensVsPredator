package com.blib.internal.common.molang.math.functions.easing.sine;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseOutSine extends EasingFunction {

    public EaseOutSine(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return Math.sin((t * Math.PI) / 2);
    }
}
