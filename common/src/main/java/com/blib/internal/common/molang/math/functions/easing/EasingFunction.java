package com.blib.internal.common.molang.math.functions.easing;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public abstract class EasingFunction extends Function {

    protected EasingFunction(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        double start = this.getArg(0);
        double end = this.getArg(1);
        double t = this.getArg(2);

        // Clamp t to [0, 1]
        t = Math.max(0, Math.min(1, t));

        double easedT = ease(t);
        return start + (end - start) * easedT;
    }

    protected abstract double ease(double t);
}
