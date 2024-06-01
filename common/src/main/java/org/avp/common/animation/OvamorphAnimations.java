package org.avp.common.animation;

import mod.azure.azurelib.common.internal.common.core.animatable.GeoAnimatable;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationController;
import mod.azure.azurelib.common.internal.common.core.animation.RawAnimation;
import mod.azure.azurelib.common.internal.common.core.object.PlayState;
import org.avp.common.entity.living.Ovamorph;

import java.util.function.Function;

public class OvamorphAnimations {

    private static final String CONTROLLER_NAME_OPEN = "open";

    private static final String ANIMATION_NAME_EGG_OPEN = "animation.egg.open";

    private static final RawAnimation ANIMATION_EGG_OPEN = RawAnimation.begin().thenPlayAndHold(ANIMATION_NAME_EGG_OPEN);

    private static final Function<Ovamorph, AnimationController.AnimationStateHandler<GeoAnimatable>> HANDLER_OPEN = entity -> event -> {
        if (entity.getOpenProgress() > 0) {
            return event.setAndContinue(ANIMATION_EGG_OPEN);
        }

        return PlayState.STOP;
    };

    public static <T extends Ovamorph & GeoAnimatable> void bootstrap(T entity, AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(
            new AnimationController<>(entity, CONTROLLER_NAME_OPEN, HANDLER_OPEN.apply(entity))
        );
    }

    private OvamorphAnimations() {
        throw new UnsupportedOperationException();
    }
}
