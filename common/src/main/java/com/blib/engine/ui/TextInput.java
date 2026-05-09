package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * Single-line text field with caret + selection + focus management. One {@code TextInput} can be focused at a time
 * across the whole workspace; the focused instance receives {@code charTyped} / {@code keyPressed} events forwarded by
 * {@link EngineWorkspaceScreen}. Click on the input rect → focus + caret positioned at click x. Click anywhere else →
 * focus cleared (the screen calls {@link #clearFocus()} before dispatching click events, so panels naturally re-focus
 * their own inputs if clicked).
 * <p>
 * Selection is a single contiguous range tracked via {@code selectionAnchor}:
 * <ul>
 * <li>Ctrl+A selects all.</li>
 * <li>Shift + arrow / Home / End extends the selection in the appropriate direction (anchored at the caret position
 * before extension started).</li>
 * <li>LMB-drag inside the input rect extends a selection from the original click position. The drag survives the
 * cursor leaving the input rect via a static {@link #getDragSelecting()} pointer routed by the workspace.</li>
 * <li>Backspace / Delete / character-typing replace the selection if any (otherwise act on the caret).</li>
 * <li>Ctrl+C / Ctrl+X copy / cut the selection to the OS clipboard. Ctrl+V pastes (newlines / tabs sanitized to
 * spaces since this is a single-line input). All three are no-ops when there's nothing useful to act on.</li>
 * <li>Any non-shift caret-movement key collapses the selection to the appropriate edge.</li>
 * </ul>
 */
@ApiStatus.Internal
public final class TextInput {

    public static final int HEIGHT = 13;

    private static final int BG_COLOR = 0xFF14141A;

    private static final int BG_FOCUSED_COLOR = 0xFF1A1A22;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int BORDER_FOCUSED_COLOR = 0xFF4F8FFF;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int PLACEHOLDER_COLOR = 0xFF606068;

    private static final int CARET_COLOR = 0xFFE0E0E0;

    private static final int SELECTION_COLOR = 0x803F6FBF;

    private static final int PADDING_X = 4;

    private static final int CARET_BLINK_PERIOD_MS = 1000;

    private static @Nullable TextInput focused;

    /**
     * The TextInput currently tracking an LMB drag for selection extension. Set in {@link #mouseClicked} when a click
     * lands inside the rect; cleared by {@link #endDragSelection()} on release. The workspace consults this in its
     * {@code mouseDragged} dispatch so drags survive the cursor leaving the input rect.
     */
    private static @Nullable TextInput dragSelecting;

    public static @Nullable TextInput getFocused() {
        return focused;
    }

    public static void clearFocus() {
        if (focused != null) {
            focused.focusedFlag = false;
            focused.clearSelection();
            focused = null;
        }
        dragSelecting = null;
    }

    public static @Nullable TextInput getDragSelecting() {
        return dragSelecting;
    }

    public static void endDragSelection() {
        dragSelecting = null;
    }

    private final String placeholder;

    private String content = "";

    private int caret;

    /**
     * The "other end" of the current selection, or {@code -1} when no selection is active. The selected range is
     * {@code [min(caret, anchor), max(caret, anchor))}; an anchor equal to caret is treated as no selection.
     */
    private int selectionAnchor = -1;

    private boolean focusedFlag;

    private int rectX;

    private int rectY;

    private int rectWidth;

    public TextInput(String placeholder) {
        this.placeholder = placeholder;
    }

    public String content() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.caret = Math.min(this.caret, content.length());
        clearSelection();
    }

    public boolean isFocused() {
        return focusedFlag;
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;

        var bg = focusedFlag ? BG_FOCUSED_COLOR : BG_COLOR;
        var border = focusedFlag ? BORDER_FOCUSED_COLOR : BORDER_COLOR;

        graphics.fill(x, y, x + width, y + HEIGHT, bg);
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + HEIGHT - 1, x + width, y + HEIGHT, border);
        graphics.fill(x, y, x + 1, y + HEIGHT, border);
        graphics.fill(x + width - 1, y, x + width, y + HEIGHT, border);

        var font = Minecraft.getInstance().font;
        // +2 compensates for MC font's descender padding so the placeholder/value text visually centers; see
        // MenuBarPanel for details.
        var textY = y + (HEIGHT - font.lineHeight + 2) / 2;
        var displayText = content.isEmpty() && !focusedFlag ? placeholder : content;
        var color = content.isEmpty() && !focusedFlag ? PLACEHOLDER_COLOR : TEXT_COLOR;

        // Selection highlight underlay — drawn before the text so glyphs sit on top.
        if (focusedFlag && hasSelection()) {
            var startX = x + PADDING_X + font.width(content.substring(0, selectionStart()));
            var endX = x + PADDING_X + font.width(content.substring(0, selectionEnd()));
            graphics.fill(startX, textY - 1, endX, textY + font.lineHeight, SELECTION_COLOR);
        }

        graphics.drawString(font, Component.literal(displayText), x + PADDING_X, textY, color, false);

        if (focusedFlag) {
            var blink = (System.currentTimeMillis() % CARET_BLINK_PERIOD_MS) < CARET_BLINK_PERIOD_MS / 2;
            if (blink) {
                var caretX = x + PADDING_X + font.width(content.substring(0, caret));
                graphics.fill(caretX, textY - 1, caretX + 1, textY + font.lineHeight, CARET_COLOR);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        var inRect = mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + HEIGHT;
        if (inRect) {
            if (focused != null && focused != this) {
                focused.focusedFlag = false;
                focused.clearSelection();
            }
            focused = this;
            this.focusedFlag = true;
            this.caret = caretIndexAt(mouseX);
            // Anchor at the click position so a subsequent drag extends the selection from here. If the user
            // releases without dragging, hasSelection() returns false (anchor == caret), so the click is just a
            // caret position and not a stale-empty-selection.
            this.selectionAnchor = this.caret;
            dragSelecting = this;
            return true;
        }
        return false;
    }

    /**
     * Extend the selection during an LMB drag by moving the caret to {@code mouseX} while the anchor stays where
     * the click landed. Called by {@link EngineWorkspaceScreen} via the static {@link #getDragSelecting()} pointer
     * so drags survive the cursor leaving the input rect.
     */
    public void mouseDraggedExtend(double mouseX) {
        if (!focusedFlag) {
            return;
        }
        this.caret = caretIndexAt(mouseX);
    }

    public boolean charTyped(char ch, int modifiers) {
        if (!focusedFlag) {
            return false;
        }
        if (ch < 32 || ch == 127) {
            return false;
        }
        if (hasSelection()) {
            deleteSelection();
        }
        content = content.substring(0, caret) + ch + content.substring(caret);
        caret++;
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focusedFlag) {
            return false;
        }

        // Clipboard / select-all shortcuts. All require pure Ctrl (no Shift, no Alt) so they don't fight with
        // future Ctrl+Shift combinations.
        if (Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_A -> {
                    if (!content.isEmpty()) {
                        selectionAnchor = 0;
                        caret = content.length();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_C -> {
                    copySelectionToClipboard();
                    return true;
                }
                case GLFW.GLFW_KEY_X -> {
                    if (hasSelection()) {
                        copySelectionToClipboard();
                        deleteSelection();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_V -> {
                    pasteFromClipboard();
                    return true;
                }
                default -> {
                    // Fall through to non-clipboard handling below.
                }
            }
        }

        var shift = Screen.hasShiftDown();

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (hasSelection()) {
                    deleteSelection();
                } else if (caret > 0) {
                    content = content.substring(0, caret - 1) + content.substring(caret);
                    caret--;
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (hasSelection()) {
                    deleteSelection();
                } else if (caret < content.length()) {
                    content = content.substring(0, caret) + content.substring(caret + 1);
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (shift) {
                    extendSelectionAnchorIfNeeded();
                    if (caret > 0) {
                        caret--;
                    }
                    collapseSelectionIfDegenerate();
                } else if (hasSelection()) {
                    caret = selectionStart();
                    clearSelection();
                } else if (caret > 0) {
                    caret--;
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (shift) {
                    extendSelectionAnchorIfNeeded();
                    if (caret < content.length()) {
                        caret++;
                    }
                    collapseSelectionIfDegenerate();
                } else if (hasSelection()) {
                    caret = selectionEnd();
                    clearSelection();
                } else if (caret < content.length()) {
                    caret++;
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                if (shift) {
                    extendSelectionAnchorIfNeeded();
                    caret = 0;
                    collapseSelectionIfDegenerate();
                } else {
                    caret = 0;
                    clearSelection();
                }
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                if (shift) {
                    extendSelectionAnchorIfNeeded();
                    caret = content.length();
                    collapseSelectionIfDegenerate();
                } else {
                    caret = content.length();
                    clearSelection();
                }
                return true;
            }
            case GLFW.GLFW_KEY_ESCAPE -> {
                this.focusedFlag = false;
                clearSelection();
                if (focused == this) {
                    focused = null;
                }
                if (dragSelecting == this) {
                    dragSelecting = null;
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Map an x-coordinate (in the same coord space as the input's rect) to a caret index in {@link #content}. Picks
     * whichever character boundary is closer to the cursor for natural click-to-position feel. Linear scan — fine
     * for short single-line content.
     */
    private int caretIndexAt(double mouseX) {
        var font = Minecraft.getInstance().font;
        var relX = mouseX - rectX - PADDING_X;
        if (relX <= 0) {
            return 0;
        }
        if (relX >= font.width(content)) {
            return content.length();
        }
        for (var i = 1; i <= content.length(); i++) {
            var w = font.width(content.substring(0, i));
            if (w >= relX) {
                var prevW = font.width(content.substring(0, i - 1));
                return (relX - prevW < w - relX) ? i - 1 : i;
            }
        }
        return content.length();
    }

    private boolean hasSelection() {
        return selectionAnchor != -1 && selectionAnchor != caret;
    }

    private int selectionStart() {
        return Math.min(selectionAnchor, caret);
    }

    private int selectionEnd() {
        return Math.max(selectionAnchor, caret);
    }

    private void clearSelection() {
        selectionAnchor = -1;
    }

    private void deleteSelection() {
        var start = selectionStart();
        var end = selectionEnd();
        content = content.substring(0, start) + content.substring(end);
        caret = start;
        clearSelection();
    }

    /**
     * Set {@link #selectionAnchor} to the current caret if no selection is yet active. Called by Shift-arrow / Shift-
     * Home / Shift-End handlers before they move the caret, so the first extension creates a selection from the
     * pre-move caret position.
     */
    private void extendSelectionAnchorIfNeeded() {
        if (selectionAnchor == -1) {
            selectionAnchor = caret;
        }
    }

    /**
     * Clear selection state if the caret has crossed back to meet the anchor. Without this, a Shift-arrow that
     * lands on the anchor leaves a degenerate selection ({@code anchor == caret}) which {@link #hasSelection}
     * already filters, but explicitly resetting the anchor lets the next non-shift caret-move start fresh.
     */
    private void collapseSelectionIfDegenerate() {
        if (selectionAnchor == caret) {
            clearSelection();
        }
    }

    private void copySelectionToClipboard() {
        if (!hasSelection()) {
            return;
        }
        var keyboard = Minecraft.getInstance().keyboardHandler;
        keyboard.setClipboard(content.substring(selectionStart(), selectionEnd()));
    }

    private void pasteFromClipboard() {
        var keyboard = Minecraft.getInstance().keyboardHandler;
        var clipboard = keyboard.getClipboard();
        if (clipboard == null || clipboard.isEmpty()) {
            return;
        }
        // Sanitize: this is a single-line input, so newlines / tabs become spaces. Other control chars are dropped
        // to match charTyped's filter (32 = space; below that is C0 control).
        var sb = new StringBuilder(clipboard.length());
        for (var i = 0; i < clipboard.length(); i++) {
            var ch = clipboard.charAt(i);
            if (ch == '\n' || ch == '\r' || ch == '\t') {
                sb.append(' ');
            } else if (ch >= 32 && ch != 127) {
                sb.append(ch);
            }
        }
        var sanitized = sb.toString();
        if (sanitized.isEmpty()) {
            return;
        }
        if (hasSelection()) {
            deleteSelection();
        }
        content = content.substring(0, caret) + sanitized + content.substring(caret);
        caret += sanitized.length();
    }
}
