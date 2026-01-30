package com.blib.internal.client.animation.easing.bedrock_easings;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public record CubicBezierCurve(
    Vector2d startPoint,
    Vector2d controlPoint1,
    Vector2d controlPoint2,
    Vector2d endPoint
) {

    private static final int BEZIER_WEIGHT = 3;

    public Vector2d getPoint(float progress) {
        // TODO: look at maybe returning null instead isntancing a new Vector2D. Requires testing.
        return getPoint(progress, new Vector2d());
    }

    public Vector2d getPoint(float progress, Vector2d target) {
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Parameter t must be in the range [0, 1].");
        }

        if (target == null) {
            target = new Vector2d();
        }

        float oneMinusProgress = 1 - progress;
        float progressSquared = progress * progress;
        float oneMinusProgressSquared = oneMinusProgress * oneMinusProgress;
        float oneMinusProgressCubed = oneMinusProgressSquared * oneMinusProgress;
        float progressCubed = progressSquared * progress;

        target.x = oneMinusProgressCubed * startPoint.x() + BEZIER_WEIGHT * oneMinusProgressSquared * progress
            * controlPoint1.x() + BEZIER_WEIGHT * oneMinusProgress * progressSquared
                * controlPoint2.x() + progressCubed * endPoint.x();
        target.y = oneMinusProgressCubed * startPoint.y() + BEZIER_WEIGHT * oneMinusProgressSquared * progress
            * controlPoint1.y() + BEZIER_WEIGHT * oneMinusProgress * progressSquared
                * controlPoint2.y() + progressCubed * endPoint.y();

        return target;
    }

    public List<Vector2d> getPoints(int divisions) {
        if (divisions <= 0) {
            throw new IllegalArgumentException("Divisions must be greater than 0.");
        }

        List<Vector2d> points = new ArrayList<>();

        for (int step = 0; step <= divisions; step++) {
            points.add(getPoint((float) step / divisions));
        }

        return points;
    }
}
