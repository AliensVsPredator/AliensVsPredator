package com.blib.azurelib.core.math.functions.classic;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;

public class ATan extends Function {

    public ATan(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.atan(this.getArg(0));
    }
}
