package com.blib.azurelib.core.math.functions.limit;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;

public class Max extends Function {

    public Max(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.max(this.getArg(0), this.getArg(1));
    }
}
