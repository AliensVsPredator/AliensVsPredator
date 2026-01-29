package com.blib.azurelib.common.animation.play_behavior;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.controller.state.machine.AzAnimationControllerStateMachine;

public class AzPlayBehaviors {

    private AzPlayBehaviors() {}

    // public static final AzPlayBehavior PING_PONG = AzPlayBehaviorRegistry.register(
    // new AzPlayBehavior("ping_pong") {
    // }
    // );

    public static final AzPlayBehavior REPEAT_X_TIMES = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("repeat_x_times") {

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

    public static final AzPlayBehavior FREEZE_ON_FRAME = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("freeze_on_frame") {

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

    public static final AzPlayBehavior HOLD_ON_LAST_FRAME = AzPlayBehaviorRegistry
        .register(
            new AzPlayBehavior("hold_on_last_frame") {

                @Override
                public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                    context.stateMachine().pause();
                }
            }
        );

    public static final AzPlayBehavior LOOP = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("loop") {

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

    public static final AzPlayBehavior PLAY_ONCE = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("play_once") {

            @Override
            public void onFinish(AzAnimationControllerStateMachine.Context<?> context) {
                context.stateMachine().stop();
            }
        }
    );
}
