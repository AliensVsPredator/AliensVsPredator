package com.blib.azurelib.common.animation.easing;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;

import com.blib.azurelib.common.animation.controller.keyframe.AzAnimationPoint;
import com.blib.azurelib.common.animation.easing.bedrock_easings.BezierEasing;
import com.blib.azurelib.core.utils.Interpolations;

public class AzEasingTypes {

    public static final com.blib.azurelib.common.animation.easing.AzEasingType NONE = AzEasingTypeRegistry.register(
        "none",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::linear
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType LINEAR = AzEasingTypeRegistry.register("linear", NONE);

    public static final com.blib.azurelib.common.animation.easing.AzEasingType STEP = AzEasingTypeRegistry.register(
        "step",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.step(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_SINE = AzEasingTypeRegistry.register(
        "easeinsine",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(com.blib.azurelib.common.animation.easing.AzEasingUtil::sine)
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_SINE = AzEasingTypeRegistry.register(
        "easeoutsine",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::sine
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_SINE = AzEasingTypeRegistry.register(
        "easeinoutsine",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::sine
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_QUAD = AzEasingTypeRegistry.register(
        "easeinquad",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::quadratic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_QUAD = AzEasingTypeRegistry.register(
        "easeoutquad",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::quadratic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_QUAD = AzEasingTypeRegistry.register(
        "easeinoutquad",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::quadratic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_CUBIC = AzEasingTypeRegistry.register(
        "easeincubic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::cubic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_CUBIC = AzEasingTypeRegistry.register(
        "easeoutcubic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::cubic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_CUBIC = AzEasingTypeRegistry.register(
        "easeinoutcubic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::cubic
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_QUART = AzEasingTypeRegistry.register(
        "easeinquart",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(4)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_QUART = AzEasingTypeRegistry.register(
        "easeoutquart",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(4)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_QUART = AzEasingTypeRegistry.register(
        "easeinoutquart",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(4)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_QUINT = AzEasingTypeRegistry.register(
        "easeinquint",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(4)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_QUINT = AzEasingTypeRegistry.register(
        "easeoutquint",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(5)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_QUINT = AzEasingTypeRegistry.register(
        "easeinoutquint",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.pow(5)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_EXPO = AzEasingTypeRegistry.register(
        "easeinexpo",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(com.blib.azurelib.common.animation.easing.AzEasingUtil::exp)
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_EXPO = AzEasingTypeRegistry.register(
        "easeoutexpo",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(com.blib.azurelib.common.animation.easing.AzEasingUtil::exp)
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_EXPO = AzEasingTypeRegistry.register(
        "easeinoutexpo",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::exp
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_CIRC = AzEasingTypeRegistry.register(
        "easeincirc",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::circle
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_CIRC = AzEasingTypeRegistry.register(
        "easeoutcirc",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::circle
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_CIRC = AzEasingTypeRegistry.register(
        "easeinoutcirc",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil::circle
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_BACK = AzEasingTypeRegistry.register(
        "easeinback",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.back(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_BACK = AzEasingTypeRegistry.register(
        "easeoutback",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.back(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_BACK = AzEasingTypeRegistry.register(
        "easeinoutback",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.back(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_ELASTIC = AzEasingTypeRegistry.register(
        "easeinelastic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.elastic(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_ELASTIC = AzEasingTypeRegistry.register(
        "easeoutelastic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.elastic(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_ELASTIC = AzEasingTypeRegistry.register(
        "easeinoutelastic",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.elastic(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_BOUNCE = AzEasingTypeRegistry.register(
        "easeinbounce",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeIn(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.bounce(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_OUT_BOUNCE = AzEasingTypeRegistry.register(
        "easeoutbounce",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.bounce(value)
        )
    );

    public static final com.blib.azurelib.common.animation.easing.AzEasingType EASE_IN_OUT_BOUNCE = AzEasingTypeRegistry.register(
        "easeinoutbounce",
        value -> com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
            com.blib.azurelib.common.animation.easing.AzEasingUtil.bounce(value)
        )
    );

    // Bedrock Animation Types
    /**
     * <b>Author:</b> <a href="https://github.com/ZigyTheBird">ZigyTheBird</a>
     */
    public static final com.blib.azurelib.common.animation.easing.AzEasingType BEZIER = AzEasingTypeRegistry.register(
        "bezier",
        new BezierEasing() {

            @Override
            public String name() {
                return "Bezier";
            }

            @Override
            public boolean isEasingBefore() {
                return true;
            }
        }
    );

    /**
     * <b>Author:</b> <a href="https://github.com/ZigyTheBird">ZigyTheBird</a>
     */
    public static final com.blib.azurelib.common.animation.easing.AzEasingType BEZIER_AFTER = AzEasingTypeRegistry.register(
        "bezier_after",
        new BezierEasing() {

            @Override
            public String name() {
                return "Bezier After";
            }

            @Override
            public boolean isEasingBefore() {
                return false;
            }
        }
    );

    /**
     * <b>Author:</b> <a href="https://github.com/ZigyTheBird">ZigyTheBird</a>
     */
    public static final com.blib.azurelib.common.animation.easing.AzEasingType CATMULLROM = AzEasingTypeRegistry.register(
        "catmullrom",
        new com.blib.azurelib.common.animation.easing.AzEasingType() {

            @Override
            public String name() {
                return "Catmull-Rom";
            }

            @Override
            public Double2DoubleFunction buildTransformer(Double value) {
                return com.blib.azurelib.common.animation.easing.AzEasingUtil.easeInOut(
                    com.blib.azurelib.common.animation.easing.AzEasingUtil::catmullRom
                );
            }

            @Override
            public double apply(AzAnimationPoint animationPoint, Double easingValue, double lerpValue) {
                if (animationPoint.currentTick() >= animationPoint.transitionLength()) {
                    return animationPoint.animationEndValue();
                }

                var easingArgs = animationPoint.keyframe().easingArgs();

                if (easingArgs.size() < 2)
                    return Interpolations.lerp(
                        buildTransformer(easingValue).apply(lerpValue),
                        animationPoint.animationStartValue(),
                        animationPoint.animationEndValue()
                    );

                return AzEasingUtil.catmullRom(
                    lerpValue,
                    easingArgs.get(0).get(),
                    animationPoint.animationStartValue(),
                    animationPoint.animationEndValue(),
                    easingArgs.get(1).get()
                );
            }
        }

    );

    public static AzEasingType random() {
        var collection = AzEasingTypeRegistry.getValues();

        return collection.stream()
            .skip((int) (collection.size() * Math.random()))
            .findFirst()
            .orElse(null);
    }
}
