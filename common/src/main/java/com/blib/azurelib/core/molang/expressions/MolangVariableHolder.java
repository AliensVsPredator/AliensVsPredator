/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package com.blib.azurelib.core.molang.expressions;

import com.blib.azurelib.core.math.IValue;
import com.blib.azurelib.core.math.Variable;

/**
 * Extension of {@link com.blib.azurelib.core.molang.expressions.MolangValue} that additionally sets the value of a
 * provided {@link Variable} when being called.
 */
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
