/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package com.blib.azurelib.core.molang.expressions;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.blib.azurelib.core.molang.LazyVariable;

/**
 * An extension of the {@link com.blib.azurelib.core.molang.expressions.MolangValue} class, allowing for compound
 * expressions.
 */
public class MolangCompoundValue extends com.blib.azurelib.core.molang.expressions.MolangValue {

    public final List<com.blib.azurelib.core.molang.expressions.MolangValue> values = new ObjectArrayList<>();

    public final Map<String, LazyVariable> locals = new Object2ObjectOpenHashMap<>();

    public MolangCompoundValue(com.blib.azurelib.core.molang.expressions.MolangValue baseValue) {
        super(baseValue);

        this.values.add(baseValue);
    }

    @Override
    public double get() {
        double value = 0;

        for (com.blib.azurelib.core.molang.expressions.MolangValue molangValue : this.values) {
            value = molangValue.get();
        }

        return value;
    }

    @Override
    public String toString() {
        StringJoiner builder = new StringJoiner("; ");

        for (MolangValue molangValue : this.values) {
            builder.add(molangValue.toString());

            if (molangValue.isReturnValue())
                break;
        }

        return builder.toString();
    }
}
