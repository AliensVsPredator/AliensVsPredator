package com.blib.api.client.animation.v1.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.api.client.animation.v1.keyframe.AzKeyframeCallbacks;
import com.blib.internal.client.animation.primitive.AzQueuedAnimation;
import com.blib.internal.client.animation.property.AzAnimationProperties;
import com.blib.internal.client.animation.track.AzAbstractAnimationTrack;
import com.blib.internal.client.animation.track.AzAnimationQueue;
import com.blib.internal.client.animation.track.AzAnimationTrackTimer;
import com.blib.internal.client.animation.track.AzBoneAnimationQueueCache;
import com.blib.internal.client.animation.track.AzBoneSnapshotCache;
import com.blib.internal.client.animation.track.keyframe.AzKeyframeManager;
import com.blib.internal.client.animation.track.state.impl.AzAnimationPauseState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationPlayState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationStopState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationTransitionState;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public class AzAnimationTrack<T> extends AzAbstractAnimationTrack {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationTrack.class);

    public static <T> AzAnimationTrackBuilder<T> builder(AzAnimator<?, T> animator, String name) {
        return new AzAnimationTrackBuilder<>(animator, name);
    }

    private final AzAnimationTrackTimer<T> trackTimer;

    private final AzAnimationQueue animationQueue;

    private final AzAnimationTrackStateMachine<T> stateMachine;

    private final AzAnimator<?, T> animator;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    private final AzKeyframeManager<T> keyframeManager;

    protected AzQueuedAnimation currentAnimation;

    private AzAnimationProperties animationProperties;

    AzAnimationTrack(
        String name,
        AzAnimator<?, T> animator,
        AzAnimationProperties animationProperties,
        AzKeyframeCallbacks<T> keyframeCallbacks
    ) {
        super(name);

        this.animator = animator;
        this.trackTimer = new AzAnimationTrackTimer<>(this);
        this.animationProperties = animationProperties;

        this.animationQueue = new AzAnimationQueue();
        this.boneAnimationQueueCache = new AzBoneAnimationQueueCache<>(animator.context().boneCache());
        this.boneSnapshotCache = new AzBoneSnapshotCache();
        this.keyframeManager = new AzKeyframeManager<>(
            this,
            boneAnimationQueueCache,
            boneSnapshotCache,
            keyframeCallbacks
        );

        var stateHolder = new AzAnimationTrackStateMachine.StateHolder<T>(
            new AzAnimationPlayState<>(),
            new AzAnimationPauseState<>(),
            new AzAnimationStopState<>(),
            new AzAnimationTransitionState<>()
        );

        this.stateMachine = new AzAnimationTrackStateMachine<>(stateHolder, this, animator.context());
    }

    @Override
    public boolean hasAnimationFinished() {
        return super.hasAnimationFinished() && stateMachine.isStopped();
    }

    public List<AzQueuedAnimation> tryCreateAnimationQueue(T animatable, AzAnimationSequence sequence) {
        var stages = sequence.stages();
        var animations = new ArrayList<AzQueuedAnimation>();

        for (var stage : stages) {
            var animation = animator.getAnimation(animatable, stage.name());

            if (animation == null) {
                LOGGER.warn(
                    "Unable to find animation: {} for {}",
                    stage.name(),
                    animatable.getClass().getSimpleName()
                );
                return List.of();
            } else {
                animations.add(new AzQueuedAnimation(animation, stage.properties().playBehavior()));
            }
        }

        return animations;
    }

    public void update() {
        // Adjust the tick before making any updates.
        trackTimer.update();
        // Run state machine updates.
        stateMachine.update();
        // Update bone animation queue cache.
        boneAnimationQueueCache.update(animationProperties.easingType());
    }

    public void run(@NotNull AzAnimationSequence sequence) {
        // Restart triggers, in order:
        //   - sequenceChanged: a new sequence always restarts.
        //   - wasStopped:      re-dispatching after the previous run finished replays it (e.g. an
        //                      idle that just looped back, or a one-shot that already stopped).
        //   - currentIsPlayOnce: re-dispatching the same sequence while a PLAY_ONCE is mid-flight
        //                        is a re-trigger (e.g. back-to-back attacks of the same type whose
        //                        previous animation hasn't finished client-side yet). Settled
        //                        behaviors (LOOP, HOLD_ON_LAST_FRAME, FREEZE_ON_FRAME) deliberately
        //                        do NOT restart on same-sequence re-dispatch — callers that fire
        //                        them every frame (e.g. facehugger hug) rely on this.
        var wasStopped = stateMachine.isStopped();

        if (wasStopped) {
            stateMachine.transition();
        }

        var currentIsPlayOnce = currentAnimation != null
            && currentAnimation.playBehavior() == AzPlayBehaviors.PLAY_ONCE;
        var sequenceChanged = !sequence.equals(currentSequence);
        var shouldRestart = sequenceChanged || wasStopped || currentIsPlayOnce;

        if (currentSequence == null || shouldRestart) {
            this.currentAnimation = null;
        }

        var animatable = animator.context().animatable();

        if (sequence.stages().isEmpty()) {
            stateMachine.stop();
            return;
        }

        if (shouldRestart) {
            var animations = tryCreateAnimationQueue(animatable, sequence);

            if (!animations.isEmpty()) {
                animationQueue.clear();
                animationQueue.addAll(animations);
                this.currentSequence = sequence;
                stateMachine.transition();
                return;
            }

            animationQueue.clear();
            this.currentSequence = null;
            stateMachine.transition();
        }
    }

    public AzAnimationProperties animationProperties() {
        return animationProperties;
    }

    public void setAnimationProperties(AzAnimationProperties animationProperties) {
        this.animationProperties = animationProperties;
    }

    public AzAnimationQueue animationQueue() {
        return animationQueue;
    }

    public AzBoneAnimationQueueCache<T> boneAnimationQueueCache() {
        return boneAnimationQueueCache;
    }

    public AzBoneSnapshotCache boneSnapshotCache() {
        return boneSnapshotCache;
    }

    public AzAnimationTrackTimer<T> trackTimer() {
        return trackTimer;
    }

    public @Nullable AzQueuedAnimation currentAnimation() {
        return currentAnimation;
    }

    public AzKeyframeManager<T> keyframeManager() {
        return keyframeManager;
    }

    public AzAnimationTrackStateMachine<T> stateMachine() {
        return stateMachine;
    }

    public void setCurrentAnimation(AzQueuedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;

        if (currentAnimation == null) {
            this.currentSequence = null;
        }
    }
}
