package com.blib.azurelib.common.animation.primitive;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.azurelib.common.animation.controller.AzAnimationController;

@Deprecated(forRemoval = true)
public interface AzLoopType {

    String name();

    boolean shouldPlayAgain(
        Object animatable,
        AzAnimationController<?> controller,
        AzBakedAnimation currentAnimation
    );

    Map<String, AzLoopType> LOOP_TYPES = new ConcurrentHashMap<>(5);

    AzLoopType FALSE = register("false", (animatable, controller, currentAnimation) -> false);

    AzLoopType TRUE = register("true", (animatable, controller, currentAnimation) -> true);

    AzLoopType PLAY_ONCE = register("play_once", FALSE);

    AzLoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (animatable, controller, currentAnimation) -> {
        controller.stateMachine().pause();

        return true;
    });

    AzLoopType LOOP = register("loop", TRUE);

    static AzLoopType fromJson(JsonElement json) {
        if (json == null || !json.isJsonPrimitive()) {
            return PLAY_ONCE;
        }

        var primitive = json.getAsJsonPrimitive();

        if (primitive.isBoolean()) {
            return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
        }

        if (primitive.isString()) {
            return fromString(primitive.getAsString());
        }

        return PLAY_ONCE;
    }

    static AzLoopType fromString(String name) {
        return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
    }

    static AzLoopType register(String name, AzLoopType loopType) {
        return register(name, (a, b, c) -> loopType.shouldPlayAgain(a, b, c));
    }

    static AzLoopType register(
        String name,
        TriFunction<Object, AzAnimationController<?>, AzBakedAnimation, Boolean> shouldPlayAgainFunction
    ) {
        var loopType = new AzLoopType() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean shouldPlayAgain(
                Object animatable,
                AzAnimationController<?> controller,
                AzBakedAnimation currentAnimation
            ) {
                return shouldPlayAgainFunction.apply(animatable, controller, currentAnimation);
            }
        };

        LOOP_TYPES.put(name, loopType);

        return loopType;
    }
}
