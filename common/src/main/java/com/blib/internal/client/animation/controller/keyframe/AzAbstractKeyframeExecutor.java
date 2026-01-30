package com.blib.internal.client.animation.controller.keyframe;

import java.util.List;

import com.blib.internal.common.model.Axis;
import com.blib.internal.common.molang.math.Constant;
import com.blib.internal.common.molang.math.IValue;

public class AzAbstractKeyframeExecutor {

    protected AzAbstractKeyframeExecutor() {}

    protected AzAnimationPoint getAnimationPointAtTick(
        List<AzKeyframe<IValue>> frames,
        double tick,
        boolean isRotation,
        Axis axis
    ) {
        AzKeyframeLocation<AzKeyframe<IValue>> location =
            frames.isEmpty()
                ? new AzKeyframeLocation<>(
                    new AzKeyframe<>(0, () -> 0, () -> 0),
                    0
                )
                : getCurrentKeyframeLocation(frames, tick);
        var currentFrame = location.keyframe();
        var startValue = currentFrame.startValue().get();
        var endValue = currentFrame.endValue().get();

        if (isRotation) {
            if (!(currentFrame.startValue() instanceof Constant)) {
                startValue = Math.toRadians(startValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    startValue *= -1;
                }
            }

            if (!(currentFrame.endValue() instanceof Constant)) {
                endValue = Math.toRadians(endValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    endValue *= -1;
                }
            }
        }

        return new AzAnimationPoint(currentFrame, location.startTick(), currentFrame.length(), startValue, endValue);
    }

    protected AzKeyframeLocation<AzKeyframe<IValue>> getCurrentKeyframeLocation(
        List<AzKeyframe<IValue>> frames,
        double ageInTicks
    ) {
        var totalFrameTime = 0.0;

        for (var frame : frames) {
            totalFrameTime += frame.length();

            if (totalFrameTime > ageInTicks) {
                return new AzKeyframeLocation<>(
                    frame,
                    (ageInTicks - (totalFrameTime - frame.length()))
                );
            }
        }

        return new AzKeyframeLocation<>(frames.get(frames.size() - 1), ageInTicks);
    }
}
