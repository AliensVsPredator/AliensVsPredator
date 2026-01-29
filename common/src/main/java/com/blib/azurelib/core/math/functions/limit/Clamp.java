package com.blib.azurelib.core.math.functions.limit;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.functions.Function;
import com.blib.azurelib.core.utils.MathUtils;

public class Clamp extends Function {

    public Clamp(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    public int getRequiredArguments() {
        return 3;
    }

    @Override
    public double get() {
        return MathUtils.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}
