package com.blib.azurelib.core.molang.functions;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;

public class SinDegrees extends Function {

    public SinDegrees(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    public double get() {
        return Math.sin(getArg(0) / 180 * Math.PI);
    }
}
