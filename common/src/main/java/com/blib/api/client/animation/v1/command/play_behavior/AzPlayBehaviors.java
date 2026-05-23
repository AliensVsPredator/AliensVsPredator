package com.blib.api.client.animation.v1.command.play_behavior;

import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public class AzPlayBehaviors {

    private AzPlayBehaviors() {}

    public static final AzPlayBehavior FREEZE_ON_FRAME = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("freeze_on_frame") {

            @Override
            public void onUpdate(AzAnimationTrackStateMachine.Context<?> context) {
                var track = context.animationTrack();
                var trackTimer = track.trackTimer();
                var freezeTickOffset = track.animationProperties().freezeTickOffset();

                if (trackTimer.getAdjustedTick() >= freezeTickOffset) {
                    trackTimer.addToAdjustedTick(0);
                    context.stateMachine().pause();
                }
            }

            @Override
            public void onFinish(AzAnimationTrackStateMachine.Context<?> context) {
                context.stateMachine().pause();
            }
        }
    );

    public static final AzPlayBehavior HOLD_ON_LAST_FRAME = AzPlayBehaviorRegistry
        .register(
            new AzPlayBehavior("hold_on_last_frame") {

                @Override
                public void onFinish(AzAnimationTrackStateMachine.Context<?> context) {
                    context.stateMachine().pause();
                }
            }
        );

    public static final AzPlayBehavior LOOP = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("loop") {

            @Override
            public void onFinish(AzAnimationTrackStateMachine.Context<?> context) {
                var track = context.animationTrack();
                var trackTimer = track.trackTimer();
                var keyframeManager = track.keyframeManager();
                var keyframeCallbackHandler = keyframeManager.keyframeCallbackHandler();

                trackTimer.reset();
                keyframeCallbackHandler.reset();
            }
        }
    );

    public static final AzPlayBehavior PLAY_ONCE = AzPlayBehaviorRegistry.register(
        new AzPlayBehavior("play_once") {

            @Override
            public void onFinish(AzAnimationTrackStateMachine.Context<?> context) {
                context.stateMachine().stop();
            }
        }
    );
}
