package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.ModelerPicker;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
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
 * LMB is intentionally not bound for camera; it stays free for cube selection (later). Modifier state is latched at MMB
 * press time so a stray Shift / Ctrl release mid-drag can't flip the gesture mode end-to-end — same latch pattern the
 * world {@code ViewportPanel} uses.
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
        renderer.render(graphics, x, y, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (panelWidth <= 0 || panelHeight <= 0) {
            return false;
        }
        if (mouseX < panelX || mouseX >= panelX + panelWidth || mouseY < panelY || mouseY >= panelY + panelHeight) {
            return false;
        }

        // Build a world-space ray from the camera through the cursor, hand it to ModelerPicker, and update the
        // selection. A miss clears the selection (Blockbench convention — click empty space to deselect).
        var relX = (float) ((mouseX - panelX) / (double) panelWidth);
        var relY = (float) ((mouseY - panelY) / (double) panelHeight);
        var aspect = (float) panelWidth / (float) panelHeight;

        var scene = ModelerScene.get();
        var camera = scene.camera;
        var rayDir = camera.unprojectCursor(relX, relY, aspect);
        var camPos = camera.position();
        var rayOrigin = new Vec3(camPos.x, camPos.y, camPos.z);

        var hit = ModelerPicker.pick(scene, rayOrigin, rayDir);
        scene.selection = hit != null ? new Selection.CubeSelection(hit.owner(), hit.cube()) : null;
        return true;
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        // MMB press latches the modifier state for the whole drag and captures the mouse, so the gesture stays
        // attached to this panel even if the cursor wanders outside its rect.
        if (button == 2) {
            mmbDrag = new MmbDrag(Screen.hasShiftDown(), Screen.hasControlDown());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
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
