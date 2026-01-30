package com.blib.internal.common.molang.math.functions.classic;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public class ATan2 extends Function {

    public ATan2(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.atan2(this.getArg(0), this.getArg(1));
    }
}
