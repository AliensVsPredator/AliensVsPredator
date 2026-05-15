package com.blib.engine.ui.panel.uvmap;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.Selection.FaceSelection;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.ModelerTextureUsage;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.TextInput;

/**
 * Top-left modeler panel: a pannable 2D plane that draws each cube's box-UV footprint as a cross-unwrap rectangle.
 * <p>
 * <strong>Layout</strong>: header strip (texture dimensions readout at top right) → UV map area (texture canvas
 * horizontally centered + top-aligned within its area) → footer strip (U and V text inputs for the primary selected
 * cube). The UV map area is the only zone that responds to pan/zoom/marquee input.
 * <p>
 * <strong>Selection</strong>: LMB on a cube selects it; LMB-drag on a selected cube moves the entire selection as a
 * group. LMB-drag on empty space draws a marquee rectangle — on release, every cube whose UV bounding rect intersects
 * the marquee joins the selection. Multi-selection lives panel-local in a {@code LinkedHashSet}; the engine's
 * {@code ModelerScene.selection} (single-cube) syncs to the primary (last-clicked) cube so the outliner and inspector
 * stay in lockstep.
 * <p>
 * <strong>Zoom</strong>: range is {@code [fit-to-area, 4 × fit-to-area]}, default is fit (zoomed out all the way).
 * Scroll-wheel zooms anchored at the cursor. Min and max recompute whenever the panel or texture size changes.
 * <p>
 * <strong>Rendering path</strong>: bypasses {@link GuiGraphics#fill} for the geometry. Cube faces/outlines, grid lines,
 * texture bg, panel bg, and the marquee rect all go into a single batched {@link BufferBuilder} per frame, drawn with
 * one {@code BufferUploader.drawWithShader} call. Each vertex pre-multiplies through the panel pose's 2D
 * scale+translate components (two FMAs/vertex instead of a 4×4 matrix multiply).
 */
@ApiStatus.Internal
public final class UvMapPanel implements Panel {

    private static final int BG_COLOR = 0xFF14141A;

    private static final int TEXTURE_BG_COLOR = 0xFF1F1F26;

    private static final int TEXTURE_BORDER_COLOR = 0xFF404048;

    private static final int CUBE_SELECTED_OUTLINE = 0x80E6C26B;

    private static final int CUBE_SELECTED_FACE = 0x20E6C26B;

    private static final int CUBE_HOVER_OUTLINE = 0xFFFFFFFF;

    private static final int IMPORTED_FACE_OUTLINE = 0x809A9AA4;

    private static final int MARQUEE_FILL_COLOR = 0x404F8FFF;

    private static final int MARQUEE_OUTLINE_COLOR = 0xFF4F8FFF;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int TEXT_MUTED_COLOR = 0xFF808088;

    /**
     * Upper bound on zoom in screen-pixels-per-UV-pixel. Past {@code 1.0} the texture is upscaled with nearest-neighbor
     * (no new detail), but individual texels become large enough to align cube UVs against pixel-precise. The min zoom
     * is the fit-to-area zoom, which is always ≤ this.
     */
    private static final double MAX_ABSOLUTE_ZOOM = 16.0;

    /** Pan-drag multiplier — 1px of cursor movement translates to PAN_SENSITIVITY px of view shift. */
    private static final double PAN_SENSITIVITY = 1.5;

    /** Pixels reserved at the top of the panel for the texture-dim readout. */
    private static final int HEADER_HEIGHT = 14;

    /** Pixels reserved at the bottom for the U/V input row. */
    private static final int FOOTER_HEIGHT = 22;

    private static final int FOOTER_PADDING_X = 4;

    private static final int INPUT_GAP = 8;

    /** Screen-pixel offsets from the "natural" texture anchor (horizontally centered, top-aligned). */
    private double panOffsetX;

    private double panOffsetY;

    /** Pixels-per-UV-unit. Clamped to {@code [minZoom, max(minZoom, MAX_ABSOLUTE_ZOOM)]} each frame. */
    private double zoom = 1.0;

    /**
     * Last-frame's fit (min) zoom. Tracked so panel resizes scale {@link #zoom} proportionally — without this,
     * shrinking the panel below the threshold where the fit zoom drops doesn't pull the current zoom down with it (the
     * clamp range moves but the existing zoom value stays inside it). Initialized to -1 so the first render is a no-op.
     */
    private double lastFitZoom = -1.0;

    private boolean viewInitialized;

    /** Full panel rect (header + UV area + footer), captured each render. */
    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** UV map subregion (panel minus header/footer), captured each render. */
    private int uvAreaX;

    private int uvAreaY;

    private int uvAreaW;

    /**
     * Visible UV map viewport height — the locked area in which the texture is rendered. Equals
     * {@code textureHeight × minZoom} (capped at {@link #uvAreaMaxH}), so this stays stable across zoom changes
     * (zooming in scales the texture inside the viewport rather than expanding the viewport itself). The footer sits
     * directly under this — its Y stays put regardless of zoom.
     */
    private int uvAreaH;

    /**
     * Panel-content height available for the UV viewport (panel height minus header and footer). Used by
     * {@link #computeMinZoom} as the upper bound on the fit zoom, and as the cap when computing the viewport
     * {@link #uvAreaH}. Unlike {@link #uvAreaH}, this does NOT shrink to the texture's natural size — it represents the
     * maximum room the viewport could grow into if the texture were large enough.
     */
    private int uvAreaMaxH;

    /**
     * Cached per-frame integer offset: screen X where UV {@code u=0} sits. Combines the natural-centering anchor with
     * {@link #panOffsetX}. All UV→screen conversions use {@code round(u*zoom) + offsetXInt}, so the entire view shifts
     * by whole pixels when pan crosses a half-pixel boundary — adjacent rects stay pixel-flush.
     */
    private int offsetXInt;

    private int offsetYInt;

    /** Visible UV-space bounds captured each render, used by cube/grid culling. */
    private double visibleMinU;

    private double visibleMaxU;

    private double visibleMinV;

    private double visibleMaxV;

    /** Imported per-face UV count visible for the active texture, displayed in the header hint. */
    private int perFaceCount;

    /**
     * Panel-local multi-selection. Insertion-ordered so the "last added" is well-defined (used as the primary). Cubes
     * are referenced by identity — when the scene tree is replaced (model load), {@link #syncFromSceneSelection} sees
     * the cleared {@code scene.selection} and drops these too.
     */
    private final Set<ModelerCube> selectedCubes = new LinkedHashSet<>();

    /** Owner bone per selected cube, captured at selection time. Needed when writing {@code scene.selection}. */
    private final Map<ModelerCube, ModelerBone> ownerByCube = new HashMap<>();

    /** Imported per-face UV selections, local to this panel so the rest of the modeler can keep cube-level context. */
    private final Set<FaceSelection> selectedFaces = new LinkedHashSet<>();

    /** Last-clicked (or last-added-via-marquee) cube. Drives the footer inputs and {@code scene.selection} sync. */
    private @Nullable ModelerCube primaryCube;

    private @Nullable ModelerBone primaryOwner;

    /** Last-clicked imported UV face. Drives per-face U/V input and drag behavior. */
    private @Nullable FaceSelection primaryFace;

    /** Cube under the cursor this frame, for hover outline. Refreshed on every render. */
    private @Nullable ModelerCube hoveredCube;

    /** Imported UV face under the cursor this frame, for hover outline. */
    private @Nullable FaceSelection hoveredFace;

    /** What the current LMB drag is doing. {@link DragState#IDLE} when no LMB-drag is in flight. */
    private DragState dragState = DragState.IDLE;

    /**
     * UV-space point at LMB-down. For cube drags, deltas are computed against this anchor so each selected cube moves
     * by the same delta regardless of where the user grabbed within the group.
     */
    private double dragGrabU;

    private double dragGrabV;

    /** Per-cube starting UV origin, snapshotted at drag start. Drag updates: {@code uv = start + (cursor - grab)}. */
    private final Map<ModelerCube, double[]> dragStartUVs = new HashMap<>();

    /** Per-face starting UV records, snapshotted at drag start. */
    private final Map<FaceSelection, ModelerCube.FaceUv> dragStartFaceUvs = new HashMap<>();

    /** Per-cube memento snapshot, snapshotted at drag start. Used at release to build the composite undo entry. */
    private final Map<ModelerCube, ModelerAction.CubeMemento> dragStartMementos = new HashMap<>();

    /** Marquee rectangle endpoints in UV space. Active when {@link #dragState} is {@link DragState#MARQUEE}. */
    private double marqueeStartU;

    private double marqueeStartV;

    private double marqueeEndU;

    private double marqueeEndV;

    /** Tracks the previous {@code scene.root} reference so we can wipe selection state when a model loads. */
    private @Nullable ModelerBone lastRoot;

    private final TextInput uInput = new TextInput("U", this::commitU);

    private final TextInput vInput = new TextInput("V", this::commitV);

    @Override
    public String title() {
        return "UV Map";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;

        var scene = ModelerScene.get();

        // Layout: header strip on top, then the UV viewport (size locked at the texture's fit-zoom footprint), then the
        // footer (inputs) directly underneath the viewport. uvAreaMaxH is the maximum room the viewport could occupy;
        // uvAreaH is the actual viewport height (texHeight × minZoom, capped). Setting uvAreaH provisionally here lets
        // the init path run before the zoom-dependent recompute below.
        this.uvAreaX = x;
        this.uvAreaY = y + HEADER_HEIGHT;
        this.uvAreaW = width;
        this.uvAreaMaxH = Math.max(0, height - HEADER_HEIGHT - FOOTER_HEIGHT);
        this.uvAreaH = uvAreaMaxH;

        // Reset selection + view state on model load — scene.root identity changes when applyModel runs.
        if (scene.root != lastRoot) {
            clearSelectionState();
            dragState = DragState.IDLE;
            lastRoot = scene.root;
            // New model = new texture dimensions, so the previous zoom/pan no longer corresponds to anything sensible.
            // Force re-init below so the new model starts fully zoomed out and centered.
            viewInitialized = false;
        }

        // Initialize view once we have a known area size. Guard on uvAreaMaxH (not uvAreaH) since uvAreaH gets locked
        // to the texture's footprint below — it could collapse to 0 for a zero-size texture even while the panel has
        // room. uvAreaMaxH reflects the panel allocation, which is what matters for "do we have a viewport yet".
        if (!viewInitialized && uvAreaW > 0 && uvAreaMaxH > 0) {
            zoom = computeMinZoom(scene);
            panOffsetX = 0;
            panOffsetY = 0;
            lastFitZoom = zoom;
            viewInitialized = true;
        }

        // When the fit zoom changes (panel resize or model load with different texture dimensions), scale the current
        // zoom + pan proportionally so the user's view tracks the panel. Without this, shrinking the panel leaves the
        // texture at its old absolute pixel size (it just overflows the smaller area) because the clamp range moves
        // but the current zoom is still inside it.
        var minZoom = computeMinZoom(scene);
        if (lastFitZoom > 0 && minZoom > 0 && minZoom != lastFitZoom) {
            var scale = minZoom / lastFitZoom;
            zoom *= scale;
            panOffsetX *= scale;
            panOffsetY *= scale;
        }
        lastFitZoom = minZoom;

        // Defensive clamp — the proportional scale above keeps zoom in range when fit changes, but the scroll-wheel
        // handler's anchor math (and any other zoom-mutating path) feeds through here too. Upper bound is the absolute
        // 1:1 ratio; raise it to minZoom when the area is smaller than texture-aspect would normally fit (degenerate
        // panels) so the range never collapses below min.
        var maxZoom = Math.max(minZoom, MAX_ABSOLUTE_ZOOM);
        zoom = clamp(zoom, minZoom, maxZoom);

        // Lock the viewport height to the texture's footprint AT THE FIT ZOOM (not the current zoom). This keeps the
        // visible UV map area a stable size as the user zooms in — the texture grows past the viewport edges and gets
        // scissor-clipped + becomes pannable, instead of the viewport stretching downward and pushing the footer.
        this.uvAreaH = Math.min((int) Math.round(scene.textureHeight * minZoom), uvAreaMaxH);

        // Clamp pan so the texture always fully covers the UV map area on each axis.
        // X axis is centered: symmetric ±(overshoot/2) — pan ranges left/right from the area center.
        // Y axis is TOP-ALIGNED (texture pinned to the top of the area at zoom-out; the inputs sit right below it).
        // Pan Y therefore ranges [uvAreaH - texH*zoom, 0]: panY=0 keeps the texture top at the area top, and panning to
        // negative pulls the texture upward so its overflowing bottom comes into view. When the texture fits in Y
        // (texH*zoom ≤ uvAreaH), the range collapses to {0} and the texture stays pinned to the top with panel bg
        // below it (the bottom of which is overdrawn by the footer).
        var halfOvershootX = Math.max(0, (scene.textureWidth * zoom - uvAreaW) / 2.0);
        var minPanY = Math.min(0.0, uvAreaH - scene.textureHeight * zoom);
        panOffsetX = clamp(panOffsetX, -halfOvershootX, halfOvershootX);
        panOffsetY = clamp(panOffsetY, minPanY, 0.0);

        // Natural anchor: X centered in the area (horizontal padding when texture is smaller than area), Y at the top
        // of the area (no vertical padding above — the texture floats up against the header strip).
        var naturalX = uvAreaX + (uvAreaW - scene.textureWidth * zoom) / 2.0;
        var naturalY = (double) uvAreaY;
        offsetXInt = (int) Math.round(naturalX + panOffsetX);
        offsetYInt = (int) Math.round(naturalY + panOffsetY);

        visibleMinU = (uvAreaX - offsetXInt) / zoom;
        visibleMaxU = (uvAreaX + uvAreaW - offsetXInt) / zoom;
        visibleMinV = (uvAreaY - offsetYInt) / zoom;
        visibleMaxV = (uvAreaY + uvAreaH - offsetYInt) / zoom;

        syncFromSceneSelection(scene);
        pruneFaceSelectionForTexture(scene.activeTexture);
        syncInputs();

        // Hover detection: UV-map hover wins while the cursor is over this panel; otherwise mirror the 3D viewport's
        // hovered face so moving across geometry highlights the matching imported UV island here.
        if (inUvArea(mouseX, mouseY)) {
            var hoverU = (mouseX - offsetXInt) / zoom;
            var hoverV = (mouseY - offsetYInt) / zoom;
            hoveredFace = pickFaceAt(scene.root, hoverU, hoverV, scene.activeTexture);
            hoveredCube = hoveredFace != null ? hoveredFace.cube() : pickHover(scene.root, hoverU, hoverV);
        } else {
            hoveredFace = viewportHoveredFaceForActiveTexture(scene);
            hoveredCube = hoveredFace != null ? hoveredFace.cube() : null;
        }

        renderGeometry(graphics, scene);
        renderHeader(graphics, scene);
        renderFooter(graphics, scene, mouseX, mouseY);
    }

    private void renderGeometry(GuiGraphics graphics, ModelerScene scene) {
        // All cube/grid/bg/marquee quads go into one batched buffer. Flush pending GuiGraphics output first so prior
        // panels' draws are committed before we apply scissor + start writing our buffer.
        graphics.flush();
        applyRawScissor(graphics, uvAreaX, uvAreaY, uvAreaW, uvAreaH);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        var pose = graphics.pose().last().pose();
        var m00 = pose.m00();
        var m11 = pose.m11();
        var m30 = pose.m30();
        var m31 = pose.m31();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // UV-area background.
        addQuad(buffer, m00, m11, m30, m31, uvAreaX, uvAreaY, uvAreaX + uvAreaW, uvAreaY + uvAreaH, BG_COLOR);

        // Texture bg + grid + outline.
        var texW = (int) scene.textureWidth;
        var texH = (int) scene.textureHeight;
        var tx0 = screenXi(0);
        var ty0 = screenYi(0);
        var tx1 = screenXi(texW);
        var ty1 = screenYi(texH);
        var activeTexture = scene.activeTexture;
        if (activeTexture == null) {
            addQuad(buffer, m00, m11, m30, m31, tx0, ty0, tx1, ty1, TEXTURE_BG_COLOR);
        } else {
            // Flush the BG quad through the position-color shader, then draw the texture overlay in a separate
            // single-quad position-tex-color pass. Restart the original buffer + shader afterwards so the grid /
            // outline / cubes / marquee passes continue layering on top of the texture.
            BufferUploader.drawWithShader(buffer.buildOrThrow());
            RenderSystem.setShaderTexture(0, activeTexture.textureId());
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            var texBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            var sx0 = m00 * tx0 + m30;
            var sx1 = m00 * tx1 + m30;
            var sy0 = m11 * ty0 + m31;
            var sy1 = m11 * ty1 + m31;
            texBuffer.addVertex(sx0, sy0, 0).setUv(0f, 0f).setColor(0xFFFFFFFF);
            texBuffer.addVertex(sx0, sy1, 0).setUv(0f, 1f).setColor(0xFFFFFFFF);
            texBuffer.addVertex(sx1, sy1, 0).setUv(1f, 1f).setColor(0xFFFFFFFF);
            texBuffer.addVertex(sx1, sy0, 0).setUv(1f, 0f).setColor(0xFFFFFFFF);
            BufferUploader.drawWithShader(texBuffer.buildOrThrow());
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        }
        addRectOutline(buffer, m00, m11, m30, m31, tx0, ty0, tx1, ty1, TEXTURE_BORDER_COLOR);

        // Cubes (with culling).
        perFaceCount = activeTexture == null ? 0 : ModelerTextureUsage.faceUsageCount(scene, activeTexture);
        addCubes(buffer, m00, m11, m30, m31, scene.root, activeTexture);

        // Marquee rectangle on top of everything.
        if (dragState == DragState.MARQUEE) {
            var u0 = Math.min(marqueeStartU, marqueeEndU);
            var v0 = Math.min(marqueeStartV, marqueeEndV);
            var u1 = Math.max(marqueeStartU, marqueeEndU);
            var v1 = Math.max(marqueeStartV, marqueeEndV);
            var x0 = screenXi(u0);
            var y0 = screenYi(v0);
            var x1 = screenXi(u1);
            var y1 = screenYi(v1);
            if (x1 > x0 && y1 > y0) {
                addQuad(buffer, m00, m11, m30, m31, x0, y0, x1, y1, MARQUEE_FILL_COLOR);
                addRectOutline(buffer, m00, m11, m30, m31, x0, y0, x1, y1, MARQUEE_OUTLINE_COLOR);
            }
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableScissor();
    }

    private void renderHeader(GuiGraphics graphics, ModelerScene scene) {
        var font = EngineFont.get();
        // Panel bg behind the header strip (the geometry buffer only painted the UV area's bg).
        graphics.fill(rectX, rectY, rectX + rectWidth, rectY + HEADER_HEIGHT, BG_COLOR);

        var dims = ((int) scene.textureWidth) + " × " + ((int) scene.textureHeight);
        var dimsWidth = font.width(dims);
        graphics.drawString(font, Component.literal(dims), rectX + rectWidth - dimsWidth - 4, rectY + 3, TEXT_COLOR, false);

        var label = selectedFaceLabel();
        if (label == null && perFaceCount > 0) {
            label = perFaceCount + (perFaceCount == 1 ? " face" : " faces");
        }
        if (label != null) {
            var color = primaryFace != null ? TEXT_COLOR : TEXT_MUTED_COLOR;
            UiText.drawClipped(graphics, font, label, rectX + 4, rectY + 3, Math.max(0, rectWidth - dimsWidth - 12), color);
        }
    }

    private void renderFooter(GuiGraphics graphics, ModelerScene scene, int mouseX, int mouseY) {
        var font = EngineFont.get();
        // Footer sits directly under the (locked) UV viewport. Because uvAreaH is set from minZoom — not the current
        // zoom — this position stays put when the user zooms in: the texture grows beyond the viewport and gets
        // scissor-clipped, but the viewport bottom (and therefore the footer) stays in place.
        var footerY = uvAreaY + uvAreaH;
        graphics.fill(rectX, footerY, rectX + rectWidth, rectY + rectHeight, BG_COLOR);

        var inputY = footerY + (FOOTER_HEIGHT - TextInput.HEIGHT) / 2;
        if (!uvInputsEnabled()) {
            if (primaryCube != null && primaryCube.hasPerFaceUv && primaryFace == null) {
                var textY = inputY + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
                graphics.drawString(font, Component.literal("Per-face UV"), rectX + FOOTER_PADDING_X, textY, TEXT_MUTED_COLOR, false);
            }
            return;
        }

        var labelWidth = font.width("U: ");
        var available = Math.max(0, rectWidth - 2 * FOOTER_PADDING_X - 2 * labelWidth - INPUT_GAP);
        var inputW = Math.max(24, available / 2);

        var labelXU = rectX + FOOTER_PADDING_X;
        var inputXU = labelXU + labelWidth;
        var labelXV = inputXU + inputW + INPUT_GAP;
        var inputXV = labelXV + labelWidth;

        // +2 compensates for MC font's descender padding so labels visually center against the input rect.
        var labelTextY = inputY + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal("U:"), labelXU, labelTextY, TEXT_COLOR, false);
        uInput.render(graphics, inputXU, inputY, inputW, mouseX, mouseY);
        graphics.drawString(font, Component.literal("V:"), labelXV, labelTextY, TEXT_COLOR, false);
        vInput.render(graphics, inputXV, inputY, inputW, mouseX, mouseY);
    }

    private @Nullable String selectedFaceLabel() {
        var face = primaryFace;
        var uv = primaryFaceUv();
        if (face == null || uv == null) {
            return null;
        }
        var rect = faceRect(uv);
        var size = Math.round(rect.u1() - rect.u0()) + "x" + Math.round(rect.v1() - rect.v0());
        var at = Math.round(rect.u0()) + "," + Math.round(rect.v0());
        var faceName = face.face().name().toLowerCase(Locale.ROOT);
        var prefix = selectedFaces.size() > 1 ? selectedFaces.size() + " faces - " : "";
        return prefix + face.cube().name + "." + faceName + "  " + size + " @ " + at;
    }

    private @Nullable FaceSelection viewportHoveredFaceForActiveTexture(ModelerScene scene) {
        var face = scene.hoveredFace;
        if (face == null) {
            return null;
        }
        var uv = face.cube().faceUv(face.face());
        return uv != null && ModelerTextureUsage.usesTexture(scene.activeTexture, uv) ? face : null;
    }

    private void addCubes(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        ModelerBone bone,
        @Nullable LoadedTexture activeTexture
    ) {
        for (var cube : bone.cubes) {
            if (cube.hasPerFaceUv) {
                addPerFaceCubeUvs(buffer, m00, m11, m30, m31, bone, cube, activeTexture);
                continue;
            }
            var w = cube.size.x;
            var h = cube.size.y;
            var d = cube.size.z;
            var u0 = cube.uvOriginU;
            var v0 = cube.uvOriginV;
            var u1 = u0 + 2 * d + 2 * w;
            var v1 = v0 + d + h;
            if (u1 < visibleMinU || u0 > visibleMaxU || v1 < visibleMinV || v0 > visibleMaxV) {
                continue;
            }
            var marqueePreview = dragState == DragState.MARQUEE && intersectsCurrentMarquee(u0, v0, u1, v1);
            addCubeCross(
                buffer,
                m00,
                m11,
                m30,
                m31,
                w,
                h,
                d,
                u0,
                v0,
                selectedCubes.contains(cube),
                cube == hoveredCube,
                marqueePreview
            );
        }
        for (var child : bone.children) {
            addCubes(buffer, m00, m11, m30, m31, child, activeTexture);
        }
    }

    private void addPerFaceCubeUvs(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        ModelerBone owner,
        ModelerCube cube,
        @Nullable LoadedTexture activeTexture
    ) {
        if (activeTexture == null || cube.faceUvs.isEmpty()) {
            return;
        }

        for (var entry : cube.faceUvs.entrySet()) {
            var face = new FaceSelection(owner, cube, entry.getKey());
            var uv = entry.getValue();
            if (!ModelerTextureUsage.usesTexture(activeTexture, uv)) {
                continue;
            }
            var rect = faceRect(uv);
            var u0 = rect.u0();
            var v0 = rect.v0();
            var u1 = rect.u1();
            var v1 = rect.v1();
            if (u1 < visibleMinU || u0 > visibleMaxU || v1 < visibleMinV || v0 > visibleMaxV) {
                continue;
            }

            var marqueePreview = dragState == DragState.MARQUEE && intersectsCurrentMarquee(u0, v0, u1, v1);
            var selected = selectedFaces.contains(face) || (selectedFaces.isEmpty() && selectedCubes.contains(cube));
            var hovered = face.equals(hoveredFace);
            if (selected) {
                addUvRect(buffer, m00, m11, m30, m31, u0, v0, u1 - u0, v1 - v0, CUBE_SELECTED_FACE);
                addUvRectOutline(buffer, m00, m11, m30, m31, u0, v0, u1, v1, CUBE_SELECTED_OUTLINE);
            } else {
                addUvRectOutline(buffer, m00, m11, m30, m31, u0, v0, u1, v1, IMPORTED_FACE_OUTLINE);
            }
            if (hovered || marqueePreview) {
                addUvRectOutline(buffer, m00, m11, m30, m31, u0, v0, u1, v1, CUBE_HOVER_OUTLINE);
            }
        }
    }

    /**
     * Emit the cube's footprint on the UV map. Default-state cubes draw nothing (so a loaded texture is fully visible);
     * selection adds a translucent yellow fill plus a yellow outline on each face, and hover adds the white outline.
     */
    private void addCubeCross(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        double w,
        double h,
        double d,
        double u,
        double v,
        boolean selected,
        boolean hovered,
        boolean marqueePreview
    ) {
        if (selected) {
            // Six face fills, in the same order as AzBakedModelFactory's per-direction layout.
            addUvRect(buffer, m00, m11, m30, m31, u + d, v, w, d, CUBE_SELECTED_FACE); // up
            addUvRect(buffer, m00, m11, m30, m31, u + d + w, v, w, d, CUBE_SELECTED_FACE); // down
            addUvRect(buffer, m00, m11, m30, m31, u, v + d, d, h, CUBE_SELECTED_FACE); // west
            addUvRect(buffer, m00, m11, m30, m31, u + d, v + d, w, h, CUBE_SELECTED_FACE); // north (front)
            addUvRect(buffer, m00, m11, m30, m31, u + d + w, v + d, d, h, CUBE_SELECTED_FACE); // east
            addUvRect(buffer, m00, m11, m30, m31, u + 2 * d + w, v + d, w, h, CUBE_SELECTED_FACE); // south (back)
        }

        if (!selected && !hovered && !marqueePreview) {
            return;
        }
        if (selected) {
            addUvCrossOutline(buffer, m00, m11, m30, m31, w, h, d, u, v, CUBE_SELECTED_OUTLINE);
        }
        if (hovered || marqueePreview) {
            addUvCrossOutline(buffer, m00, m11, m30, m31, w, h, d, u, v, CUBE_HOVER_OUTLINE);
        }
    }

    /**
     * Emit the cross-unwrap outline: perimeter + internal face seams, each at 1px. Replaces the previous "6 rects with
     * 4 edges each = 24 segments" approach where every internal seam was double-drawn (once by each adjacent face's
     * outline) and read as a 2px stroke. Here every shared edge is a single 1px segment; verticals at columns shared
     * between the top row and middle row (left edge of up = west|north seam; up|down seam = north|east seam) are drawn
     * as one continuous segment spanning both rows, and the horizontal at V=v+d is the full middle-row top (subsuming
     * the two perimeter "step" segments + the up|north / down|east|south horizontal seam).
     */
    private void addUvCrossOutline(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        double w,
        double h,
        double d,
        double u,
        double v,
        int color
    ) {
        if (w <= 0 || h <= 0 || d <= 0) {
            return;
        }
        var sxTopL = screenXi(u + d); // left of up = west|north seam
        var sxTopM = screenXi(u + d + w); // up|down seam = north|east seam
        var sxTopR = screenXi(u + d + 2 * w); // right of down
        var sxEastSouth = screenXi(u + 2 * d + w); // east|south seam
        var sxMidL = screenXi(u); // left of west
        var sxMidR = screenXi(u + 2 * d + 2 * w); // right of south
        var syT = screenYi(v);
        var syM = screenYi(v + d);
        var syB = screenYi(v + d + h);
        if (sxTopR <= sxTopL || sxMidR <= sxMidL || syM <= syT || syB <= syM) {
            return;
        }

        // Horizontals.
        addQuad(buffer, m00, m11, m30, m31, sxTopL, syT, sxTopR, syT + 1, color); // top of up+down
        addQuad(buffer, m00, m11, m30, m31, sxMidL, syM, sxMidR, syM + 1, color); // top of middle row (full)
        addQuad(buffer, m00, m11, m30, m31, sxMidL, syB - 1, sxMidR, syB, color); // bottom of middle row

        // Verticals — full height (top-row + middle-row, since both rows share this column at a face boundary).
        addQuad(buffer, m00, m11, m30, m31, sxTopL, syT, sxTopL + 1, syB, color); // left of up + west|north seam
        addQuad(buffer, m00, m11, m30, m31, sxTopM, syT, sxTopM + 1, syB, color); // up|down + north|east seam

        // Verticals — top-row only.
        addQuad(buffer, m00, m11, m30, m31, sxTopR - 1, syT, sxTopR, syM, color); // right of down

        // Verticals — middle-row only.
        addQuad(buffer, m00, m11, m30, m31, sxMidL, syM, sxMidL + 1, syB, color); // left of west
        addQuad(buffer, m00, m11, m30, m31, sxEastSouth, syM, sxEastSouth + 1, syB, color); // east|south seam
        addQuad(buffer, m00, m11, m30, m31, sxMidR - 1, syM, sxMidR, syB, color); // right of south
    }

    private void addUvRect(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        double u,
        double v,
        double w,
        double h,
        int color
    ) {
        if (w <= 0 || h <= 0) {
            return;
        }
        var x0 = screenXi(u);
        var y0 = screenYi(v);
        var x1 = screenXi(u + w);
        var y1 = screenYi(v + h);
        if (x1 <= x0 || y1 <= y0) {
            return;
        }
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x1, y1, color);
    }

    private void addUvRectOutline(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        double u0,
        double v0,
        double u1,
        double v1,
        int color
    ) {
        var x0 = screenXi(u0);
        var y0 = screenYi(v0);
        var x1 = screenXi(u1);
        var y1 = screenYi(v1);
        if (x1 <= x0 || y1 <= y0) {
            return;
        }
        addRectOutline(buffer, m00, m11, m30, m31, x0, y0, x1, y1, color);
    }

    private static void addRectOutline(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        int x0,
        int y0,
        int x1,
        int y1,
        int color
    ) {
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x1, y0 + 1, color); // top
        addQuad(buffer, m00, m11, m30, m31, x0, y1 - 1, x1, y1, color); // bottom
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x0 + 1, y1, color); // left
        addQuad(buffer, m00, m11, m30, m31, x1 - 1, y0, x1, y1, color); // right
    }

    /**
     * Emit one 4-vertex quad into the buffer with manual pose pre-multiplication. The 2D-pose decomposition
     * ({@code m00, m11, m30, m31}) lets each vertex compute its screen position with two FMAs (one per axis) instead of
     * the full 16-mul/16-add matrix-vector multiply that {@code GuiGraphics.fill} runs through
     * {@code Matrix4f.transformPosition} per vertex.
     */
    private static void addQuad(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        float x0,
        float y0,
        float x1,
        float y1,
        int color
    ) {
        var sx0 = m00 * x0 + m30;
        var sx1 = m00 * x1 + m30;
        var sy0 = m11 * y0 + m31;
        var sy1 = m11 * y1 + m31;
        buffer.addVertex(sx0, sy0, 0).setColor(color);
        buffer.addVertex(sx0, sy1, 0).setColor(color);
        buffer.addVertex(sx1, sy1, 0).setColor(color);
        buffer.addVertex(sx1, sy0, 0).setColor(color);
    }

    // ----- Selection sync -----

    private void clearSelectionState() {
        clearCubeSelection();
        clearFaceSelection();
    }

    private void clearCubeSelection() {
        selectedCubes.clear();
        ownerByCube.clear();
        primaryCube = null;
        primaryOwner = null;
    }

    private void clearFaceSelection() {
        selectedFaces.clear();
        primaryFace = null;
    }

    private void pruneFaceSelectionForTexture(@Nullable LoadedTexture activeTexture) {
        if (selectedFaces.isEmpty()) {
            return;
        }
        var changed = selectedFaces.removeIf(face -> {
            var uv = face.cube().faceUv(face.face());
            return uv == null || !ModelerTextureUsage.usesTexture(activeTexture, uv);
        });
        if (!changed) {
            return;
        }
        primaryFace = selectedFaces.contains(primaryFace) ? primaryFace : lastSelectedFace();
        if (primaryFace != null) {
            primaryCube = primaryFace.cube();
            primaryOwner = primaryFace.owner();
        } else {
            primaryCube = null;
            primaryOwner = null;
        }
        writeSceneSelection(ModelerScene.get());
    }

    /**
     * Reconcile our panel-local multi-selection with {@code scene.selection}. The scene's selection is the source of
     * truth: external mutations (outliner click, viewport click, etc.) flip us to single-selection on that cube;
     * MultiCubeSelection — which we typically wrote ourselves — feeds back identically. When the scene's selection is
     * cleared or set to a non-cube (bone), drop our set.
     */
    private void syncFromSceneSelection(ModelerScene scene) {
        if (scene.selection instanceof FaceSelection fs) {
            if (selectedFaces.size() == 1 && selectedFaces.contains(fs)) {
                primaryFace = fs;
                primaryCube = fs.cube();
                primaryOwner = fs.owner();
                return;
            }
            clearSelectionState();
            selectedFaces.add(fs);
            primaryFace = fs;
            primaryCube = fs.cube();
            primaryOwner = fs.owner();
            return;
        }

        if (!selectedFaces.isEmpty()) {
            if (sceneSelectionMatchesSelectedFaces(scene.selection)) {
                return;
            }
            selectedFaces.clear();
            primaryFace = null;
        }

        if (scene.selection instanceof Selection.CubeSelection cs) {
            // Single selection. If we already had it as our sole selection, only the primary needs refreshing.
            if (selectedCubes.size() == 1 && selectedCubes.contains(cs.cube())) {
                primaryCube = cs.cube();
                primaryOwner = cs.owner();
                return;
            }
            selectedCubes.clear();
            ownerByCube.clear();
            selectedCubes.add(cs.cube());
            ownerByCube.put(cs.cube(), cs.owner());
            primaryCube = cs.cube();
            primaryOwner = cs.owner();
        } else if (scene.selection instanceof Selection.MultiCubeSelection ms) {
            // Multi selection. Rebuild our local set only if it differs from the scene's — avoids churn when we just
            // wrote this exact list ourselves.
            var sceneCubes = ms.cubes();
            if (sceneCubes.size() != selectedCubes.size() || !ms.contains(primaryCube != null ? primaryCube : ms.primary().cube())) {
                selectedCubes.clear();
                ownerByCube.clear();
                for (var cs : sceneCubes) {
                    selectedCubes.add(cs.cube());
                    ownerByCube.put(cs.cube(), cs.owner());
                }
            }
            var primary = ms.primary();
            primaryCube = primary.cube();
            primaryOwner = primary.owner();
        } else if (!selectedCubes.isEmpty()) {
            clearCubeSelection();
        }
    }

    /**
     * Mirror the local multi-selection back to {@code scene.selection}. Empty → null; single →
     * {@link Selection.CubeSelection}; two-or-more → {@link Selection.MultiCubeSelection} with the primary cube placed
     * last so {@code ms.primary()} returns the right one.
     */
    private void writeSceneSelection(ModelerScene scene) {
        if (!selectedFaces.isEmpty()) {
            writeFaceSceneSelection(scene);
            return;
        }
        if (selectedCubes.isEmpty()) {
            scene.selection = null;
            return;
        }
        if (selectedCubes.size() == 1) {
            var cube = selectedCubes.iterator().next();
            var owner = ownerByCube.get(cube);
            scene.selection = owner != null ? new Selection.CubeSelection(owner, cube) : null;
            return;
        }
        var list = new ArrayList<Selection.CubeSelection>(selectedCubes.size());
        for (var cube : selectedCubes) {
            if (cube == primaryCube) {
                continue;
            }
            var owner = ownerByCube.get(cube);
            if (owner != null) {
                list.add(new Selection.CubeSelection(owner, cube));
            }
        }
        if (primaryCube != null && primaryOwner != null) {
            list.add(new Selection.CubeSelection(primaryOwner, primaryCube));
        }
        scene.selection = list.size() >= 2 ? new Selection.MultiCubeSelection(list) : (list.size() == 1 ? list.get(0) : null);
    }

    private boolean sceneSelectionMatchesSelectedFaces(@Nullable Selection selection) {
        if (selection instanceof FaceSelection fs) {
            return selectedFaces.size() == 1 && selectedFaces.contains(fs);
        }
        if (selection instanceof Selection.CubeSelection cs) {
            return selectedFaceCubeCount() == 1
                && selectedFaces.iterator().next().cube() == cs.cube()
                && selectedFaces.iterator().next().owner() == cs.owner();
        }
        if (selection instanceof Selection.MultiCubeSelection ms) {
            var cubeCount = selectedFaceCubeCount();
            if (ms.cubes().size() != cubeCount) {
                return false;
            }
            for (var face : selectedFaces) {
                var found = false;
                for (var cs : ms.cubes()) {
                    if (cs.cube() == face.cube() && cs.owner() == face.owner()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private int selectedFaceCubeCount() {
        var cubes = new LinkedHashSet<ModelerCube>();
        for (var face : selectedFaces) {
            cubes.add(face.cube());
        }
        return cubes.size();
    }

    private void writeFaceSceneSelection(ModelerScene scene) {
        if (selectedFaces.size() == 1 && primaryFace != null) {
            scene.selection = primaryFace;
            return;
        }
        var list = new ArrayList<Selection.CubeSelection>();
        var seen = new LinkedHashSet<ModelerCube>();
        for (var face : selectedFaces) {
            if (face == primaryFace) {
                continue;
            }
            if (seen.add(face.cube())) {
                list.add(new Selection.CubeSelection(face.owner(), face.cube()));
            }
        }
        if (primaryFace != null) {
            list.removeIf(cs -> cs.cube() == primaryFace.cube());
            list.add(new Selection.CubeSelection(primaryFace.owner(), primaryFace.cube()));
        }
        scene.selection = list.size() >= 2 ? new Selection.MultiCubeSelection(list) : (list.size() == 1 ? list.get(0) : null);
    }

    // ----- Inputs -----

    private void syncInputs() {
        var faceUv = primaryFaceUv();
        if (faceUv != null) {
            var rect = faceRect(faceUv);
            syncInput(uInput, formatInt(rect.u0()));
            syncInput(vInput, formatInt(rect.v0()));
            return;
        }

        if (primaryCube == null || primaryCube.hasPerFaceUv) {
            if (uInput.isFocused() || vInput.isFocused()) {
                TextInput.clearFocus();
            }
            if (!uInput.content().isEmpty()) {
                uInput.setContent("");
            }
            if (!vInput.content().isEmpty()) {
                vInput.setContent("");
            }
            return;
        }
        syncInput(uInput, formatInt(primaryCube.uvOriginU));
        syncInput(vInput, formatInt(primaryCube.uvOriginV));
    }

    private boolean uvInputsEnabled() {
        return primaryFaceUv() != null || (primaryCube != null && !primaryCube.hasPerFaceUv);
    }

    private @Nullable ModelerCube.FaceUv primaryFaceUv() {
        var face = primaryFace;
        return face == null ? null : face.cube().faceUv(face.face());
    }

    private static void syncInput(TextInput input, String value) {
        if (input.isFocused()) {
            return;
        }
        if (!input.content().equals(value)) {
            input.setContent(value);
        }
    }

    /** Format as a plain integer — decimal UVs aren't supported. */
    private static String formatInt(double v) {
        return Long.toString(Math.round(v));
    }

    private void commitU(String text) {
        try {
            if (primaryFace != null) {
                var uv = primaryFaceUv();
                if (uv == null) {
                    return;
                }
                var rounded = (double) Math.round(Double.parseDouble(text.trim()));
                moveSelectedFaces(rounded - faceRect(uv).u0(), 0.0, "Edit face UV U");
                return;
            }

            var cube = primaryCube;
            if (cube == null || cube.hasPerFaceUv) {
                return;
            }
            var scene = ModelerScene.get();
            var rounded = (double) Math.round(Double.parseDouble(text.trim()));
            var bboxW = 2 * cube.size.z + 2 * cube.size.x;
            var maxU = Math.max(0, scene.textureWidth - bboxW);
            var clamped = Math.max(0, Math.min(maxU, rounded));
            var before = ModelerAction.CubeMemento.of(cube);
            cube.uvOriginU = clamped;
            pushCubeMemento(cube, before, "Edit UV U " + cube.name);
        } catch (NumberFormatException ignored) {
            // Bad input — leave the cube alone, next syncInputs will rewrite the field to a valid value.
        }
    }

    private void commitV(String text) {
        try {
            if (primaryFace != null) {
                var uv = primaryFaceUv();
                if (uv == null) {
                    return;
                }
                var rounded = (double) Math.round(Double.parseDouble(text.trim()));
                moveSelectedFaces(0.0, rounded - faceRect(uv).v0(), "Edit face UV V");
                return;
            }

            var cube = primaryCube;
            if (cube == null || cube.hasPerFaceUv) {
                return;
            }
            var scene = ModelerScene.get();
            var rounded = (double) Math.round(Double.parseDouble(text.trim()));
            var bboxH = cube.size.z + cube.size.y;
            var maxV = Math.max(0, scene.textureHeight - bboxH);
            var clamped = Math.max(0, Math.min(maxV, rounded));
            var before = ModelerAction.CubeMemento.of(cube);
            cube.uvOriginV = clamped;
            pushCubeMemento(cube, before, "Edit UV V " + cube.name);
        } catch (NumberFormatException ignored) {
            // Bad input — leave the cube alone, next syncInputs will rewrite the field to a valid value.
        }
    }

    private static void pushCubeMemento(ModelerCube cube, ModelerAction.CubeMemento before, String description) {
        var after = ModelerAction.CubeMemento.of(cube);
        if (!after.differsFrom(before)) {
            return;
        }
        ModelerActionHistory.push(
            new ModelerAction.CubeMementoAction("cube_edit", description, System.currentTimeMillis(), cube, before, after)
        );
    }

    // ----- Mouse handling -----

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Footer inputs claim clicks within their rects regardless of which strip they're in. Forwarded first so a
        // click on an input rect doesn't fall through to the UV area's selection logic.
        if (uvInputsEnabled()) {
            if (uInput.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (vInput.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (!inUvArea(mouseX, mouseY)) {
            return false;
        }

        if (button == 0) {
            var scene = ModelerScene.get();
            var faceCandidates = faceCandidatesAt(scene, mouseX, mouseY);
            if (!faceCandidates.isEmpty()) {
                selectClickedFace(scene, faceCandidates, Screen.hasShiftDown());
                if (!Screen.hasShiftDown()) {
                    startFaceDrag(mouseX, mouseY);
                }
                return true;
            }

            var hit = pickCubeWithOwnerAt(scene, mouseX, mouseY);
            if (hit != null) {
                // Clicked a cube. If it's already in the selection, keep the group; otherwise replace the selection
                // with just this cube. Either way the clicked cube becomes the primary and we start a drag.
                if (!selectedCubes.contains(hit.cube)) {
                    clearSelectionState();
                    selectedCubes.add(hit.cube);
                    ownerByCube.put(hit.cube, hit.owner);
                }
                primaryCube = hit.cube;
                primaryOwner = hit.owner;
                writeSceneSelection(scene);

                if (selectedCubesUseBoxUvsOnly()) {
                    startCubeDrag(mouseX, mouseY);
                } else {
                    dragState = DragState.IDLE;
                }
                return true;
            }
            // Empty space — provisional marquee. We don't actually commit to MARQUEE state until the mouse moves; a
            // pure click on empty (no drag) clears the selection.
            marqueeStartU = uvAtScreenX(mouseX);
            marqueeStartV = uvAtScreenY(mouseY);
            marqueeEndU = marqueeStartU;
            marqueeEndV = marqueeStartV;
            dragState = DragState.MAYBE_MARQUEE;
            return true;
        }
        if (button == 2) {
            // MMB just primes the pan drag; mouseDragged does the actual translation.
            return true;
        }
        return false;
    }

    private void startCubeDrag(double mouseX, double mouseY) {
        dragGrabU = uvAtScreenX(mouseX);
        dragGrabV = uvAtScreenY(mouseY);
        dragStartUVs.clear();
        dragStartMementos.clear();
        for (var cube : selectedCubes) {
            dragStartUVs.put(cube, new double[] { cube.uvOriginU, cube.uvOriginV });
            dragStartMementos.put(cube, ModelerAction.CubeMemento.of(cube));
        }
        dragState = DragState.DRAGGING_CUBE;
    }

    private void selectClickedFace(ModelerScene scene, List<FaceSelection> candidates, boolean extendSelection) {
        var clicked = faceCandidateForClick(candidates);
        clearCubeSelection();
        if (extendSelection) {
            if (selectedFaces.remove(clicked)) {
                if (clicked.equals(primaryFace)) {
                    primaryFace = lastSelectedFace();
                }
            } else {
                selectedFaces.add(clicked);
                primaryFace = clicked;
            }
        } else if (selectedFaces.contains(clicked)) {
            primaryFace = clicked;
        } else {
            clearFaceSelection();
            selectedFaces.add(clicked);
            primaryFace = clicked;
        }

        if (primaryFace != null) {
            primaryCube = primaryFace.cube();
            primaryOwner = primaryFace.owner();
        } else {
            primaryCube = null;
            primaryOwner = null;
        }
        writeSceneSelection(scene);
    }

    private FaceSelection faceCandidateForClick(List<FaceSelection> candidates) {
        if (primaryFace != null && candidates.size() > 1) {
            var index = candidates.indexOf(primaryFace);
            if (index >= 0) {
                return candidates.get((index + 1) % candidates.size());
            }
        }
        return candidates.get(0);
    }

    private @Nullable FaceSelection lastSelectedFace() {
        FaceSelection last = null;
        for (var face : selectedFaces) {
            last = face;
        }
        return last;
    }

    private void startFaceDrag(double mouseX, double mouseY) {
        dragGrabU = uvAtScreenX(mouseX);
        dragGrabV = uvAtScreenY(mouseY);
        dragStartFaceUvs.clear();
        dragStartMementos.clear();
        for (var face : selectedFaces) {
            var uv = face.cube().faceUv(face.face());
            if (uv == null) {
                continue;
            }
            dragStartFaceUvs.put(face, uv);
            dragStartMementos.putIfAbsent(face.cube(), ModelerAction.CubeMemento.of(face.cube()));
        }
        dragState = DragState.DRAGGING_FACE;
    }

    private boolean selectedCubesUseBoxUvsOnly() {
        if (selectedCubes.isEmpty()) {
            return false;
        }
        for (var cube : selectedCubes) {
            if (cube.hasPerFaceUv) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            if (dragState == DragState.DRAGGING_CUBE) {
                var deltaU = uvAtScreenX(mouseX) - dragGrabU;
                var deltaV = uvAtScreenY(mouseY) - dragGrabV;
                // Group-clamp: compute the tightest delta range across all selected cubes (each cube's UV bbox must
                // stay inside [0, texW] × [0, texH]). The cubes move together by the same clamped delta, so a single
                // cube hitting an edge stops the whole group rather than letting it drift apart.
                var scene = ModelerScene.get();
                var minDeltaU = Double.NEGATIVE_INFINITY;
                var maxDeltaU = Double.POSITIVE_INFINITY;
                var minDeltaV = Double.NEGATIVE_INFINITY;
                var maxDeltaV = Double.POSITIVE_INFINITY;
                for (var cube : selectedCubes) {
                    var start = dragStartUVs.get(cube);
                    if (start == null) {
                        continue;
                    }
                    var bboxW = 2 * cube.size.z + 2 * cube.size.x;
                    var bboxH = cube.size.z + cube.size.y;
                    var maxU = Math.max(0, scene.textureWidth - bboxW);
                    var maxV = Math.max(0, scene.textureHeight - bboxH);
                    minDeltaU = Math.max(minDeltaU, -start[0]);
                    maxDeltaU = Math.min(maxDeltaU, maxU - start[0]);
                    minDeltaV = Math.max(minDeltaV, -start[1]);
                    maxDeltaV = Math.min(maxDeltaV, maxV - start[1]);
                }
                // Guard against an impossible range (some cube already further out of bounds than another can
                // compensate
                // for) — collapse to zero rather than letting the result flip signs unpredictably.
                if (minDeltaU > maxDeltaU) {
                    minDeltaU = maxDeltaU = 0;
                }
                if (minDeltaV > maxDeltaV) {
                    minDeltaV = maxDeltaV = 0;
                }
                deltaU = Math.max(minDeltaU, Math.min(maxDeltaU, deltaU));
                deltaV = Math.max(minDeltaV, Math.min(maxDeltaV, deltaV));
                for (var cube : selectedCubes) {
                    var start = dragStartUVs.get(cube);
                    if (start == null) {
                        continue;
                    }
                    // Snap to whole pixels — decimal UVs aren't authored by Bedrock and would round on save anyway.
                    cube.uvOriginU = Math.round(start[0] + deltaU);
                    cube.uvOriginV = Math.round(start[1] + deltaV);
                }
                return true;
            }
            if (dragState == DragState.DRAGGING_FACE) {
                var deltaU = uvAtScreenX(mouseX) - dragGrabU;
                var deltaV = uvAtScreenY(mouseY) - dragGrabV;
                applySelectedFaceDelta(deltaU, deltaV, dragStartFaceUvs);
                return true;
            }
            if (dragState == DragState.MAYBE_MARQUEE || dragState == DragState.MARQUEE) {
                // First drag event after an LMB-down on empty space promotes us to active marquee.
                dragState = DragState.MARQUEE;
                marqueeEndU = uvAtScreenX(mouseX);
                marqueeEndV = uvAtScreenY(mouseY);
                return true;
            }
            return false;
        }
        if (button == 2) {
            // Float accumulation: panX/Y collect sub-pixel deltas, but render snaps to integer screen pixels via
            // offsetXInt/Y. PAN_SENSITIVITY scales how fast the view tracks cursor motion.
            panOffsetX += deltaX * PAN_SENSITIVITY;
            panOffsetY += deltaY * PAN_SENSITIVITY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        var state = dragState;
        dragState = DragState.IDLE;

        if (state == DragState.DRAGGING_CUBE) {
            finalizeCubeDrag();
            return true;
        }
        if (state == DragState.DRAGGING_FACE) {
            finalizeFaceDrag();
            return true;
        }
        if (state == DragState.MAYBE_MARQUEE) {
            // LMB on empty space, released without dragging — clear the selection.
            clearSelectionState();
            writeSceneSelection(ModelerScene.get());
            return true;
        }
        if (state == DragState.MARQUEE) {
            marqueeEndU = uvAtScreenX(mouseX);
            marqueeEndV = uvAtScreenY(mouseY);
            finalizeMarquee();
            return true;
        }
        return false;
    }

    /**
     * Build a composite memento covering every cube that actually moved during the drag, push it as one undo entry.
     * Single-cube drags push a plain CubeMementoAction; group drags wrap N actions in a CompositeAction so
     * {@code Ctrl+Z} reverts the entire group move in one step.
     */
    private void finalizeCubeDrag() {
        var root = ModelerScene.get().root;
        var actions = new ArrayList<ModelerAction>();
        for (var cube : selectedCubes) {
            var before = dragStartMementos.get(cube);
            if (before == null) {
                continue;
            }
            // Guard against a model reload mid-drag detaching the cube from the scene tree — pushing a memento against
            // a detached cube would let undo write to a heap object nothing else references.
            if (!isCubeReachable(root, cube)) {
                continue;
            }
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions.add(
                    new ModelerAction.CubeMementoAction(
                        "cube_edit",
                        "Move UV " + cube.name,
                        System.currentTimeMillis(),
                        cube,
                        before,
                        after
                    )
                );
            }
        }
        if (actions.size() == 1) {
            ModelerActionHistory.push(actions.get(0));
        } else if (actions.size() > 1) {
            ModelerActionHistory.push(
                new ModelerAction.CompositeAction(
                    "uv_group_move",
                    "Move " + actions.size() + " UVs",
                    System.currentTimeMillis(),
                    List.copyOf(actions)
                )
            );
        }
        dragStartUVs.clear();
        dragStartMementos.clear();
    }

    private void finalizeFaceDrag() {
        pushFaceMementoActions(dragStartMementos, "Move face UVs");
        dragStartFaceUvs.clear();
        dragStartMementos.clear();
    }

    private void moveSelectedFaces(double deltaU, double deltaV, String description) {
        if (selectedFaces.isEmpty()) {
            return;
        }
        var starts = new HashMap<FaceSelection, ModelerCube.FaceUv>();
        var before = new HashMap<ModelerCube, ModelerAction.CubeMemento>();
        for (var face : selectedFaces) {
            var uv = face.cube().faceUv(face.face());
            if (uv == null) {
                continue;
            }
            starts.put(face, uv);
            before.putIfAbsent(face.cube(), ModelerAction.CubeMemento.of(face.cube()));
        }
        if (starts.isEmpty()) {
            return;
        }
        applySelectedFaceDelta(deltaU, deltaV, starts);
        pushFaceMementoActions(before, description);
    }

    private void applySelectedFaceDelta(double deltaU, double deltaV, Map<FaceSelection, ModelerCube.FaceUv> starts) {
        if (starts.isEmpty()) {
            return;
        }
        var scene = ModelerScene.get();
        var minDeltaU = Double.NEGATIVE_INFINITY;
        var maxDeltaU = Double.POSITIVE_INFINITY;
        var minDeltaV = Double.NEGATIVE_INFINITY;
        var maxDeltaV = Double.POSITIVE_INFINITY;
        for (var uv : starts.values()) {
            var rect = faceRect(uv);
            minDeltaU = Math.max(minDeltaU, -rect.u0());
            maxDeltaU = Math.min(maxDeltaU, scene.textureWidth - rect.u1());
            minDeltaV = Math.max(minDeltaV, -rect.v0());
            maxDeltaV = Math.min(maxDeltaV, scene.textureHeight - rect.v1());
        }
        if (minDeltaU > maxDeltaU) {
            minDeltaU = maxDeltaU = 0.0;
        }
        if (minDeltaV > maxDeltaV) {
            minDeltaV = maxDeltaV = 0.0;
        }
        deltaU = Math.max(minDeltaU, Math.min(maxDeltaU, deltaU));
        deltaV = Math.max(minDeltaV, Math.min(maxDeltaV, deltaV));
        for (var entry : starts.entrySet()) {
            var face = entry.getKey();
            var start = entry.getValue();
            face.cube()
                .setFaceUv(
                    face.face(),
                    new ModelerCube.FaceUv(
                        Math.round(start.u() + deltaU),
                        Math.round(start.v() + deltaV),
                        start.width(),
                        start.height(),
                        start.rotation(),
                        start.textureSource()
                    )
                );
        }
    }

    private void pushFaceMementoActions(Map<ModelerCube, ModelerAction.CubeMemento> beforeByCube, String description) {
        var root = ModelerScene.get().root;
        var actions = new ArrayList<ModelerAction>();
        for (var entry : beforeByCube.entrySet()) {
            var cube = entry.getKey();
            if (!isCubeReachable(root, cube)) {
                continue;
            }
            var before = entry.getValue();
            var after = ModelerAction.CubeMemento.of(cube);
            if (after.differsFrom(before)) {
                actions.add(
                    new ModelerAction.CubeMementoAction(
                        "cube_edit",
                        description + " " + cube.name,
                        System.currentTimeMillis(),
                        cube,
                        before,
                        after
                    )
                );
            }
        }
        if (actions.size() == 1) {
            ModelerActionHistory.push(actions.get(0));
        } else if (actions.size() > 1) {
            ModelerActionHistory.push(
                new ModelerAction.CompositeAction(
                    "face_uv_group_edit",
                    description + " (" + actions.size() + " cubes)",
                    System.currentTimeMillis(),
                    List.copyOf(actions)
                )
            );
        }
    }

    private void finalizeMarquee() {
        var mu0 = Math.min(marqueeStartU, marqueeEndU);
        var mv0 = Math.min(marqueeStartV, marqueeEndV);
        var mu1 = Math.max(marqueeStartU, marqueeEndU);
        var mv1 = Math.max(marqueeStartV, marqueeEndV);

        clearSelectionState();
        var scene = ModelerScene.get();
        collectMarqueeHits(scene.root, mu0, mv0, mu1, mv1, scene.activeTexture);

        if (!selectedFaces.isEmpty()) {
            FaceSelection lastFace = null;
            for (var face : selectedFaces) {
                lastFace = face;
            }
            primaryFace = lastFace;
            primaryCube = lastFace != null ? lastFace.cube() : null;
            primaryOwner = lastFace != null ? lastFace.owner() : null;
            writeSceneSelection(scene);
            return;
        }

        if (selectedCubes.isEmpty()) {
            primaryCube = null;
            primaryOwner = null;
            writeSceneSelection(scene);
            return;
        }
        // Primary = last cube added during DFS walk (deepest in tree order = topmost in draw order).
        ModelerCube last = null;
        for (var cube : selectedCubes) {
            last = cube;
        }
        primaryCube = last;
        primaryOwner = ownerByCube.get(last);
        writeSceneSelection(scene);
    }

    private boolean intersectsCurrentMarquee(double u0, double v0, double u1, double v1) {
        var mu0 = Math.min(marqueeStartU, marqueeEndU);
        var mv0 = Math.min(marqueeStartV, marqueeEndV);
        var mu1 = Math.max(marqueeStartU, marqueeEndU);
        var mv1 = Math.max(marqueeStartV, marqueeEndV);
        return intersects(u0, v0, u1, v1, mu0, mv0, mu1, mv1);
    }

    private void collectMarqueeHits(
        ModelerBone bone,
        double mu0,
        double mv0,
        double mu1,
        double mv1,
        @Nullable LoadedTexture activeTexture
    ) {
        for (var cube : bone.cubes) {
            if (cube.hasPerFaceUv) {
                collectIntersectingFaces(bone, cube, mu0, mv0, mu1, mv1, activeTexture);
                continue;
            }
            var w = cube.size.x;
            var h = cube.size.y;
            var d = cube.size.z;
            var u0 = cube.uvOriginU;
            var v0 = cube.uvOriginV;
            var u1 = u0 + 2 * d + 2 * w;
            var v1 = v0 + d + h;
            // Rect-rect intersection test (any overlap counts — even a corner-touch).
            if (intersects(u0, v0, u1, v1, mu0, mv0, mu1, mv1)) {
                selectedCubes.add(cube);
                ownerByCube.put(cube, bone);
            }
        }
        for (var child : bone.children) {
            collectMarqueeHits(child, mu0, mv0, mu1, mv1, activeTexture);
        }
    }

    private void collectIntersectingFaces(
        ModelerBone owner,
        ModelerCube cube,
        double mu0,
        double mv0,
        double mu1,
        double mv1,
        @Nullable LoadedTexture activeTexture
    ) {
        for (var entry : cube.faceUvs.entrySet()) {
            var uv = entry.getValue();
            if (!ModelerTextureUsage.usesTexture(activeTexture, uv)) {
                continue;
            }
            var rect = faceRect(uv);
            if (intersects(rect.u0(), rect.v0(), rect.u1(), rect.v1(), mu0, mv0, mu1, mv1)) {
                selectedFaces.add(new FaceSelection(owner, cube, entry.getKey()));
            }
        }
    }

    private static boolean intersects(double u0, double v0, double u1, double v1, double mu0, double mv0, double mu1, double mv1) {
        return u0 < mu1 && u1 > mu0 && v0 < mv1 && v1 > mv0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!inUvArea(mouseX, mouseY)) {
            return false;
        }
        var scene = ModelerScene.get();
        var minZoom = computeMinZoom(scene);
        var maxZoom = Math.max(minZoom, MAX_ABSOLUTE_ZOOM);

        // Capture the UV point under the cursor before the zoom change, then reposition panOffsetX/Y so the same UV
        // point lands at the same screen position after the new zoom is applied (anchored zoom).
        var uvBeforeX = uvAtScreenX(mouseX);
        var uvBeforeY = uvAtScreenY(mouseY);
        var factor = scrollY > 0 ? 1.1 : (1.0 / 1.1);
        zoom = clamp(zoom * factor, minZoom, maxZoom);

        var naturalX = uvAreaX + (uvAreaW - scene.textureWidth * zoom) / 2.0;
        var naturalY = (double) uvAreaY;
        panOffsetX = mouseX - uvBeforeX * zoom - naturalX;
        panOffsetY = mouseY - uvBeforeY * zoom - naturalY;
        return true;
    }

    // ----- Coordinate transforms -----

    /**
     * UV → screen, rounded to integer pixel and using the per-frame {@code offsetXInt}. Adjacent UV values that should
     * share a pixel boundary always round to the same screen pixel (because the offset is shared); the whole view
     * shifts by integer pixels when pan crosses a half-pixel boundary, eliminating the per-line wiggle that would
     * otherwise show up at non-integer zoom levels.
     */
    private int screenXi(double u) {
        return (int) Math.round(u * zoom) + offsetXInt;
    }

    private int screenYi(double v) {
        return (int) Math.round(v * zoom) + offsetYInt;
    }

    private double uvAtScreenX(double sx) {
        return (sx - offsetXInt) / zoom;
    }

    private double uvAtScreenY(double sy) {
        return (sy - offsetYInt) / zoom;
    }

    private boolean inUvArea(double mx, double my) {
        return mx >= uvAreaX && mx < uvAreaX + uvAreaW && my >= uvAreaY && my < uvAreaY + uvAreaH;
    }

    /**
     * Fit-to-area zoom: largest zoom at which the texture fits inside the UV map area in both dimensions, capped at the
     * 1:1 absolute. The cap matters when the area is larger than the texture at native pixel size — without it, "fit"
     * would upscale the texture past native, which looks blurry and steals the viewport feel of padding around a 1:1
     * texture. With the cap, a large panel locks the zoom range at {@code [1.0, 1.0]} and the texture renders at native
     * size centered in the area with panel-background padding on every side.
     */
    private double computeMinZoom(ModelerScene scene) {
        // Use uvAreaMaxH (panel-content max) here, NOT uvAreaH — uvAreaH is the locked viewport, which itself depends
        // on minZoom, so using it here would be circular. The fit zoom is the largest at which the texture fits within
        // the FULL available panel space; the viewport then sizes itself to the texture at that zoom.
        if (uvAreaW <= 0 || uvAreaMaxH <= 0 || scene.textureWidth <= 0 || scene.textureHeight <= 0) {
            return MAX_ABSOLUTE_ZOOM;
        }
        var fit = Math.min(uvAreaW / scene.textureWidth, uvAreaMaxH / scene.textureHeight);
        return Math.min(fit, MAX_ABSOLUTE_ZOOM);
    }

    // ----- Cube traversal + hit testing -----

    private @Nullable CubeWithOwner pickCubeWithOwnerAt(ModelerScene scene, double mx, double my) {
        if (!inUvArea(mx, my)) {
            return null;
        }
        var uvX = uvAtScreenX(mx);
        var uvY = uvAtScreenY(my);
        return pickBoxInBoneReverse(scene.root, uvX, uvY);
    }

    /**
     * Hover-only variant of {@link #pickCubeWithOwnerAt} that returns just the cube. Called every frame during render,
     * so it stays out of the click/selection path to avoid mutating selection state.
     */
    private @Nullable ModelerCube pickHover(ModelerBone bone, double uvX, double uvY) {
        var hit = pickBoxInBoneReverse(bone, uvX, uvY);
        return hit == null ? null : hit.cube;
    }

    private @Nullable CubeWithOwner pickBoxInBoneReverse(ModelerBone bone, double uvX, double uvY) {
        // Walk children + cubes in reverse so later-drawn cubes win the hit-test (matches DFS draw order: cubes-of-self
        // first, then DFS into children — so deepest-last child is topmost).
        for (var i = bone.children.size() - 1; i >= 0; i--) {
            var hit = pickBoxInBoneReverse(bone.children.get(i), uvX, uvY);
            if (hit != null) {
                return hit;
            }
        }
        for (var i = bone.cubes.size() - 1; i >= 0; i--) {
            var cube = bone.cubes.get(i);
            if (!cube.hasPerFaceUv && boundingContains(cube, uvX, uvY)) {
                return new CubeWithOwner(bone, cube);
            }
        }
        return null;
    }

    private @Nullable FaceSelection pickFaceAt(
        ModelerBone bone,
        double uvX,
        double uvY,
        @Nullable LoadedTexture activeTexture
    ) {
        var hits = new ArrayList<FaceSelection>();
        collectFaceHitsReverse(bone, uvX, uvY, activeTexture, hits);
        return hits.isEmpty() ? null : hits.get(0);
    }

    private List<FaceSelection> faceCandidatesAt(ModelerScene scene, double mx, double my) {
        if (!inUvArea(mx, my)) {
            return List.of();
        }
        var uvX = uvAtScreenX(mx);
        var uvY = uvAtScreenY(my);
        var hits = new ArrayList<FaceSelection>();
        collectFaceHitsReverse(scene.root, uvX, uvY, scene.activeTexture, hits);
        return hits;
    }

    private void collectFaceHitsReverse(
        ModelerBone bone,
        double uvX,
        double uvY,
        @Nullable LoadedTexture activeTexture,
        List<FaceSelection> hits
    ) {
        for (var i = bone.children.size() - 1; i >= 0; i--) {
            collectFaceHitsReverse(bone.children.get(i), uvX, uvY, activeTexture, hits);
        }
        for (var i = bone.cubes.size() - 1; i >= 0; i--) {
            var cube = bone.cubes.get(i);
            if (!cube.hasPerFaceUv) {
                continue;
            }
            var faces = ModelerCube.Face.values();
            for (var f = faces.length - 1; f >= 0; f--) {
                var face = faces[f];
                var uv = cube.faceUv(face);
                if (uv == null || !ModelerTextureUsage.usesTexture(activeTexture, uv)) {
                    continue;
                }
                var rect = faceRect(uv);
                if (uvX >= rect.u0() && uvX < rect.u1() && uvY >= rect.v0() && uvY < rect.v1()) {
                    hits.add(new FaceSelection(bone, cube, face));
                }
            }
        }
    }

    private static boolean boundingContains(ModelerCube cube, double uvX, double uvY) {
        var w = cube.size.x;
        var h = cube.size.y;
        var d = cube.size.z;
        var u0 = cube.uvOriginU;
        var v0 = cube.uvOriginV;
        var u1 = u0 + 2 * d + 2 * w;
        var v1 = v0 + d + h;
        return uvX >= u0 && uvX < u1 && uvY >= v0 && uvY < v1;
    }

    private static boolean isCubeReachable(ModelerBone bone, ModelerCube target) {
        for (var cube : bone.cubes) {
            if (cube == target) {
                return true;
            }
        }
        for (var child : bone.children) {
            if (isCubeReachable(child, target)) {
                return true;
            }
        }
        return false;
    }

    private static UvRect faceRect(ModelerCube.FaceUv uv) {
        return new UvRect(
            Math.min(uv.u(), uv.u() + uv.width()),
            Math.min(uv.v(), uv.v() + uv.height()),
            Math.max(uv.u(), uv.u() + uv.width()),
            Math.max(uv.v(), uv.v() + uv.height())
        );
    }

    private record UvRect(
        double u0,
        double v0,
        double u1,
        double v1
    ) {}

    private record CubeWithOwner(
        ModelerBone owner,
        ModelerCube cube
    ) {}

    private enum DragState {
        IDLE,
        /** LMB-down on empty space; waiting to see if the user drags (→ {@link #MARQUEE}) or releases (→ clear). */
        MAYBE_MARQUEE,
        MARQUEE,
        DRAGGING_CUBE,
        DRAGGING_FACE
    }

    // ----- Helpers -----

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Apply a raw GL scissor that respects the workspace's pose scale. The engine renders at a 0.375× pose scale, so
     * {@code GuiGraphics.enableScissor} (which works in logical GUI coords) clips at the wrong place. Same pattern as
     * {@code TerritoryMapPanel.applyRawScissor}.
     */
    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());
        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }
}
