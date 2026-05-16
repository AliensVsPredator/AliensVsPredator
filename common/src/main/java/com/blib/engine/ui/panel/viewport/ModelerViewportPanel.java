package com.blib.engine.ui.panel.viewport;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.api.client.registry.v1.AzItemRendererRegistry;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.engine.gizmo.BLibItemTransformOverrides;
import com.blib.engine.modeler.ModelerBlockModelLoader;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCamera;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.ModelerPicker;
import com.blib.engine.modeler.ModelerRecentFiles;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerSceneLoader;
import com.blib.engine.modeler.ModelerTransformOps;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.animation.AnimationCollisionState;
import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.gizmo.ModelerGizmoInput;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.ModelerFaceTextureMapping;
import com.blib.engine.modeler.texture.ModelerTextureUsage;
import com.blib.engine.render.modeler.ModelerRenderer;
import com.blib.engine.session.EngineCameraBasis;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TexturePaintOps;
import com.blib.engine.texture.TextureTool;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.panel.chrome.ModelerMenuBar;
import com.blib.engine.ui.panel.chrome.ModelerViewportToolbar;
import com.blib.engine.ui.panel.texture.TextureTabIndicators;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.workspace.WorkspaceLayoutController;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.SearchableSelect;

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

    /**
     * Cube state captured at gizmo drag-start; on release we diff against the live cube to push a
     * {@link ModelerAction.CubeMementoAction}. Null when the drag targets a bone — exactly one of
     * {@code (gizmoDragTarget,
     * gizmoDragBoneTarget)} is non-null per drag.
     */
    private @Nullable ModelerCube gizmoDragTarget;

    private @Nullable ModelerAction.CubeMemento gizmoDragBefore;

    /**
     * Bone state captured at gizmo drag-start; on release we diff against the live bone to push a
     * {@link ModelerAction.BoneMementoAction}. Null when the drag targets a cube.
     */
    private @Nullable ModelerBone gizmoDragBoneTarget;

    private @Nullable ModelerAction.BoneMemento gizmoDragBoneBefore;

    private @Nullable ModelerGizmoMode gizmoDragMode;

    /**
     * Item-transform drag baseline. Set at drag-start when the drag targets the modeler item-config shim bone (vs. a
     * real scene bone) so the drag-end can push a {@link ModelerAction.ItemTransformMementoAction} keyed on the session
     * slot the drag was authored against. Captured at start because the user could change
     * {@code session.editingContext} / {@code session.mode} mid-drag in principle, and the memento must record the slot
     * the user was editing.
     */
    private @Nullable ModelerAction.ItemTransformMemento gizmoDragItemBefore;

    private @Nullable ResourceLocation gizmoDragItemId;

    private @Nullable BLibItemTransformMode gizmoDragItemMode;

    private @Nullable ItemDisplayContext gizmoDragItemContext;

    private boolean gizmoDragItemWallFixed;

    private boolean texturePaintActive;

    private @Nullable LoadedTexture texturePaintTexture;

    private @Nullable ModelerAction.TexturePixelsMemento texturePaintBefore;

    private int lastTexturePaintX;

    private int lastTexturePaintY;

    private @Nullable TexturePaintSurface lastTexturePaintSurface;

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
    public @Nullable Panel.TabIndicator tabIndicator() {
        return TextureTabIndicators.activeTextureDirty();
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
        var animationInteraction = isAnimationInteractionMode();
        if (cursorInsidePanel(mouseX, mouseY) && !gizmoDragActive) {
            if (isTexturePaintToolActive()) {
                var target = texturePaintTargetAt(mouseX, mouseY);
                scene.hoveredBone = null;
                scene.hoveredCube = null;
                scene.hoveredFace = null;
                scene.hoveredTexturePixel = target == null
                    ? null
                    : new ModelerScene.TexturePixelHover(
                        target.owner(),
                        target.cube(),
                        target.face(),
                        target.pixel().x(),
                        target.pixel().y()
                    );
                ModelerGizmoState.setHover(null);
            } else {
                var hit = pickCubeAt(mouseX, mouseY);
                if (hit != null && animationInteraction) {
                    scene.hoveredBone = hit.owner();
                    scene.hoveredCube = null;
                    scene.hoveredFace = null;
                } else if (hit != null) {
                    scene.hoveredBone = null;
                    scene.hoveredCube = hit.cube();
                    scene.hoveredFace = new Selection.FaceSelection(hit.owner(), hit.cube(), hit.face());
                } else {
                    scene.hoveredBone = null;
                    scene.hoveredCube = null;
                    scene.hoveredFace = null;
                }
                scene.hoveredTexturePixel = null;
                ModelerGizmoInput.updateHover(mouseX - panelX, mouseY - panelY, panelWidth, panelHeight);
            }
        } else {
            scene.hoveredBone = null;
            scene.hoveredCube = null;
            scene.hoveredFace = null;
            scene.hoveredTexturePixel = null;
            ModelerGizmoState.setHover(null);
        }

        renderer.render(graphics, x, y, width, height);
        // Panel-local menu bar lives along the top edge of the viewport — modeler-only file/edit/etc. menus that
        // would clutter the global menu bar if they lived there. Toolbar is shifted down by the menu-bar height so
        // the two strips stack instead of overlapping.
        ModelerMenuBar.render(graphics, x, y, width, mouseX, mouseY);
        int toolbarY = y + ModelerMenuBar.HEIGHT;
        ModelerViewportToolbar.render(graphics, x, toolbarY);
        // Navigation axis gizmo at the bottom-right — three labeled colored balls that follow the camera so the user
        // can read world orientation at a glance, and click an axis to snap the view orthogonally.
        ModelerAxisGizmo.render(graphics, x, y, width, height, scene.camera);

        // Toolbar / axis-gizmo tooltips. Refresh after the overlays render so the hit-test is against the rects just
        // drawn this frame (panel resize / layout changes are picked up on the same frame). Collision tooltips are
        // lower priority than viewport chrome. Suppressed during gizmo drag — the user is busy manipulating, not
        // exploring controls.
        hoveredTooltip = null;
        if (!gizmoDragActive) {
            var modeHover = ModelerViewportToolbar.hitTestMode(mouseX, mouseY, x, toolbarY);
            if (modeHover != null) {
                hoveredTooltip = ModelerViewportToolbar.tooltipForMode(modeHover);
            } else if (ModelerViewportToolbar.hitTestFrame(mouseX, mouseY, x, toolbarY)) {
                hoveredTooltip = ModelerViewportToolbar.tooltipForFrame();
            } else {
                var axisHover = ModelerAxisGizmo.hitTest(mouseX, mouseY, x, y, width, height, scene.camera);
                if (axisHover != null) {
                    hoveredTooltip = ModelerAxisGizmo.tooltipFor(axisHover);
                }
            }
            if (hoveredTooltip == null && animationInteraction) {
                hoveredTooltip = collisionTooltip(scene.hoveredBone);
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
        if (isTexturePaintToolActive()) {
            return beginTexturePaint(mouseX, mouseY);
        }

        // Build a world-space ray from the camera through the cursor, hand it to ModelerPicker, and update the
        // selection. A miss clears the selection (Blockbench convention — click empty space to deselect).
        var hit = pickCubeAt(mouseX, mouseY);
        var scene = ModelerScene.get();
        if (isAnimationInteractionMode()) {
            if (hit == null) {
                scene.selection = null;
                AnimationEditorState.get().selectBone(null);
            } else {
                scene.selection = new Selection.BoneSelection(hit.owner());
                AnimationEditorState.get().selectBone(hit.owner().name);
            }
            return true;
        }
        if (hit == null) {
            scene.selection = null;
        } else {
            var uv = hit.cube().faceUv(hit.face());
            if (uv != null) {
                if (selectTextureForFace(scene, uv)) {
                    scene.selection = new Selection.FaceSelection(hit.owner(), hit.cube(), hit.face());
                } else {
                    scene.selection = new Selection.CubeSelection(hit.owner(), hit.cube());
                }
            } else {
                scene.selection = new Selection.CubeSelection(hit.owner(), hit.cube());
            }
        }
        return true;
    }

    /** Cursor rect check shared by hover + click handling. */
    private boolean cursorInsidePanel(double mouseX, double mouseY) {
        if (panelWidth <= 0 || panelHeight <= 0) {
            return false;
        }
        return mouseX >= panelX && mouseX < panelX + panelWidth && mouseY >= panelY && mouseY < panelY + panelHeight;
    }

    private static boolean isAnimationInteractionMode() {
        return WorkspaceLayoutController.activeLayoutHasAnimationPanel();
    }

    private static @Nullable Component collisionTooltip(@Nullable ModelerBone hoveredBone) {
        if (hoveredBone == null) {
            return null;
        }
        var partners = AnimationCollisionState.get().currentCollisionPartners(hoveredBone);
        if (partners.isEmpty()) {
            return null;
        }
        return Component.literal("Bone: " + hoveredBone.name + "\nColliding with " + String.join(", ", partners));
    }

    /**
     * Ray-pick the cube under the cursor. Returns the {@link ModelerPicker.Hit} or null on a miss. Used by both the
     * hover refresh and the click handler so the two stay aligned with the renderer's transform stack.
     */
    private @Nullable ModelerPicker.Hit pickCubeAt(double mouseX, double mouseY) {
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

    private static boolean selectTextureForFace(ModelerScene scene, ModelerCube.FaceUv uv) {
        if (ModelerTextureUsage.usesTexture(scene.activeTexture, uv)) {
            return true;
        }
        for (var texture : scene.textures) {
            if (ModelerTextureUsage.usesTexture(texture, uv)) {
                scene.activeTexture = texture;
                return true;
            }
        }
        return false;
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
            // Navigation axis gizmo (bottom-right) — click on an axis ball snaps the camera to look down that axis.
            // Checked before gizmo-handle picking so the bottom-right overlay always wins clicks against any handle
            // that happens to project to the same area.
            var axisHit = ModelerAxisGizmo.hitTest(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight, ModelerScene.get().camera);
            if (axisHit != null) {
                ModelerAxisGizmo.snapCamera(ModelerScene.get().camera, axisHit);
                return true;
            }
            if (isTexturePaintToolActive()) {
                return beginTexturePaint(mouseX, mouseY);
            }

            // Gizmo handle drag — pickHandle in panel-relative coords and threshold against the captured snapshot.
            var panelRelX = mouseX - panelX;
            var panelRelY = mouseY - panelY;
            if (panelWidth > 0 && panelHeight > 0 && ModelerGizmoInput.tryStartDrag(panelRelX, panelRelY, panelWidth, panelHeight)) {
                gizmoDragActive = true;
                // Snapshot the target's full mutable state at drag-start so on release we can push a memento action
                // that restores it on undo. The drag mutates fields in place; capturing every editable field keeps the
                // memento type uniform with inspector edits.
                var drag = ModelerGizmoState.drag();
                if (drag != null) {
                    var snapshot = drag.startSnapshot();
                    if (snapshot.isCube()) {
                        var startCube = snapshot.cube();
                        gizmoDragTarget = startCube;
                        gizmoDragBefore = ModelerAction.CubeMemento.of(startCube);
                    } else if (snapshot.isBone()) {
                        var startBone = snapshot.bone();
                        // Distinguish a real bone-selection drag from an item-transform shim drag — when the bone is
                        // the active session's shim, capture the override slot identity (mode/context/wallFixed) and
                        // an ItemTransformMemento read straight from BLibItemTransformOverrides at drag-start. The
                        // sync in ModelerItemPreviewRenderer keeps shim.{position,rotation,scale,pivot} in lockstep
                        // with the override on entry, so the shim's drag-start fields ARE the before-transform.
                        var session = ModelerScene.get().itemSession;
                        if (session != null && startBone == session.gizmoShimBone) {
                            // The inspector's editingContext drives both the rendered preview and the gizmo target,
                            // so the undo memento records that slot's transform at drag-start.
                            var activeContext = session.editingContext;
                            var wall = activeContext == ItemDisplayContext.FIXED && session.wallFixedActive;
                            var current = wall
                                ? BLibItemTransformOverrides.getEffectiveWallFixed(session.itemId, session.mode)
                                : BLibItemTransformOverrides
                                    .getEffective(session.itemId, session.mode, activeContext);
                            gizmoDragItemBefore = ModelerAction.ItemTransformMemento.of(current);
                            gizmoDragItemId = session.itemId;
                            gizmoDragItemMode = session.mode;
                            gizmoDragItemContext = activeContext;
                            gizmoDragItemWallFixed = wall;
                        } else {
                            gizmoDragBoneTarget = startBone;
                            gizmoDragBoneBefore = ModelerAction.BoneMemento.of(startBone);
                        }
                    }
                    gizmoDragMode = drag.mode();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && texturePaintActive) {
            continueTexturePaint(mouseX, mouseY);
            return true;
        }
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
        if (button == 0 && texturePaintActive) {
            finishTexturePaint();
            return true;
        }
        if (button == 0 && gizmoDragActive) {
            ModelerGizmoInput.endDrag();
            gizmoDragActive = false;
            // Diff before-snapshot against the post-drag target. If anything changed, the gesture's a real edit and
            // belongs on the undo stack; a click that didn't move the cursor far enough to register a delta is a
            // no-op and we skip the push to keep the action stack clean. Branches on which target captured at
            // drag-start — exactly one of {cube, bone} is non-null per drag.
            var cubeBefore = gizmoDragBefore;
            var cubeTarget = gizmoDragTarget;
            var boneBefore = gizmoDragBoneBefore;
            var boneTarget = gizmoDragBoneTarget;
            var itemBefore = gizmoDragItemBefore;
            var itemId = gizmoDragItemId;
            var itemMode = gizmoDragItemMode;
            var itemContext = gizmoDragItemContext;
            var itemWallFixed = gizmoDragItemWallFixed;
            var mode = gizmoDragMode;
            gizmoDragBefore = null;
            gizmoDragTarget = null;
            gizmoDragBoneBefore = null;
            gizmoDragBoneTarget = null;
            gizmoDragItemBefore = null;
            gizmoDragItemId = null;
            gizmoDragItemMode = null;
            gizmoDragItemContext = null;
            gizmoDragItemWallFixed = false;
            gizmoDragMode = null;
            if (mode != null && cubeBefore != null && cubeTarget != null) {
                var after = ModelerAction.CubeMemento.of(cubeTarget);
                if (after.differsFrom(cubeBefore)) {
                    var typeId = switch (mode) {
                        case TRANSLATE -> "cube_translate";
                        case ROTATE -> "cube_rotate";
                        case RESIZE -> "cube_resize";
                        case PIVOT -> "cube_pivot";
                        default -> "cube_edit";
                    };
                    var description = switch (mode) {
                        case TRANSLATE -> "Translate cube " + cubeTarget.name;
                        case ROTATE -> "Rotate cube " + cubeTarget.name;
                        case RESIZE -> "Resize cube " + cubeTarget.name;
                        case PIVOT -> "Move pivot of cube " + cubeTarget.name;
                        default -> "Edit cube " + cubeTarget.name;
                    };
                    ModelerActionHistory.push(
                        new ModelerAction.CubeMementoAction(typeId, description, System.currentTimeMillis(), cubeTarget, cubeBefore, after)
                    );
                }
            } else if (mode != null && itemBefore != null && itemId != null && itemMode != null && itemContext != null) {
                // Item-transform drag — read the current effective transform back from the override (the gizmo sync
                // already wrote shim → override each frame during the drag), build the after-memento, and push
                // unless the drag was a no-op.
                var current = itemWallFixed
                    ? BLibItemTransformOverrides.getEffectiveWallFixed(itemId, itemMode)
                    : BLibItemTransformOverrides.getEffective(itemId, itemMode, itemContext);
                var after = ModelerAction.ItemTransformMemento.of(current);
                if (after.differsFrom(itemBefore)) {
                    var typeId = switch (mode) {
                        case TRANSLATE -> "item_transform_translate";
                        case ROTATE -> "item_transform_rotate";
                        case SCALE -> "item_transform_scale";
                        case PIVOT -> "item_transform_pivot";
                        default -> "item_transform_edit";
                    };
                    var description = switch (mode) {
                        case TRANSLATE -> "Translate " + itemId + " (" + itemContext.name().toLowerCase(Locale.ROOT) + ")";
                        case ROTATE -> "Rotate " + itemId + " (" + itemContext.name().toLowerCase(Locale.ROOT) + ")";
                        case SCALE -> "Scale " + itemId + " (" + itemContext.name().toLowerCase(Locale.ROOT) + ")";
                        case PIVOT -> "Move pivot of " + itemId + " (" + itemContext.name().toLowerCase(Locale.ROOT) + ")";
                        default -> "Edit " + itemId;
                    };
                    ModelerActionHistory.push(
                        new ModelerAction.ItemTransformMementoAction(
                            typeId,
                            description,
                            System.currentTimeMillis(),
                            itemId,
                            itemMode,
                            itemContext,
                            itemWallFixed,
                            itemBefore,
                            after
                        )
                    );
                }
            } else if (mode != null && boneBefore != null && boneTarget != null) {
                var after = ModelerAction.BoneMemento.of(boneTarget);
                if (after.differsFrom(boneBefore)) {
                    var typeId = switch (mode) {
                        case TRANSLATE -> "bone_translate";
                        case ROTATE -> "bone_rotate";
                        case PIVOT -> "bone_pivot";
                        case SCALE -> "bone_scale";
                        default -> "bone_edit";
                    };
                    var description = switch (mode) {
                        case TRANSLATE -> "Translate bone " + boneTarget.name;
                        case ROTATE -> "Rotate bone " + boneTarget.name;
                        case PIVOT -> "Move pivot of bone " + boneTarget.name;
                        case SCALE -> "Scale bone " + boneTarget.name;
                        default -> "Edit bone " + boneTarget.name;
                    };
                    ModelerActionHistory.push(
                        new ModelerAction.BoneMementoAction(typeId, description, System.currentTimeMillis(), boneTarget, boneBefore, after)
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

    private boolean beginTexturePaint(double mouseX, double mouseY) {
        var tool = TextureEditorState.tool();
        var target = texturePaintTargetAt(mouseX, mouseY);
        if (target == null) {
            return true;
        }

        var scene = ModelerScene.get();
        scene.activeTexture = target.texture();

        if (tool == TextureTool.PENCIL) {
            texturePaintActive = true;
            texturePaintTexture = target.texture();
            texturePaintBefore = ModelerAction.TexturePixelsMemento.of(target.pixels());
            lastTexturePaintX = target.pixel().x();
            lastTexturePaintY = target.pixel().y();
            lastTexturePaintSurface = target.surface();
            TexturePaintOps.paintLine(
                target.texture(),
                target.pixels(),
                target.pixel().x(),
                target.pixel().y(),
                target.pixel().x(),
                target.pixel().y(),
                false
            );
            return true;
        }
        if (tool == TextureTool.BUCKET) {
            var before = ModelerAction.TexturePixelsMemento.of(target.pixels());
            if (TexturePaintOps.bucketFill(target.texture(), target.pixels(), target.pixel().x(), target.pixel().y(), false)) {
                var after = ModelerAction.TexturePixelsMemento.of(target.pixels());
                TexturePaintOps.pushTexturePixelsAction("texture_model_bucket", "Model Bucket Fill", target.texture(), before, after);
            }
            return true;
        }
        return false;
    }

    private void continueTexturePaint(double mouseX, double mouseY) {
        var target = texturePaintTargetAt(mouseX, mouseY);
        var texture = texturePaintTexture;
        if (target == null || texture == null || target.texture() != texture) {
            return;
        }
        var nextX = target.pixel().x();
        var nextY = target.pixel().y();
        if (target.surface().equals(lastTexturePaintSurface)) {
            TexturePaintOps.paintLine(texture, target.pixels(), lastTexturePaintX, lastTexturePaintY, nextX, nextY, false);
        } else {
            TexturePaintOps.paintLine(texture, target.pixels(), nextX, nextY, nextX, nextY, false);
        }
        lastTexturePaintX = nextX;
        lastTexturePaintY = nextY;
        lastTexturePaintSurface = target.surface();
    }

    private void finishTexturePaint() {
        var texture = texturePaintTexture;
        var before = texturePaintBefore;
        texturePaintActive = false;
        texturePaintTexture = null;
        texturePaintBefore = null;
        lastTexturePaintSurface = null;
        if (texture == null || before == null) {
            return;
        }
        var pixels = texture.texture().getPixels();
        if (pixels == null) {
            return;
        }
        var after = ModelerAction.TexturePixelsMemento.of(pixels);
        TexturePaintOps.pushTexturePixelsAction("texture_model_pencil", "Model Pencil Stroke", texture, before, after);
    }

    private @Nullable TexturePaintTarget texturePaintTargetAt(double mouseX, double mouseY) {
        var hit = pickCubeAt(mouseX, mouseY);
        if (hit == null) {
            return null;
        }
        var scene = ModelerScene.get();
        var texture = ModelerFaceTextureMapping.textureForFace(scene, hit.cube(), hit.face());
        var pixels = texture == null ? null : texture.texture().getPixels();
        if (texture == null || pixels == null) {
            return null;
        }
        var uvSheetWidth = scene.textureWidth > 0.0 ? scene.textureWidth : pixels.getWidth();
        var uvSheetHeight = scene.textureHeight > 0.0 ? scene.textureHeight : pixels.getHeight();
        var pixel = ModelerFaceTextureMapping.pixelAt(
            hit.cube(),
            hit.face(),
            hit.localPoint(),
            uvSheetWidth,
            uvSheetHeight,
            pixels.getWidth(),
            pixels.getHeight()
        );
        if (pixel == null) {
            return null;
        }
        return new TexturePaintTarget(
            texture,
            pixels,
            hit.owner(),
            hit.cube(),
            hit.face(),
            pixel,
            new TexturePaintSurface(hit.owner(), hit.cube(), hit.face())
        );
    }

    private static boolean isTexturePaintToolActive() {
        var tool = TextureEditorState.tool();
        return tool == TextureTool.PENCIL || tool == TextureTool.BUCKET;
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

    private static void applyPan(ModelerCamera camera, double rawDx, double rawDy) {
        // Drag-the-scene convention in both axes: cursor and scene move together. Formula matches
        // EngineNavigation.applyPanDelta exactly — focus += right·dx + up·dy. The subtle point is that
        // EngineCameraBasis.screenRight is named from a screen-space-mapping POV (the direction the camera shifts
        // when the cursor moves +x), which under MC's left-handed yaw is actually opposite the camera's world-space
        // right axis. Negating dx here would un-do that and give push-the-camera; the plus signs give drag-the-scene.
        var right = EngineCameraBasis.screenRight(camera.yaw);
        var up = EngineCameraBasis.screenUp(camera.yaw, camera.pitch);
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

    private record TexturePaintTarget(
        LoadedTexture texture,
        NativeImage pixels,
        ModelerBone owner,
        ModelerCube cube,
        ModelerCube.Face face,
        ModelerFaceTextureMapping.Pixel pixel,
        TexturePaintSurface surface
    ) {}

    private record TexturePaintSurface(
        ModelerBone owner,
        ModelerCube cube,
        ModelerCube.Face face
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
                var items = new ArrayList<DropdownMenu.Item>();
                items.add(new DropdownMenu.Item("New", () -> {}, buildNewSubmenu()));
                items.add(new DropdownMenu.Item("Recent", () -> {}, buildRecentSubmenu()));
                items.add(new DropdownMenu.Item("Open", () -> {}, buildOpenSubmenu()));
                yield new DropdownMenu(anchorX, anchorY, items);
            }
            case ModelerMenuBar.CHIP_TRANSFORM -> {
                var items = new ArrayList<DropdownMenu.Item>();
                items.add(new DropdownMenu.Item("Rotate", () -> {}, buildRotateSubmenu()));
                items.add(new DropdownMenu.Item("Flip", () -> {}, buildFlipSubmenu()));
                items.add(new DropdownMenu.Item("Center", () -> {}, buildCenterSubmenu()));
                items.add(new DropdownMenu.Item("Properties", () -> {}, buildPropertiesSubmenu()));
                yield new DropdownMenu(anchorX, anchorY, items);
            }
            default -> null;
        };
    }

    /**
     * "Rotate" submenu — two entries per axis (+90 and -90). The hand-written labels include the degree symbol so the
     * dropdown reads as a precise gesture rather than an ambiguous direction toggle.
     */
    private static List<DropdownMenu.Item> buildRotateSubmenu() {
        var canRotate = canUseRotateTransform();
        var disabledTooltip = Component.literal("Java block models do not store group rotation. Select elements to rotate them.");
        return List
            .of(
                new DropdownMenu.Item(
                    "+90° around X",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.X, 90),
                    canRotate,
                    disabledTooltip
                ),
                new DropdownMenu.Item(
                    "-90° around X",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.X, -90),
                    canRotate,
                    disabledTooltip
                ),
                new DropdownMenu.Item(
                    "+90° around Y",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.Y, 90),
                    canRotate,
                    disabledTooltip
                ),
                new DropdownMenu.Item(
                    "-90° around Y",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.Y, -90),
                    canRotate,
                    disabledTooltip
                ),
                new DropdownMenu.Item(
                    "+90° around Z",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.Z, 90),
                    canRotate,
                    disabledTooltip
                ),
                new DropdownMenu.Item(
                    "-90° around Z",
                    () -> ModelerTransformOps.rotate(ModelerTransformOps.Axis.Z, -90),
                    canRotate,
                    disabledTooltip
                )
            );
    }

    private static boolean canUseRotateTransform() {
        var scene = ModelerScene.get();
        return !scene.isJavaBlockModel() || hasCubeSelection();
    }

    /** "Flip" submenu — one entry per axis. */
    private static List<DropdownMenu.Item> buildFlipSubmenu() {
        return List
            .of(
                new DropdownMenu.Item(
                    "Flip across X",
                    () -> ModelerTransformOps.flip(ModelerTransformOps.Axis.X)
                ),
                new DropdownMenu.Item(
                    "Flip across Y",
                    () -> ModelerTransformOps.flip(ModelerTransformOps.Axis.Y)
                ),
                new DropdownMenu.Item(
                    "Flip across Z",
                    () -> ModelerTransformOps.flip(ModelerTransformOps.Axis.Z)
                )
            );
    }

    /**
     * "Center" submenu — per-axis entries plus the lateral combo (X + Z). The lateral option exists because entity
     * authors often want a footprint centered on origin while keeping the model's vertical placement (feet on the
     * ground, head reaching upward) — neither a per-axis option nor a full-3D center captures that ergonomically.
     */
    private static List<DropdownMenu.Item> buildCenterSubmenu() {
        return List
            .of(
                new DropdownMenu.Item(
                    "Center on X",
                    () -> ModelerTransformOps.center(ModelerTransformOps.Axis.X)
                ),
                new DropdownMenu.Item(
                    "Center on Y",
                    () -> ModelerTransformOps.center(ModelerTransformOps.Axis.Y)
                ),
                new DropdownMenu.Item(
                    "Center on Z",
                    () -> ModelerTransformOps.center(ModelerTransformOps.Axis.Z)
                ),
                new DropdownMenu.Item("Center laterally (X + Z)", ModelerTransformOps::centerLateral)
            );
    }

    private static List<DropdownMenu.Item> buildPropertiesSubmenu() {
        return List
            .of(
                new DropdownMenu.Item(
                    "Mirror UV",
                    ModelerTransformOps::toggleSelectedMirrorUv,
                    hasCubeSelection(),
                    Component.literal("Select one or more cubes to mirror their box UVs.")
                )
            );
    }

    private static boolean hasCubeSelection() {
        var selection = ModelerScene.get().selection;
        return selection instanceof Selection.CubeSelection
            || selection instanceof Selection.FaceSelection
            || selection instanceof Selection.MultiCubeSelection;
    }

    /**
     * "New" submenu items. Only Entity for now — the modeler's data model implicitly assumes entity-shaped output;
     * Block / Item types will land alongside their authoring affordances later.
     */
    private static List<DropdownMenu.Item> buildNewSubmenu() {
        return List.of(new DropdownMenu.Item("Entity", ModelerViewportPanel::newEntityModel));
    }

    /**
     * "Open" submenu — split between disk-backed geo models ("From File…") and in-game registered item configs ("Item
     * Config…"). The two flows are different enough that putting them at the same level avoids the asymmetry of having
     * the inspector own the item picker while disk loads live in the menu bar.
     */
    private static List<DropdownMenu.Item> buildOpenSubmenu() {
        return List
            .of(
                new DropdownMenu.Item("From File…", ModelerViewportPanel::openGeoModelFromFile),
                new DropdownMenu.Item("Block Model…", ModelerViewportPanel::openBlockModelPicker),
                new DropdownMenu.Item("Item Config…", ModelerViewportPanel::openItemConfigPicker)
            );
    }

    /**
     * "Recent" submenu — newest-first list of modeler opens from {@link ModelerRecentFiles}, scoped to the active
     * project. Entries carry their source kind so disk files, block models, and item configs can sit together without
     * ambiguous labels.
     */
    private static List<DropdownMenu.Item> buildRecentSubmenu() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return List.of(new DropdownMenu.Item("(no project active)", () -> {}));
        }
        var recents = ModelerRecentFiles.list(project);
        if (recents.isEmpty()) {
            return List.of(new DropdownMenu.Item("(no recent files)", () -> {}));
        }
        var items = new ArrayList<DropdownMenu.Item>(recents.size());
        for (var entry : recents) {
            items.add(new DropdownMenu.Item(recentLabel(entry), () -> openRecent(entry)));
        }
        return items;
    }

    private static String recentLabel(ModelerRecentFiles.Entry entry) {
        var name = entry.source() == ModelerRecentFiles.Source.EXTERNAL
            ? externalRecentName(entry.target())
            : entry.target();
        return name + " [" + entry.source().label() + "]";
    }

    private static String externalRecentName(String target) {
        try {
            var fileName = Path.of(target).getFileName();
            return fileName != null ? fileName.toString() : target;
        } catch (InvalidPathException ignored) {
            return target;
        }
    }

    /** "New → Entity": replace the modeler scene with a fresh default-cube seed. */
    private static void newEntityModel() {
        ModelerScene.get().resetToEntity();
    }

    /**
     * FILE → Open → "From File…" — OS-native open-file dialog via {@link ModelerFilePicker#pickGeoModel}, then hand the
     * path to {@link ModelerSceneLoader#loadFromFile} which replaces the modeler's active scene. On a successful load
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
                ModelerRecentFiles.recordExternalOpen(project, picked.toString());
            }
        }
    }

    /**
     * FILE → Open → "Item Config…" — pops a searchable list of every tunable item ID. Selecting one swaps the modeler
     * into item-config mode via {@link ModelerScene#attachItemSession}: the entity scene is cleared, the inspector
     * shows the Item Config section, and the viewport renders the item through vanilla's {@code ItemRenderer} at the
     * session's default display context.
     * <p>
     * Force-instantiates every registered item renderer first because the renderer constructors are what call
     * {@code BLibTunableItemTransforms.wrap} and populate {@link BLibItemTransformOverrides#tunableItemIds()}. Without
     * this, items the user has never seen in-game won't appear in the picker. {@link AzItemRendererRegistry} caches via
     * computeIfAbsent so the walk is cheap on subsequent invocations.
     */
    private static void openItemConfigPicker() {
        for (var item : AzItemRendererRegistry.registeredItems()) {
            AzItemRendererRegistry.getOrNull(item);
        }
        var ids = new ArrayList<ResourceLocation>(BLibItemTransformOverrides.tunableItemIds());
        ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
        var items = new ArrayList<SearchableSelect.Item<ResourceLocation>>(ids.size());
        for (var id : ids) {
            items.add(new SearchableSelect.Item<>(id, id.toString()));
        }
        // Anchor the popup just below the File chip — same position the dropdown lived in, so the visual flow from
        // menu click to picker feels continuous. Popup clamps itself to the viewport when this overflows.
        var chipRect = ModelerMenuBar.chipRect(ModelerMenuBar.CHIP_FILE);
        int anchorX = chipRect != null ? chipRect.x() : 8;
        int anchorY = chipRect != null ? chipRect.y() : 8;
        int anchorHeight = chipRect != null ? chipRect.height() : 12;
        SearchableSelect
            .openPopupAt(
                anchorX,
                anchorY,
                280,
                anchorHeight,
                items,
                ResourceLocation::toString,
                null,
                ModelerViewportPanel::openItemConfig
            );
    }

    private static void openItemConfig(ResourceLocation itemId) {
        ModelerScene.get().attachItemSession(itemId);
        var project = ProjectSession.activeProjectName();
        if (!project.isEmpty()) {
            ModelerRecentFiles.recordItemConfigOpen(project, itemId.toString());
        }
    }

    /**
     * FILE → Open → "Block Model…" — list every registered block, then import the selected blockstate/model JSON into
     * the modeler as cubes plus resource-backed block textures.
     */
    private static void openBlockModelPicker() {
        var ids = new ArrayList<ResourceLocation>();
        for (var block : BuiltInRegistries.BLOCK) {
            ids.add(BuiltInRegistries.BLOCK.getKey(block));
        }
        ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));

        var items = new ArrayList<SearchableSelect.Item<ResourceLocation>>(ids.size());
        for (var id : ids) {
            items.add(new SearchableSelect.Item<>(id, id.toString()));
        }

        var chipRect = ModelerMenuBar.chipRect(ModelerMenuBar.CHIP_FILE);
        int anchorX = chipRect != null ? chipRect.x() : 8;
        int anchorY = chipRect != null ? chipRect.y() : 8;
        int anchorHeight = chipRect != null ? chipRect.height() : 12;
        SearchableSelect
            .openPopupAt(
                anchorX,
                anchorY,
                280,
                anchorHeight,
                items,
                ResourceLocation::toString,
                null,
                ModelerViewportPanel::openBlockModel
            );
    }

    private static void openBlockModel(ResourceLocation blockId) {
        if (!ModelerBlockModelLoader.load(blockId)) {
            return;
        }
        var project = ProjectSession.activeProjectName();
        if (!project.isEmpty()) {
            ModelerRecentFiles.recordBlockOpen(project, blockId.toString());
        }
    }

    /**
     * Parent directory of the project's most-recent imported model, or null when there's no recent history (no project,
     * no recents yet, or the recorded path has no resolvable parent). Falling back to null lets the picker use the OS
     * default location for a fresh-start feel rather than guessing at game-dir or similar.
     */
    private static @Nullable Path inferInitialPickerDir() {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        for (var recent : ModelerRecentFiles.list(project)) {
            if (recent.source() == ModelerRecentFiles.Source.EXTERNAL) {
                try {
                    return Path.of(recent.target()).getParent();
                } catch (InvalidPathException ignored) {
                    // Skip malformed legacy entries and keep looking for a usable external path.
                }
            }
        }
        return null;
    }

    /**
     * "Recent → &lt;model&gt;" — same load paths as the Open submenu but skips the picker. Re-records the entry so
     * opening from Recent promotes it back to the top, which is the standard convention.
     */
    private static void openRecent(ModelerRecentFiles.Entry entry) {
        switch (entry.source()) {
            case EXTERNAL -> {
                try {
                    var path = Path.of(entry.target());
                    if (ModelerSceneLoader.loadFromFile(path)) {
                        var project = ProjectSession.activeProjectName();
                        if (!project.isEmpty()) {
                            ModelerRecentFiles.recordExternalOpen(project, entry.target());
                        }
                    }
                } catch (InvalidPathException ignored) {
                    // Leave malformed legacy entries visible but inert.
                }
            }
            case BLOCK -> {
                var id = ResourceLocation.tryParse(entry.target());
                if (id != null) {
                    openBlockModel(id);
                }
            }
            case ITEM_CONFIG -> {
                var id = ResourceLocation.tryParse(entry.target());
                if (id != null) {
                    openItemConfig(id);
                }
            }
        }
    }
}
