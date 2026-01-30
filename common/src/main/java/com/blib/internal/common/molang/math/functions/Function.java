package com.blib.internal.common.molang.math.functions;

import com.blib.internal.common.molang.math.IValue;

public abstract class Function implements IValue {

    protected IValue[] args;

    protected String name;

    protected Function(IValue[] values, String name) throws Exception {
        if (values.length < this.getRequiredArguments()) {
            String message = String.format(
                "Function '%s' requires at least %s arguments. %s are given!",
                this.getName(),
                this.getRequiredArguments(),
                values.length
            );

            throw new Exception(message);
        }

        this.args = values;
        this.name = name;
    }

    public double getArg(int index) {
        if (index < 0 || index >= this.args.length) {
            return 0;
        }

        return this.args[index].get();
    }

    @Override
    public String toString() {
        StringBuilder argsBuilder = new StringBuilder();

        for (int i = 0; i < this.args.length; i++) {
            argsBuilder.append(this.args[i].toString());

            if (i < this.args.length - 1) {
                argsBuilder.append(", ");
            }
        }

        return this.getName() + "(" + argsBuilder + ")";
    }

    public String getName() {
        return this.name;
    }

    public int getRequiredArguments() {
        return 0;
    }
}
