package com.blib.engine.ui.panel.viewport;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import com.blib.engine.modeler.item.ModelerItemSession;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.popup.PanelMenuOpener;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Top-right overlay button on the modeler viewport that picks the current "Preview as" mode for an attached
 * {@link ModelerItemSession}. Default mode is "Edit Model" (the existing geo + cube rendering); other modes invoke
 * vanilla's {@code ItemRenderer.renderStatic} per {@link ItemDisplayContext} so the preview is pixel-identical to
 * in-game. Only visible / interactive when an item session is attached.
 */
@ApiStatus.Internal
public final class ModelerPreviewOverlay {

    /** Pixel rect of the most recently rendered button, captured for hit-testing. */
    private static int btnX, btnY, btnW, btnH;

    private static boolean visible;

    private static final int BUTTON_HEIGHT = 14;

    private static final int BUTTON_WIDTH = 170;

    private static final int RIGHT_MARGIN = 6;

    private static final int TOP_MARGIN = 4;

    private static final int BG_COLOR = 0xCC1A1A22;

    private static final int BG_HOVER_COLOR = 0xEE2A2A32;

    private static final int BORDER_COLOR = 0xFF505058;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private ModelerPreviewOverlay() {}

    /**
     * Render the button. {@code anchorTop} should be the panel-content top (below menu/toolbar) so the button sits just
     * under the chrome strip. No-op when {@code session} is null.
     */
    public static void render(
        GuiGraphics graphics,
        int panelX,
        int panelWidth,
        int anchorTop,
        @Nullable ModelerItemSession session,
        int mouseX,
        int mouseY
    ) {
        if (session == null) {
            visible = false;
            return;
        }
        visible = true;
        btnW = BUTTON_WIDTH;
        btnH = BUTTON_HEIGHT;
        btnX = panelX + panelWidth - RIGHT_MARGIN - btnW;
        btnY = anchorTop + TOP_MARGIN;

        var hovered = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= btnY && mouseY < btnY + btnH;
        graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, hovered ? BG_HOVER_COLOR : BG_COLOR);
        graphics.fill(btnX, btnY, btnX + btnW, btnY + 1, BORDER_COLOR);
        graphics.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, BORDER_COLOR);
        graphics.fill(btnX, btnY, btnX + 1, btnY + btnH, BORDER_COLOR);
        graphics.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, BORDER_COLOR);

        var font = EngineFont.get();
        var label = "Preview: " + currentLabel(session);
        var maxWidth = btnW - 8;
        var truncated = font.plainSubstrByWidth(label, maxWidth);
        graphics.drawString(font, Component.literal(truncated), btnX + 4, btnY + (btnH - font.lineHeight + 2) / 2, TEXT_COLOR, false);
    }

    /** Click hit-test. Returns true and opens the dropdown menu when the click lands on the button. */
    public static boolean mouseClicked(
        double mouseX,
        double mouseY,
        @Nullable PanelMenuOpener menuOpener,
        @Nullable ModelerItemSession session
    ) {
        if (!visible || session == null) {
            return false;
        }
        if (mouseX < btnX || mouseX >= btnX + btnW || mouseY < btnY || mouseY >= btnY + btnH) {
            return false;
        }
        if (menuOpener != null) {
            menuOpener.open(buildMenu(session));
        }
        return true;
    }

    private static DropdownMenu buildMenu(ModelerItemSession session) {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("Edit Model", () -> {
            session.previewContext = null;
            session.previewWallFixed = false;
        }));
        for (
            var ctx : new ItemDisplayContext[] {
                ItemDisplayContext.GUI,
                ItemDisplayContext.GROUND,
                ItemDisplayContext.FIXED,
                ItemDisplayContext.HEAD,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            }
        ) {
            items.add(new DropdownMenu.Item(contextLabel(ctx), () -> {
                session.previewContext = ctx;
                session.previewWallFixed = false;
            }));
        }
        items.add(new DropdownMenu.Item("Fixed (Wall)", () -> {
            session.previewContext = ItemDisplayContext.FIXED;
            session.previewWallFixed = true;
        }));
        return new DropdownMenu(btnX, btnY + btnH, items);
    }

    private static String currentLabel(ModelerItemSession session) {
        if (session.previewContext == null) {
            return "Edit Model";
        }
        if (session.previewContext == ItemDisplayContext.FIXED && session.previewWallFixed) {
            return "Fixed (Wall)";
        }
        return contextLabel(session.previewContext);
    }

    private static String contextLabel(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> "GUI";
            case GROUND -> "Ground";
            case FIXED -> "Fixed (Frame)";
            case HEAD -> "Head";
            case FIRST_PERSON_LEFT_HAND -> "FP Left Hand";
            case FIRST_PERSON_RIGHT_HAND -> "FP Right Hand";
            case THIRD_PERSON_LEFT_HAND -> "TP Left Hand";
            case THIRD_PERSON_RIGHT_HAND -> "TP Right Hand";
            case NONE -> "None";
        };
    }
}
