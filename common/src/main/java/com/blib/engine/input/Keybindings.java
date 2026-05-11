package com.blib.engine.input;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * Static catalog of every named input binding in the engine workspace. Display surfaces (the status bar's hint region;
 * future cheat-sheet overlay and toolbar hotkey labels) read entries here for their canonical display strings, so
 * renaming or rebinding stays single-source.
 * <p>
 * Handler sites still use literal {@code GLFW_KEY_*} constants — they'll migrate to {@link Keybinding#matchesKey} et
 * al. in a follow-up. For now, the catalog and the handlers are kept in sync by convention; any drift surfaces in the
 * status bar before it surfaces in behavior.
 */
@ApiStatus.Internal
public final class Keybindings {

    // ----- Edit -----

    public static final Keybinding UNDO = ctrlKey(GLFW.GLFW_KEY_Z, "edit.undo", "Undo");

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

    public static final Keybinding GRID_BYPASS = modifier(Input.MOD_ALT, "jigsaw.grid_bypass", "Bypass grid");

    // ----- Gizmos (active when an entity or block volume is selected) -----

    public static final Keybinding GIZMO_TRANSLATE = key(GLFW.GLFW_KEY_T, "gizmo.translate", "Translate");

    public static final Keybinding GIZMO_SCALE = key(GLFW.GLFW_KEY_S, "gizmo.scale", "Scale");

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

    private Keybindings() {}

    private static Keybinding key(int keyCode, String id, String label) {
        return new Keybinding(id, label, new Input.Key(keyCode, 0));
    }

    private static Keybinding ctrlKey(int keyCode, String id, String label) {
        return new Keybinding(id, label, new Input.Key(keyCode, Input.MOD_CTRL));
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
