package com.blib.azurelib.core.math.functions.classic;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;

public class Pow extends Function {

    public Pow(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 2;
    }

    @Override
    public double get() {
        return Math.pow(this.getArg(0), this.getArg(1));
    }
}
