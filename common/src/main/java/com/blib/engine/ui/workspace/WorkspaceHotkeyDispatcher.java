package com.blib.engine.ui.workspace;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.command.api.Command;
import com.blib.engine.command.api.CommandBus;
import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionOps;
import com.blib.engine.input.ActiveKeybindings;
import com.blib.engine.input.Keybindings;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.ui.EngineTickControl;
import com.blib.engine.ui.widget.TextInput;

/**
 * Workspace hotkey dispatch — undo / redo, reload, play-pause, jigsaw R/M/T, modeler T/R/S/P gizmos, world T/S/M
 * gizmos, clipboard Ctrl-C/X/V, and Delete. Each hotkey returns true on consume.
 * <p>
 * The dispatcher does not handle Esc cascades, modal dispatch, popup absorbers, wrapped-screen forwarding, or text
 * input focus — those stay in the screen's {@code keyPressed} where they share local state (e.g. the wrapped screen
 * reference). This class concentrates the long tail of single-key actions so the keyPressed method doesn't accumulate a
 * new 20-line block every time a feature ships.
 */
@ApiStatus.Internal
public final class WorkspaceHotkeyDispatcher {

    /** Workspace-side hooks the dispatcher invokes for state the dispatcher itself doesn't own. */
    public interface Host {

        CommandBus commands();

        boolean layoutHasModelerPanel();

        boolean layoutHasLocalHistoryPanel();

        boolean layoutHasAnimationTimelinePanel();
    }

    private final Host host;

    public WorkspaceHotkeyDispatcher(Host host) {
        this.host = host;
    }

    /**
     * Dispatch a key event against the hotkey table. Returns true if the key was consumed; false if the caller should
     * continue its own handling (Esc cascade, super.keyPressed, etc.).
     */
    public boolean dispatch(int keyCode, int modifiers) {
        // Ctrl+Z = universal undo. Local authoring layouts (modeler / texture) route to the client-side history;
        // everywhere else routes to the server-side ActionHistory.
        if (ActiveKeybindings.matchesKey(Keybindings.UNDO, keyCode, modifiers)) {
            if (host.layoutHasLocalHistoryPanel()) {
                ModelerActionHistory.undo();
            } else {
                host.commands().dispatch(new Command.UndoAction());
            }
            return true;
        }

        if (ActiveKeybindings.matchesKey(Keybindings.REDO, keyCode, modifiers)) {
            if (host.layoutHasLocalHistoryPanel()) {
                ModelerActionHistory.redo();
            } else {
                host.commands().dispatch(new Command.RedoAction());
            }
            return true;
        }

        // Reload Project. Wipes the tag-staging overlay since reload catches the runtime registry up to disk.
        if (ActiveKeybindings.matchesKey(Keybindings.RELOAD_PROJECT, keyCode, modifiers)) {
            if (ProjectSession.activeProject() != null) {
                host.commands().dispatch(new Command.ReloadProject(ProjectSession.activeProjectName()));
                TagStagingCache.clear();
            }
            return true;
        }

        // Space = play / pause toggle. Animation authoring owns this when its timeline is visible; otherwise keep the
        // existing world/viewport transport behavior.
        if (ActiveKeybindings.matchesKey(Keybindings.VIEWPORT_PLAY_PAUSE, keyCode, modifiers)) {
            if (host.layoutHasAnimationTimelinePanel()) {
                var animationState = AnimationEditorState.get();
                if (animationState.hasPlayableSelection()) {
                    animationState.togglePlayback();
                }
                return true;
            }
            EngineTickControl.toggle();
            return true;
        }

        // Placement-mode hotkeys while a jigsaw piece is held.
        if (JigsawPieceSelection.hasSelection()) {
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_ROTATE, keyCode, modifiers)) {
                JigsawPieceSelection.cycleRotation(1);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_MIRROR, keyCode, modifiers)) {
                JigsawPieceSelection.cycleMirror();
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_CYCLE_MODE, keyCode, modifiers)) {
                JigsawTool.cycleNextImplementedMode();
                return true;
            }
        }

        // Modeler-layout gizmo hotkeys take priority — T translate, R rotate, S resize, P pivot.
        if (host.layoutHasModelerPanel()) {
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_TRANSLATE, keyCode, modifiers)) {
                ModelerGizmoState.setMode(ModelerGizmoMode.TRANSLATE);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_ROTATE, keyCode, modifiers)) {
                ModelerGizmoState.setMode(ModelerGizmoMode.ROTATE);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_SCALE, keyCode, modifiers)) {
                ModelerGizmoState.setMode(ModelerGizmoMode.RESIZE);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_PIVOT, keyCode, modifiers)) {
                ModelerGizmoState.setMode(ModelerGizmoMode.PIVOT);
                return true;
            }
        }

        // World gizmo hotkeys: T/S/M auto-route to whichever gizmo matches the active selection.
        var tssel = SelectionManager.current().single();
        if (tssel instanceof EntitySelectable) {
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_TRANSLATE, keyCode, modifiers)) {
                EntityGizmoMode.set(EntityGizmoMode.TRANSLATE);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_SCALE, keyCode, modifiers)) {
                EntityGizmoMode.set(EntityGizmoMode.SCALE);
                return true;
            }
            // GIZMO_MOVE_BLOCKS intentionally not handled — entity gizmo has no MOVE_BLOCKS analog.
        } else {
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_TRANSLATE, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.TRANSLATE_VOLUME);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_SCALE, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.SCALE_VOLUME);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_MOVE_BLOCKS, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.MOVE_BLOCKS);
                return true;
            }
        }

        // Clipboard hotkeys gated on no focused text input so Ctrl+C in a name field doesn't copy blocks.
        if (TextInput.getFocused() == null) {
            if (ActiveKeybindings.matchesKey(Keybindings.COPY, keyCode, modifiers)) {
                BlockSelectionOps.copy(false);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.CUT, keyCode, modifiers)) {
                BlockSelectionOps.copy(true);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.PASTE, keyCode, modifiers)) {
                BlockSelectionOps.paste();
                return true;
            }
        }
        if (TextInput.getFocused() == null && ActiveKeybindings.matchesKey(Keybindings.DELETE, keyCode, modifiers)) {
            // Modeler-layout delete takes priority and always consumes the key.
            if (host.layoutHasModelerPanel()) {
                ModelerScene.get().deleteSelection();
                return true;
            }
            ViewportSelectionDelete.deleteCurrentSelection(host.commands());
            return true;
        }
        return false;
    }
}
