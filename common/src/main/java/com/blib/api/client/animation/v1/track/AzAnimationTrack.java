package com.blib.api.client.animation.v1.track;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.policy.OnBlockedByEndless;
import com.blib.api.client.animation.v1.command.policy.OnPropertiesChanged;
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

    /**
     * Convenience: build a track using a typed {@link AzTrackHandle} as the source of the track's name. The handle's
     * animatable type parameter must match the animator's, enforced by the type system.
     */
    public static <T> AzAnimationTrackBuilder<T> builder(AzAnimator<?, T> animator, AzTrackHandle<? super T> handle) {
        return new AzAnimationTrackBuilder<>(animator, handle.name());
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

    public void run(@NotNull AzAnimationSequence sequence, @NotNull AzDispatchPolicy policy) {
        if (sequence.stages().isEmpty()) {
            stateMachine.stop();
            return;
        }

        switch (policy.mode()) {
            case REPLAY -> runReplay(sequence);
            case PLAY_IF_NOT_PLAYING -> runIdempotent(sequence, policy.onPropertiesChanged());
            case ENQUEUE -> runEnqueue(sequence, policy.onBlockedByEndless());
        }
    }

    private void runReplay(AzAnimationSequence sequence) {
        var animatable = animator.context().animatable();
        var animations = tryCreateAnimationQueue(animatable, sequence);

        this.currentAnimation = null;
        animationQueue.clear();

        if (animations.isEmpty()) {
            this.currentSequence = null;
            stateMachine.transition();
            return;
        }

        animationQueue.addAll(animations);
        this.currentSequence = sequence;
        stateMachine.transition();
    }

    private void runIdempotent(AzAnimationSequence sequence, OnPropertiesChanged onPropertiesChanged) {
        if (isAlreadyActive(sequence, onPropertiesChanged)) {
            // X is already the active animation; drop any stale follow-ups so the track's intent
            // matches "X is what should be playing."
            animationQueue.clear();
            return;
        }

        runReplay(sequence);
    }

    private void runEnqueue(AzAnimationSequence sequence, OnBlockedByEndless onBlockedByEndless) {
        if (currentAnimation == null && stateMachine.isStopped()) {
            // Nothing to wait for — start now.
            runReplay(sequence);
            return;
        }

        if (currentAnimation != null && currentAnimation.playBehavior() == AzPlayBehaviors.LOOP) {
            switch (onBlockedByEndless) {
                case REJECT -> {
                    LOGGER.warn(
                        "ENQUEUE rejected on track '{}': current animation is LOOP'd and will not finish.",
                        name()
                    );
                    return;
                }
                case PROMOTE_TO_REPLAY -> {
                    runReplay(sequence);
                    return;
                }
                case APPEND_ANYWAY -> {
                    // fall through to enqueue
                }
            }
        }

        var animatable = animator.context().animatable();
        var animations = tryCreateAnimationQueue(animatable, sequence);
        animationQueue.addAll(animations);
    }

    private boolean isAlreadyActive(AzAnimationSequence sequence, OnPropertiesChanged onPropertiesChanged) {
        if (currentSequence == null || stateMachine.isStopped()) {
            return false;
        }

        return switch (onPropertiesChanged) {
            case RESTART -> sequence.equals(currentSequence);
            case UPDATE_IN_PLACE -> sameRetunableSequence(sequence, currentSequence);
        };
    }

    private static boolean sameRetunableSequence(AzAnimationSequence a, AzAnimationSequence b) {
        var aStages = a.stages();
        var bStages = b.stages();

        if (aStages.size() != bStages.size()) {
            return false;
        }

        for (int i = 0; i < aStages.size(); i++) {
            var aStage = aStages.get(i);
            var bStage = bStages.get(i);

            if (!aStage.name().equals(bStage.name())) {
                return false;
            }

            if (aStage.properties().playBehavior() != bStage.properties().playBehavior()) {
                return false;
            }
        }

        return true;
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
