package org.avp.common.animation;

import mod.azure.azurelib.common.internal.common.core.animatable.GeoAnimatable;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationController;
import mod.azure.azurelib.common.internal.common.core.animation.RawAnimation;
import mod.azure.azurelib.common.internal.common.core.object.PlayState;
import org.avp.common.entity.living.Facehugger;

import java.util.function.Function;

public class FacehuggerAnimations {

    private static final String CONTROLLER_NAME_IDLE = "idle";
    private static final String CONTROLLER_NAME_MOVE = "move";

    private static final String ANIMATION_NAME_TAIL_SWAY = "animation.tailsway";

    private static final String ANIMATION_NAME_WALK = "animation.walk";

    private static final RawAnimation ANIMATION_CRAWL = RawAnimation.begin().thenPlay(ANIMATION_NAME_WALK);

    private static final RawAnimation ANIMATION_TAIL_SWAY = RawAnimation.begin().thenPlay(ANIMATION_NAME_TAIL_SWAY);

    private static final Function<Facehugger, AnimationController.AnimationStateHandler<GeoAnimatable>> HANDLER_IDLE = entity -> event -> {
        if (!event.isMoving()) {
            return event.setAndContinue(ANIMATION_TAIL_SWAY);
        }

        return PlayState.STOP;
    };

    private static final Function<Facehugger, AnimationController.AnimationStateHandler<GeoAnimatable>> HANDLER_MOVEMENT = entity -> event -> {
        if (event.isMoving()) {
            return event.setAndContinue(ANIMATION_CRAWL);
        }

        return PlayState.STOP;
    };

    public static <T extends Facehugger & GeoAnimatable> void bootstrap(T entity, AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(
            new AnimationController<>(entity, CONTROLLER_NAME_IDLE, HANDLER_IDLE.apply(entity)),
            new AnimationController<>(entity, CONTROLLER_NAME_MOVE, HANDLER_MOVEMENT.apply(entity))
        );
    }

    private FacehuggerAnimations() {
        throw new UnsupportedOperationException();
    }
}
