package com.blib.azurelib.core.math.functions.easing.quint;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.easing.EasingFunction;

public class EaseOutQuint extends EasingFunction {

    public EaseOutQuint(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return 1 - Math.pow(1 - t, 5);
    }
}
