package com.blib.azurelib.core.math;

/**
 * Negative operator class This class is responsible for inverting given value
 */
public class Negative implements com.blib.azurelib.core.math.IValue {

    public com.blib.azurelib.core.math.IValue value;

    public Negative(IValue value) {
        this.value = value;
    }

    @Override
    public double get() {
        return -this.value.get();
    }

    @Override
    public String toString() {
        return "-" + this.value.toString();
    }
}
