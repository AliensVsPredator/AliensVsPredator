package com.blib.internal.common.molang.math.functions.easing.quart;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseInQuart extends EasingFunction {

    public EaseInQuart(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return t * t * t * t;
    }
}
