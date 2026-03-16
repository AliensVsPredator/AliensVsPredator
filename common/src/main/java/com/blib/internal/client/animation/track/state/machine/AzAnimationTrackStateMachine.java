package com.blib.internal.client.animation.track.state.machine;

import com.blib.api.client.animation.v1.animator.AzAnimationContext;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.internal.client.animation.track.state.AzAnimationState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationPauseState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationPlayState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationStopState;
import com.blib.internal.client.animation.track.state.impl.AzAnimationTransitionState;
import com.blib.internal.common.model.state_machine.StateMachine;
import com.blib.internal.common.model.state_machine.StateMachineContext;

public class AzAnimationTrackStateMachine<T> extends StateMachine<AzAnimationTrackStateMachine.Context<T>, AzAnimationState<T>> {

    private final StateHolder<T> stateHolder;

    public AzAnimationTrackStateMachine(
        StateHolder<T> stateHolder,
        AzAnimationTrack<T> animationTrack,
        AzAnimationContext<T> animationContext
    ) {
        super(stateHolder.stopState());
        this.stateHolder = stateHolder;
        getContext().stateMachine = this;
        getContext().animationTrack = animationTrack;
        getContext().animationContext = animationContext;
    }

    @Override
    public Context<T> createContext() {
        return new Context<>();
    }

    public void update() {
        super.update(getContext());
    }

    public void pause() {
        setState(stateHolder.pauseState);
    }

    public void play() {
        setState(stateHolder.playState);
    }

    public void transition() {
        setState(stateHolder.transitionState);
    }

    public void stop() {
        setState(stateHolder.stopState);
    }

    public boolean isPlaying() {
        return getState() == stateHolder.playState;
    }

    public boolean isPaused() {
        return getState() == stateHolder.pauseState;
    }

    public boolean isStopped() {
        return getState() == stateHolder.stopState;
    }

    public boolean isTransitioning() {
        return getState() == stateHolder.transitionState;
    }

    public record StateHolder<T>(
        AzAnimationPlayState<T> playState,
        AzAnimationPauseState<T> pauseState,
        AzAnimationStopState<T> stopState,
        AzAnimationTransitionState<T> transitionState
    ) {}

    public static class Context<T> implements StateMachineContext {

        private AzAnimationContext<T> animationContext;

        private AzAnimationTrack<T> animationTrack;

        private AzAnimationTrackStateMachine<T> stateMachine;

        private Context() {}

        public AzAnimationContext<T> animationContext() {
            return animationContext;
        }

        public AzAnimationTrack<T> animationTrack() {
            return animationTrack;
        }

        public AzAnimationTrackStateMachine<T> stateMachine() {
            return stateMachine;
        }
    }
}
