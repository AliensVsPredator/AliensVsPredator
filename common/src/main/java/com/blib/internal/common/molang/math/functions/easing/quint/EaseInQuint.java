package com.blib.internal.common.molang.math.functions.easing.quint;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseInQuint extends EasingFunction {

    public EaseInQuint(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return t * t * t * t * t;
    }
}
