package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blib.engine.input.Input;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;

/**
 * Inline "binding chip" widget — renders the current {@link Input}'s display string. Clicking arms the widget; the next
 * key, mouse button, or scroll event fires the {@code onCaptured} callback with the constructed {@link Input}. While
 * armed, the chip renders "Press a key…" and consumes the next eligible event. Esc cancels without firing.
 * <p>
 * The widget is stateless about position — the parent dialog tells it where to render each frame. Only the "armed" flag
 * is internal state. Designed to be one-shot per click: after capture or cancel, the widget disarms automatically and
 * the parent decides what (if anything) to do next.
 */
@ApiStatus.Internal
public final class KeyCaptureWidget {

    public static final int HEIGHT = 14;

    private static final int BG_IDLE = 0xFF14141A;

    private static final int BG_HOVER = 0xFF1F1F26;

    private static final int BG_ARMED = 0xFF3C3C46;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int BORDER_ARMED_COLOR = 0xFFE6C26B;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int TEXT_ARMED_COLOR = 0xFFE6C26B;

    private static final String PROMPT = "Press a key…";

    private final Supplier<Input> current;

    private final Consumer<Input> onCaptured;

    private final Runnable onCancelled;

    private boolean armed;

    private int rectX;

    private int rectY;

    private int rectWidth;

    public KeyCaptureWidget(Supplier<Input> current, Consumer<Input> onCaptured, Runnable onCancelled) {
        this.current = current;
        this.onCaptured = onCaptured;
        this.onCancelled = onCancelled;
    }

    public boolean isArmed() {
        return armed;
    }

    public void disarm() {
        this.armed = false;
    }

    /**
     * Force the widget into the armed state without requiring a click. The host calls this after constructing the
     * widget when the user already clicked the chip in the parent surface (the chip's own rect isn't yet known here).
     */
    public void armDirectly() {
        this.armed = true;
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        if (width <= 0) {
            return;
        }

        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + HEIGHT;
        var bg = armed ? BG_ARMED : (hovered ? BG_HOVER : BG_IDLE);
        var border = armed ? BORDER_ARMED_COLOR : BORDER_COLOR;
        var textColor = armed ? TEXT_ARMED_COLOR : TEXT_COLOR;

        graphics.fill(x, y, x + width, y + HEIGHT, bg);
        // Border (1px frame).
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + HEIGHT - 1, x + width, y + HEIGHT, border);
        graphics.fill(x, y, x + 1, y + HEIGHT, border);
        graphics.fill(x + width - 1, y, x + width, y + HEIGHT, border);

        var font = EngineFont.get();
        var text = armed ? PROMPT : current.get().format();
        if (text.isEmpty()) {
            text = "(none)";
        }
        UiText.drawCentered(graphics, font, text, UiRect.of(x + 3, y, Math.max(0, width - 6), HEIGHT), textColor);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (armed) {
            // Capture a mouse button while armed. LMB plain → toggle to disarm + cancel feels surprising — but the
            // user just clicked the chip to arm it, so the next mouse click usually means "I'm rebinding to LMB."
            // We capture every button; Esc-key cancels are the escape hatch.
            var mods = 0;
            if (Screen.hasControlDown()) {
                mods |= GLFW.GLFW_MOD_CONTROL;
            }
            if (Screen.hasShiftDown()) {
                mods |= GLFW.GLFW_MOD_SHIFT;
            }
            if (Screen.hasAltDown()) {
                mods |= GLFW.GLFW_MOD_ALT;
            }
            armed = false;
            onCaptured.accept(new Input.MouseButton(button, mods));
            return true;
        }
        var inside = mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + HEIGHT;
        if (inside && button == 0) {
            armed = true;
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!armed) {
            return false;
        }
        // Esc cancels without firing — gives the user an explicit way to back out after arming. We don't capture
        // Esc-as-binding because Esc is reserved across the engine for popup dismissal.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            armed = false;
            onCancelled.run();
            return true;
        }
        // Modifier-only press: the user pressed Shift / Ctrl / Alt alone. These come through keyPressed as the modifier
        // keycode itself with the mod bit set. Treat as Input.Modifier so users can rebind "snap-int" without picking
        // up a companion key.
        if (isPureModifierKey(keyCode)) {
            armed = false;
            onCaptured.accept(new Input.Modifier(modBitOf(keyCode)));
            return true;
        }
        armed = false;
        // Strip the modifier bit corresponding to the pressed key — e.g. pressing Shift+A reports modifier=SHIFT,
        // keyCode=A; pressing A alone reports modifier=0, keyCode=A. {@link Input#DISPLAY_MOD_MASK} narrows to the
        // bits the engine cares about (Ctrl/Shift/Alt).
        onCaptured.accept(new Input.Key(keyCode, modifiers & Input.DISPLAY_MOD_MASK));
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!armed) {
            return false;
        }
        if (scrollY == 0.0) {
            return false;
        }
        var mods = 0;
        if (Screen.hasControlDown()) {
            mods |= GLFW.GLFW_MOD_CONTROL;
        }
        if (Screen.hasShiftDown()) {
            mods |= GLFW.GLFW_MOD_SHIFT;
        }
        if (Screen.hasAltDown()) {
            mods |= GLFW.GLFW_MOD_ALT;
        }
        armed = false;
        onCaptured.accept(new Input.Scroll(mods));
        return true;
    }

    private static boolean isPureModifierKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
            || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
            || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
            || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
            || keyCode == GLFW.GLFW_KEY_LEFT_ALT
            || keyCode == GLFW.GLFW_KEY_RIGHT_ALT;
    }

    private static int modBitOf(int keyCode) {
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> Input.MOD_SHIFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> Input.MOD_CTRL;
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> Input.MOD_ALT;
            default -> 0;
        };
    }
}
