package com.blib.azurelib.core.math.functions.classic;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;

public class Pi extends Function {

    public Pi(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public double get() {
        return Math.PI;
    }
}
