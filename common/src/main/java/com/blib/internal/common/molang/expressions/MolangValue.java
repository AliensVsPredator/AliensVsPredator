package com.blib.internal.common.molang.expressions;

import com.blib.internal.common.molang.MolangParser;
import com.blib.internal.common.molang.math.Constant;
import com.blib.internal.common.molang.math.IValue;

public class MolangValue implements IValue {

    private final IValue value;

    private final boolean returns;

    public MolangValue(IValue value) {
        this(value, false);
    }

    public MolangValue(IValue value, boolean isReturn) {
        this.value = value;
        this.returns = isReturn;
    }

    @Override
    public double get() {
        return this.value.get();
    }

    public IValue getValueHolder() {
        return this.value;
    }

    public boolean isReturnValue() {
        return this.returns;
    }

    public boolean isConstant() {
        return getClass() == MolangValue.class && value instanceof Constant;
    }

    @Override
    public String toString() {
        return (this.returns ? MolangParser.RETURN : "") + this.value.toString();
    }
}
