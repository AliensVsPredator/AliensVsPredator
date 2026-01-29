package com.blib.azurelib.common.animation.easing;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

import com.blib.azurelib.common.animation.controller.keyframe.AzAnimationPoint;

public class AzEasingUtil {

    public static Double2DoubleFunction linear(Double2DoubleFunction function) {
        return function;
    }

    public static double catmullRom(double delta, double p0, double p1, double p2, double p3) {
        return 0.5d * (2d * p1 + (p2 - p0) * delta +
            (2d * p0 - 5d * p1 + 4d * p2 - p3) * delta * delta +
            (3d * p1 - p0 - 3d * p2 + p3) * delta * delta * delta);
    }

    public static double catmullRom(double n) {
        // Using default control points for simple interpolation
        return catmullRom(n, 0, 0, 1, 1);
    }

    public static Double2DoubleFunction easeIn(Double2DoubleFunction function) {
        return function;
    }

    // ---> Easing Transition Type Functions <--- //

    public static Double2DoubleFunction easeOut(Double2DoubleFunction function) {
        return time -> 1 - function.apply(1 - time);
    }

    public static Double2DoubleFunction easeInOut(Double2DoubleFunction function) {
        return time -> {
            if (time < 0.5d)
                return function.apply(time * 2d) / 2d;

            return 1 - function.apply((1 - time) * 2d) / 2d;
        };
    }

    public static Double2DoubleFunction stepPositive(Double2DoubleFunction function) {
        return n -> n > 0 ? 1 : 0;
    }

    public static Double2DoubleFunction stepNonNegative(Double2DoubleFunction function) {
        return n -> n >= 0 ? 1 : 0;
    }

    public static double linear(double n) {
        return n;
    }

    // ---> Stepping Functions <--- //

    public static double quadratic(double n) {
        return n * n;
    }

    public static double cubic(double n) {
        return n * n * n;
    }

    // ---> Mathematical Functions <--- //

    public static double sine(double n) {
        return 1 - Math.cos(n * Math.PI / 2f);
    }

    public static double circle(double n) {
        return 1 - Math.sqrt(1 - n * n);
    }

    public static double exp(double n) {
        return Math.pow(2, 10 * (n - 1));
    }

    public static Double2DoubleFunction elastic(Double n) {
        double n2 = n == null ? 1 : n;

        return t -> 1 - Math.pow(Math.cos(t * Math.PI / 2f), 3) * Math.cos(t * n2 * Math.PI);
    }

    public static Double2DoubleFunction bounce(Double n) {
        final double n2 = n == null ? 0.5d : n;

        Double2DoubleFunction one = x -> 121f / 16f * x * x;
        Double2DoubleFunction two = x -> 121f / 4f * n2 * Math.pow(x - 6f / 11f, 2) + 1 - n2;
        Double2DoubleFunction three = x -> 121 * n2 * n2 * Math.pow(x - 9f / 11f, 2) + 1 - n2 * n2;
        Double2DoubleFunction four = x -> 484 * n2 * n2 * n2 * Math.pow(x - 10.5f / 11f, 2) + 1 - n2 * n2 * n2;

        return t -> Math.min(Math.min(one.apply(t), two.apply(t)), Math.min(three.apply(t), four.apply(t)));
    }

    public static Double2DoubleFunction back(Double n) {
        final double n2 = n == null ? 1.70158d : n * 1.70158d;

        return t -> t * t * ((n2 + 1) * t - n2);
    }

    // ---> Easing Curve Functions <--- //

    public static Double2DoubleFunction pow(double n) {
        return t -> Math.pow(t, n);
    }

    public static Double2DoubleFunction step(Double n) {
        double n2 = n == null ? 2 : n;

        if (n2 < 2)
            throw new IllegalArgumentException("Steps must be >= 2, got: " + n2);

        final int steps = (int) n2;

        return t -> {
            double result = 0;

            if (t < 0)
                return result;

            double stepLength = (1 / (double) steps);

            if (t > (result = (steps - 1) * stepLength))
                return result;

            int testIndex;
            int leftBorderIndex = 0;
            int rightBorderIndex = steps - 1;

            while (rightBorderIndex - leftBorderIndex != 1) {
                testIndex = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;

                if (t >= testIndex * stepLength) {
                    leftBorderIndex = testIndex;
                } else {
                    rightBorderIndex = testIndex;
                }
            }

            return leftBorderIndex * stepLength;
        };
    }

    public static double lerpWithOverride(AzAnimationPoint animationPoint, AzEasingType override) {
        var easingType = override;

        if (override == null) {
            easingType = animationPoint.keyframe() == null
                ? AzEasingTypes.LINEAR
                : animationPoint.keyframe().easingType();
        }

        return easingType.apply(animationPoint);
    }
}
