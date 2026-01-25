package com.blib.azurelib.core.math.functions.easing.expo;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.easing.EasingFunction;

public class EaseOutExpo extends EasingFunction {

    public EaseOutExpo(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return t == 1 ? 1 : 1 - Math.pow(2, -10 * t);
    }
}
