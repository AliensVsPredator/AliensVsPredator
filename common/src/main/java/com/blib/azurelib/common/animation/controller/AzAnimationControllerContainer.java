package com.blib.azurelib.common.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * A container class for managing a collection of
 * {@link com.blib.azurelib.common.animation.controller.AzAnimationController} instances. Provides methods to add,
 * retrieve, and access animation controllers by their names.
 *
 * @param <T> the type of the animation data or state managed by
 *            {@link com.blib.azurelib.common.animation.controller.AzAnimationController}.
 */
public class AzAnimationControllerContainer<T> {

    private final Map<String, com.blib.azurelib.common.animation.controller.AzAnimationController<T>> animationControllersByName;

    public AzAnimationControllerContainer() {
        this.animationControllersByName = new Object2ObjectArrayMap<>();
    }

    /**
     * Adds one or more {@link com.blib.azurelib.common.animation.controller.AzAnimationController} instances to the
     * container. Each controller is stored in the container using its unique name as the key.
     *
     * @param controller  the primary {@link com.blib.azurelib.common.animation.controller.AzAnimationController} to be
     *                    added to the container.
     * @param controllers additional {@link com.blib.azurelib.common.animation.controller.AzAnimationController}
     *                    instances to be added to the container. Multiple controllers can be specified in the varargs.
     */
    @SafeVarargs
    public final void add(
        com.blib.azurelib.common.animation.controller.AzAnimationController<T> controller,
        com.blib.azurelib.common.animation.controller.AzAnimationController<T>... controllers
    ) {
        animationControllersByName.put(controller.name(), controller);

        for (var extraController : controllers) {
            animationControllersByName.put(extraController.name(), extraController);
        }
    }

    /**
     * Retrieves an animation controller by its name. If no controller is associated with the given name, this method
     * returns null.
     *
     * @param controllerName the name of the animation controller to retrieve
     * @return the {@link com.blib.azurelib.common.animation.controller.AzAnimationController} instance associated with
     *         the given name, or null if no such controller exists
     */
    public @Nullable com.blib.azurelib.common.animation.controller.AzAnimationController<T> getOrNull(String controllerName) {
        return animationControllersByName.get(controllerName);
    }

    /**
     * Retrieves all animation controllers managed by this container as a collection. Each controller corresponds to an
     * entry stored within the container, which are indexed by their unique names.
     *
     * @return a collection of all {@link com.blib.azurelib.common.animation.controller.AzAnimationController} instances
     *         stored in this container
     */
    public Collection<AzAnimationController<T>> getAll() {
        return animationControllersByName.values();
    }
}
