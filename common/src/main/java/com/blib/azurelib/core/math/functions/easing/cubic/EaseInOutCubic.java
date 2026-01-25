package com.blib.azurelib.core.math.functions.easing.cubic;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.easing.EasingFunction;

public class EaseInOutCubic extends EasingFunction {

    public EaseInOutCubic(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    protected double ease(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }
}
