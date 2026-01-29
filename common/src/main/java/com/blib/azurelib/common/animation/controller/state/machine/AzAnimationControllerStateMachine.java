package com.blib.azurelib.common.animation.controller.state.machine;

import com.blib.azurelib.common.animation.AzAnimationContext;
import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.controller.state.AzAnimationState;
import com.blib.azurelib.common.animation.controller.state.impl.AzAnimationPauseState;
import com.blib.azurelib.common.animation.controller.state.impl.AzAnimationPlayState;
import com.blib.azurelib.common.animation.controller.state.impl.AzAnimationStopState;
import com.blib.azurelib.common.animation.controller.state.impl.AzAnimationTransitionState;
import com.blib.azurelib.common.util.state.StateMachine;
import com.blib.azurelib.common.util.state.StateMachineContext;

public class AzAnimationControllerStateMachine<T> extends StateMachine<AzAnimationControllerStateMachine.Context<T>, AzAnimationState<T>> {

    private final StateHolder<T> stateHolder;

    public AzAnimationControllerStateMachine(
        StateHolder<T> stateHolder,
        AzAnimationController<T> animationController,
        AzAnimationContext<T> animationContext
    ) {
        super(stateHolder.stopState());
        this.stateHolder = stateHolder;
        getContext().stateMachine = this;
        getContext().animationController = animationController;
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

        private AzAnimationController<T> animationController;

        private AzAnimationControllerStateMachine<T> stateMachine;

        private Context() {}

        public AzAnimationContext<T> animationContext() {
            return animationContext;
        }

        public AzAnimationController<T> animationController() {
            return animationController;
        }

        public AzAnimationControllerStateMachine<T> stateMachine() {
            return stateMachine;
        }
    }
}
