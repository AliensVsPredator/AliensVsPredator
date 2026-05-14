package com.blib.engine.ui.panel.details;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.api.client.registry.v1.AzItemRendererRegistry;
import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.api.client.render.v1.item.BLibItemTransforms;
import com.blib.engine.gizmo.BLibItemTransformOverrides;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.item.ModelerItemSession;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.TextInput;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SWriteItemRendererConfigPayload;

/**
 * Inspector section for editing the per-pose item display transforms of the currently-attached
 * {@link ModelerItemSession}. Reads the effective transform from {@link BLibItemTransformOverrides} every frame, and
 * writes every commit back to the same store so both the modeler preview and the live in-world render see the change on
 * the next frame.
 * <p>
 * Layout, top to bottom:
 * <ul>
 * <li>Item picker — {@link SearchableSelect} of {@link BLibItemTransformOverrides#registeredItemIds()}.</li>
 * <li>Mode toggle — Idle / Blocking buttons (target which transform set is being edited).</li>
 * <li>Context picker — {@link SearchableSelect} of {@link ItemDisplayContext} values (which display context is being
 * edited).</li>
 * <li>Wall toggle (only when context is FIXED) — switches the edit target to the wall-fixed slot.</li>
 * <li>Translation / Rotation / Scale / Pivot — four vec3 sections matching
 * {@link ModelerInspectorPanel#renderVecSection}'s style.</li>
 * <li>Pivot Visualization checkbox — toggles the wireframe-pivot overlay the geo-bone item renderer draws.</li>
 * </ul>
 * Auto-save: on every commit, if the selected item is asset-backed (registered via
 * {@code BLibClientRegistryAccess#registerGeoBoneItemRendererFromAsset}), the full effective config is serialized and
 * sent to the server via {@link C2SWriteItemRendererConfigPayload}, which writes it into the active project's resource
 * pack. The user picks up the new bytes by clicking "Reload Project" in the engine menu — that triggers both server-
 * data and client-resource reloads. Java-backed items have no auto-save target and silently no-op; the inspector shows
 * a small notice when one is selected.
 */
@ApiStatus.Internal
public final class ModelerItemConfigSection {

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int SECTION_HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int LABEL_COLOR = 0xFFB8B8C0;

    private static final int BUTTON_BG = 0xFF2A2A32;

    private static final int BUTTON_HOVER_BG = 0xFF3A3A48;

    private static final int BUTTON_ACTIVE_BG = 0xFF4F8FFF;

    private static final int BUTTON_BORDER = 0xFF505058;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int CHECKBOX_FILL = 0xFF4F8FFF;

    private static final int AXIS_RED = 0xFFFF3333;

    private static final int AXIS_GREEN = 0xFF33CC33;

    private static final int AXIS_BLUE = 0xFF3366FF;

    private static final int[] AXIS_COLORS = { AXIS_RED, AXIS_GREEN, AXIS_BLUE };

    private static final int SECTION_HEADER_HEIGHT = 11;

    private static final int CONTENT_PADDING = 5;

    private static final int INPUT_GAP = 3;

    private static final int ROW_GAP = 4;

    private static final int BUTTON_HEIGHT = 14;

    private static final int CHECKBOX_SIZE = 10;

    private static final int AXIS_CORNER_SIZE = 4;

    /** Mirrors the order the renderer uses; NONE is excluded since it's not a real display surface. */
    private static final ItemDisplayContext[] EDITABLE_CONTEXTS = {
        ItemDisplayContext.GUI,
        ItemDisplayContext.GROUND,
        ItemDisplayContext.FIXED,
        ItemDisplayContext.HEAD,
        ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
        ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
        ItemDisplayContext.THIRD_PERSON_LEFT_HAND
    };

    private final SearchableSelect<ResourceLocation> itemPicker;

    private final SearchableSelect<ItemDisplayContext> contextPicker;

    private final TextInput txInput = new TextInput("X", v -> commit(Field.TX, v));

    private final TextInput tyInput = new TextInput("Y", v -> commit(Field.TY, v));

    private final TextInput tzInput = new TextInput("Z", v -> commit(Field.TZ, v));

    private final TextInput rxInput = new TextInput("X", v -> commit(Field.RX, v));

    private final TextInput ryInput = new TextInput("Y", v -> commit(Field.RY, v));

    private final TextInput rzInput = new TextInput("Z", v -> commit(Field.RZ, v));

    private final TextInput sxInput = new TextInput("X", v -> commit(Field.SX, v));

    private final TextInput syInput = new TextInput("Y", v -> commit(Field.SY, v));

    private final TextInput szInput = new TextInput("Z", v -> commit(Field.SZ, v));

    private final TextInput pxInput = new TextInput("X", v -> commit(Field.PX, v));

    private final TextInput pyInput = new TextInput("Y", v -> commit(Field.PY, v));

    private final TextInput pzInput = new TextInput("Z", v -> commit(Field.PZ, v));

    /** Inputs rendered this frame, so the parent panel only routes clicks to live rects. */
    private final List<TextInput> visibleInputs = new ArrayList<>();

    /** Hit rects captured per frame for the buttons + checkboxes; reset at the start of each render. */
    private int modeIdleX, modeIdleY, modeBlockingX, modeBlockingY, modeButtonW;

    private int wallToggleX, wallToggleY, wallToggleW, wallToggleH;

    private boolean wallToggleVisible;

    private int pivotVizX, pivotVizY, pivotVizW, pivotVizH;

    public ModelerItemConfigSection() {
        this.itemPicker = new SearchableSelect<>(
            () -> {
                // Item renderers are registered as lazy suppliers — their constructors (which call
                // BLibTunableItemTransforms.wrap and populate BLibItemTransformOverrides) only run when vanilla
                // first renders the item. From the title screen / engine-open path the user has never rendered
                // these items, so the override registry is empty. Force-instantiate every registered renderer up
                // front so the picker sees AVP-Alien-style items immediately. getOrNull is computeIfAbsent so
                // repeated calls are cheap map lookups.
                for (var item : AzItemRendererRegistry.registeredItems()) {
                    AzItemRendererRegistry.getOrNull(item);
                }
                // tunableItemIds() = items that called BLibTunableItemTransforms.wrap (the bases). registeredItemIds()
                // would only surface items that someone has set an override on, which excludes freshly-wrapped items
                // the user hasn't touched yet — the modeler picker needs to see EVERY tuner-wrapped item.
                var ids = new ArrayList<ResourceLocation>(BLibItemTransformOverrides.tunableItemIds());
                ids.sort((a, b) -> a.toString().compareToIgnoreCase(b.toString()));
                var items = new ArrayList<SearchableSelect.Item<ResourceLocation>>();
                for (var id : ids) {
                    items.add(new SearchableSelect.Item<>(id, id.toString()));
                }
                return items;
            },
            ResourceLocation::toString,
            null,
            this::onItemPicked
        );
        this.contextPicker = new SearchableSelect<>(
            () -> {
                var items = new ArrayList<SearchableSelect.Item<ItemDisplayContext>>();
                for (var ctx : EDITABLE_CONTEXTS) {
                    items.add(new SearchableSelect.Item<>(ctx, contextLabel(ctx)));
                }
                return items;
            },
            ModelerItemConfigSection::contextLabel,
            ItemDisplayContext.GUI,
            this::onContextPicked
        );
    }

    /** Render section. {@code y} is the top edge; returns the next-Y for chained rendering downstream. */
    public int render(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        visibleInputs.clear();
        wallToggleVisible = false;

        var font = EngineFont.get();
        var scene = ModelerScene.get();
        var session = scene.itemSession;

        var rowY = drawSectionHeader(graphics, font, x, y, width, "Item Config");
        rowY += CONTENT_PADDING;

        // Item picker — sync `currentValue` from the scene so external changes (clearing the session) flip
        // the dropdown back to "(none)".
        itemPicker.setCurrentValue(session == null ? null : session.itemId);
        var pickerX = x + CONTENT_PADDING;
        var pickerW = Math.max(40, width - 2 * CONTENT_PADDING);
        itemPicker.render(graphics, pickerX, rowY, pickerW, mouseX, mouseY);
        rowY += SearchableSelect.HEIGHT + ROW_GAP;

        if (session == null) {
            graphics.drawString(
                font,
                Component.literal("Pick an item to begin editing transforms."),
                x + CONTENT_PADDING,
                rowY,
                LABEL_COLOR,
                false
            );
            return rowY + font.lineHeight + ROW_GAP;
        }

        // Mode toggle — two side-by-side buttons.
        modeButtonW = (pickerW - INPUT_GAP) / 2;
        modeIdleX = pickerX;
        modeIdleY = rowY;
        modeBlockingX = pickerX + modeButtonW + INPUT_GAP;
        modeBlockingY = rowY;
        drawToggleButton(
            graphics,
            font,
            modeIdleX,
            modeIdleY,
            modeButtonW,
            "Idle",
            session.mode == BLibItemTransformMode.IDLE,
            mouseX,
            mouseY
        );
        drawToggleButton(
            graphics,
            font,
            modeBlockingX,
            modeBlockingY,
            modeButtonW,
            "Blocking",
            session.mode == BLibItemTransformMode.BLOCKING,
            mouseX,
            mouseY
        );
        rowY += BUTTON_HEIGHT + ROW_GAP;

        // Context picker.
        contextPicker.setCurrentValue(session.editingContext);
        contextPicker.render(graphics, pickerX, rowY, pickerW, mouseX, mouseY);
        rowY += SearchableSelect.HEIGHT + ROW_GAP;

        // Wall-fixed toggle — only meaningful when editing the FIXED context.
        if (session.editingContext == ItemDisplayContext.FIXED) {
            wallToggleVisible = true;
            wallToggleX = pickerX;
            wallToggleY = rowY;
            wallToggleW = pickerW;
            wallToggleH = BUTTON_HEIGHT;
            drawCheckboxRow(
                graphics,
                font,
                wallToggleX,
                wallToggleY,
                wallToggleW,
                "Edit Wall Variant",
                session.wallFixedActive,
                mouseX,
                mouseY
            );
            rowY += BUTTON_HEIGHT + ROW_GAP;
        }

        // Pull the current effective transform — fall back to IDENTITY when nothing is set (so inputs show
        // 0/0/0/1/1/1/0/0/0 rather than blanking out).
        var current = readEffective(session);
        syncVec(txInput, tyInput, tzInput, current.translation());
        syncVec(rxInput, ryInput, rzInput, current.rotation());
        syncVec(sxInput, syInput, szInput, current.scale());
        syncVec(pxInput, pyInput, pzInput, current.pivot());

        rowY = renderVecSection(graphics, font, x, rowY, width, "Translation", txInput, tyInput, tzInput, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Rotation (deg)", rxInput, ryInput, rzInput, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Scale", sxInput, syInput, szInput, mouseX, mouseY);
        rowY = renderVecSection(graphics, font, x, rowY, width, "Pivot", pxInput, pyInput, pzInput, mouseX, mouseY);

        // Pivot-viz row.
        pivotVizX = pickerX;
        pivotVizY = rowY;
        pivotVizW = pickerW;
        pivotVizH = BUTTON_HEIGHT;
        drawCheckboxRow(
            graphics,
            font,
            pivotVizX,
            pivotVizY,
            pivotVizW,
            "Pivot Viz",
            BLibItemTransformOverrides.isPivotVisualizationEnabled(),
            mouseX,
            mouseY
        );
        rowY += BUTTON_HEIGHT + ROW_GAP;

        // Read-only notice for Java-backed items: their transforms live in code, so auto-save has nowhere to go. The
        // inputs still let the user nudge values in-memory (good for one-off tuning), but the bytes can't be persisted
        // until the renderer is migrated to registerGeoBoneItemRendererFromAsset.
        if (assetConfigIdFor(session.itemId) == null) {
            graphics.drawString(
                font,
                Component.literal("Configured in Java — edits are session-only."),
                pickerX,
                rowY,
                LABEL_COLOR,
                false
            );
            rowY += font.lineHeight + ROW_GAP;
        }

        visibleInputs.add(txInput);
        visibleInputs.add(tyInput);
        visibleInputs.add(tzInput);
        visibleInputs.add(rxInput);
        visibleInputs.add(ryInput);
        visibleInputs.add(rzInput);
        visibleInputs.add(sxInput);
        visibleInputs.add(syInput);
        visibleInputs.add(szInput);
        visibleInputs.add(pxInput);
        visibleInputs.add(pyInput);
        visibleInputs.add(pzInput);

        return rowY;
    }

    /** Route click to the section's interactive widgets. Returns true when the click is consumed. */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (itemPicker.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (contextPicker.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var session = ModelerScene.get().itemSession;
        if (session != null && button == 0) {
            if (insideRect(mouseX, mouseY, modeIdleX, modeIdleY, modeButtonW, BUTTON_HEIGHT)) {
                session.mode = BLibItemTransformMode.IDLE;
                return true;
            }
            if (insideRect(mouseX, mouseY, modeBlockingX, modeBlockingY, modeButtonW, BUTTON_HEIGHT)) {
                session.mode = BLibItemTransformMode.BLOCKING;
                return true;
            }
            if (wallToggleVisible && insideRect(mouseX, mouseY, wallToggleX, wallToggleY, wallToggleW, wallToggleH)) {
                session.wallFixedActive = !session.wallFixedActive;
                return true;
            }
            if (insideRect(mouseX, mouseY, pivotVizX, pivotVizY, pivotVizW, pivotVizH)) {
                BLibItemTransformOverrides.setPivotVisualizationEnabled(!BLibItemTransformOverrides.isPivotVisualizationEnabled());
                return true;
            }
        }
        for (var input : visibleInputs) {
            if (input.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    private void onItemPicked(ResourceLocation itemId) {
        var scene = ModelerScene.get();
        var existing = scene.itemSession;
        if (existing != null && existing.itemId.equals(itemId)) {
            return;
        }
        scene.itemSession = new ModelerItemSession(itemId);
    }

    private void onContextPicked(ItemDisplayContext context) {
        var session = ModelerScene.get().itemSession;
        if (session == null) {
            return;
        }
        session.editingContext = context;
        // When swapping away from FIXED, the wall-fixed sub-mode no longer applies — drop it so the inputs
        // resolve to the regular per-context slot. Re-entering FIXED leaves it off until the user re-checks.
        if (context != ItemDisplayContext.FIXED) {
            session.wallFixedActive = false;
        }
    }

    /**
     * Resolve the asset-backed config id for {@code itemId}, or null when the item isn't asset-backed (Java
     * registration). Used both by auto-save (to pick a write target) and by the inspector's read-only notice (to decide
     * whether to show it).
     */
    private static @Nullable ResourceLocation assetConfigIdFor(ResourceLocation itemId) {
        var item = BuiltInRegistries.ITEM.get(itemId);

        if (item == null) {
            return null;
        }

        var renderer = AzItemRendererRegistry.getOrNull(item);

        if (!(renderer instanceof BLibGeoBoneItemRenderer geoRenderer)) {
            return null;
        }

        return geoRenderer.geoBoneItemConfig().assetConfigId();
    }

    /**
     * Fire after each commit: if the selected item is asset-backed and an engine project is active, serialize the full
     * current config (model/texture/bone from the live registry, idle+blocking transforms from the override registry)
     * and ship it to the server via {@link C2SWriteItemRendererConfigPayload}. The bytes land in
     * {@code <project>/resourcepack/assets/<ns>/blib/item_renderers/<id>.json}; the user picks them up on the next
     * "Reload Project" action.
     */
    private void triggerAutoSave(ResourceLocation itemId) {
        var configId = assetConfigIdFor(itemId);

        if (configId == null) {
            return;
        }

        var projectName = ProjectSession.activeProjectName();

        if (projectName == null || projectName.isEmpty()) {
            return;
        }

        var item = BuiltInRegistries.ITEM.get(itemId);

        if (item == null) {
            return;
        }

        var renderer = AzItemRendererRegistry.getOrNull(item);

        if (!(renderer instanceof BLibGeoBoneItemRenderer geoRenderer)) {
            return;
        }

        var config = geoRenderer.geoBoneItemConfig();
        var idleTransforms = buildEffectiveTransforms(itemId, BLibItemTransformMode.IDLE);
        var blockingTransforms = buildEffectiveTransforms(itemId, BLibItemTransformMode.BLOCKING);

        // Build a RawItemRendererConfig payload via the inner JSON shape — we don't use the codec on
        // RawItemRendererConfig directly because that class lives in internal/. Compose the JSON manually instead
        // so the inspector doesn't need to import internals.
        var jsonObject = new JsonObject();
        jsonObject.addProperty("model", config.geoModel().toString());
        jsonObject.addProperty("texture", config.texture().toString());
        jsonObject.addProperty("bone", config.boneName());
        var transformsObj = new JsonObject();

        if (hasAnyEntry(idleTransforms)) {
            transformsObj.add("idle", encodeTransforms(idleTransforms));
        }

        if (hasAnyEntry(blockingTransforms)) {
            transformsObj.add("blocking", encodeTransforms(blockingTransforms));
        }

        jsonObject.add("transforms", transformsObj);

        var jsonString = jsonObject.toString();
        BLib.MOD.networking().sendToServer(new C2SWriteItemRendererConfigPayload(projectName, configId, jsonString));
    }

    /**
     * Build a {@link BLibItemTransforms} containing only the entries that are actually authored — either via an
     * override (recent edit) or via the asset-backed base (already-persisted value). Empty entries are omitted so the
     * serialized JSON doesn't pin perspectives to identity that weren't explicitly set.
     */
    private static BLibItemTransforms buildEffectiveTransforms(ResourceLocation itemId, BLibItemTransformMode mode) {
        var builder = BLibItemTransforms.builder();

        for (var ctx : EDITABLE_CONTEXTS) {
            var t = BLibItemTransformOverrides.getModeValueOrNull(itemId, mode, ctx);

            if (t != null) {
                builder.set(ctx, t);
            }
        }

        var wall = BLibItemTransformOverrides.getWallEffectiveOrNull(itemId, mode);

        if (wall != null) {
            builder.fixedWall(wall);
        }

        return builder.build();
    }

    private static JsonElement encodeTransforms(BLibItemTransforms transforms) {
        var result = BLibItemTransforms.CODEC.encodeStart(JsonOps.INSTANCE, transforms);
        return result.result().orElse(new JsonObject());
    }

    private static boolean hasAnyEntry(BLibItemTransforms transforms) {
        for (var ctx : EDITABLE_CONTEXTS) {
            if (transforms.getOrNull(ctx) != null) {
                return true;
            }
        }

        return transforms.getFixedWallOrNull() != null;
    }

    private static BLibTransform readEffective(ModelerItemSession session) {
        var wall = session.wallFixedActive && session.editingContext == ItemDisplayContext.FIXED;
        if (wall) {
            return BLibItemTransformOverrides.getEffectiveWallFixed(session.itemId, session.mode);
        }
        return BLibItemTransformOverrides.getEffective(session.itemId, session.mode, session.editingContext);
    }

    private void commit(Field field, String text) {
        var parsed = parseFloat(text);
        if (parsed == null) {
            return;
        }
        var session = ModelerScene.get().itemSession;
        if (session == null) {
            return;
        }
        var current = readEffective(session);
        var tx = current.translation().x;
        var ty = current.translation().y;
        var tz = current.translation().z;
        var rx = current.rotation().x;
        var ry = current.rotation().y;
        var rz = current.rotation().z;
        var sx = current.scale().x;
        var sy = current.scale().y;
        var sz = current.scale().z;
        var px = current.pivot().x;
        var py = current.pivot().y;
        var pz = current.pivot().z;
        switch (field) {
            case TX -> tx = parsed;
            case TY -> ty = parsed;
            case TZ -> tz = parsed;
            case RX -> rx = parsed;
            case RY -> ry = parsed;
            case RZ -> rz = parsed;
            case SX -> sx = parsed;
            case SY -> sy = parsed;
            case SZ -> sz = parsed;
            case PX -> px = parsed;
            case PY -> py = parsed;
            case PZ -> pz = parsed;
        }
        var updated = new BLibTransform(
            new Vector3f(tx, ty, tz),
            new Vector3f(rx, ry, rz),
            new Vector3f(sx, sy, sz),
            new Vector3f(px, py, pz)
        );
        var wall = session.wallFixedActive && session.editingContext == ItemDisplayContext.FIXED;
        var before = ModelerAction.ItemTransformMemento.of(current);
        var after = ModelerAction.ItemTransformMemento.of(updated);
        if (wall) {
            BLibItemTransformOverrides.setWallFixed(session.itemId, session.mode, updated);
        } else {
            BLibItemTransformOverrides.set(session.itemId, session.mode, session.editingContext, updated);
        }
        // Inspector commits fire on Enter / focus-loss even when the value matches what's already there (user
        // clicks in / out without typing). Skip the no-op push so the action stack doesn't fill with empty edits.
        if (after.differsFrom(before)) {
            var typeId = wall ? "item_transform_inspector_wall" : "item_transform_inspector";
            var description = "Edit " + session.itemId + " (" + fieldLabel(field) + ")";
            ModelerActionHistory.push(
                new ModelerAction.ItemTransformMementoAction(
                    typeId,
                    description,
                    System.currentTimeMillis(),
                    session.itemId,
                    session.mode,
                    session.editingContext,
                    wall,
                    before,
                    after
                )
            );
            triggerAutoSave(session.itemId);
        }
    }

    private static String fieldLabel(Field field) {
        return switch (field) {
            case TX, TY, TZ -> "translation";
            case RX, RY, RZ -> "rotation";
            case SX, SY, SZ -> "scale";
            case PX, PY, PZ -> "pivot";
        };
    }

    // === Layout helpers (mirror ModelerInspectorPanel) ===

    private int renderVecSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        TextInput xIn,
        TextInput yIn,
        TextInput zIn,
        int mouseX,
        int mouseY
    ) {
        var rowY = drawSectionHeader(graphics, font, x, y, width, label);
        rowY += CONTENT_PADDING / 2;
        var inputsStart = x + CONTENT_PADDING;
        var available = Math.max(0, width - 2 * CONTENT_PADDING - 2 * INPUT_GAP);
        var perInput = Math.max(24, available / 3);
        var xX = inputsStart;
        var yX = inputsStart + perInput + INPUT_GAP;
        var zX = inputsStart + 2 * (perInput + INPUT_GAP);
        xIn.render(graphics, xX, rowY, perInput, mouseX, mouseY);
        yIn.render(graphics, yX, rowY, perInput, mouseX, mouseY);
        zIn.render(graphics, zX, rowY, perInput, mouseX, mouseY);
        drawAxisCorner(graphics, xX, rowY, perInput, AXIS_COLORS[0]);
        drawAxisCorner(graphics, yX, rowY, perInput, AXIS_COLORS[1]);
        drawAxisCorner(graphics, zX, rowY, perInput, AXIS_COLORS[2]);
        return rowY + TextInput.HEIGHT + ROW_GAP;
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        graphics.drawString(
            font,
            Component.literal(label),
            x + CONTENT_PADDING,
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            SECTION_HEADER_TEXT_COLOR,
            false
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    private static void drawAxisCorner(GuiGraphics graphics, int inputX, int inputY, int inputWidth, int color) {
        var rightEdge = inputX + inputWidth - 1;
        var topY = inputY;
        for (var i = 0; i < AXIS_CORNER_SIZE; i++) {
            var rowWidth = AXIS_CORNER_SIZE - i;
            var rowTop = topY + i;
            graphics.fill(rightEdge + 1 - rowWidth, rowTop, rightEdge + 1, rowTop + 1, color);
        }
    }

    private static void drawToggleButton(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        boolean active,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
        int bg = active ? BUTTON_ACTIVE_BG : (hovered ? BUTTON_HOVER_BG : BUTTON_BG);
        graphics.fill(x, y, x + width, y + BUTTON_HEIGHT, bg);
        drawBorder(graphics, x, y, width, BUTTON_HEIGHT);
        var labelWidth = font.width(label);
        graphics.drawString(
            font,
            Component.literal(label),
            x + (width - labelWidth) / 2,
            y + (BUTTON_HEIGHT - font.lineHeight + 2) / 2,
            BUTTON_TEXT,
            false
        );
    }

    private static void drawCheckboxRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        boolean checked,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
        graphics.fill(x, y, x + width, y + BUTTON_HEIGHT, hovered ? BUTTON_HOVER_BG : BUTTON_BG);
        drawBorder(graphics, x, y, width, BUTTON_HEIGHT);
        var boxX = x + 3;
        var boxY = y + (BUTTON_HEIGHT - CHECKBOX_SIZE) / 2;
        graphics.fill(boxX, boxY, boxX + CHECKBOX_SIZE, boxY + CHECKBOX_SIZE, 0xFF101014);
        graphics.fill(boxX, boxY, boxX + CHECKBOX_SIZE, boxY + 1, BUTTON_BORDER);
        graphics.fill(boxX, boxY + CHECKBOX_SIZE - 1, boxX + CHECKBOX_SIZE, boxY + CHECKBOX_SIZE, BUTTON_BORDER);
        graphics.fill(boxX, boxY, boxX + 1, boxY + CHECKBOX_SIZE, BUTTON_BORDER);
        graphics.fill(boxX + CHECKBOX_SIZE - 1, boxY, boxX + CHECKBOX_SIZE, boxY + CHECKBOX_SIZE, BUTTON_BORDER);
        if (checked) {
            graphics.fill(boxX + 2, boxY + 2, boxX + CHECKBOX_SIZE - 2, boxY + CHECKBOX_SIZE - 2, CHECKBOX_FILL);
        }
        graphics.drawString(
            font,
            Component.literal(label),
            boxX + CHECKBOX_SIZE + 4,
            y + (BUTTON_HEIGHT - font.lineHeight + 2) / 2,
            BUTTON_TEXT,
            false
        );
    }

    private static void drawBorder(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + 1, BUTTON_BORDER);
        graphics.fill(x, y + height - 1, x + width, y + height, BUTTON_BORDER);
        graphics.fill(x, y, x + 1, y + height, BUTTON_BORDER);
        graphics.fill(x + width - 1, y, x + width, y + height, BUTTON_BORDER);
    }

    private static boolean insideRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static void syncVec(TextInput xIn, TextInput yIn, TextInput zIn, Vector3f v) {
        syncInput(xIn, formatFloat(v.x));
        syncInput(yIn, formatFloat(v.y));
        syncInput(zIn, formatFloat(v.z));
    }

    private static void syncInput(TextInput input, String value) {
        if (input.isFocused()) {
            return;
        }
        if (!input.content().equals(value)) {
            input.setContent(value);
        }
    }

    private static String formatFloat(float v) {
        return String.format(Locale.ROOT, "%.4f", v);
    }

    private static @Nullable Float parseFloat(String text) {
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String contextLabel(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> "GUI";
            case GROUND -> "Ground";
            case FIXED -> "Fixed (Item Frame)";
            case HEAD -> "Head";
            case FIRST_PERSON_LEFT_HAND -> "First Person — Left Hand";
            case FIRST_PERSON_RIGHT_HAND -> "First Person — Right Hand";
            case THIRD_PERSON_LEFT_HAND -> "Third Person — Left Hand";
            case THIRD_PERSON_RIGHT_HAND -> "Third Person — Right Hand";
            case NONE -> "None";
        };
    }

    private enum Field {
        TX,
        TY,
        TZ,
        RX,
        RY,
        RZ,
        SX,
        SY,
        SZ,
        PX,
        PY,
        PZ
    }
}
