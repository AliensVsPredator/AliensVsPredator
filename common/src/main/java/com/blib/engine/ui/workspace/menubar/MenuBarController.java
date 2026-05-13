package com.blib.engine.ui.workspace.menubar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.layout.PanelRegistry;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Owns the workspace's menu-bar dropdown state: which top-level chip is open, the optional cascading submenu, and the
 * builders that turn each chip click into a fresh {@link DropdownMenu}. The hosting screen delegates every menu-related
 * lifecycle hook (open / close / hover-update / render / hit-test) to this controller, so it never has to know about
 * the cascade rules or the per-menu item lists.
 * <p>
 * Lives behind an {@link Actions} interface so the controller stays unaware of which workspace methods back each item —
 * adding a new menu item only requires editing the matching builder here and adding one method to {@link Actions}.
 */
@ApiStatus.Internal
public final class MenuBarController {

    /**
     * Workspace-side operations the menu items invoke. Implemented by {@code EngineWorkspaceScreen}; expressed as an
     * interface so the controller has zero direct screen dependencies.
     */
    public interface Actions {

        void dispatchUndo();

        void dispatchRedo();

        void openPreferencesDialog();

        void switchLayout(String id);

        void openSaveAsDialog();

        void openRenameDialog();

        void openDuplicateDialog();

        void openDeleteConfirm();

        void resetLayout();

        void openNewFromTemplateDialog(LayoutTemplate template);

        void openManageLayoutsDialog();

        void openLayoutsFolder();

        PanelRegistry.Context panelCtx();

        void reopenPanel(Class<? extends Panel> panelClass, Supplier<Panel> factory);

        void openPicker(boolean createMode);

        void reloadProject();

        /** Open a destructive confirm for project deletion; {@code onConfirm} runs on user confirmation. */
        void requestDeleteProjectConfirm(String projectName, Runnable onConfirm);

        void closeEngine();

        void deleteProject(String name);

        /** Active layout id, read for the LAYOUT menu's "•" prefix and Reset-eligibility check. */
        String activeLayoutId();
    }

    private final Actions actions;

    private @Nullable DropdownMenu openMenu;

    /**
     * One-level cascading submenu of {@link #openMenu} (e.g. Dismember… → limb list). Always cleared in lock-step with
     * {@link #openMenu} so a stale child can't outlive its parent.
     */
    private @Nullable DropdownMenu openSubmenu;

    /**
     * Index of the {@link #openMenu} item that {@link #openSubmenu} was spawned from. Tracked so hover-driven submenu
     * opening doesn't re-spawn the same submenu every frame, and so moving the cursor onto a different parent-menu item
     * swaps which submenu is shown. {@code null} whenever {@link #openSubmenu} is null.
     */
    private @Nullable Integer openSubmenuParentIndex;

    public MenuBarController(Actions actions) {
        this.actions = actions;
    }

    public @Nullable DropdownMenu openMenu() {
        return openMenu;
    }

    public @Nullable DropdownMenu openSubmenu() {
        return openSubmenu;
    }

    public boolean isAnyMenuOpen() {
        return openMenu != null;
    }

    /** Open the given menu as the active overlay. Used by both the global menu bar and panel-local menu bars. */
    public void open(DropdownMenu menu) {
        setOpenMenu(menu);
    }

    /** Close any open menu + submenu. */
    public void closeAll() {
        setOpenMenu(null);
    }

    private void setOpenMenu(@Nullable DropdownMenu menu) {
        this.openMenu = menu;
        closeSubmenu();
    }

    private void closeSubmenu() {
        this.openSubmenu = null;
        this.openSubmenuParentIndex = null;
    }

    /**
     * Hover-driven cascading-submenu opener. Runs once per frame: when the cursor sits on a parent-menu item with
     * children, spawn (or keep) the submenu for that item; when it sits on a leaf item, close any open submenu. Cursor
     * over the submenu itself or in the gap leaves state untouched so users can move diagonally from parent → submenu
     * without flicker.
     */
    public void updateHoverSubmenu(int mouseX, int mouseY, int logicalWidth, int logicalHeight) {
        if (openMenu == null) {
            closeSubmenu();
            return;
        }
        if (openSubmenu != null && openSubmenu.isInside(mouseX, mouseY)) {
            return;
        }
        if (!openMenu.isInside(mouseX, mouseY)) {
            return;
        }
        var idx = openMenu.hitItemAt(mouseX, mouseY);
        if (idx < 0) {
            return;
        }
        var item = openMenu.itemAt(idx);
        if (item.hasSubmenu()) {
            if (openSubmenuParentIndex == null || openSubmenuParentIndex != idx) {
                openSubmenu = DropdownMenu.spawnSubmenu(openMenu, idx, item.children(), logicalWidth, logicalHeight);
                openSubmenuParentIndex = idx;
            }
        } else {
            closeSubmenu();
        }
    }

    public void render(GuiGraphics graphics, int logicalMouseX, int logicalMouseY) {
        if (openMenu != null) {
            openMenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        if (openSubmenu != null) {
            openSubmenu.render(graphics, logicalMouseX, logicalMouseY);
        }
    }

    /** Whether {@code (mouseX, mouseY)} is inside the open menu or its submenu. */
    public boolean isInsideOpenMenu(double mouseX, double mouseY) {
        return (openMenu != null && openMenu.isInside(mouseX, mouseY))
            || (openSubmenu != null && openSubmenu.isInside(mouseX, mouseY));
    }

    /**
     * Outcome of a click while menus may be open. Lets the screen decide whether to keep dispatching the click after
     * the menu state finishes reacting.
     */
    public enum ClickOutcome {

        /** No menu was open; the screen should run its normal click pipeline. */
        NO_MENU_OPEN,

        /** Click hit an item, or was absorbed inside an open menu's frame; the screen should not dispatch further. */
        CONSUMED,

        /**
         * Click closed the open menu and didn't hit an item. The screen may want to re-handle the click as a chip click
         * (so the user can close-and-reopen by clicking a different chip in one motion).
         */
        CLOSED_TRY_CHIP_REOPEN
    }

    /**
     * Apply a click against any open menu or submenu. Encapsulates the absorber behaviour: a click on an item runs the
     * action; a click on a submenu-parent row re-spawns the child; a click outside an item closes the menu (and signals
     * the screen to consider re-handling for the same-chip-different-menu case).
     */
    public ClickOutcome handleClick(double logicalX, double logicalY, int button, int logicalWidth, int logicalHeight) {
        if (openSubmenu != null && button == 0 && openSubmenu.isInside(logicalX, logicalY)) {
            var subIdx = openSubmenu.hitItemAt(logicalX, logicalY);
            if (subIdx >= 0) {
                var subItem = openSubmenu.itemAt(subIdx);
                setOpenMenu(null);
                subItem.action().run();
                return ClickOutcome.CONSUMED;
            }
            // Inside submenu but on a border / dead row: consume and keep both menus open.
            return ClickOutcome.CONSUMED;
        }
        if (openMenu != null) {
            if (button == 0) {
                var idx = openMenu.hitItemAt(logicalX, logicalY);
                if (idx >= 0) {
                    var item = openMenu.itemAt(idx);
                    if (item.hasSubmenu()) {
                        // Submenus open on hover (see updateHoverSubmenu); a click on the parent item is a no-op that
                        // just keeps everything open. Defensive re-spawn in case hover never fired for this item.
                        if (openSubmenuParentIndex == null || openSubmenuParentIndex != idx) {
                            openSubmenu = DropdownMenu.spawnSubmenu(openMenu, idx, item.children(), logicalWidth, logicalHeight);
                            openSubmenuParentIndex = idx;
                        }
                        return ClickOutcome.CONSUMED;
                    }
                    setOpenMenu(null);
                    item.action().run();
                    return ClickOutcome.CONSUMED;
                }
            }
            setOpenMenu(null);
            return ClickOutcome.CLOSED_TRY_CHIP_REOPEN;
        }
        return ClickOutcome.NO_MENU_OPEN;
    }

    public DropdownMenu buildEditMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("Undo", actions::dispatchUndo));
        items.add(new DropdownMenu.Item("Redo", actions::dispatchRedo));
        items.add(new DropdownMenu.Item("Preferences…", actions::openPreferencesDialog));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * View dropdown — toggles that affect what's drawn in the viewport without changing project state. Items use a
     * leading "✓" prefix when on / blank prefix when off ({@link DropdownMenu} doesn't have a checkbox UI, so the
     * label-prefix idiom is the cheapest way to convey toggle state).
     */
    public DropdownMenu buildViewMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        var territoryOn = com.blib.engine.territory.ClaimPaintTool.isOverlayVisible();
        items.add(
            new DropdownMenu.Item(
                (territoryOn ? "✓ " : "   ") + "Show Territory Claims",
                com.blib.engine.territory.ClaimPaintTool::toggleOverlayVisible
            )
        );
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * Build the WINDOW dropdown by iterating {@link PanelRegistry} — every body-eligible panel gets a
     * {@code "Reopen <title>"} entry where the title comes from the panel's own {@link Panel#title()}. Renaming a
     * panel's title automatically updates the menu label since both paths read the same string.
     */
    public DropdownMenu buildWindowMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        for (var id : PanelRegistry.orderedIds()) {
            var sample = PanelRegistry.create(id, actions.panelCtx());
            if (sample == null) {
                continue;
            }
            var displayName = sample.title();
            var panelClass = sample.getClass();
            items.add(
                new DropdownMenu.Item(
                    "Reopen " + displayName,
                    () -> actions.reopenPanel(panelClass, () -> PanelRegistry.create(id, actions.panelCtx()))
                )
            );
        }
        items.add(new DropdownMenu.Item("Reset Layout", actions::resetLayout));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    public DropdownMenu buildLayoutMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        var activeId = actions.activeLayoutId();
        for (var doc : LayoutCatalog.listAll()) {
            var prefix = doc.id().equals(activeId) ? "• " : "  ";
            var suffix = LayoutCatalog.isTemplateId(doc.id()) ? "  (template)" : "";
            items.add(new DropdownMenu.Item(prefix + doc.displayName() + suffix, () -> actions.switchLayout(doc.id())));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        items.add(new DropdownMenu.Item("Save As New…", actions::openSaveAsDialog));
        items.add(new DropdownMenu.Item("Rename…", actions::openRenameDialog));
        items.add(new DropdownMenu.Item("Duplicate…", actions::openDuplicateDialog));
        items.add(new DropdownMenu.Item("Delete…", actions::openDeleteConfirm));
        var current = LayoutCatalog.get(activeId);
        var canReset = LayoutCatalog.isTemplateId(activeId) || (current != null && current.templateBase() != null);
        if (canReset) {
            items.add(new DropdownMenu.Item("Reset to Template", actions::resetLayout));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        for (var t : LayoutTemplate.all()) {
            items.add(new DropdownMenu.Item("New from " + t.displayName() + "…", () -> actions.openNewFromTemplateDialog(t)));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        items.add(new DropdownMenu.Item("Manage Layouts…", actions::openManageLayoutsDialog));
        items.add(new DropdownMenu.Item("Show Layouts Folder", actions::openLayoutsFolder));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    public DropdownMenu buildProjectMenu(int anchorX, int anchorY) {
        var hasProject = ProjectSession.activeProject() != null;
        var hasWorld = Minecraft.getInstance().level != null;
        var items = new ArrayList<DropdownMenu.Item>();

        // Project CRUD requires the integrated server (server-side handlers for the picker / reload / delete payloads).
        // With no world, all four items are inert — surface that in the label.
        var noWorldSuffix = hasWorld ? "" : " (no world)";
        items.add(new DropdownMenu.Item("Create…" + noWorldSuffix, () -> {
            if (!hasWorld) {
                return;
            }
            actions.openPicker(true);
        }));
        items.add(new DropdownMenu.Item("Open…" + noWorldSuffix, () -> {
            if (!hasWorld) {
                return;
            }
            actions.openPicker(false);
        }));
        items.add(
            new DropdownMenu.Item(hasProject ? "Reload" : "Reload" + (hasWorld ? " (no project)" : noWorldSuffix), () -> {
                if (!hasWorld || !hasProject) {
                    return;
                }
                actions.reloadProject();
            })
        );
        items.add(
            new DropdownMenu.Item(hasProject ? "Delete…" : "Delete…" + (hasWorld ? " (no project)" : noWorldSuffix), () -> {
                if (!hasWorld || !hasProject) {
                    return;
                }
                // Destructive — gate behind the modal confirm. On confirm we fire the delete and bounce back to
                // the picker (the workspace is meaningless once its project disappears).
                var name = ProjectSession.activeProjectName();
                actions.requestDeleteProjectConfirm(name, () -> {
                    actions.deleteProject(name);
                    actions.openPicker(false);
                });
            })
        );
        items.add(new DropdownMenu.Item("Close Engine", actions::closeEngine));
        return new DropdownMenu(anchorX, anchorY, items);
    }
}
