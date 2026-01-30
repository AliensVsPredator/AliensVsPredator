package com.blib.internal.common.molang.math.functions.classic;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public class Sqrt extends Function {

    public Sqrt(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.sqrt(this.getArg(0));
    }
}
