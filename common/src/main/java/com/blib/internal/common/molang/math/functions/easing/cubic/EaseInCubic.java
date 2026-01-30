package com.blib.internal.common.molang.math.functions.easing.cubic;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseInCubic extends EasingFunction {

    public EaseInCubic(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return t * t * t;
    }
}
