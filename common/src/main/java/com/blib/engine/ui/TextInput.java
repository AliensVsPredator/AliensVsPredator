package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

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
 * <li>LMB-drag inside the input rect extends a selection from the original click position. The drag survives the cursor
 * leaving the input rect via a static {@link #getDragSelecting()} pointer routed by the workspace.</li>
 * <li>Backspace / Delete / character-typing replace the selection if any (otherwise act on the caret).</li>
 * <li>Ctrl+C / Ctrl+X copy / cut the selection to the OS clipboard. Ctrl+V pastes (newlines / tabs sanitized to spaces
 * since this is a single-line input). All three are no-ops when there's nothing useful to act on.</li>
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

    private static final int ELLIPSIS_COLOR = 0xFF606068;

    private static final String ELLIPSIS = "…";

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
        // If the click never actually dragged (anchor still equal to caret), clear the anchor so it doesn't
        // linger as a phantom anchor for the next thing that moves the caret. Otherwise typing a character right
        // after a click would create an off-by-one selection (anchor at click position, caret advanced by 1) that
        // the next charTyped call would then treat as an active selection and replace.
        if (dragSelecting != null && dragSelecting.selectionAnchor == dragSelecting.caret) {
            dragSelecting.clearSelection();
        }
        dragSelecting = null;
    }

    private final String placeholder;

    /**
     * Optional callback fired when the user commits the input via Enter — the editor inspector wires this up to
     * dispatch a server packet with the new value. Constructor-supplied so the field is final and the caller controls
     * the commit semantics; {@code null} means Enter is a no-op (matches the field's prior behavior).
     */
    private final @Nullable Consumer<String> onCommit;

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

    /**
     * Index into {@link #content} of the leftmost visible character — non-zero when content overflows the rect and
     * we've scrolled horizontally to keep the caret visible. Recomputed every frame in {@link #render}, so callers
     * never need to update it directly; click → caret math reads the cached value from the prior render's layout.
     */
    private int viewStart;

    /**
     * Whether the most recent render drew a left-edge ellipsis (because content extends to the left of the visible
     * window). Cached so {@link #caretIndexAt} can offset the cursor-X reference by the ellipsis reservation — without
     * it, clicks on a scrolled field would land one ellipsis-width too far right.
     */
    private boolean leftEllipsisShown;

    public TextInput(String placeholder) {
        this(placeholder, null);
    }

    public TextInput(String placeholder, @Nullable Consumer<String> onCommit) {
        this.placeholder = placeholder;
        this.onCommit = onCommit;
    }

    public String content() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.caret = Math.min(this.caret, content.length());
        // Reset view to the start so a content swap (e.g. selection switching to a new jigsaw block) doesn't leave
        // the field scrolled into nowhere relative to the new content. Re-converges to keep caret visible on the
        // next render.
        this.viewStart = 0;
        clearSelection();
    }

    public boolean isFocused() {
        return focusedFlag;
    }

    /**
     * Programmatically focus this input — equivalent to a click, but without requiring the cursor to be over the input
     * rect. Used by overlay widgets (e.g. {@link SearchableSelect}) that auto-focus their search box on open. Stomps
     * any other focused input the same way a click would.
     */
    public void focus() {
        if (focused != null && focused != this) {
            focused.focusedFlag = false;
            focused.clearSelection();
        }
        focused = this;
        this.focusedFlag = true;
        this.caret = content.length();
        clearSelection();
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

        var font = EngineFont.get();
        // +2 compensates for MC font's descender padding so the placeholder/value text visually centers; see
        // MenuBarPanel for details.
        var textY = y + (HEIGHT - font.lineHeight + 2) / 2;
        var maxTextWidth = Math.max(0, width - 2 * PADDING_X);

        if (content.isEmpty() && !focusedFlag) {
            // Placeholder path: no caret, no selection, no scrolling. Truncate so a long placeholder doesn't leak
            // past the input either (matches the user-content path behavior).
            viewStart = 0;
            leftEllipsisShown = false;
            var visiblePlaceholder = font.plainSubstrByWidth(placeholder, maxTextWidth);
            graphics.drawString(font, Component.literal(visiblePlaceholder), x + PADDING_X, textY, PLACEHOLDER_COLOR, false);
            return;
        }

        // Slide the visible window so the caret stays in view; otherwise typing past the right edge would render
        // glyphs (and the caret) outside the input rect.
        recomputeViewStart(font, maxTextWidth);

        // Decide which sides need ellipses. Content hidden to the left when viewStart > 0; content hidden to the
        // right when the maxTextWidth-fitted substring doesn't reach content.length(). Determine the flags FIRST
        // (with the unreserved width), then re-fit the visible window with the ellipsis reservations subtracted —
        // otherwise the ellipsis would either overlap the edge characters or push them out of view.
        var ellipsisWidth = font.width(ELLIPSIS);
        var unadjustedVisible = font.plainSubstrByWidth(content.substring(viewStart), maxTextWidth);
        var leftEllipsis = viewStart > 0;
        var rightEllipsis = (viewStart + unadjustedVisible.length()) < content.length();

        var leftReserve = leftEllipsis ? ellipsisWidth : 0;
        var rightReserve = rightEllipsis ? ellipsisWidth : 0;
        var textMaxWidth = Math.max(0, maxTextWidth - leftReserve - rightReserve);
        if (leftEllipsis || rightEllipsis) {
            recomputeViewStart(font, textMaxWidth);
            // Recheck flags after re-fit: the second pass may have moved viewStart forward (less room → more
            // scroll), which can flip a side off (e.g. caret at end → no right ellipsis once left-reserved).
            leftEllipsis = viewStart > 0;
            var refit = font.plainSubstrByWidth(content.substring(viewStart), textMaxWidth);
            rightEllipsis = (viewStart + refit.length()) < content.length();
            leftReserve = leftEllipsis ? ellipsisWidth : 0;
            rightReserve = rightEllipsis ? ellipsisWidth : 0;
            textMaxWidth = Math.max(0, maxTextWidth - leftReserve - rightReserve);
        }

        var textX = x + PADDING_X + leftReserve;
        var visibleText = font.plainSubstrByWidth(content.substring(viewStart), textMaxWidth);
        var visibleEnd = viewStart + visibleText.length();
        leftEllipsisShown = leftEllipsis;

        // Selection highlight — only the portion that intersects the visible window.
        if (focusedFlag && hasSelection()) {
            var visSelStart = Math.max(selectionStart(), viewStart);
            var visSelEnd = Math.min(selectionEnd(), visibleEnd);
            if (visSelStart < visSelEnd) {
                var sx = textX + font.width(content.substring(viewStart, visSelStart));
                var ex = textX + font.width(content.substring(viewStart, visSelEnd));
                graphics.fill(sx, textY - 1, ex, textY + font.lineHeight, SELECTION_COLOR);
            }
        }

        graphics.drawString(font, Component.literal(visibleText), textX, textY, TEXT_COLOR, false);

        if (leftEllipsis) {
            graphics.drawString(font, Component.literal(ELLIPSIS), x + PADDING_X, textY, ELLIPSIS_COLOR, false);
        }
        if (rightEllipsis) {
            graphics.drawString(font, Component.literal(ELLIPSIS), textX + font.width(visibleText), textY, ELLIPSIS_COLOR, false);
        }

        if (focusedFlag) {
            var blink = (System.currentTimeMillis() % CARET_BLINK_PERIOD_MS) < CARET_BLINK_PERIOD_MS / 2;
            // Caret renders only when it falls inside the visible window — recomputeViewStart guarantees this for
            // the common case, but guard anyway in case width was zero or the caret somehow drifted.
            if (blink && caret >= viewStart && caret <= visibleEnd) {
                var caretX = textX + font.width(content.substring(viewStart, caret));
                graphics.fill(caretX, textY - 1, caretX + 1, textY + font.lineHeight, CARET_COLOR);
            }
        }
    }

    /**
     * Slide {@link #viewStart} so the caret is always within {@code maxWidth} pixels of the visible left edge. Three
     * passes: (1) clamp into bounds, (2) advance right if the caret is past the right edge, (3) pull back left if
     * there's slack (content shrank, caret moved left, or the rect grew). The third pass keeps the field showing as
     * much content as possible from the start when the content fits.
     */
    private void recomputeViewStart(Font font, int maxWidth) {
        if (maxWidth <= 0) {
            viewStart = 0;
            return;
        }

        if (viewStart < 0) {
            viewStart = 0;
        }
        if (viewStart > content.length()) {
            viewStart = content.length();
        }

        if (caret < viewStart) {
            viewStart = caret;
        }

        // Advance viewStart while the prefix from viewStart..caret overflows the available width.
        while (viewStart < caret && font.width(content.substring(viewStart, caret)) > maxWidth) {
            viewStart++;
        }

        // Pull back while there's room — show as much as possible from the left when content fits.
        while (viewStart > 0) {
            var trial = viewStart - 1;
            if (font.width(content.substring(trial)) <= maxWidth) {
                viewStart = trial;
            } else {
                break;
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
     * Extend the selection during an LMB drag by moving the caret to {@code mouseX} while the anchor stays where the
     * click landed. Called by {@link EngineWorkspaceScreen} via the static {@link #getDragSelecting()} pointer so drags
     * survive the cursor leaving the input rect.
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
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                // Commit semantics: fire the callback with the current content, then defocus to give visual
                // feedback that the value was accepted. The defocus also matches user expectations from form
                // fields elsewhere — Enter "submits and moves on".
                if (onCommit != null) {
                    onCommit.accept(content);
                }
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
     * whichever character boundary is closer to the cursor for natural click-to-position feel. Honors the current
     * {@link #viewStart} so a click in a horizontally-scrolled field lands on the visible character under the cursor
     * rather than the corresponding offset from the literal content origin, and offsets by the left-ellipsis
     * reservation when one is shown so clicks just past the ellipsis still land on the first visible character. Linear
     * scan — fine for short single-line content.
     */
    private int caretIndexAt(double mouseX) {
        var font = EngineFont.get();
        var leftReserve = leftEllipsisShown ? font.width(ELLIPSIS) : 0;
        var relX = mouseX - rectX - PADDING_X - leftReserve;
        if (relX <= 0) {
            return viewStart;
        }
        var visibleSuffix = content.substring(viewStart);
        if (relX >= font.width(visibleSuffix)) {
            return content.length();
        }
        for (var i = 1; i <= visibleSuffix.length(); i++) {
            var w = font.width(visibleSuffix.substring(0, i));
            if (w >= relX) {
                var prevW = font.width(visibleSuffix.substring(0, i - 1));
                return viewStart + ((relX - prevW < w - relX) ? i - 1 : i);
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
     * Clear selection state if the caret has crossed back to meet the anchor. Without this, a Shift-arrow that lands on
     * the anchor leaves a degenerate selection ({@code anchor == caret}) which {@link #hasSelection} already filters,
     * but explicitly resetting the anchor lets the next non-shift caret-move start fresh.
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
