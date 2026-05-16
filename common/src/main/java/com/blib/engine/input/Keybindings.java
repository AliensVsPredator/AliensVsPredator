package com.blib.engine.input;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Static catalog of every named input binding in the engine workspace. Display surfaces (the status bar's hint region;
 * future cheat-sheet overlay and toolbar hotkey labels) read entries here for their canonical display strings, so
 * renaming or rebinding stays single-source.
 * <p>
 * Handlers route through {@link ActiveKeybindings} so the active {@link KeybindingProfile}'s overrides take effect at
 * the resolution site. Esc-as-popup-dismissal in {@code EngineWorkspaceScreen} (and similar focus-gate checks) stays
 * literal on purpose — those are system controls, not user-rebindable.
 */
@ApiStatus.Internal
public final class Keybindings {

    // ----- File -----

    public static final Keybinding RELOAD_PROJECT = ctrlKey(GLFW.GLFW_KEY_R, "file.reload_project", "Reload Project");

    // ----- Edit -----

    public static final Keybinding UNDO = ctrlKey(GLFW.GLFW_KEY_Z, "edit.undo", "Undo");

    public static final Keybinding REDO = ctrlKey(GLFW.GLFW_KEY_Y, "edit.redo", "Redo");

    public static final Keybinding COPY = ctrlKey(GLFW.GLFW_KEY_C, "edit.copy", "Copy");

    public static final Keybinding CUT = ctrlKey(GLFW.GLFW_KEY_X, "edit.cut", "Cut");

    public static final Keybinding PASTE = ctrlKey(GLFW.GLFW_KEY_V, "edit.paste", "Paste");

    public static final Keybinding DELETE = key(GLFW.GLFW_KEY_DELETE, "edit.delete", "Delete");

    public static final Keybinding CANCEL = key(GLFW.GLFW_KEY_ESCAPE, "edit.cancel", "Cancel");

    // ----- Jigsaw placement (active when JigsawPieceSelection.hasSelection()) -----

    public static final Keybinding JIGSAW_PLACE = mouseButton(0, "jigsaw.place", "Place");

    public static final Keybinding JIGSAW_ROTATE = key(GLFW.GLFW_KEY_R, "jigsaw.rotate", "Rotate");

    // (Scroll-wheel rotation was removed — scroll trapped the user out of zooming. The R key is the rotation binding.)

    public static final Keybinding JIGSAW_MIRROR = key(GLFW.GLFW_KEY_M, "jigsaw.mirror", "Mirror");

    public static final Keybinding JIGSAW_CYCLE_MODE = key(GLFW.GLFW_KEY_T, "jigsaw.cycle_mode", "Cycle mode");

    // ----- Gizmos (active when an entity or block volume is selected) -----

    public static final Keybinding GIZMO_TRANSLATE = key(GLFW.GLFW_KEY_T, "gizmo.translate", "Translate");

    public static final Keybinding GIZMO_SCALE = key(GLFW.GLFW_KEY_S, "gizmo.scale", "Scale");

    /**
     * Modeler-only — the world-engine gizmo has no rotate mode (block volumes don't rotate). Defined alongside the
     * other gizmo bindings so the preferences dialog groups them together. Defaults to R, matching Blender / Blockbench
     * muscle memory.
     */
    public static final Keybinding GIZMO_ROTATE = key(GLFW.GLFW_KEY_R, "gizmo.rotate", "Rotate");

    /**
     * Modeler-only — drag the selected cube's pivot point along an axis. Defaults to P (mnemonic for "Pivot"); matches
     * the existing modeler convention of single-key mode toggles.
     */
    public static final Keybinding GIZMO_PIVOT = key(GLFW.GLFW_KEY_P, "gizmo.pivot", "Pivot");

    public static final Keybinding GIZMO_MOVE_BLOCKS = key(GLFW.GLFW_KEY_M, "gizmo.move_blocks", "Move blocks");

    public static final Keybinding GIZMO_SNAP_INT = modifier(Input.MOD_SHIFT, "gizmo.snap_integer", "Snap integer");

    // (MOVE_BLOCKS_COPY removed — Alt conflicts with the Linux window-manager's alt+drag-to-move. Underlying
    // BlockSelection.setMoveCopyMode is intact for a future re-bind.)

    // ----- Viewport navigation (no piece held, no special mode) -----

    public static final Keybinding VIEWPORT_SELECT = mouseButton(0, "viewport.select", "Select");

    public static final Keybinding VIEWPORT_CONTEXT = mouseButton(1, "viewport.context", "Context menu");

    public static final Keybinding VIEWPORT_BOX_SELECT = mouseDrag(0, "viewport.box_select", "Box select");

    public static final Keybinding VIEWPORT_ORBIT = mouseDrag(2, "viewport.orbit", "Orbit");

    public static final Keybinding VIEWPORT_PAN = mouseDragWithMods(2, Input.MOD_SHIFT, "viewport.pan", "Pan");

    public static final Keybinding VIEWPORT_DOLLY = mouseDragWithMods(2, Input.MOD_CTRL, "viewport.dolly", "Dolly");

    public static final Keybinding VIEWPORT_ZOOM = scroll("viewport.zoom", "Zoom");

    // ----- Viewport transport -----

    public static final Keybinding VIEWPORT_PLAY_PAUSE = key(GLFW.GLFW_KEY_SPACE, "viewport.play_pause", "Play/Pause");

    // ----- Territory Map panel -----

    public static final Keybinding TMAP_CLAIM = mouseButton(0, "tmap.claim", "Claim");

    public static final Keybinding TMAP_UNCLAIM = mouseButton(1, "tmap.unclaim", "Unclaim");

    public static final Keybinding TMAP_PAINT_CLAIM = mouseDrag(0, "tmap.paint_claim", "Paint claim");

    public static final Keybinding TMAP_PAINT_UNCLAIM = mouseDrag(1, "tmap.paint_unclaim", "Paint unclaim");

    public static final Keybinding TMAP_PAN = mouseDrag(2, "tmap.pan", "Pan map");

    public static final Keybinding TMAP_ZOOM = scroll("tmap.zoom", "Zoom");

    // ----- Paint mode (3D viewport while ClaimPaintTool.isActive()) -----

    public static final Keybinding PAINT_CLAIM = mouseDrag(0, "paint.claim", "Claim chunk");

    public static final Keybinding PAINT_UNCLAIM = mouseDrag(1, "paint.unclaim", "Unclaim chunk");

    // ----- Tabs -----

    public static final Keybinding TAB_CLOSE = mouseButton(2, "tabs.close", "Close tab");

    public static final Keybinding TAB_NEXT = keyWithMods(GLFW.GLFW_KEY_TAB, Input.MOD_CTRL, "tabs.next", "Next tab");

    public static final Keybinding TAB_PREVIOUS = keyWithMods(
        GLFW.GLFW_KEY_TAB,
        Input.MOD_CTRL | Input.MOD_SHIFT,
        "tabs.previous",
        "Previous tab"
    );

    private static final List<Keybinding> ALL_DEFAULTS = List.of(
        RELOAD_PROJECT,
        UNDO,
        REDO,
        COPY,
        CUT,
        PASTE,
        DELETE,
        CANCEL,
        JIGSAW_PLACE,
        JIGSAW_ROTATE,
        JIGSAW_MIRROR,
        JIGSAW_CYCLE_MODE,
        GIZMO_TRANSLATE,
        GIZMO_SCALE,
        GIZMO_ROTATE,
        GIZMO_PIVOT,
        GIZMO_MOVE_BLOCKS,
        GIZMO_SNAP_INT,
        VIEWPORT_SELECT,
        VIEWPORT_CONTEXT,
        VIEWPORT_BOX_SELECT,
        VIEWPORT_ORBIT,
        VIEWPORT_PAN,
        VIEWPORT_DOLLY,
        VIEWPORT_ZOOM,
        VIEWPORT_PLAY_PAUSE,
        TMAP_CLAIM,
        TMAP_UNCLAIM,
        TMAP_PAINT_CLAIM,
        TMAP_PAINT_UNCLAIM,
        TMAP_PAN,
        TMAP_ZOOM,
        PAINT_CLAIM,
        PAINT_UNCLAIM,
        TAB_CLOSE,
        TAB_NEXT,
        TAB_PREVIOUS
    );

    private Keybindings() {}

    /** Every default binding in declaration order. Used by the preferences UI to enumerate rows and reset overrides. */
    public static List<Keybinding> defaults() {
        return ALL_DEFAULTS;
    }

    /** Returns the category key (text before the first {@code .}), or {@code "other"} for unrecognised ids. */
    public static String categoryOf(String id) {
        var dot = id.indexOf('.');
        return dot < 0 ? "other" : id.substring(0, dot);
    }

    /** Human-readable label for a category key. Used for the category rail in the preferences dialog. */
    public static String categoryLabel(String categoryKey) {
        return switch (categoryKey) {
            case "file" -> "File";
            case "edit" -> "Edit";
            case "jigsaw" -> "Jigsaw";
            case "gizmo" -> "Gizmo";
            case "viewport" -> "Viewport";
            case "tmap" -> "Territory Map";
            case "paint" -> "Paint";
            case "tabs" -> "Tabs";
            default -> categoryKey;
        };
    }

    private static Keybinding key(int keyCode, String id, String label) {
        return new Keybinding(id, label, new Input.Key(keyCode, 0));
    }

    private static Keybinding ctrlKey(int keyCode, String id, String label) {
        return new Keybinding(id, label, new Input.Key(keyCode, Input.MOD_CTRL));
    }

    private static Keybinding keyWithMods(int keyCode, int mods, String id, String label) {
        return new Keybinding(id, label, new Input.Key(keyCode, mods));
    }

    private static Keybinding mouseButton(int button, String id, String label) {
        return new Keybinding(id, label, new Input.MouseButton(button, 0));
    }

    private static Keybinding mouseDrag(int button, String id, String label) {
        return new Keybinding(id, label, new Input.MouseDrag(button, 0));
    }

    private static Keybinding mouseDragWithMods(int button, int mods, String id, String label) {
        return new Keybinding(id, label, new Input.MouseDrag(button, mods));
    }

    private static Keybinding scroll(String id, String label) {
        return new Keybinding(id, label, new Input.Scroll(0));
    }

    private static Keybinding modifier(int mask, String id, String label) {
        return new Keybinding(id, label, new Input.Modifier(mask));
    }
}
