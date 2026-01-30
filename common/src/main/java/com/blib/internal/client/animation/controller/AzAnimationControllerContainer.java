package com.blib.internal.client.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import com.blib.api.client.animation.v1.controller.AzAnimationController;

public class AzAnimationControllerContainer<T> {

    private final Map<String, AzAnimationController<T>> animationControllersByName;

    public AzAnimationControllerContainer() {
        this.animationControllersByName = new Object2ObjectArrayMap<>();
    }

    @SafeVarargs
    public final void add(
        AzAnimationController<T> controller,
        AzAnimationController<T>... controllers
    ) {
        animationControllersByName.put(controller.name(), controller);

        for (var extraController : controllers) {
            animationControllersByName.put(extraController.name(), extraController);
        }
    }

    public @Nullable AzAnimationController<T> getOrNull(String controllerName) {
        return animationControllersByName.get(controllerName);
    }

    public Collection<AzAnimationController<T>> getAll() {
        return animationControllersByName.values();
    }
}
