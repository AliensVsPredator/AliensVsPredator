package com.blib.internal.common.molang.math.functions.easing.quad;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.easing.EasingFunction;

public class EaseOutQuad extends EasingFunction {

    public EaseOutQuad(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return 1 - (1 - t) * (1 - t);
    }
}
