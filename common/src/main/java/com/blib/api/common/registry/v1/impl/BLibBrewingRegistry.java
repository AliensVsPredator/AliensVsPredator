package com.blib.api.common.registry.v1.impl;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibBrewingRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibBrewingRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void registerMix(Holder<Potion> input, Supplier<? extends Item> ingredient, Holder<Potion> output) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a brewing recipe outside of mod's initialization window. Mod State: %s"
                    .formatted(mod.state())
            );
        }

        BLibInternalServices.REGISTRY.registerBrewingRecipe(mod, input, ingredient, output);
    }
}
