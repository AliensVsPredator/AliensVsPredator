package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * Single-line text field with caret + focus management. One {@code TextInput} can be focused at a time across the whole
 * workspace; the focused instance receives {@code charTyped} / {@code keyPressed} events forwarded by
 * {@link EngineWorkspaceScreen}. Click on the input rect → focus. Click anywhere else → focus cleared (the screen calls
 * {@link #clearFocus()} before dispatching click events, so panels naturally re-focus their own inputs if clicked).
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

    private static final int PADDING_X = 4;

    private static final int CARET_BLINK_PERIOD_MS = 1000;

    private static @Nullable TextInput focused;

    public static @Nullable TextInput getFocused() {
        return focused;
    }

    public static void clearFocus() {
        if (focused != null) {
            focused.focusedFlag = false;
            focused = null;
        }
    }

    private final String placeholder;

    private String content = "";

    private int caret;

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
            }
            focused = this;
            this.focusedFlag = true;
            this.caret = content.length();
            return true;
        }
        return false;
    }

    public boolean charTyped(char ch, int modifiers) {
        if (!focusedFlag) {
            return false;
        }
        if (ch < 32 || ch == 127) {
            return false;
        }
        content = content.substring(0, caret) + ch + content.substring(caret);
        caret++;
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focusedFlag) {
            return false;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (caret > 0) {
                    content = content.substring(0, caret - 1) + content.substring(caret);
                    caret--;
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (caret < content.length()) {
                    content = content.substring(0, caret) + content.substring(caret + 1);
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (caret > 0) {
                    caret--;
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (caret < content.length()) {
                    caret++;
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                caret = 0;
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                caret = content.length();
                return true;
            }
            case GLFW.GLFW_KEY_ESCAPE -> {
                this.focusedFlag = false;
                if (focused == this) {
                    focused = null;
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
