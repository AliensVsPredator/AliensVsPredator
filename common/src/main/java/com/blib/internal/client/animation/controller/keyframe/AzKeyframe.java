package com.blib.internal.client.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Objects;

import com.blib.internal.client.animation.easing.AzEasingType;
import com.blib.internal.client.animation.easing.AzEasingTypes;
import com.blib.internal.common.molang.math.IValue;

public record AzKeyframe<T extends IValue>(
    double length,
    T startValue,
    T endValue,
    AzEasingType easingType,
    List<T> easingArgs
) {

    public AzKeyframe(double length, T startValue, T endValue) {
        this(length, startValue, endValue, AzEasingTypes.LINEAR);
    }

    public AzKeyframe(double length, T startValue, T endValue, AzEasingType easingType) {
        this(length, startValue, endValue, easingType, new ObjectArrayList<>(0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.length, this.startValue, this.endValue, this.easingType, this.easingArgs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return hashCode() == obj.hashCode();
    }
}
