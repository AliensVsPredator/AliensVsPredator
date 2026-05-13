package com.blib.engine.input.router;

import org.jetbrains.annotations.ApiStatus;

/**
 * Single-method interface for an entry in the {@link InputRouter}'s priority chain. Each handler decides per event
 * whether to claim it (stop the chain) or continue. Replaces the prior pattern in {@code EngineWorkspaceScreen} where
 * {@code mouseClicked}/{@code keyPressed} were 200+ line methods mixing priority decisions with handler dispatch.
 */
@ApiStatus.Internal
@FunctionalInterface
public interface InputHandler {

    InputClaim handle(InputEvent event);
}
