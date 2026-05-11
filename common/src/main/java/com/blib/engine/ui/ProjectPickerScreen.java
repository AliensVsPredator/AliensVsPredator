package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.session.ProjectSession;
import com.blib.internal.common.storage.EngineProjectIO;
import com.blib.internal.common.storage.ProjectInfo;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCreateProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SListProjectsPayload;
import com.blib.mod.common.network.packet.C2SOpenProjectPayload;
import com.blib.mod.common.network.packet.ProjectOp;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;

/**
 * Project-management gateway for the BLib engine. {@code /blib engine} routes here first; from this screen the user
 * picks an existing project to open, creates a new one, or deletes one. Only after a successful Open transitions to
 * {@link EngineWorkspaceScreen} — that's the requirement that engine open is gated on project selection.
 * <p>
 * Two modes: LIST (default — scroll through project cards, click Open or Delete) and CREATE (name / description inputs
 * + Create button). The CREATE form has live name validation that mirrors {@link EngineProjectIO#validateProjectName}
 * so the user sees rejection reasons before hitting submit.
 * <p>
 * Server interactions are async: we send a packet and wait for the matching {@link S2CProjectOpResultPayload} via
 * {@link ProjectSession#setOpResultCallback}. While waiting, {@link #pendingOp} is non-null and the matching button
 * shows a "..." indicator. The {@code BLibClientListener} delivers replies regardless of which screen is open, so a
 * delayed reply that lands after the user has navigated away is harmless.
 * <p>
 * Cancel from LIST returns the player to the game without entering engine mode (no
 * {@code EngineTickControl.captureAndPause()} runs until {@link #onConfirmedOpen} fires). Cancel from CREATE returns to
 * LIST.
 */
@ApiStatus.Internal
public final class ProjectPickerScreen extends Screen {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int PANEL_BG = 0xFF1A1A22;

    private static final int PANEL_BG_HOVER = 0xFF22222C;

    private static final int PANEL_BORDER = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int VALUE_COLOR = 0xFFD0D0D0;

    private static final int META_COLOR = 0xFF808088;

    private static final int ERROR_COLOR = 0xFFE06868;

    private static final int BUTTON_BG = 0xFF1F1F26;

    private static final int BUTTON_BG_HOVER = 0xFF2C2C32;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DELETE_TEXT = 0xFFE06868;

    private static final int CONTENT_PADDING = 16;

    private static final int CARD_HEIGHT = 36;

    private static final int CARD_GAP = 4;

    private static final int CARD_PADDING_X = 8;

    private static final int CARD_PADDING_Y = 4;

    private static final int BUTTON_HEIGHT = 14;

    private static final int BUTTON_GAP = 4;

    private static final int OPEN_BUTTON_WIDTH = 44;

    private static final int DELETE_BUTTON_WIDTH = 44;

    private static final int TOOLBAR_HEIGHT = 22;

    private static final int FOOTER_HEIGHT = 28;

    private enum Mode {
        LIST,
        CREATE
    }

    private final Runnable onConfirmedOpen;

    private Mode mode = Mode.LIST;

    private final TextInput nameInput = new TextInput("name (lowercase, '_' or '-')");

    private final TextInput descriptionInput = new TextInput("description (optional)");

    private @Nullable String validationMessage;

    /** Non-null while a server op is in flight; used to show a "..." indicator and block re-fires. */
    private @Nullable ProjectOp pendingOp;

    private @Nullable String pendingProjectName;

    /** Cached during render so click handlers can hit-test without recomputing layout. */
    private final List<CardRect> cardRects = new ArrayList<>();

    private @Nullable Rect newProjectButton;

    private @Nullable Rect cancelButton;

    private @Nullable Rect createButton;

    private @Nullable Rect createCancelButton;

    /** Non-null when a destructive action is awaiting user confirmation; takes priority over all other input. */
    private @Nullable ConfirmDialog confirmDialog;

    private int scrollY;

    public ProjectPickerScreen(Runnable onConfirmedOpen) {
        this(onConfirmedOpen, false);
    }

    public ProjectPickerScreen(Runnable onConfirmedOpen, boolean startInCreateMode) {
        super(Component.literal("BLib Engine — Projects"));
        this.onConfirmedOpen = onConfirmedOpen;
        if (startInCreateMode) {
            this.mode = Mode.CREATE;
        }
    }

    @Override
    protected void init() {
        super.init();
        // Refresh on every open — the world's datapack folder may have been edited by the user externally between
        // sessions. Empty list isn't fatal; the picker's "(no projects)" state guides the user to "+ New Project".
        BLib.MOD.networking().sendToServer(C2SListProjectsPayload.INSTANCE);
        ProjectSession.setOpResultCallback(this::onOpResult);
    }

    @Override
    public void removed() {
        super.removed();
        ProjectSession.setOpResultCallback(null);
        TextInput.clearFocus();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);

        if (mode == Mode.LIST) {
            renderList(graphics, mouseX, mouseY);
        } else {
            renderCreate(graphics, mouseX, mouseY);
        }

        // Confirm dialog renders last so it sits on top of the dim background overlay it draws itself.
        if (confirmDialog != null) {
            confirmDialog.render(graphics, this.width, this.height, mouseX, mouseY);
        }
    }

    private void renderList(GuiGraphics graphics, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var contentX = CONTENT_PADDING;
        var contentY = CONTENT_PADDING;
        var contentW = this.width - 2 * CONTENT_PADDING;

        graphics.drawString(font, Component.literal("BLib Engine — Open Project"), contentX, contentY, TITLE_COLOR, false);
        var titleBottom = contentY + font.lineHeight + 6;

        // Toolbar
        var toolbarY = titleBottom;
        newProjectButton = new Rect(contentX, toolbarY, 110, BUTTON_HEIGHT);
        renderButton(graphics, newProjectButton, "+ New Project", mouseX, mouseY, BUTTON_TEXT);
        var countText = ProjectSession.availableProjects().size() + " project(s)";
        var countX = contentX + contentW - font.width(countText);
        var countY = toolbarY + (BUTTON_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(countText), countX, countY, META_COLOR, false);

        // Card list
        var listY = toolbarY + TOOLBAR_HEIGHT;
        var listH = this.height - listY - FOOTER_HEIGHT - CONTENT_PADDING;
        cardRects.clear();
        var projects = ProjectSession.availableProjects();
        if (projects.isEmpty()) {
            var emptyText = "No projects yet. Click '+ New Project' to create one.";
            var ex = contentX + (contentW - font.width(emptyText)) / 2;
            var ey = listY + listH / 2 - font.lineHeight / 2;
            graphics.drawString(font, Component.literal(emptyText), ex, ey, META_COLOR, false);
        } else {
            for (var i = 0; i < projects.size(); i++) {
                var rowY = listY + i * (CARD_HEIGHT + CARD_GAP) - scrollY;
                if (rowY + CARD_HEIGHT < listY || rowY > listY + listH) {
                    continue;
                }
                renderCard(graphics, projects.get(i), contentX, rowY, contentW, mouseX, mouseY);
            }
        }

        // Footer
        var footerY = this.height - FOOTER_HEIGHT;
        var cancelX = this.width - CONTENT_PADDING - 60;
        cancelButton = new Rect(cancelX, footerY + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2, 60, BUTTON_HEIGHT);
        renderButton(graphics, cancelButton, "Cancel", mouseX, mouseY, BUTTON_TEXT);

        // Pending indicator (top-right of footer)
        if (pendingOp != null && pendingProjectName != null) {
            var pendingText = pendingLabel(pendingOp) + " '" + pendingProjectName + "'…";
            var pendingX = CONTENT_PADDING;
            var pendingY = footerY + (FOOTER_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(pendingText), pendingX, pendingY, LABEL_COLOR, false);
        } else if (validationMessage != null) {
            var msgY = footerY + (FOOTER_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(validationMessage), CONTENT_PADDING, msgY, ERROR_COLOR, false);
        }
    }

    private void renderCard(GuiGraphics graphics, ProjectInfo project, int x, int y, int width, int mouseX, int mouseY) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + CARD_HEIGHT;
        graphics.fill(x, y, x + width, y + CARD_HEIGHT, hovered ? PANEL_BG_HOVER : PANEL_BG);
        graphics.fill(x, y, x + width, y + 1, PANEL_BORDER);
        graphics.fill(x, y + CARD_HEIGHT - 1, x + width, y + CARD_HEIGHT, PANEL_BORDER);
        graphics.fill(x, y, x + 1, y + CARD_HEIGHT, PANEL_BORDER);
        graphics.fill(x + width - 1, y, x + width, y + CARD_HEIGHT, PANEL_BORDER);

        var font = EngineFont.get();
        var openX = x + width - CARD_PADDING_X - OPEN_BUTTON_WIDTH;
        var deleteX = openX - BUTTON_GAP - DELETE_BUTTON_WIDTH;
        var buttonY = y + (CARD_HEIGHT - BUTTON_HEIGHT) / 2;

        var openRect = new Rect(openX, buttonY, OPEN_BUTTON_WIDTH, BUTTON_HEIGHT);
        var deleteRect = new Rect(deleteX, buttonY, DELETE_BUTTON_WIDTH, BUTTON_HEIGHT);
        renderButton(graphics, openRect, "Open", mouseX, mouseY, BUTTON_TEXT);
        renderButton(graphics, deleteRect, "Delete", mouseX, mouseY, BUTTON_DELETE_TEXT);
        cardRects.add(new CardRect(project, openRect, deleteRect));

        var nameY = y + CARD_PADDING_Y;
        graphics.drawString(font, Component.literal(project.name()), x + CARD_PADDING_X, nameY, VALUE_COLOR, false);

        var descMaxW = deleteX - (x + CARD_PADDING_X) - 8;
        var descText = project.description().isEmpty() ? "(no description)" : project.description();
        var descTrunc = font.plainSubstrByWidth(descText, descMaxW);
        var descY = nameY + font.lineHeight + 1;
        graphics.drawString(font, Component.literal(descTrunc), x + CARD_PADDING_X, descY, META_COLOR, false);

        var createdText = "created " + project.createdAt();
        var createdTrunc = font.plainSubstrByWidth(createdText, descMaxW);
        var createdY = descY + font.lineHeight + 1;
        graphics.drawString(font, Component.literal(createdTrunc), x + CARD_PADDING_X, createdY, LABEL_COLOR, false);
    }

    private void renderCreate(GuiGraphics graphics, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var formW = 280;
        var formH = 152;
        var formX = (this.width - formW) / 2;
        var formY = (this.height - formH) / 2;

        // Form background
        graphics.fill(formX, formY, formX + formW, formY + formH, PANEL_BG);
        graphics.fill(formX, formY, formX + formW, formY + 1, PANEL_BORDER);
        graphics.fill(formX, formY + formH - 1, formX + formW, formY + formH, PANEL_BORDER);
        graphics.fill(formX, formY, formX + 1, formY + formH, PANEL_BORDER);
        graphics.fill(formX + formW - 1, formY, formX + formW, formY + formH, PANEL_BORDER);

        var contentX = formX + 12;
        var contentW = formW - 24;
        var cursorY = formY + 10;

        graphics.drawString(font, Component.literal("Create Project"), contentX, cursorY, TITLE_COLOR, false);
        cursorY += font.lineHeight + 4;

        // Hint that projects live in the game dir, not the world — answers the most common question users have when
        // they realize the picker isn't world-scoped.
        var hint = "Projects live in your game directory and are shared across all worlds.";
        for (var line : font.split(Component.literal(hint), contentW)) {
            graphics.drawString(font, line, contentX, cursorY, META_COLOR, false);
            cursorY += font.lineHeight;
        }
        cursorY += 4;

        graphics.drawString(font, Component.literal("Name"), contentX, cursorY, LABEL_COLOR, false);
        cursorY += font.lineHeight + 2;
        nameInput.render(graphics, contentX, cursorY, contentW, mouseX, mouseY);
        cursorY += TextInput.HEIGHT + 6;

        graphics.drawString(font, Component.literal("Description"), contentX, cursorY, LABEL_COLOR, false);
        cursorY += font.lineHeight + 2;
        descriptionInput.render(graphics, contentX, cursorY, contentW, mouseX, mouseY);
        cursorY += TextInput.HEIGHT + 6;

        if (validationMessage != null) {
            graphics.drawString(font, Component.literal(validationMessage), contentX, cursorY, ERROR_COLOR, false);
        }

        var buttonY = formY + formH - BUTTON_HEIGHT - 8;
        createCancelButton = new Rect(contentX, buttonY, 60, BUTTON_HEIGHT);
        createButton = new Rect(formX + formW - 12 - 60, buttonY, 60, BUTTON_HEIGHT);
        renderButton(graphics, createCancelButton, "Cancel", mouseX, mouseY, BUTTON_TEXT);
        renderButton(graphics, createButton, pendingOp == ProjectOp.CREATE ? "Creating…" : "Create", mouseX, mouseY, BUTTON_TEXT);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        // Confirm dialog has top priority — clicks on its buttons fire the matching handler and dismiss; clicks
        // outside its buttons are intentional no-ops (destructive confirmations require an explicit decision).
        if (confirmDialog != null) {
            if (confirmDialog.mouseClicked(mouseX, mouseY, button)) {
                confirmDialog = null;
            }
            return true;
        }
        TextInput.clearFocus();
        if (mode == Mode.LIST) {
            if (newProjectButton != null && newProjectButton.contains(mouseX, mouseY)) {
                enterCreateMode();
                return true;
            }
            if (cancelButton != null && cancelButton.contains(mouseX, mouseY)) {
                Minecraft.getInstance().setScreen(null);
                return true;
            }
            for (var card : cardRects) {
                if (card.openRect.contains(mouseX, mouseY)) {
                    sendOpen(card.project.name());
                    return true;
                }
                if (card.deleteRect.contains(mouseX, mouseY)) {
                    sendDelete(card.project.name());
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        // CREATE mode
        if (createCancelButton != null && createCancelButton.contains(mouseX, mouseY)) {
            mode = Mode.LIST;
            validationMessage = null;
            nameInput.setContent("");
            descriptionInput.setContent("");
            return true;
        }
        if (createButton != null && createButton.contains(mouseX, mouseY)) {
            sendCreate();
            return true;
        }
        nameInput.mouseClicked(mouseX, mouseY, button);
        descriptionInput.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && TextInput.getDragSelecting() != null) {
            TextInput.endDragSelection();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        var dragInput = TextInput.getDragSelecting();
        if (dragInput != null && button == 0) {
            dragInput.mouseDraggedExtend(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mode == Mode.LIST) {
            var projects = ProjectSession.availableProjects();
            var contentH = projects.size() * (CARD_HEIGHT + CARD_GAP);
            var visibleH = this.height - CONTENT_PADDING * 2 - TOOLBAR_HEIGHT - FOOTER_HEIGHT - 30;
            var maxScroll = Math.max(0, contentH - visibleH);
            this.scrollY = Math.max(0, Math.min(maxScroll, this.scrollY - (int) (scrollY * 16)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        if (mode == Mode.CREATE) {
            var focused = TextInput.getFocused();
            if (focused != null && focused.charTyped(ch, modifiers)) {
                if (focused == nameInput) {
                    revalidateName();
                }
                return true;
            }
        }
        return super.charTyped(ch, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Confirm dialog absorbs Esc (treats it as Cancel) before the screen-level Esc handling below, so a stray
        // Esc tap doesn't dismiss the picker entirely while the user was answering a confirm.
        if (confirmDialog != null && confirmDialog.keyPressed(keyCode)) {
            confirmDialog = null;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (mode == Mode.CREATE) {
                mode = Mode.LIST;
                validationMessage = null;
                return true;
            }
            Minecraft.getInstance().setScreen(null);
            return true;
        }
        if (mode == Mode.CREATE) {
            var focused = TextInput.getFocused();
            if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) {
                if (focused == nameInput) {
                    revalidateName();
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                sendCreate();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void enterCreateMode() {
        mode = Mode.CREATE;
        nameInput.setContent("");
        descriptionInput.setContent("");
        validationMessage = null;
    }

    private void revalidateName() {
        try {
            EngineProjectIO.validateProjectName(nameInput.content());
            validationMessage = null;
        } catch (IllegalArgumentException e) {
            validationMessage = e.getMessage();
        }
    }

    private void sendCreate() {
        if (pendingOp != null) {
            return;
        }
        var name = nameInput.content();
        try {
            EngineProjectIO.validateProjectName(name);
        } catch (IllegalArgumentException e) {
            validationMessage = e.getMessage();
            return;
        }
        pendingOp = ProjectOp.CREATE;
        pendingProjectName = name;
        validationMessage = null;
        BLib.MOD.networking().sendToServer(new C2SCreateProjectPayload(name, descriptionInput.content()));
    }

    private void sendOpen(String name) {
        if (pendingOp != null) {
            return;
        }
        pendingOp = ProjectOp.OPEN;
        pendingProjectName = name;
        validationMessage = null;
        BLib.MOD.networking().sendToServer(new C2SOpenProjectPayload(name));
    }

    private void sendDelete(String name) {
        if (pendingOp != null || confirmDialog != null) {
            return;
        }
        // Destructive — never fire the packet directly from the click. Spawn the confirmation modal; only on
        // explicit confirm do we dispatch the delete.
        confirmDialog = new ConfirmDialog(
            "Delete project?",
            "Are you sure you want to delete '" + name + "'? This will remove the entire datapack folder from "
                + "the world's datapacks directory and cannot be undone.",
            "Delete",
            "Cancel",
            true,
            () -> {
                pendingOp = ProjectOp.DELETE;
                pendingProjectName = name;
                validationMessage = null;
                BLib.MOD.networking().sendToServer(new C2SDeleteProjectPayload(name));
            },
            () -> {}
        );
    }

    private void onOpResult(S2CProjectOpResultPayload result) {
        // The callback may fire for ops the picker didn't initiate (e.g. a Reload from the workspace's FILE menu in
        // a hypothetical future where the menu itself ran while picker is open — currently impossible but the guard
        // is cheap). Only react to ops we have a pending intent for, with a matching project name.
        if (pendingOp == null || pendingProjectName == null) {
            return;
        }
        if (result.op() != pendingOp || !pendingProjectName.equals(result.projectName())) {
            return;
        }
        var op = pendingOp;
        var projectName = pendingProjectName;
        pendingOp = null;
        pendingProjectName = null;

        if (!result.success()) {
            validationMessage = result.errorMessage().isEmpty() ? "Operation failed" : result.errorMessage();
            return;
        }
        switch (op) {
            case OPEN -> {
                var matched = findProject(projectName);
                if (matched != null) {
                    ProjectSession.setActiveProject(matched);
                }
                onConfirmedOpen.run();
            }
            case CREATE -> {
                mode = Mode.LIST;
                nameInput.setContent("");
                descriptionInput.setContent("");
                validationMessage = null;
            }
            case DELETE -> {
                // List refreshes via the S2CProjectListPayload the server sends right after the success ack —
                // nothing to do here.
            }
            default -> {}
        }
    }

    private static @Nullable ProjectInfo findProject(String name) {
        for (var p : ProjectSession.availableProjects()) {
            if (p.name().equals(name)) {
                return p;
            }
        }
        return null;
    }

    private static String pendingLabel(ProjectOp op) {
        return switch (op) {
            case CREATE -> "Creating";
            case DELETE -> "Deleting";
            case OPEN -> "Opening";
            case RELOAD -> "Reloading";
            case CAPTURE -> "Capturing";
        };
    }

    private record CardRect(
        ProjectInfo project,
        Rect openRect,
        Rect deleteRect
    ) {}

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
}
