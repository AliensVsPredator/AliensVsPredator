package com.blib.internal.common.molang.math.functions.utility;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public class CopySign extends Function {

    public CopySign(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        double magnitude = Math.abs(this.getArg(0));
        double sign = this.getArg(1);

        return sign < 0 ? -magnitude : magnitude;
    }
}
