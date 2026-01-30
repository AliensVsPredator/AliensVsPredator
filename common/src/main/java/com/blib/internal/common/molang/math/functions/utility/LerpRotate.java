package com.blib.internal.common.molang.math.functions.utility;

import com.blib.internal.client.animation.Interpolations;
import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public class LerpRotate extends Function {

    public LerpRotate(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return Interpolations.lerpYaw(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
