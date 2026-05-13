package com.blib.engine.runtime.tool;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.runtime.EngineSessionHolder;
import com.blib.engine.runtime.EventBus;

/**
 * Single source of truth for which editor tool is armed. Publishes {@link ToolChangedEvent} on every transition so each
 * tool's static singleton can self-disarm when another tool takes over, instead of having every tool's
 * {@code select()/activate()} method call {@code .clear()} on every other tool's singleton.
 * <p>
 * Session-scoped instance owned by {@code EngineSessionScope.services()}: a fresh state machine is created on every
 * engine activation and discarded on close, so the active tool always starts at {@link ActiveTool#SELECT} for a fresh
 * session without an explicit reset.
 */
@ApiStatus.Internal
public final class ToolStateMachine {

    private ActiveTool active = ActiveTool.SELECT;

    public ToolStateMachine() {}

    /**
     * The active session's tool state machine. Looks up the instance via {@link EngineSessionHolder}, which is set by
     * {@code EngineMode.enter} before any subscribers are installed.
     *
     * @throws IllegalStateException if called outside an engine session
     */
    public static ToolStateMachine get() {
        var scope = EngineSessionHolder.current();
        if (scope == null) {
            throw new IllegalStateException("ToolStateMachine.get() called outside an engine session");
        }
        return scope.services().require(ToolStateMachine.class);
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
}
