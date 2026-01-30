package com.blib.internal.client.animation.easing.bedrock_easings;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

import java.util.List;

import com.blib.internal.client.animation.controller.keyframe.AzAnimationPoint;
import com.blib.internal.client.animation.easing.AzEasingType;
import com.blib.internal.client.animation.easing.AzEasingUtil;
import com.blib.internal.common.molang.math.IValue;

public abstract class BezierEasing implements AzEasingType {

    private static final double DEFAULT_RIGHT_TIME = 0.1;

    private static final double DEFAULT_LEFT_TIME = -0.1;

    private static final int CURVE_RESOLUTION = 200;

    private static final double TICKS_PER_SECOND = 20;

    @Override
    public Double2DoubleFunction buildTransformer(Double value) {
        return AzEasingUtil.easeIn(AzEasingUtil::linear);
    }

    @Override
    public double apply(AzAnimationPoint animationPoint, Double easingValue, double lerpValue) {
        List<? extends IValue> easingArgs = animationPoint.keyframe().easingArgs();
        if (easingArgs.isEmpty()) {
            return handleNoEasingArgs(animationPoint, easingValue, lerpValue);
        }

        boolean easingBefore = isEasingBefore();
        double rightValue = easingBefore ? 0 : easingArgs.getFirst().get();
        double rightTime = easingBefore ? DEFAULT_RIGHT_TIME : easingArgs.get(1).get();
        double leftValue = easingBefore ? easingArgs.getFirst().get() : 0;
        double leftTime = easingBefore ? easingArgs.get(1).get() : DEFAULT_LEFT_TIME;

        if (easingArgs.size() > 3) {
            rightValue = easingArgs.get(2).get();
            rightTime = easingArgs.get(3).get();
        }

        leftValue = Math.toRadians(leftValue);
        rightValue = Math.toRadians(rightValue);

        double normalizedTransitionDuration = animationPoint.transitionLength() / TICKS_PER_SECOND;
        double clampedRightTime = Math.clamp(rightTime, 0, normalizedTransitionDuration);
        double clampedLeftTime = Math.clamp(leftTime, -normalizedTransitionDuration, 0);

        CubicBezierCurve curve = buildBezierCurve(
            animationPoint,
            clampedLeftTime,
            clampedRightTime,
            leftValue,
            rightValue,
            normalizedTransitionDuration
        );
        double time = normalizedTransitionDuration * lerpValue;

        List<Vector2d> points = curve.getPoints(CURVE_RESOLUTION);
        Vector2d[] closestPoints = findClosestPoints(points, time);

        return Mth.lerp(
            Math.clamp(
                Mth.lerp(time, closestPoints[0].x, closestPoints[1].x),
                0,
                1
            ),
            closestPoints[0].y,
            closestPoints[1].y
        );
    }

    public abstract boolean isEasingBefore();

    private double handleNoEasingArgs(AzAnimationPoint animationPoint, Double easingValue, double lerpValue) {
        Double2DoubleFunction transformer = buildTransformer(easingValue);
        return Mth.lerp(
            transformer.apply(lerpValue),
            animationPoint.animationStartValue(),
            animationPoint.animationEndValue()
        );
    }

    private CubicBezierCurve buildBezierCurve(
        AzAnimationPoint animationPoint,
        double clampedLeftTime,
        double clampedRightTime,
        double leftValue,
        double rightValue,
        double normalizedTransitionDuration
    ) {
        return new CubicBezierCurve(
            new Vector2d(0, animationPoint.animationStartValue()),
            new Vector2d(clampedRightTime, animationPoint.animationStartValue() + rightValue),
            new Vector2d(
                clampedLeftTime + normalizedTransitionDuration,
                animationPoint.animationEndValue() + leftValue
            ),
            new Vector2d(normalizedTransitionDuration, animationPoint.animationEndValue())
        );
    }

    private Vector2d[] findClosestPoints(List<Vector2d> points, double time) {
        Vector2d closest = new Vector2d();
        Vector2d secondClosest = new Vector2d();
        double closestDiff = Double.POSITIVE_INFINITY;
        double secondClosestDiff = Double.POSITIVE_INFINITY;

        for (Vector2d point : points) {
            double diff = Math.abs(point.x - time);
            if (diff < closestDiff) {
                secondClosest.set(closest);
                secondClosestDiff = closestDiff;

                closest.set(point);
                closestDiff = diff;
            } else if (diff < secondClosestDiff) {
                secondClosest.set(point);
                secondClosestDiff = diff;
            }
        }
        return new Vector2d[] { closest, secondClosest };
    }

}
