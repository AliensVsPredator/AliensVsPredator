package com.blib.azurelib.common.animation.play_behavior;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AzPlayBehaviorRegistry {

    private static final Map<String, com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior> PLAY_BEHAVIORS = new HashMap<>();

    public static com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior register(
        com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior playBehavior
    ) {
        PLAY_BEHAVIORS.put(playBehavior.name(), playBehavior);
        return playBehavior;
    }

    public static com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior getOrDefault(
        String name,
        @NotNull com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior defaultValue
    ) {
        return PLAY_BEHAVIORS.getOrDefault(name, defaultValue);
    }

    public static @Nullable com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior getOrNull(String name) {
        return PLAY_BEHAVIORS.get(name);
    }

    public static Collection<AzPlayBehavior> getValues() {
        return Collections.unmodifiableCollection(PLAY_BEHAVIORS.values());
    }
}
