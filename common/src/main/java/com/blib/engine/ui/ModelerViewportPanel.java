package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.blib.engine.modeler.ModelerPicker;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.gizmo.ModelerGizmoInput;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.render.ModelerRenderer;

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

    /** Modifier state latched at MMB press time. Non-null while a middle-button drag is active. */
    private @Nullable MmbDrag mmbDrag;

    /** True while the LMB is held over a gizmo handle and we own the drag. */
    private boolean gizmoDragActive;

    /** Panel rect captured at render time so click handlers can convert workspace coords → viewport-relative. */
    private int panelX, panelY, panelWidth, panelHeight;

    @Override
    public String title() {
        return "Modeler Viewport";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;

        // Hover state — refresh before each scene render so the cube renderer can outline whichever cube is under
        // the cursor. Suppressed while a gizmo drag is in flight so the hover outline doesn't fight the drag-visual.
        var scene = ModelerScene.get();
        if (cursorInsidePanel(mouseX, mouseY) && !gizmoDragActive) {
            var hit = pickCubeAt(mouseX, mouseY);
            scene.hoveredCube = hit != null ? hit.cube() : null;
        } else {
            scene.hoveredCube = null;
        }

        renderer.render(graphics, x, y, width, height);
        ModelerViewportToolbar.render(graphics, x, y);
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
            // Toolbar takes priority — if the cursor is over a button, switch modes and consume the click so it
            // doesn't fall through to selection / gizmo picking.
            var toolbarHit = ModelerViewportToolbar.hitTest(mouseX, mouseY, panelX, panelY);
            if (toolbarHit != null) {
                ModelerGizmoState.setMode(toolbarHit);
                return true;
            }

            // Gizmo handle drag — pickHandle in panel-relative coords and threshold against the captured snapshot.
            var panelRelX = mouseX - panelX;
            var panelRelY = mouseY - panelY;
            if (panelWidth > 0 && panelHeight > 0 && ModelerGizmoInput.tryStartDrag(panelRelX, panelRelY, panelWidth, panelHeight)) {
                gizmoDragActive = true;
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
}
