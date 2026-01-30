package com.blib.internal.client.animation.controller.keyframe;

import java.util.LinkedList;
import java.util.Queue;

import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.model.AzBoneSnapshot;
import com.blib.mod.BLib;

public record AzBoneAnimationQueue(
    AzBone bone,
    Queue<AzAnimationPoint> rotationXQueue,
    Queue<AzAnimationPoint> rotationYQueue,
    Queue<AzAnimationPoint> rotationZQueue,
    Queue<AzAnimationPoint> positionXQueue,
    Queue<AzAnimationPoint> positionYQueue,
    Queue<AzAnimationPoint> positionZQueue,
    Queue<AzAnimationPoint> scaleXQueue,
    Queue<AzAnimationPoint> scaleYQueue,
    Queue<AzAnimationPoint> scaleZQueue
) {

    public AzBoneAnimationQueue(AzBone bone) {
        // TODO: Optimize
        this(
            bone,
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>()
        );
    }

    public void addPosXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionXQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addPosYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionYQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addPosZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionZQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addNextPosition(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addPosXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetX(),
            nextXPoint.animationStartValue()
        );
        addPosYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetY(),
            nextYPoint.animationStartValue()
        );
        addPosZPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetZ(),
            nextZPoint.animationStartValue()
        );
    }

    public void addScaleXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleXQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addScaleYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleYQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addScaleZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleZQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addNextScale(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addScaleXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleX(),
            nextXPoint.animationStartValue()
        );
        addScaleYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleY(),
            nextYPoint.animationStartValue()
        );
        addScaleZPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleZ(),
            nextZPoint.animationStartValue()
        );
    }

    public void addRotationXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationXQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addRotationYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationYQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addRotationZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationZQueue.add(
            new AzAnimationPoint(
                keyframe,
                lerpedTick,
                transitionLength,
                startValue,
                endValue
            )
        );
    }

    public void addNextRotation(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzBoneSnapshot initialSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        if (startSnapshot == null) {
            BLib.LOGGER.warn("Warning: startSnapshot is null. Animation may not behave as expected.");
            return;
        }
        addRotationXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotX() - initialSnapshot.getRotX(),
            nextXPoint.animationStartValue()
        );
        addRotationYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotY() - initialSnapshot.getRotY(),
            nextYPoint.animationStartValue()
        );
        addRotationZPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotZ() - initialSnapshot.getRotZ(),
            nextZPoint.animationStartValue()
        );
    }

    public void addPositions(
        AzAnimationPoint xPoint,
        AzAnimationPoint yPoint,
        AzAnimationPoint zPoint
    ) {
        this.positionXQueue.add(xPoint);
        this.positionYQueue.add(yPoint);
        this.positionZQueue.add(zPoint);
    }

    public void addScales(
        AzAnimationPoint xPoint,
        AzAnimationPoint yPoint,
        AzAnimationPoint zPoint
    ) {
        this.scaleXQueue.add(xPoint);
        this.scaleYQueue.add(yPoint);
        this.scaleZQueue.add(zPoint);
    }

    public void addRotations(
        AzAnimationPoint xPoint,
        AzAnimationPoint yPoint,
        AzAnimationPoint zPoint
    ) {
        this.rotationXQueue.add(xPoint);
        this.rotationYQueue.add(yPoint);
        this.rotationZQueue.add(zPoint);
    }
}
