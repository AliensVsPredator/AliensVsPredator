package com.blib.engine.runtime.tool;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Published by {@link ToolStateMachine#activate} when the active tool changes. Tools whose state is per-tool (jigsaw
 * piece on cursor, entity-to-spawn, AABB corners, paint target) subscribe and clear their own state when the next tool
 * is not theirs — this replaces the implicit O(N²) graph of {@code Foo.select() → Bar.clear() → Baz.clear()}
 * cross-singleton calls.
 *
 * @param previous the previously-active tool, or {@code null} on the first activation of a session
 * @param current  the newly-active tool
 */
@ApiStatus.Internal
public record ToolChangedEvent(
    @Nullable ActiveTool previous,
    ActiveTool current
) {}
