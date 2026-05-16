package com.blib.engine.ui.panel.recipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blib.engine.recipe.RecipeAuthoringState;
import com.blib.engine.recipe.RecipeAuthoringState.DraftSlot;
import com.blib.engine.recipe.RecipeAuthoringState.RecipeDraftType;
import com.blib.engine.recipe.RecipeAuthoringState.SlotRef;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class RecipeEditorPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF18181C;

    private static final int SLOT_SELECTED = 0xFFE6C26B;

    private static final int BUTTON_BG = 0xFF25252C;

    private static final int BUTTON_BG_HOVER = 0xFF34343C;

    private static final int BUTTON_BORDER = 0xFF3D3D45;

    private static final int BUTTON_TEXT = 0xFFE6E6E6;

    private static final int BUTTON_DISABLED_TEXT = 0xFF686872;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int MUTED_TEXT = 0xFF8C8C96;

    private static final int ERROR_TEXT = 0xFFE08A8A;

    private static final int CONTENT_PADDING = 8;

    private static final int BUTTON_WIDTH = 52;

    private static final int BUTTON_HEIGHT = TextInput.HEIGHT;

    private static final int TYPE_BUTTON_HEIGHT = 16;

    private static final int TYPE_BUTTON_GAP = 4;

    private static final int GUI_WIDTH = 176;

    private static final int GUI_HEIGHT = 83;

    private static final int SLOT_SIZE = 18;

    private static final int VANILLA_TEXTURE_SIZE = 256;

    private static final long DOUBLE_CLICK_MS = 350L;

    private static final int COUNT_EDITOR_WIDTH = 24;

    private static final int COUNT_STEPPER_WIDTH = 14;

    private static final int COUNT_STEPPER_HEIGHT = 5;

    private static final ResourceLocation CRAFTING_TABLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/crafting_table.png"
    );

    private static final ResourceLocation FURNACE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/furnace.png"
    );

    private static final ResourceLocation BLAST_FURNACE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/blast_furnace.png"
    );

    private static final ResourceLocation SMOKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/smoker.png"
    );

    private static final ResourceLocation STONECUTTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/stonecutter.png"
    );

    private static final ResourceLocation SMITHING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/smithing.png"
    );

    private final TextInput recipeIdInput = new TextInput("namespace:path");

    private final TextInput inlineCountInput = new TextInput("Count", this::commitInlineCount, this::cancelInlineCountEdit);

    private final TextInput experienceInput = new TextInput("XP", this::commitExperience, this::resetMetaInputs);

    private final TextInput cookingTimeInput = new TextInput("Ticks", this::commitCookingTime, this::resetMetaInputs);

    private final @Nullable PanelMenuOpener panelMenuOpener;

    private final UiRect[] gridRects = new UiRect[9];

    private final List<TypeHit> typeHits = new ArrayList<>();

    private @Nullable UiRect outputRect;

    private @Nullable UiRect newRect;

    private @Nullable UiRect saveRect;

    private @Nullable UiRect incrementCountRect;

    private @Nullable UiRect decrementCountRect;

    private @Nullable SlotRef editingCountSlot;

    private @Nullable SlotRef lastClickedSlot;

    private @Nullable Component hoveredTooltip;

    private long lastSlotClickMs;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    public RecipeEditorPanel() {
        this(null);
    }

    public RecipeEditorPanel(@Nullable PanelMenuOpener panelMenuOpener) {
        this.panelMenuOpener = panelMenuOpener;
    }

    @Override
    public String title() {
        return "Recipe Editor";
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void onShown() {
        syncRecipeIdInput();
        resetInlineCountInput();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        this.hoveredTooltip = null;
        typeHits.clear();
        Arrays.fill(gridRects, null);
        outputRect = null;
        incrementCountRect = null;
        decrementCountRect = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        syncRecipeIdInput();
        syncInlineCountInput();

        var font = EngineFont.get();
        var topY = y + CONTENT_PADDING;
        var buttonGap = 4;
        newRect = UiRect.of(x + width - CONTENT_PADDING - BUTTON_WIDTH * 2 - buttonGap, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        saveRect = UiRect.of(x + width - CONTENT_PADDING - BUTTON_WIDTH, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        var inputW = Math.max(0, newRect.x() - (x + CONTENT_PADDING) - buttonGap);
        recipeIdInput.render(graphics, x + CONTENT_PADDING, topY, inputW, mouseX, mouseY);
        renderButton(graphics, newRect, "New", mouseX, mouseY, BUTTON_TEXT, true);
        renderButton(graphics, saveRect, RecipeAuthoringState.dirty() ? "Save*" : "Save", mouseX, mouseY, BUTTON_TEXT, true);

        var typeY = topY + TextInput.HEIGHT + 6;
        var typeH = renderTypeButtons(graphics, x + CONTENT_PADDING, typeY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var workbenchX = x + Math.max(CONTENT_PADDING, (width - GUI_WIDTH) / 2);
        var workbenchY = typeY + typeH + 10;
        renderRecipeSurface(graphics, workbenchX, workbenchY, mouseX, mouseY);

        var detailsY = workbenchY + GUI_HEIGHT + 12;
        renderSelectedSlotDetails(graphics, x + CONTENT_PADDING, detailsY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var status = RecipeAuthoringState.status();
        var statusColor = status.startsWith("Invalid") || status.startsWith("Open") || status.startsWith("Choose") || status.startsWith("Add")
            ? ERROR_TEXT
            : MUTED_TEXT;
        UiText.drawClipped(graphics, font, status, x + CONTENT_PADDING, y + height - CONTENT_PADDING - font.lineHeight, width - 2 * CONTENT_PADDING, statusColor);

        RecipeAuthoringState.publishEditorSlots(gridRects, outputRect == null ? UiRect.of(0, 0, 0, 0) : outputRect);
        renderDraggedStack(graphics, mouseX, mouseY);
    }

    private int renderTypeButtons(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var cursorX = x;
        var cursorY = y;
        var maxRight = x + width;
        for (var type : RecipeDraftType.authorableTypes()) {
            var buttonW = Math.max(42, font.width(type.label()) + 12);
            if (cursorX > x && cursorX + buttonW > maxRight) {
                cursorX = x;
                cursorY += TYPE_BUTTON_HEIGHT + TYPE_BUTTON_GAP;
            }
            var rect = UiRect.of(cursorX, cursorY, Math.min(buttonW, Math.max(0, maxRight - cursorX)), TYPE_BUTTON_HEIGHT);
            renderButton(graphics, rect, type.label(), mouseX, mouseY, BUTTON_TEXT, true, type == RecipeAuthoringState.recipeType());
            typeHits.add(new TypeHit(rect, type));
            cursorX += buttonW + TYPE_BUTTON_GAP;
        }
        return cursorY - y + TYPE_BUTTON_HEIGHT;
    }

    private void renderRecipeSurface(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        switch (RecipeAuthoringState.recipeType()) {
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS -> renderWorkbench(graphics, x, y, mouseX, mouseY);
            case SMELTING, BLASTING, SMOKING -> renderCooking(graphics, x, y, cookingTexture(), mouseX, mouseY);
            case CAMPFIRE_COOKING -> renderCampfireCooking(graphics, x, y, mouseX, mouseY);
            case STONECUTTING -> renderStonecutting(graphics, x, y, mouseX, mouseY);
            case SMITHING_TRANSFORM, SMITHING_TRIM -> renderSmithing(graphics, x, y, mouseX, mouseY);
            case UNSUPPORTED -> renderUnsupported(graphics, x, y);
        }
    }

    private void renderWorkbench(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(CRAFTING_TABLE_TEXTURE, x, y, 0.0F, 0.0F, GUI_WIDTH, GUI_HEIGHT, VANILLA_TEXTURE_SIZE, VANILLA_TEXTURE_SIZE);

        var gridX = x + 30;
        var gridY = y + 17;
        for (var row = 0; row < 3; row++) {
            for (var col = 0; col < 3; col++) {
                var index = row * 3 + col;
                var rect = UiRect.of(gridX + col * SLOT_SIZE, gridY + row * SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
                gridRects[index] = rect;
                renderSlot(graphics, rect, RecipeAuthoringState.gridSlot(index), SlotRef.input(index), mouseX, mouseY);
            }
        }

        outputRect = UiRect.of(x + 124, y + 35, SLOT_SIZE, SLOT_SIZE);
        renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
    }

    private void renderCooking(GuiGraphics graphics, int x, int y, ResourceLocation texture, int mouseX, int mouseY) {
        graphics.blit(texture, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        var inputRect = UiRect.of(x + 56, y + 17, SLOT_SIZE, SLOT_SIZE);
        gridRects[0] = inputRect;
        renderSlot(graphics, inputRect, RecipeAuthoringState.gridSlot(0), SlotRef.input(0), mouseX, mouseY);
        outputRect = UiRect.of(x + 116, y + 35, SLOT_SIZE, SLOT_SIZE);
        renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
    }

    private void renderCampfireCooking(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        renderPlainRecipeSurface(graphics, x, y, "Campfire Cooking");
        var inputRect = UiRect.of(x + 46, y + 32, SLOT_SIZE, SLOT_SIZE);
        gridRects[0] = inputRect;
        renderSlotFrame(graphics, inputRect);
        renderSlot(graphics, inputRect, RecipeAuthoringState.gridSlot(0), SlotRef.input(0), mouseX, mouseY);
        renderArrow(graphics, x + 78, y + 37);
        outputRect = UiRect.of(x + 112, y + 32, SLOT_SIZE, SLOT_SIZE);
        renderSlotFrame(graphics, outputRect);
        renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
    }

    private void renderStonecutting(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(STONECUTTER_TEXTURE, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        var inputRect = UiRect.of(x + 20, y + 33, SLOT_SIZE, SLOT_SIZE);
        gridRects[0] = inputRect;
        renderSlot(graphics, inputRect, RecipeAuthoringState.gridSlot(0), SlotRef.input(0), mouseX, mouseY);
        outputRect = UiRect.of(x + 143, y + 33, SLOT_SIZE, SLOT_SIZE);
        renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
    }

    private void renderSmithing(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(SMITHING_TEXTURE, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        var template = UiRect.of(x + 8, y + 48, SLOT_SIZE, SLOT_SIZE);
        var base = UiRect.of(x + 26, y + 48, SLOT_SIZE, SLOT_SIZE);
        var addition = UiRect.of(x + 44, y + 48, SLOT_SIZE, SLOT_SIZE);
        gridRects[0] = template;
        gridRects[1] = base;
        gridRects[2] = addition;
        renderSlot(graphics, template, RecipeAuthoringState.gridSlot(0), SlotRef.input(0), mouseX, mouseY);
        renderSlot(graphics, base, RecipeAuthoringState.gridSlot(1), SlotRef.input(1), mouseX, mouseY);
        renderSlot(graphics, addition, RecipeAuthoringState.gridSlot(2), SlotRef.input(2), mouseX, mouseY);
        if (RecipeAuthoringState.recipeType() == RecipeDraftType.SMITHING_TRANSFORM) {
            outputRect = UiRect.of(x + 98, y + 48, SLOT_SIZE, SLOT_SIZE);
            renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
        } else {
            outputRect = null;
        }
    }

    private void renderUnsupported(GuiGraphics graphics, int x, int y) {
        renderPlainRecipeSurface(graphics, x, y, "Read-only recipe");
        var font = EngineFont.get();
        UiText.drawWrappedCentered(
            graphics,
            font,
            "This recipe serializer is not editable yet.",
            UiRect.of(x + 12, y + 28, GUI_WIDTH - 24, 34),
            MUTED_TEXT
        );
    }

    private static ResourceLocation cookingTexture() {
        return switch (RecipeAuthoringState.recipeType()) {
            case BLASTING -> BLAST_FURNACE_TEXTURE;
            case SMOKING -> SMOKER_TEXTURE;
            default -> FURNACE_TEXTURE;
        };
    }

    private void renderSlot(GuiGraphics graphics, UiRect rect, DraftSlot slot, SlotRef ref, int mouseX, int mouseY) {
        var selected = ref.equals(RecipeAuthoringState.selectedSlot());

        if (!slot.isEmpty()) {
            var stack = slot.toStack();
            graphics.renderItem(stack, rect.x(), rect.y());
            if (!isEditingCount(ref)) {
                graphics.renderItemDecorations(EngineFont.get(), stack, rect.x(), rect.y());
            }
        }

        if (selected) {
            graphics.renderOutline(rect.x(), rect.y(), rect.width(), rect.height(), SLOT_SELECTED);
        }

        if (!slot.isEmpty() && isEditingCount(ref)) {
            renderInlineCountEditor(graphics, rect, mouseX, mouseY);
        }

        if (rect.contains(mouseX, mouseY)) {
            graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), 0x30FFFFFF);
            hoveredTooltip = slot.isEmpty()
                ? Component.literal(ref.kind() == RecipeAuthoringState.SlotKind.OUTPUT ? "Output slot" : "Ingredient slot")
                : Component.literal(slot.toStack().getHoverName().getString() + "\nID: " + slot.itemId() + "\nCount: " + slot.count());
        }
    }

    private static void renderPlainRecipeSurface(GuiGraphics graphics, int x, int y, String title) {
        graphics.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFFC6C6C6);
        graphics.fill(x, y, x + GUI_WIDTH, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + GUI_HEIGHT, 0xFFFFFFFF);
        graphics.fill(x, y + GUI_HEIGHT - 1, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF555555);
        graphics.fill(x + GUI_WIDTH - 1, y, x + GUI_WIDTH, y + GUI_HEIGHT, 0xFF555555);
        UiText.drawClipped(graphics, EngineFont.get(), title, x + 8, y + 7, GUI_WIDTH - 16, 0xFF404040);
    }

    private static void renderSlotFrame(GuiGraphics graphics, UiRect rect) {
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), 0xFF8B8B8B);
        graphics.fill(rect.x() + 1, rect.y() + 1, rect.right() - 1, rect.bottom() - 1, 0xFFCFCFCF);
    }

    private static void renderArrow(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y + 4, x + 24, y + 8, 0xFF6D6D6D);
        graphics.fill(x + 18, y, x + 22, y + 12, 0xFF6D6D6D);
        graphics.fill(x + 22, y + 2, x + 26, y + 10, 0xFF6D6D6D);
    }

    private void renderSelectedSlotDetails(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        var font = EngineFont.get();
        if (isCookingType()) {
            var timeW = Math.min(74, Math.max(44, width / 4));
            var xpW = Math.min(74, Math.max(44, width / 4));
            UiText.drawClipped(graphics, font, "XP", x, y + (TextInput.HEIGHT - font.lineHeight + 2) / 2, 18, MUTED_TEXT);
            experienceInput.render(graphics, x + 22, y, xpW, mouseX, mouseY);
            var timeX = x + 22 + xpW + 10;
            UiText.drawClipped(graphics, font, "Ticks", timeX, y + (TextInput.HEIGHT - font.lineHeight + 2) / 2, 34, MUTED_TEXT);
            cookingTimeInput.render(graphics, timeX + 38, y, timeW, mouseX, mouseY);
            y += TextInput.HEIGHT + 8;
        }

        var ref = RecipeAuthoringState.selectedSlot();
        if (ref == null) {
            UiText.drawClipped(graphics, font, "Select a slot.", x, y, width, MUTED_TEXT);
            return;
        }

        var slot = RecipeAuthoringState.slot(ref);
        var label = RecipeAuthoringState.slotLabel(ref);
        UiText.drawClipped(graphics, font, label, x, y, width, TEXT_COLOR);
        y += font.lineHeight + 5;

        if (slot.isEmpty()) {
            UiText.drawClipped(graphics, font, "(empty)", x, y, width, MUTED_TEXT);
            return;
        }

        UiText.drawClipped(graphics, font, slot.itemId().toString(), x, y, width, MUTED_TEXT);
    }

    private void renderInlineCountEditor(GuiGraphics graphics, UiRect slotRect, int mouseX, int mouseY) {
        var inputX = Math.max(rectX + 2, Math.min(slotRect.right() - COUNT_EDITOR_WIDTH + 1, rectX + rectWidth - COUNT_EDITOR_WIDTH - 2));
        var inputY = slotRect.y() + Math.max(0, (slotRect.height() - TextInput.HEIGHT) / 2);
        var inputRect = UiRect.of(inputX, inputY, COUNT_EDITOR_WIDTH, TextInput.HEIGHT);
        incrementCountRect = UiRect.of(
            inputRect.x() + Math.max(0, (inputRect.width() - COUNT_STEPPER_WIDTH) / 2),
            inputRect.y() - COUNT_STEPPER_HEIGHT - 1,
            COUNT_STEPPER_WIDTH,
            COUNT_STEPPER_HEIGHT
        );
        decrementCountRect = UiRect.of(
            inputRect.x() + Math.max(0, (inputRect.width() - COUNT_STEPPER_WIDTH) / 2),
            inputRect.bottom() + 1,
            COUNT_STEPPER_WIDTH,
            COUNT_STEPPER_HEIGHT
        );

        inlineCountInput.render(graphics, inputRect.x(), inputRect.y(), inputRect.width(), mouseX, mouseY);
        renderCountStepper(graphics, incrementCountRect, true, mouseX, mouseY);
        renderCountStepper(graphics, decrementCountRect, false, mouseX, mouseY);
    }

    private static void renderCountStepper(GuiGraphics graphics, UiRect rect, boolean up, int mouseX, int mouseY) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, BUTTON_BORDER);
        graphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), BUTTON_BORDER);
        graphics.fill(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), BUTTON_BORDER);
        graphics.fill(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), BUTTON_BORDER);
        drawArrowHead(graphics, rect, up);
    }

    private static void drawArrowHead(GuiGraphics graphics, UiRect rect, boolean up) {
        var color = BUTTON_TEXT;
        var cx = rect.x() + rect.width() / 2;
        if (up) {
            var baseY = rect.y() + rect.height() - 2;
            graphics.fill(cx, rect.y() + 1, cx + 1, rect.y() + 2, color);
            graphics.fill(cx - 1, rect.y() + 2, cx + 2, rect.y() + 3, color);
            graphics.fill(cx - 2, baseY - 1, cx + 3, baseY, color);
        } else {
            var topY = rect.y() + 1;
            graphics.fill(cx - 2, topY, cx + 3, topY + 1, color);
            graphics.fill(cx - 1, topY + 1, cx + 2, topY + 2, color);
            graphics.fill(cx, rect.bottom() - 2, cx + 1, rect.bottom() - 1, color);
        }
    }

    private static void renderButton(GuiGraphics graphics, UiRect rect, String label, int mouseX, int mouseY, int textColor, boolean enabled) {
        renderButton(graphics, rect, label, mouseX, mouseY, textColor, enabled, false);
    }

    private static void renderButton(
        GuiGraphics graphics,
        UiRect rect,
        String label,
        int mouseX,
        int mouseY,
        int textColor,
        boolean enabled,
        boolean active
    ) {
        var hovered = enabled && rect.contains(mouseX, mouseY);
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), active ? 0xFF3C3C46 : (hovered ? BUTTON_BG_HOVER : BUTTON_BG));
        var border = active ? SLOT_SELECTED : BUTTON_BORDER;
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, border);
        graphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), border);
        graphics.fill(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), border);
        graphics.fill(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), border);
        UiText.drawCentered(graphics, EngineFont.get(), label, rect.inset(0, 2, 0, 2), enabled ? textColor : BUTTON_DISABLED_TEXT);
    }

    private static void renderDraggedStack(GuiGraphics graphics, int mouseX, int mouseY) {
        var drag = RecipeAuthoringState.draggedStack();
        if (drag == null) {
            return;
        }
        var stack = drag.toStack();
        if (stack.isEmpty()) {
            return;
        }
        graphics.renderItem(stack, mouseX + 8, mouseY + 8);
        graphics.renderItemDecorations(EngineFont.get(), stack, mouseX + 8, mouseY + 8);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && incrementCountRect != null && incrementCountRect.contains(mouseX, mouseY)) {
            changeInlineCount(1);
            return true;
        }
        if (button == 0 && decrementCountRect != null && decrementCountRect.contains(mouseX, mouseY)) {
            changeInlineCount(-1);
            return true;
        }
        if (inlineCountInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        finishInlineCountEdit();

        if (recipeIdInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (experienceInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (cookingTimeInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            for (var hit : typeHits) {
                if (hit.rect().contains(mouseX, mouseY)) {
                    if (hit.type() != RecipeAuthoringState.recipeType()) {
                        RecipeAuthoringState.newDraft(hit.type());
                        syncRecipeIdInput();
                        resetInlineCountInput();
                        resetMetaInputs();
                    }
                    return true;
                }
            }
        }
        if (button == 0 && newRect != null && newRect.contains(mouseX, mouseY)) {
            RecipeAuthoringState.newDraft();
            syncRecipeIdInput();
            resetInlineCountInput();
            resetMetaInputs();
            return true;
        }
        if (button == 0 && saveRect != null && saveRect.contains(mouseX, mouseY)) {
            commitVisibleInputs();
            try {
                RecipeAuthoringState.saveToProject(recipeIdInput.content());
            } catch (IOException e) {
                RecipeAuthoringState.setStatus("Save failed: " + e.getMessage());
            }
            return true;
        }

        var target = RecipeAuthoringState.slotAt(mouseX, mouseY);
        if (target == null) {
            return isInside(mouseX, mouseY);
        }

        if (button == 1) {
            RecipeAuthoringState.selectSlot(target);
            openSlotContextMenu(mouseX, mouseY, target);
            return true;
        }
        if (button == 0) {
            if (RecipeAuthoringState.hasDrag()) {
                RecipeAuthoringState.dropDraggedAt(mouseX, mouseY);
                RecipeAuthoringState.clearDrag();
            } else {
                RecipeAuthoringState.selectSlot(target);
                if (isSlotDoubleClick(target) && !RecipeAuthoringState.slot(target).isEmpty()) {
                    beginInlineCountEdit(target);
                    return true;
                }
            }
            resetInlineCountInput();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && RecipeAuthoringState.hasDrag()) {
            RecipeAuthoringState.dropDraggedAt(mouseX, mouseY);
            RecipeAuthoringState.clearDrag();
            resetInlineCountInput();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var target = RecipeAuthoringState.slotAt(mouseX, mouseY);
        if (target == null) {
            return false;
        }
        RecipeAuthoringState.changeCount(target, scrollY > 0.0D ? 1 : -1);
        if (isEditingCount(target)) {
            resetInlineCountInput();
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode != GLFW.GLFW_KEY_DELETE) {
            return false;
        }
        var selected = RecipeAuthoringState.selectedSlot();
        if (selected == null || RecipeAuthoringState.slot(selected).isEmpty()) {
            return false;
        }
        RecipeAuthoringState.clearSlot(selected);
        cancelInlineCountEdit();
        return true;
    }

    private void commitVisibleInputs() {
        RecipeAuthoringState.setRecipeIdText(recipeIdInput.content());
        commitInlineCountIfEditing();
        if (isCookingType()) {
            commitExperience(experienceInput.content());
            commitCookingTime(cookingTimeInput.content());
        }
    }

    private void commitInlineCount(String value) {
        var ref = editingCountSlot;
        if (ref == null) {
            return;
        }
        try {
            RecipeAuthoringState.setCount(ref, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
            resetInlineCountInput();
        }
        cancelInlineCountEdit();
    }

    private void commitExperience(String value) {
        try {
            RecipeAuthoringState.setExperience(Float.parseFloat(value.trim()));
        } catch (NumberFormatException ignored) {
            resetMetaInputs();
        }
    }

    private void commitCookingTime(String value) {
        try {
            RecipeAuthoringState.setCookingTime(Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
            resetMetaInputs();
        }
    }

    private void resetInlineCountInput() {
        var ref = editingCountSlot;
        if (ref == null || RecipeAuthoringState.slot(ref).isEmpty()) {
            inlineCountInput.setContent("");
            return;
        }
        inlineCountInput.setContent(Integer.toString(RecipeAuthoringState.slot(ref).count()));
    }

    private void resetMetaInputs() {
        experienceInput.setContent(trimFloat(RecipeAuthoringState.experience()));
        cookingTimeInput.setContent(Integer.toString(RecipeAuthoringState.cookingTime()));
    }

    private void syncRecipeIdInput() {
        if (!recipeIdInput.isFocused() && !recipeIdInput.content().equals(RecipeAuthoringState.recipeIdText())) {
            recipeIdInput.setContent(RecipeAuthoringState.recipeIdText());
        }
    }

    private void syncInlineCountInput() {
        if (editingCountSlot != null && RecipeAuthoringState.slot(editingCountSlot).isEmpty()) {
            cancelInlineCountEdit();
        } else if (!inlineCountInput.isFocused()) {
            resetInlineCountInput();
        }
        if (!experienceInput.isFocused() && !cookingTimeInput.isFocused()) {
            resetMetaInputs();
        }
    }

    private boolean isEditingCount(SlotRef ref) {
        return ref.equals(editingCountSlot);
    }

    private void beginInlineCountEdit(SlotRef ref) {
        if (RecipeAuthoringState.slot(ref).isEmpty()) {
            cancelInlineCountEdit();
            return;
        }
        editingCountSlot = ref;
        resetInlineCountInput();
        inlineCountInput.focus();
        inlineCountInput.selectAll();
    }

    private void finishInlineCountEdit() {
        if (editingCountSlot == null) {
            return;
        }
        commitInlineCountIfEditing();
        cancelInlineCountEdit();
    }

    private void cancelInlineCountEdit() {
        editingCountSlot = null;
        incrementCountRect = null;
        decrementCountRect = null;
        inlineCountInput.setContent("");
        if (inlineCountInput.isFocused()) {
            TextInput.clearFocus();
        }
    }

    private void commitInlineCountIfEditing() {
        if (editingCountSlot != null) {
            commitInlineCount(inlineCountInput.content());
        }
    }

    private void changeInlineCount(int delta) {
        var ref = editingCountSlot;
        if (ref == null) {
            return;
        }
        RecipeAuthoringState.changeCount(ref, delta);
        resetInlineCountInput();
        inlineCountInput.focus();
        inlineCountInput.selectAll();
    }

    private boolean isSlotDoubleClick(SlotRef target) {
        var now = System.currentTimeMillis();
        var doubleClick = target.equals(lastClickedSlot) && now - lastSlotClickMs <= DOUBLE_CLICK_MS;
        lastClickedSlot = target;
        lastSlotClickMs = now;
        return doubleClick;
    }

    private void openSlotContextMenu(double mouseX, double mouseY, SlotRef target) {
        if (panelMenuOpener == null) {
            return;
        }
        var hasItem = !RecipeAuthoringState.slot(target).isEmpty();
        var delete = new DropdownMenu.Item(
            "Delete",
            () -> {
                RecipeAuthoringState.clearSlot(target);
                cancelInlineCountEdit();
            },
            hasItem,
            Component.literal("This slot is empty.")
        );
        panelMenuOpener.open(new DropdownMenu((int) mouseX, (int) mouseY, List.of(delete)));
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + rectHeight;
    }

    private static boolean isCookingType() {
        return switch (RecipeAuthoringState.recipeType()) {
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> true;
            default -> false;
        };
    }

    private static String trimFloat(float value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }
        return Float.toString(value);
    }

    private record TypeHit(UiRect rect, RecipeDraftType type) {}
}
