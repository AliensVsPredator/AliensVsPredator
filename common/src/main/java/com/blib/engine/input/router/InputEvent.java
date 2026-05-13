package com.blib.engine.input.router;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.input.router.coord.LogicalPx;
import com.blib.engine.input.router.coord.RawPx;

/**
 * Sealed event model used by {@link InputRouter}. Replaces the prior pattern where each input flavour landed via its
 * own mixin entry point ({@code MixinKeyboardHandler_EngineHotkey}, {@code MixinMouseHandler_Gizmo},
 * {@code MixinKeyboardInput_Engine}, etc.) and then poked workspace state directly. With the sealed type every input
 * has the same shape — a single dispatcher consumes it and asks priority handlers whether they claim it.
 * <p>
 * Coordinate fields use {@link RawPx}/{@link LogicalPx} so it's impossible to accidentally compare physical pixels with
 * the engine's logical-pixel space (post-{@code 0.375f} scale). Converters live alongside the wrappers; the prior
 * codebase did the conversion inline at every input-dispatch boundary in {@code EngineWorkspaceScreen}.
 */
@ApiStatus.Internal
public sealed interface InputEvent permits InputEvent.MouseDown, InputEvent.MouseUp, InputEvent.MouseMove, InputEvent.MouseDrag, InputEvent.Scroll, InputEvent.KeyDown, InputEvent.KeyUp, InputEvent.Char {

    record MouseDown(
        RawPx rawX,
        RawPx rawY,
        LogicalPx logicalX,
        LogicalPx logicalY,
        int button,
        int modifiers
    ) implements InputEvent {}

    record MouseUp(
        RawPx rawX,
        RawPx rawY,
        LogicalPx logicalX,
        LogicalPx logicalY,
        int button,
        int modifiers
    ) implements InputEvent {}

    record MouseMove(
        RawPx rawX,
        RawPx rawY,
        LogicalPx logicalX,
        LogicalPx logicalY,
        RawPx deltaRawX,
        RawPx deltaRawY
    ) implements InputEvent {}

    record MouseDrag(
        RawPx rawX,
        RawPx rawY,
        LogicalPx logicalX,
        LogicalPx logicalY,
        RawPx deltaRawX,
        RawPx deltaRawY,
        int button,
        int modifiers
    ) implements InputEvent {}

    record Scroll(
        RawPx rawX,
        RawPx rawY,
        LogicalPx logicalX,
        LogicalPx logicalY,
        double deltaX,
        double deltaY
    ) implements InputEvent {}

    record KeyDown(
        int keyCode,
        int scanCode,
        int modifiers
    ) implements InputEvent {}

    record KeyUp(
        int keyCode,
        int scanCode,
        int modifiers
    ) implements InputEvent {}

    record Char(
        int codepoint,
        int modifiers
    ) implements InputEvent {}
}
