package com.blib.engine.runtime.tool;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.runtime.EventBus;

/**
 * Single source of truth for which editor tool is armed. Publishes {@link ToolChangedEvent} on every transition so each
 * tool's static singleton can self-disarm when another tool takes over, instead of having every tool's
 * {@code select()/activate()} method call {@code .clear()} on every other tool's singleton.
 * <p>
 * Static singleton for parity with the existing engine state model; promoted to a session-scoped instance in Step 9
 * when {@code SessionScope} owns all services. {@link #reset} is registered on the session scope so the active tool
 * always starts at {@link ActiveTool#SELECT} for a fresh session.
 */
@ApiStatus.Internal
public final class ToolStateMachine {

    private static final ToolStateMachine INSTANCE = new ToolStateMachine();

    private ActiveTool active = ActiveTool.SELECT;

    private ToolStateMachine() {}

    public static ToolStateMachine get() {
        return INSTANCE;
    }

    public ActiveTool active() {
        return active;
    }

    /**
     * Switch the active tool. No-op if already on the requested tool — saves a redundant event broadcast and prevents
     * listeners from running their disarm logic against state that's already correct (which used to manifest as gizmo
     * flicker on duplicate clicks).
     */
    public void activate(ActiveTool tool) {
        if (tool == active) {
            return;
        }
        var previous = active;
        active = tool;
        EventBus.get().publish(new ToolChangedEvent(previous, tool));
    }

    /** Restore the default armed tool. Wired to {@code EngineSessionScope.onClose} via {@code EngineMode.enter}. */
    public void reset() {
        active = ActiveTool.SELECT;
    }
}
