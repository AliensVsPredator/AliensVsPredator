package com.blib.engine.ui.dialog;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutStorage;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.modal.ModalDialog;

/**
 * Larger modal dialog presenting every available layout in a vertical list with per-row Rename / Duplicate / Delete
 * actions. Footer controls expose "Refresh" (re-scans the layouts folder so externally-dropped JSON files appear) and
 * "Open Layouts Folder" (uses {@link Util#getPlatform()} to launch the OS file manager pointing at the layouts dir,
 * covering both the import and export use cases without needing an in-game file picker).
 * <p>
 * Per-row action callbacks are constructor-supplied because the actions ultimately mutate workspace state (active
 * layout, body root) — keeping the host wiring outside this class avoids coupling. The dialog itself only issues "the
 * user clicked Rename on row N" callbacks; the host opens a {@link LayoutNameDialog} or {@link ConfirmDialog} in
 * response.
 * <p>
 * Lifecycle mirrors {@link CaptureDialog}: the host owns the field, dispatches input through it before panels, and
 * clears it on close.
 */
@ApiStatus.Internal
public final class ManageLayoutsDialog extends ModalDialog {

    @Override
    public String tag() {
        return "manage_layouts";
    }

    private static final int DIM_COLOR = 0x80000000;

    private static final int BG_COLOR = 0xFF1F1F26;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int LABEL_COLOR = 0xFFD0D0D0;

    private static final int META_COLOR = 0xFF808088;

    private static final int ACTIVE_TAG_COLOR = 0xFF80E080;

    private static final int TEMPLATE_TAG_COLOR = 0xFFB6A267;

    private static final int ROW_BG_COLOR = 0xFF14141A;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1A1A22;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DESTRUCTIVE_TEXT = 0xFFE06868;

    private static final int BOX_WIDTH = 460;

    private static final int BOX_HEIGHT = 300;

    private static final int BOX_PAD_X = 16;

    private static final int BOX_PAD_Y = 12;

    private static final int ROW_HEIGHT = 22;

    private static final int ROW_GAP = 4;

    private static final int ROW_PAD_X = 8;

    private static final int ACTION_BUTTON_HEIGHT = 12;

    private static final int ACTION_BUTTON_WIDTH = 56;

    private static final int ACTION_BUTTON_GAP = 4;

    private static final int FOOTER_BUTTON_HEIGHT = 14;

    private static final int FOOTER_BUTTON_WIDTH = 110;

    private static final int FOOTER_BUTTON_GAP = 8;

    private final Runnable onClose;

    private final Consumer<LayoutDoc> onRename;

    private final Consumer<LayoutDoc> onDuplicate;

    private final Consumer<LayoutDoc> onDelete;

    private final Consumer<LayoutDoc> onActivate;

    private List<LayoutDoc> rows;

    private String activeLayoutId;

    private final List<RowRect> rowRects = new ArrayList<>();

    private @Nullable Rect refreshRect;

    private @Nullable Rect openFolderRect;

    private @Nullable Rect closeRect;

    public ManageLayoutsDialog(
        String activeLayoutId,
        Runnable onClose,
        Consumer<LayoutDoc> onActivate,
        Consumer<LayoutDoc> onRename,
        Consumer<LayoutDoc> onDuplicate,
        Consumer<LayoutDoc> onDelete
    ) {
        this.activeLayoutId = activeLayoutId;
        this.onClose = onClose;
        this.onActivate = onActivate;
        this.onRename = onRename;
        this.onDuplicate = onDuplicate;
        this.onDelete = onDelete;
        this.rows = LayoutCatalog.listAll();
    }

    /** Update which id is currently active without recreating the dialog. */
    public void setActiveLayoutId(String activeLayoutId) {
        this.activeLayoutId = activeLayoutId;
    }

    /** Re-read the catalog. Called by the Refresh button and any time the host saves/deletes via this dialog. */
    public void refresh() {
        this.rows = LayoutCatalog.listAll();
    }

    public void render(GuiGraphics graphics, int screenWidth, int screenHeight, int mouseX, int mouseY) {
        graphics.fill(0, 0, screenWidth, screenHeight, DIM_COLOR);

        var font = EngineFont.get();
        var boxX = (screenWidth - BOX_WIDTH) / 2;
        var boxY = (screenHeight - BOX_HEIGHT) / 2;

        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, BG_COLOR);
        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + 1, BORDER_COLOR);
        graphics.fill(boxX, boxY + BOX_HEIGHT - 1, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, BORDER_COLOR);
        graphics.fill(boxX, boxY, boxX + 1, boxY + BOX_HEIGHT, BORDER_COLOR);
        graphics.fill(boxX + BOX_WIDTH - 1, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, BORDER_COLOR);

        graphics.drawString(font, Component.literal("Manage Layouts"), boxX + BOX_PAD_X, boxY + BOX_PAD_Y, TITLE_COLOR, false);

        // Row list. Truncated to whatever fits in the box; no scrolling for v1 since the typical layout count is small.
        rowRects.clear();
        var listTopY = boxY + BOX_PAD_Y + font.lineHeight + ROW_GAP * 2;
        var listLeftX = boxX + BOX_PAD_X;
        var listW = BOX_WIDTH - 2 * BOX_PAD_X;
        var footerY = boxY + BOX_HEIGHT - BOX_PAD_Y - FOOTER_BUTTON_HEIGHT;
        var listMaxBottom = footerY - ROW_GAP * 2;
        var rowY = listTopY;

        for (var i = 0; i < rows.size(); i++) {
            if (rowY + ROW_HEIGHT > listMaxBottom) {
                // Out of space. Show a "(N more — open folder to see all)" line and stop.
                graphics.drawString(
                    font,
                    Component.literal("(" + (rows.size() - i) + " more not shown — Open Layouts Folder)"),
                    listLeftX,
                    rowY + 2,
                    META_COLOR,
                    false
                );
                break;
            }
            var doc = rows.get(i);
            var rowHover = mouseX >= listLeftX && mouseX < listLeftX + listW && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            var rowBg = rowHover ? ROW_BG_HOVER_COLOR : ROW_BG_COLOR;
            graphics.fill(listLeftX, rowY, listLeftX + listW, rowY + ROW_HEIGHT, rowBg);

            // Display name + (active) / (template) tags.
            var labelX = listLeftX + ROW_PAD_X;
            var labelBaselineY = rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(doc.displayName()), labelX, labelBaselineY, LABEL_COLOR, false);
            var nameWidth = font.width(doc.displayName());

            var tagX = labelX + nameWidth + 6;
            if (doc.id().equals(activeLayoutId)) {
                var activeTag = "(active)";
                graphics.drawString(font, Component.literal(activeTag), tagX, labelBaselineY, ACTIVE_TAG_COLOR, false);
                tagX += font.width(activeTag) + 6;
            }
            if (LayoutCatalog.isTemplateId(doc.id())) {
                var templateTag = "(template)";
                graphics.drawString(font, Component.literal(templateTag), tagX, labelBaselineY, TEMPLATE_TAG_COLOR, false);
            }

            // Action buttons: Activate, Rename, Duplicate, Delete (right-aligned).
            var buttonRightEdge = listLeftX + listW - ROW_PAD_X;
            var buttonY = rowY + (ROW_HEIGHT - ACTION_BUTTON_HEIGHT) / 2;

            var deleteX = buttonRightEdge - ACTION_BUTTON_WIDTH;
            var duplicateX = deleteX - ACTION_BUTTON_GAP - ACTION_BUTTON_WIDTH;
            var renameX = duplicateX - ACTION_BUTTON_GAP - ACTION_BUTTON_WIDTH;
            var activateX = renameX - ACTION_BUTTON_GAP - ACTION_BUTTON_WIDTH;

            var activateRect = new Rect(activateX, buttonY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
            var renameRect = new Rect(renameX, buttonY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
            var duplicateRect = new Rect(duplicateX, buttonY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);
            var deleteRect = new Rect(deleteX, buttonY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT);

            var isActive = doc.id().equals(activeLayoutId);
            renderButton(graphics, activateRect, isActive ? "Active" : "Activate", mouseX, mouseY, isActive ? META_COLOR : BUTTON_TEXT);
            renderButton(graphics, renameRect, "Rename", mouseX, mouseY, BUTTON_TEXT);
            renderButton(graphics, duplicateRect, "Duplicate", mouseX, mouseY, BUTTON_TEXT);
            renderButton(graphics, deleteRect, "Delete", mouseX, mouseY, BUTTON_DESTRUCTIVE_TEXT);

            rowRects.add(new RowRect(doc, activateRect, renameRect, duplicateRect, deleteRect));
            rowY += ROW_HEIGHT + ROW_GAP;
        }

        // Footer: Refresh, Open Folder, Close.
        var footerRightEdge = boxX + BOX_WIDTH - BOX_PAD_X;
        var closeX = footerRightEdge - FOOTER_BUTTON_WIDTH;
        var openFolderX = closeX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;
        var refreshX = openFolderX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;

        refreshRect = new Rect(refreshX, footerY, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT);
        openFolderRect = new Rect(openFolderX, footerY, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT);
        closeRect = new Rect(closeX, footerY, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT);

        renderButton(graphics, refreshRect, "Refresh", mouseX, mouseY, BUTTON_TEXT);
        renderButton(graphics, openFolderRect, "Open Layouts Folder", mouseX, mouseY, BUTTON_TEXT);
        renderButton(graphics, closeRect, "Close", mouseX, mouseY, BUTTON_TEXT);
    }

    private static void renderButton(GuiGraphics graphics, Rect rect, String label, int mouseX, int mouseY, int textColor) {
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + rect.h, hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(rect.x, rect.y, rect.x + rect.w, rect.y + 1, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y + rect.h - 1, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x, rect.y, rect.x + 1, rect.y + rect.h, BUTTON_BORDER);
        graphics.fill(rect.x + rect.w - 1, rect.y, rect.x + rect.w, rect.y + rect.h, BUTTON_BORDER);

        var font = EngineFont.get();
        var textX = rect.x + (rect.w - font.width(label)) / 2;
        var textY = rect.y + (rect.h - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (var rr : rowRects) {
            if (rr.activate.contains(mouseX, mouseY)) {
                onActivate.accept(rr.doc);
                return true;
            }
            if (rr.rename.contains(mouseX, mouseY)) {
                onRename.accept(rr.doc);
                return true;
            }
            if (rr.duplicate.contains(mouseX, mouseY)) {
                onDuplicate.accept(rr.doc);
                return true;
            }
            if (rr.delete.contains(mouseX, mouseY)) {
                onDelete.accept(rr.doc);
                return true;
            }
        }
        if (refreshRect != null && refreshRect.contains(mouseX, mouseY)) {
            refresh();
            return true;
        }
        if (openFolderRect != null && openFolderRect.contains(mouseX, mouseY)) {
            try {
                LayoutStorage.ensureRootExists();
            } catch (java.io.IOException ignored) {
                // Best-effort; if the directory can't be created, fall through to openUri which will likely fail too
                // and the user will see no folder open. Silent rather than blocking the dialog.
            }
            Util.getPlatform().openUri(LayoutStorage.layoutsRoot().toUri());
            return true;
        }
        if (closeRect != null && closeRect.contains(mouseX, mouseY)) {
            onClose.run();
            return true;
        }
        // Outside-clicks are absorbed but no-op so a stray click on the dim doesn't dismiss.
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose.run();
            return true;
        }
        return false;
    }

    private record Rect(
        int x,
        int y,
        int w,
        int h
    ) {

        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private record RowRect(
        LayoutDoc doc,
        Rect activate,
        Rect rename,
        Rect duplicate,
        Rect delete
    ) {}
}
