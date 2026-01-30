package com.blib.internal.common.molang.math.functions.utility;

import com.blib.internal.common.molang.math.IValue;
import com.blib.internal.common.molang.math.functions.Function;

public class Sign extends Function {

    public Sign(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        double value = this.getArg(0);

        if (value < 0)
            return -1;
        if (value > 0)
            return 1;
        return 0;
    }
}
