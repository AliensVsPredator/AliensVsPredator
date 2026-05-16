package com.blib.engine.ui.panel.recipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

import com.blib.engine.recipe.RecipeAuthoringState;
import com.blib.engine.recipe.RecipeAuthoringState.DraftSlot;
import com.blib.engine.recipe.RecipeAuthoringState.SlotRef;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.TextInput;

@ApiStatus.Internal
public final class RecipeEditorPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF18181C;

    private static final int WORKBENCH_BG = 0xFFC6C6C6;

    private static final int WORKBENCH_SHADOW = 0xFF555555;

    private static final int WORKBENCH_HIGHLIGHT = 0xFFFFFFFF;

    private static final int SLOT_BG = 0xFF8B8B8B;

    private static final int SLOT_INNER = 0xFFCFCFCF;

    private static final int SLOT_SELECTED = 0xFFE6C26B;

    private static final int ARROW_COLOR = 0xFF6D6D6D;

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

    private static final int GUI_WIDTH = 176;

    private static final int GUI_HEIGHT = 96;

    private static final int SLOT_SIZE = 18;

    private final TextInput recipeIdInput = new TextInput("namespace:path");

    private final TextInput countInput = new TextInput("Count", this::commitCount, this::resetCountInput);

    private final UiRect[] gridRects = new UiRect[9];

    private @Nullable UiRect outputRect;

    private @Nullable UiRect newRect;

    private @Nullable UiRect saveRect;

    private @Nullable Component hoveredTooltip;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

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
        resetCountInput();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        this.hoveredTooltip = null;
        Arrays.fill(gridRects, null);
        outputRect = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        syncRecipeIdInput();
        syncCountInput();

        var font = EngineFont.get();
        var topY = y + CONTENT_PADDING;
        var buttonGap = 4;
        newRect = UiRect.of(x + width - CONTENT_PADDING - BUTTON_WIDTH * 2 - buttonGap, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        saveRect = UiRect.of(x + width - CONTENT_PADDING - BUTTON_WIDTH, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        var inputW = Math.max(0, newRect.x() - (x + CONTENT_PADDING) - buttonGap);
        recipeIdInput.render(graphics, x + CONTENT_PADDING, topY, inputW, mouseX, mouseY);
        renderButton(graphics, newRect, "New", mouseX, mouseY, BUTTON_TEXT, true);
        renderButton(graphics, saveRect, RecipeAuthoringState.dirty() ? "Save*" : "Save", mouseX, mouseY, BUTTON_TEXT, true);

        var workbenchX = x + Math.max(CONTENT_PADDING, (width - GUI_WIDTH) / 2);
        var workbenchY = topY + TextInput.HEIGHT + 18;
        renderWorkbench(graphics, workbenchX, workbenchY, mouseX, mouseY);

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

    private void renderWorkbench(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.fill(x, y, x + GUI_WIDTH, y + GUI_HEIGHT, WORKBENCH_BG);
        graphics.fill(x, y, x + GUI_WIDTH, y + 1, WORKBENCH_HIGHLIGHT);
        graphics.fill(x, y, x + 1, y + GUI_HEIGHT, WORKBENCH_HIGHLIGHT);
        graphics.fill(x, y + GUI_HEIGHT - 1, x + GUI_WIDTH, y + GUI_HEIGHT, WORKBENCH_SHADOW);
        graphics.fill(x + GUI_WIDTH - 1, y, x + GUI_WIDTH, y + GUI_HEIGHT, WORKBENCH_SHADOW);

        var gridX = x + 30;
        var gridY = y + 20;
        for (var row = 0; row < 3; row++) {
            for (var col = 0; col < 3; col++) {
                var index = row * 3 + col;
                var rect = UiRect.of(gridX + col * SLOT_SIZE, gridY + row * SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
                gridRects[index] = rect;
                renderSlot(graphics, rect, RecipeAuthoringState.gridSlot(index), SlotRef.input(index), mouseX, mouseY);
            }
        }

        renderArrow(graphics, x + 92, y + 41);

        outputRect = UiRect.of(x + 124, y + 38, SLOT_SIZE, SLOT_SIZE);
        renderSlot(graphics, outputRect, RecipeAuthoringState.outputSlot(), SlotRef.output(), mouseX, mouseY);
    }

    private void renderSlot(GuiGraphics graphics, UiRect rect, DraftSlot slot, SlotRef ref, int mouseX, int mouseY) {
        var selected = ref.equals(RecipeAuthoringState.selectedSlot());
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), selected ? SLOT_SELECTED : SLOT_BG);
        graphics.fill(rect.x() + 1, rect.y() + 1, rect.right() - 1, rect.bottom() - 1, SLOT_INNER);

        if (!slot.isEmpty()) {
            var stack = slot.toStack();
            graphics.renderItem(stack, rect.x() + 1, rect.y() + 1);
            graphics.renderItemDecorations(EngineFont.get(), stack, rect.x() + 1, rect.y() + 1);
        }

        if (rect.contains(mouseX, mouseY)) {
            graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), 0x30FFFFFF);
            hoveredTooltip = slot.isEmpty()
                ? Component.literal(ref.kind() == RecipeAuthoringState.SlotKind.OUTPUT ? "Output slot" : "Ingredient slot")
                : Component.literal(slot.toStack().getHoverName().getString() + "\nID: " + slot.itemId() + "\nCount: " + slot.count());
        }
    }

    private static void renderArrow(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y + 4, x + 24, y + 8, ARROW_COLOR);
        graphics.fill(x + 18, y, x + 22, y + 12, ARROW_COLOR);
        graphics.fill(x + 22, y + 2, x + 26, y + 10, ARROW_COLOR);
    }

    private void renderSelectedSlotDetails(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var ref = RecipeAuthoringState.selectedSlot();
        if (ref == null) {
            UiText.drawClipped(graphics, font, "Select a slot to edit its count.", x, y, width, MUTED_TEXT);
            return;
        }

        var slot = RecipeAuthoringState.slot(ref);
        var label = ref.kind() == RecipeAuthoringState.SlotKind.OUTPUT ? "Output" : "Ingredient " + (ref.index() + 1);
        UiText.drawClipped(graphics, font, label, x, y, width, TEXT_COLOR);
        y += font.lineHeight + 5;

        if (slot.isEmpty()) {
            UiText.drawClipped(graphics, font, "(empty)", x, y, width, MUTED_TEXT);
            return;
        }

        var countW = Math.min(74, Math.max(44, width / 4));
        var labelW = Math.min(48, Math.max(0, width - countW - 6));
        UiText.drawClipped(graphics, font, "Count", x, y + (TextInput.HEIGHT - font.lineHeight + 2) / 2, labelW, MUTED_TEXT);
        countInput.render(graphics, x + labelW + 6, y, countW, mouseX, mouseY);
        var itemX = x + labelW + countW + 14;
        UiText.drawClipped(graphics, font, slot.itemId().toString(), itemX, y + (TextInput.HEIGHT - font.lineHeight + 2) / 2, Math.max(0, x + width - itemX), MUTED_TEXT);
    }

    private static void renderButton(GuiGraphics graphics, UiRect rect, String label, int mouseX, int mouseY, int textColor, boolean enabled) {
        var hovered = enabled && rect.contains(mouseX, mouseY);
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, BUTTON_BORDER);
        graphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), BUTTON_BORDER);
        graphics.fill(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), BUTTON_BORDER);
        graphics.fill(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), BUTTON_BORDER);
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
        if (recipeIdInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (countInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && newRect != null && newRect.contains(mouseX, mouseY)) {
            RecipeAuthoringState.newDraft();
            syncRecipeIdInput();
            resetCountInput();
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
            RecipeAuthoringState.clearSlot(target);
            resetCountInput();
            return true;
        }
        if (button == 0) {
            if (RecipeAuthoringState.hasDrag()) {
                RecipeAuthoringState.dropDraggedAt(mouseX, mouseY);
                RecipeAuthoringState.clearDrag();
            } else {
                RecipeAuthoringState.selectSlot(target);
            }
            resetCountInput();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && RecipeAuthoringState.hasDrag()) {
            RecipeAuthoringState.dropDraggedAt(mouseX, mouseY);
            RecipeAuthoringState.clearDrag();
            resetCountInput();
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
        resetCountInput();
        return true;
    }

    private void commitVisibleInputs() {
        RecipeAuthoringState.setRecipeIdText(recipeIdInput.content());
        var ref = RecipeAuthoringState.selectedSlot();
        if (ref != null) {
            commitCount(countInput.content());
        }
    }

    private void commitCount(String value) {
        var ref = RecipeAuthoringState.selectedSlot();
        if (ref == null) {
            return;
        }
        try {
            RecipeAuthoringState.setCount(ref, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
            resetCountInput();
        }
    }

    private void resetCountInput() {
        var ref = RecipeAuthoringState.selectedSlot();
        if (ref == null || RecipeAuthoringState.slot(ref).isEmpty()) {
            countInput.setContent("");
            return;
        }
        countInput.setContent(Integer.toString(RecipeAuthoringState.slot(ref).count()));
    }

    private void syncRecipeIdInput() {
        if (!recipeIdInput.isFocused() && !recipeIdInput.content().equals(RecipeAuthoringState.recipeIdText())) {
            recipeIdInput.setContent(RecipeAuthoringState.recipeIdText());
        }
    }

    private void syncCountInput() {
        if (!countInput.isFocused()) {
            resetCountInput();
        }
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + rectHeight;
    }
}
