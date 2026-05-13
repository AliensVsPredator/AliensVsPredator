package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.ModelerPicker;
import com.blib.engine.modeler.ModelerRecentFiles;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerSceneLoader;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.gizmo.ModelerGizmoInput;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.render.ModelerRenderer;
import com.blib.engine.session.ProjectSession;

/**
 * 3D viewport for the in-engine Blockbench-style modeler. Renders an orbital-camera view of {@code ModelerScene} via an
 * offscreen framebuffer that's blit into the panel rect each frame.
 * <p>
 * Camera input mirrors the live-world {@link ViewportPanel}:
 * <ul>
 * <li>MMB drag → orbit yaw / pitch around the focus point.</li>
 * <li>MMB + Shift drag → pan (translate the focus point in screen space).</li>
 * <li>MMB + Ctrl drag → dolly (scale distance from the focus point).</li>
 * <li>Scroll wheel → zoom (same as dolly).</li>
 * </ul>
 * LMB is dispatched in priority order: toolbar buttons (mode switch), gizmo handle drag, then plain cube selection.
 * Hotkeys T / R / S set the active gizmo mode; Esc clears it.
 */
@ApiStatus.Internal
public final class ModelerViewportPanel implements Panel {

    private static final float ORBIT_SENSITIVITY = 0.4f;

    private static final float PAN_SENSITIVITY = 0.05f;

    private static final float DOLLY_SENSITIVITY = 0.01f;

    private static final float ZOOM_FACTOR_PER_NOTCH = 0.1f;

    private final ModelerRenderer renderer = new ModelerRenderer();

    /**
     * Bridge to the workspace's dropdown overlay — used by the panel-local {@link ModelerMenuBar} to open menus on chip
     * click. Null when the panel was constructed without a context (e.g. in tests); the menu-bar click handler gates on
     * it before attempting to open.
     */
    private final @Nullable PanelMenuOpener menuOpener;

    /** Modifier state latched at MMB press time. Non-null while a middle-button drag is active. */
    private @Nullable MmbDrag mmbDrag;

    /** True while the LMB is held over a gizmo handle and we own the drag. */
    private boolean gizmoDragActive;

    /** Cube state captured at gizmo drag-start; on release we diff against the live cube to push a memento action. */
    private @Nullable ModelerCube gizmoDragTarget;

    private @Nullable ModelerAction.CubeMemento gizmoDragBefore;

    private @Nullable ModelerGizmoMode gizmoDragMode;

    /** Panel rect captured at render time so click handlers can convert workspace coords → viewport-relative. */
    private int panelX, panelY, panelWidth, panelHeight;

    /**
     * Latest toolbar tooltip — refreshed each frame from {@link ModelerViewportToolbar}'s hit-test. The workspace's
     * tooltip pipeline calls {@link #tooltipText()} and renders the result near the cursor, so the panel just has to
     * keep this field current.
     */
    private @Nullable Component hoveredTooltip;

    /** Tests / direct callers can construct without a workspace-bound menu opener; chip clicks no-op in that case. */
    public ModelerViewportPanel() {
        this(null);
    }

    public ModelerViewportPanel(@Nullable PanelMenuOpener menuOpener) {
        this.menuOpener = menuOpener;
    }

    @Override
    public String title() {
        return "Modeler Viewport";
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;

        // Hover state — refresh before each scene render so the cube renderer can outline whichever cube is under
        // the cursor, and the gizmo renderer can brighten whichever handle is under the cursor. Suppressed while a
        // gizmo drag is in flight so the hover outline doesn't fight the drag-visual.
        var scene = ModelerScene.get();
        if (cursorInsidePanel(mouseX, mouseY) && !gizmoDragActive) {
            var hit = pickCubeAt(mouseX, mouseY);
            scene.hoveredCube = hit != null ? hit.cube() : null;
            ModelerGizmoInput.updateHover(mouseX - panelX, mouseY - panelY, panelWidth, panelHeight);
        } else {
            scene.hoveredCube = null;
            ModelerGizmoState.setHover(null);
        }

        renderer.render(graphics, x, y, width, height);
        // Panel-local menu bar lives along the top edge of the viewport — modeler-only file/edit/etc. menus that
        // would clutter the global menu bar if they lived there. Toolbar is shifted down by the menu-bar height so
        // the two strips stack instead of overlapping.
        ModelerMenuBar.render(graphics, x, y, width, mouseX, mouseY);
        int toolbarY = y + ModelerMenuBar.HEIGHT;
        ModelerViewportToolbar.render(graphics, x, toolbarY);

        // Toolbar tooltips. Refresh after toolbar render so the hit-test is against the rects just drawn this frame
        // (panel resize / layout changes are picked up on the same frame). Suppressed during gizmo drag — the user
        // is busy manipulating, not exploring controls.
        hoveredTooltip = null;
        if (!gizmoDragActive) {
            var modeHover = ModelerViewportToolbar.hitTestMode(mouseX, mouseY, x, toolbarY);
            if (modeHover != null) {
                hoveredTooltip = ModelerViewportToolbar.tooltipForMode(modeHover);
            } else if (ModelerViewportToolbar.hitTestFrame(mouseX, mouseY, x, toolbarY)) {
                hoveredTooltip = ModelerViewportToolbar.tooltipForFrame();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (!cursorInsidePanel(mouseX, mouseY)) {
            return false;
        }

        // Build a world-space ray from the camera through the cursor, hand it to ModelerPicker, and update the
        // selection. A miss clears the selection (Blockbench convention — click empty space to deselect).
        var hit = pickCubeAt(mouseX, mouseY);
        var scene = ModelerScene.get();
        scene.selection = hit != null ? new Selection.CubeSelection(hit.owner(), hit.cube()) : null;
        return true;
    }

    /** Cursor rect check shared by hover + click handling. */
    private boolean cursorInsidePanel(double mouseX, double mouseY) {
        if (panelWidth <= 0 || panelHeight <= 0) {
            return false;
        }
        return mouseX >= panelX && mouseX < panelX + panelWidth && mouseY >= panelY && mouseY < panelY + panelHeight;
    }

    /**
     * Ray-pick the cube under the cursor. Returns the {@link ModelerPicker.Hit} or null on a miss. Used by both the
     * hover refresh and the click handler so the two stay aligned with the renderer's transform stack.
     */
    private @org.jetbrains.annotations.Nullable ModelerPicker.Hit pickCubeAt(double mouseX, double mouseY) {
        var relX = (float) ((mouseX - panelX) / (double) panelWidth);
        var relY = (float) ((mouseY - panelY) / (double) panelHeight);
        var aspect = (float) panelWidth / (float) panelHeight;

        var scene = ModelerScene.get();
        var camera = scene.camera;
        var rayDir = camera.unprojectCursor(relX, relY, aspect);
        var camPos = camera.position();
        var rayOrigin = new Vec3(camPos.x, camPos.y, camPos.z);
        return ModelerPicker.pick(scene, rayOrigin, rayDir);
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        // MMB press latches the modifier state for the whole drag and captures the mouse, so the gesture stays
        // attached to this panel even if the cursor wanders outside its rect.
        if (button == 2) {
            mmbDrag = new MmbDrag(Screen.hasShiftDown(), Screen.hasControlDown());
            return true;
        }

        if (button == 0) {
            // Menu bar chip click — opens the matching modeler-scoped dropdown. Handled before the toolbar so a chip
            // that visually overlaps a toolbar slot (it doesn't today, but future layouts might be tighter) goes to
            // the menu. Consume the click for any hit inside the menu-bar strip to avoid falling through to gizmo
            // picking / cube selection underneath.
            int menuBarBottom = panelY + ModelerMenuBar.HEIGHT;
            if (mouseY >= panelY && mouseY < menuBarBottom) {
                var chip = ModelerMenuBar.hitChipAt(mouseX, mouseY);
                if (chip != null && menuOpener != null) {
                    var menu = buildModelerMenuFor(chip);
                    if (menu != null) {
                        menuOpener.open(menu);
                    }
                }
                return true;
            }
            // Toolbar takes priority — if the cursor is over a button, switch modes and consume the click so it
            // doesn't fall through to selection / gizmo picking.
            int toolbarY = panelY + ModelerMenuBar.HEIGHT;
            var modeHit = ModelerViewportToolbar.hitTestMode(mouseX, mouseY, panelX, toolbarY);
            if (modeHit != null) {
                ModelerGizmoState.setMode(modeHit);
                return true;
            }
            // Frame cycle button — click rotates LOCAL → GLOBAL → … so multi-frame support extends without a UI
            // redesign.
            if (ModelerViewportToolbar.hitTestFrame(mouseX, mouseY, panelX, toolbarY)) {
                ModelerGizmoState.setFrame(ModelerGizmoState.frame().next());
                return true;
            }

            // Gizmo handle drag — pickHandle in panel-relative coords and threshold against the captured snapshot.
            var panelRelX = mouseX - panelX;
            var panelRelY = mouseY - panelY;
            if (panelWidth > 0 && panelHeight > 0 && ModelerGizmoInput.tryStartDrag(panelRelX, panelRelY, panelWidth, panelHeight)) {
                gizmoDragActive = true;
                // Snapshot the cube's full mutable state at drag-start so on release we can push a memento action that
                // restores it on undo. The drag mutates origin/size/rotation in place; capturing all fields keeps the
                // memento type uniform with inspector edits (which also touch pivot/inflate).
                var drag = ModelerGizmoState.drag();
                if (drag != null) {
                    var startCube = drag.startSnapshot().cube();
                    gizmoDragTarget = startCube;
                    gizmoDragBefore = ModelerAction.CubeMemento.of(startCube);
                    gizmoDragMode = drag.mode();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && gizmoDragActive) {
            var panelRelX = mouseX - panelX;
            var panelRelY = mouseY - panelY;
            ModelerGizmoInput.updateDrag(panelRelX, panelRelY, panelWidth, panelHeight);
            return true;
        }

        if (button != 2 || mmbDrag == null) {
            return false;
        }

        // Convert workspace-logical deltas to raw-pixel deltas so sensitivity is independent of GUI scale (mirrors
        // ViewportPanel.mouseDragged).
        var guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        var rawDx = deltaX * guiScale;
        var rawDy = deltaY * guiScale;

        var camera = ModelerScene.get().camera;

        if (mmbDrag.ctrl()) {
            // Dolly: drag down = farther, drag up = closer. Multiplicative so it feels uniform at any distance.
            var factor = (float) Math.exp(rawDy * DOLLY_SENSITIVITY);
            camera.distance *= factor;
            camera.clampDistance();
        } else if (mmbDrag.shift()) {
            applyPan(camera, rawDx, rawDy);
        } else {
            // Sign matches EngineNavigation.applyOrbitDelta exactly: yaw += dx, pitch += dy. Under the engine's yaw
            // convention these signs give drag-the-scene orbit (drag right → camera circles to its world-left → scene
            // appears to rotate right with the cursor; drag down → camera tilts up → scene drops with the cursor).
            // The previous -= signs gave push-the-camera under this convention.
            camera.yaw += (float) rawDx * ORBIT_SENSITIVITY;
            camera.pitch += (float) rawDy * ORBIT_SENSITIVITY;
            camera.clampPitch();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && gizmoDragActive) {
            ModelerGizmoInput.endDrag();
            gizmoDragActive = false;
            // Diff before-snapshot against the post-drag cube. If anything changed, the gesture's a real edit and
            // belongs on the undo stack; a click that didn't move the cursor far enough to register a delta is a
            // no-op and we skip the push to keep the action stack clean.
            var before = gizmoDragBefore;
            var target = gizmoDragTarget;
            var mode = gizmoDragMode;
            gizmoDragBefore = null;
            gizmoDragTarget = null;
            gizmoDragMode = null;
            if (before != null && target != null && mode != null) {
                var after = ModelerAction.CubeMemento.of(target);
                if (after.differsFrom(before)) {
                    var typeId = switch (mode) {
                        case TRANSLATE -> "cube_translate";
                        case ROTATE -> "cube_rotate";
                        case RESIZE -> "cube_resize";
                        case PIVOT -> "cube_pivot";
                        default -> "cube_edit";
                    };
                    var description = switch (mode) {
                        case TRANSLATE -> "Translate cube " + target.name;
                        case ROTATE -> "Rotate cube " + target.name;
                        case RESIZE -> "Resize cube " + target.name;
                        case PIVOT -> "Move pivot of cube " + target.name;
                        default -> "Edit cube " + target.name;
                    };
                    ModelerActionHistory.push(
                        new ModelerAction.CubeMementoAction(typeId, description, System.currentTimeMillis(), target, before, after)
                    );
                }
            }
            return true;
        }
        if (button == 2) {
            mmbDrag = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var camera = ModelerScene.get().camera;
        var factor = scrollY > 0 ? (1.0f - ZOOM_FACTOR_PER_NOTCH) : (1.0f + ZOOM_FACTOR_PER_NOTCH);
        camera.distance *= factor;
        camera.clampDistance();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // T / R / S switch gizmo modes; Esc clears. Matches the toolbar buttons and is industry-standard for modeling
        // tools (Blockbench / Maya / Blender use similar bindings, though their letter choices differ).
        switch (keyCode) {
            case GLFW.GLFW_KEY_T -> {
                ModelerGizmoState.setMode(ModelerGizmoMode.TRANSLATE);
                return true;
            }
            case GLFW.GLFW_KEY_R -> {
                ModelerGizmoState.setMode(ModelerGizmoMode.ROTATE);
                return true;
            }
            case GLFW.GLFW_KEY_S -> {
                ModelerGizmoState.setMode(ModelerGizmoMode.RESIZE);
                return true;
            }
            case GLFW.GLFW_KEY_ESCAPE -> {
                if (ModelerGizmoState.mode() != ModelerGizmoMode.OFF) {
                    ModelerGizmoState.setMode(ModelerGizmoMode.OFF);
                    return true;
                }
            }
            default -> {
                /* fall through */
            }
        }
        return false;
    }

    private static void applyPan(com.blib.engine.modeler.ModelerCamera camera, double rawDx, double rawDy) {
        // Drag-the-scene convention in both axes: cursor and scene move together. Formula matches
        // EngineNavigation.applyPanDelta exactly — focus += right·dx + up·dy. The subtle point is that
        // EngineCameraBasis.screenRight is named from a screen-space-mapping POV (the direction the camera shifts
        // when the cursor moves +x), which under MC's left-handed yaw is actually opposite the camera's world-space
        // right axis. Negating dx here would un-do that and give push-the-camera; the plus signs give drag-the-scene.
        var right = com.blib.engine.session.EngineCameraBasis.screenRight(camera.yaw);
        var up = com.blib.engine.session.EngineCameraBasis.screenUp(camera.yaw, camera.pitch);
        var scale = PAN_SENSITIVITY * camera.distance / 64.0;
        var dx = (right.x * rawDx + up.x * rawDy) * scale;
        var dy = (right.y * rawDx + up.y * rawDy) * scale;
        var dz = (right.z * rawDx + up.z * rawDy) * scale;
        camera.focusPoint = camera.focusPoint.add(dx, dy, dz);
    }

    private record MmbDrag(
        boolean shift,
        boolean ctrl
    ) {}

    /**
     * Build the dropdown for a {@link ModelerMenuBar} chip click. Anchored just below the chip rect so the dropdown
     * pins to the affordance the user just clicked. Returns null when the chip is unrecognized or its rect hasn't been
     * laid out yet (first frame before {@link ModelerMenuBar#render} ran — practically never reached, but the null
     * check keeps the dispatch defensive).
     */
    private @Nullable DropdownMenu buildModelerMenuFor(String chip) {
        var chipRect = ModelerMenuBar.chipRect(chip);
        if (chipRect == null) {
            return null;
        }
        int anchorX = chipRect.x();
        int anchorY = chipRect.y() + chipRect.height() + 1;

        return switch (chip) {
            case ModelerMenuBar.CHIP_FILE -> {
                var items = new java.util.ArrayList<DropdownMenu.Item>();
                items.add(new DropdownMenu.Item("New", () -> {}, buildNewSubmenu()));
                items.add(new DropdownMenu.Item("Recent", () -> {}, buildRecentSubmenu()));
                items.add(new DropdownMenu.Item("Open Model", ModelerViewportPanel::openGeoModelFromFile));
                yield new DropdownMenu(anchorX, anchorY, items);
            }
            default -> null;
        };
    }

    /**
     * "New" submenu items. Only Entity for now — the modeler's data model implicitly assumes entity-shaped output;
     * Block / Item types will land alongside their authoring affordances later.
     */
    private static java.util.List<DropdownMenu.Item> buildNewSubmenu() {
        return java.util.List.of(new DropdownMenu.Item("Entity", ModelerViewportPanel::newEntityModel));
    }

    /**
     * "Recent" submenu — newest-first list of paths from {@link ModelerRecentFiles}, scoped to the active project. Each
     * item, when clicked, loads its file and bumps it back to the head of the list. Falls back to a single
     * disabled-looking placeholder when there's no project or no history yet — submitting an empty submenu would render
     * a 0-row dropdown that looks broken.
     */
    private static java.util.List<DropdownMenu.Item> buildRecentSubmenu() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return java.util.List.of(new DropdownMenu.Item("(no project active)", () -> {}));
        }
        var paths = ModelerRecentFiles.list(project);
        if (paths.isEmpty()) {
            return java.util.List.of(new DropdownMenu.Item("(no recent files)", () -> {}));
        }
        var items = new java.util.ArrayList<DropdownMenu.Item>(paths.size());
        for (var pathStr : paths) {
            var label = java.nio.file.Path.of(pathStr).getFileName().toString();
            items.add(new DropdownMenu.Item(label, () -> openRecentFile(pathStr)));
        }
        return items;
    }

    /** "New → Entity": replace the modeler scene with a fresh default-cube seed. */
    private static void newEntityModel() {
        ModelerScene.get().resetToEntity();
    }

    /**
     * FILE → "Open Model" — OS-native open-file dialog via {@link ModelerFilePicker#pickGeoModel}, then hand the path
     * to {@link ModelerSceneLoader#loadFromFile} which replaces the modeler's active scene. On a successful load
     * records the path in {@link ModelerRecentFiles} so it shows up under Recent next time. No-op on cancel; load
     * errors are logged inside the loader.
     * <p>
     * Seeds the picker with the parent directory of the most recently opened model for the active project (when any),
     * so authors who keep coming back to the same folder don't have to navigate from scratch every time. The recent
     * list already churns to the latest opened file, so this hint stays in sync without separate state.
     */
    private static void openGeoModelFromFile() {
        var picked = ModelerFilePicker.pickGeoModel(inferInitialPickerDir());
        if (picked == null) {
            return;
        }
        if (ModelerSceneLoader.loadFromFile(picked)) {
            var project = ProjectSession.activeProjectName();
            if (!project.isEmpty()) {
                ModelerRecentFiles.recordOpen(project, picked.toString());
            }
        }
    }

    /**
     * Parent directory of the project's most-recent imported model, or null when there's no recent history (no project,
     * no recents yet, or the recorded path has no resolvable parent). Falling back to null lets the picker use the OS
     * default location for a fresh-start feel rather than guessing at game-dir or similar.
     */
    private static @Nullable java.nio.file.Path inferInitialPickerDir() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        var recents = ModelerRecentFiles.list(project);
        if (recents.isEmpty()) {
            return null;
        }
        return java.nio.file.Path.of(recents.get(0)).getParent();
    }

    /**
     * "Recent → &lt;file&gt;" — same load path as {@link #openGeoModelFromFile} but skips the file picker. Re-records
     * the path so opening from Recent promotes the entry back to the top, which is the standard convention.
     */
    private static void openRecentFile(String pathStr) {
        var path = java.nio.file.Path.of(pathStr);
        if (ModelerSceneLoader.loadFromFile(path)) {
            var project = ProjectSession.activeProjectName();
            if (!project.isEmpty()) {
                ModelerRecentFiles.recordOpen(project, pathStr);
            }
        }
    }
}
