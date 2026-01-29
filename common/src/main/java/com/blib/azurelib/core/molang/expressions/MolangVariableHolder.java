package com.blib.azurelib.core.molang.expressions;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.Variable;

public class MolangVariableHolder extends MolangValue {

    public Variable variable;

    public MolangVariableHolder(Variable variable, IValue value) {
        super(value);

        this.variable = variable;
    }

    @Override
    public double get() {
        double value = super.get();

        this.variable.set(value);

        return value;
    }

    @Override
    public String toString() {
        return this.variable.getName() + " = " + super.toString();
    }
}
