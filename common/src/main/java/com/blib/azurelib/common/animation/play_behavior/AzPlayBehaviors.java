package com.blib.azurelib.common.animation.play_behavior;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.controller.state.machine.AzAnimationControllerStateMachine;

public class AzPlayBehaviors {

    private AzPlayBehaviors() {}

    // public static final AzPlayBehavior PING_PONG = AzPlayBehaviorRegistry.register(
    // new AzPlayBehavior("ping_pong") {
    // }
    // );

    /**
     * Represents a play behavior where an animation is repeated a specified number of times. The behavior resets the
     * animation controller's timer and keyframe callback handler after each iteration and continues playing until the
     * maximum repeat count is reached. Once the repeat count is met, the animation stops.
     */
    public static final com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior REPEAT_X_TIMES = AzPlayBehaviorRegistry.register(
        new com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior("repeat_x_times") {

            private int currentRepeatCount = 0;

            @Override
            public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                AzAnimationController<?> controller = context.animationController();
                var maxRepeats = controller.animationProperties().repeatXTimes();

                currentRepeatCount++;

                if (maxRepeats > 1 && currentRepeatCount <= maxRepeats) {
                    var controllerTimer = controller.controllerTimer();
                    var keyframeManager = controller.keyframeManager();
                    var keyframeCallbackHandler = keyframeManager.keyframeCallbackHandler();

                    controllerTimer.reset();
                    keyframeCallbackHandler.reset();

                    context.stateMachine().play();
                } else {
                    context.stateMachine().stop();
                    currentRepeatCount = 0;
                }
            }
        }
    );

    /**
     * A predefined {@code AzPlayBehavior} that freezes the animation at a specific frame and pauses the state machine.
     */
    public static final com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior FREEZE_ON_FRAME = AzPlayBehaviorRegistry.register(
        new com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior("freeze_on_frame") {

            @Override
            public void onUpdate(AzAnimationControllerStateMachine.Context<?> context) {
                var controller = context.animationController();
                var controllerTimer = controller.controllerTimer();
                var freezeTickOffset = controller.animationProperties().freezeTickOffset();

                if (controllerTimer.getAdjustedTick() >= freezeTickOffset) {
                    controllerTimer.addToAdjustedTick(0);
                    context.stateMachine().pause();
                }
            }

            @Override
            public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                context.stateMachine().pause();
            }
        }
    );

    /**
     * Represents a play behavior where an animation holds on its last frame upon completion. When the animation
     * finishes, the associated state machine is paused, effectively freezing the animation on the final frame.
     */
    public static final com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior HOLD_ON_LAST_FRAME = AzPlayBehaviorRegistry
        .register(
            new com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior("hold_on_last_frame") {

                @Override
                public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                    context.stateMachine().pause();
                }
            }
        );

    /**
     * A predefined {@link com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior} that loops an animation
     * indefinitely.
     */
    public static final com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior LOOP = AzPlayBehaviorRegistry.register(
        new com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior("loop") {

            @Override
            public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                var controller = context.animationController();
                var controllerTimer = controller.controllerTimer();
                var keyframeManager = controller.keyframeManager();
                var keyframeCallbackHandler = keyframeManager.keyframeCallbackHandler();

                controllerTimer.reset();
                keyframeCallbackHandler.reset();
            }
        }
    );

    /**
     * A predefined {@link com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior} that plays an animation once
     * and stops the state machine upon completion.
     */
    public static final com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior PLAY_ONCE = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("play_once") {

            @Override
            public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                context.stateMachine().stop();
            }
        }
    );
}
