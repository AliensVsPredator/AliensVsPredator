package org.avp.api.common.ai;

import net.minecraft.util.Mth;

public record CostConstraint(
    int minValue,
    int maxValue
) {

    public static final CostConstraint DEFAULT = new CostConstraint(0, 100);

    public int apply(int value) {
        return (int) Mth.map(value, minValue, maxValue, 0, 100);
    }
}
