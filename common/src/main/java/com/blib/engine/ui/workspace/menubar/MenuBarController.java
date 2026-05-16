package com.blib.engine.ui.workspace.menubar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.layout.PanelRegistry;
import com.blib.engine.modeler.animation.AnimationCollisionState;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.workspace.HoverOverlayRenderer;

/**
 * Owns the workspace's menu-bar dropdown state: which top-level chip is open, any cascading submenus, and the
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

    /** Cascading submenus, ordered from the child of {@link #openMenu} to the deepest visible submenu. */
    private final ArrayList<DropdownMenu> openSubmenus = new ArrayList<>();

    /** Parent item index for each entry in {@link #openSubmenus}; index 0 belongs to {@link #openMenu}. */
    private final ArrayList<Integer> openSubmenuParentIndices = new ArrayList<>();

    public MenuBarController(Actions actions) {
        this.actions = actions;
    }

    public @Nullable DropdownMenu openMenu() {
        return openMenu;
    }

    public @Nullable DropdownMenu openSubmenu() {
        return openSubmenus.isEmpty() ? null : openSubmenus.get(0);
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
        openSubmenus.clear();
        openSubmenuParentIndices.clear();
    }

    /**
     * Hover-driven cascading-submenu opener. Runs once per frame: when the cursor sits on a menu item with children,
     * spawn (or keep) the next-level submenu for that item; when it sits on a leaf item, close that item's descendants.
     * Cursor in the gap between cascades leaves state untouched so users can move diagonally between menus.
     */
    public void updateHoverSubmenu(int mouseX, int mouseY, int logicalWidth, int logicalHeight) {
        fitOpenMenuToViewport(logicalWidth, logicalHeight);
        if (openMenu == null) {
            closeSubmenu();
            return;
        }

        var level = menuLevelAt(mouseX, mouseY);
        if (level < 0) {
            return;
        }
        var menu = menuAtLevel(level);
        if (menu == null) {
            return;
        }

        var idx = menu.hitItemAt(mouseX, mouseY);
        if (idx < 0) {
            return;
        }
        var item = menu.itemAt(idx);
        if (!item.enabled()) {
            trimSubmenusFromLevel(level + 1);
            return;
        }
        if (item.hasSubmenu()) {
            ensureSubmenu(level, menu, idx, item, logicalWidth, logicalHeight);
        } else {
            trimSubmenusFromLevel(level + 1);
        }
    }

    public void render(GuiGraphics graphics, int logicalMouseX, int logicalMouseY, int logicalWidth, int logicalHeight) {
        fitOpenMenuToViewport(logicalWidth, logicalHeight);
        if (openMenu != null) {
            openMenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        for (var submenu : openSubmenus) {
            submenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        Component disabledTooltip = null;
        for (var i = openSubmenus.size() - 1; i >= 0 && disabledTooltip == null; i--) {
            disabledTooltip = openSubmenus.get(i).disabledTooltipAt(logicalMouseX, logicalMouseY);
        }
        if (disabledTooltip == null && openMenu != null) {
            disabledTooltip = openMenu.disabledTooltipAt(logicalMouseX, logicalMouseY);
        }
        if (disabledTooltip != null) {
            HoverOverlayRenderer.drawTooltipBox(graphics, disabledTooltip, logicalMouseX, logicalMouseY, logicalWidth, logicalHeight);
        }
    }

    /** Whether {@code (mouseX, mouseY)} is inside the open menu or its submenu. */
    public boolean isInsideOpenMenu(double mouseX, double mouseY) {
        return menuLevelAt(mouseX, mouseY) >= 0;
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
        fitOpenMenuToViewport(logicalWidth, logicalHeight);
        if (openMenu != null) {
            if (button == 0) {
                var level = menuLevelAt(logicalX, logicalY);
                var menu = menuAtLevel(level);
                if (menu == null) {
                    setOpenMenu(null);
                    return ClickOutcome.CLOSED_TRY_CHIP_REOPEN;
                }
                var idx = menu.hitItemAt(logicalX, logicalY);
                if (idx >= 0) {
                    var item = menu.itemAt(idx);
                    if (!item.enabled()) {
                        return ClickOutcome.CONSUMED;
                    }
                    if (item.hasSubmenu()) {
                        // Submenus open on hover (see updateHoverSubmenu); a click on the parent item is a no-op that
                        // just keeps everything open. Defensive re-spawn in case hover never fired for this item.
                        ensureSubmenu(level, menu, idx, item, logicalWidth, logicalHeight);
                        return ClickOutcome.CONSUMED;
                    }
                    setOpenMenu(null);
                    item.action().run();
                    return ClickOutcome.CONSUMED;
                }
                if (level >= 0) {
                    return ClickOutcome.CONSUMED;
                }
            }
            setOpenMenu(null);
            return ClickOutcome.CLOSED_TRY_CHIP_REOPEN;
        }
        return ClickOutcome.NO_MENU_OPEN;
    }

    private void fitOpenMenuToViewport(int logicalWidth, int logicalHeight) {
        if (openMenu != null && openMenu.fitRootToViewport(logicalWidth, logicalHeight)) {
            closeSubmenu();
        }
    }

    private int menuLevelAt(double mouseX, double mouseY) {
        for (var level = openSubmenus.size(); level >= 1; level--) {
            if (openSubmenus.get(level - 1).isInside(mouseX, mouseY)) {
                return level;
            }
        }
        return openMenu != null && openMenu.isInside(mouseX, mouseY) ? 0 : -1;
    }

    private @Nullable DropdownMenu menuAtLevel(int level) {
        if (level == 0) {
            return openMenu;
        }
        var submenuIndex = level - 1;
        return submenuIndex >= 0 && submenuIndex < openSubmenus.size() ? openSubmenus.get(submenuIndex) : null;
    }

    private void ensureSubmenu(
        int parentLevel,
        DropdownMenu parent,
        int parentItemIndex,
        DropdownMenu.Item item,
        int logicalWidth,
        int logicalHeight
    ) {
        var childLevel = parentLevel + 1;
        var childIndex = childLevel - 1;
        if (childIndex < openSubmenuParentIndices.size() && openSubmenuParentIndices.get(childIndex) == parentItemIndex) {
            trimSubmenusFromLevel(childLevel + 1);
            return;
        }

        trimSubmenusFromLevel(childLevel);
        openSubmenus.add(DropdownMenu.spawnSubmenu(parent, parentItemIndex, item.children(), logicalWidth, logicalHeight));
        openSubmenuParentIndices.add(parentItemIndex);
    }

    private void trimSubmenusFromLevel(int childLevel) {
        var firstSubmenuIndex = childLevel - 1;
        while (openSubmenus.size() > firstSubmenuIndex && firstSubmenuIndex >= 0) {
            openSubmenus.remove(openSubmenus.size() - 1);
            openSubmenuParentIndices.remove(openSubmenuParentIndices.size() - 1);
        }
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
        var territoryOn = ClaimPaintTool.isOverlayVisible();
        items.add(
            new DropdownMenu.Item(
                (territoryOn ? "✓ " : "   ") + "Show Territory Claims",
                ClaimPaintTool::toggleOverlayVisible
            )
        );
        var collisionsOn = AnimationCollisionState.get().isEnabled();
        items.add(
            new DropdownMenu.Item(
                (collisionsOn ? "✓ " : "   ") + "Animation Collision Detection",
                AnimationCollisionState.get()::toggleEnabled
            )
        );
        var collisionDebugTooltipsOn = AnimationCollisionState.get().isDebugTooltipsEnabled();
        items.add(
            new DropdownMenu.Item(
                (collisionDebugTooltipsOn ? "✓ " : "   ") + "Animation Collision Debug Tooltips",
                AnimationCollisionState.get()::toggleDebugTooltipsEnabled
            )
        );
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * Build the WINDOW dropdown from {@link PanelRegistry}'s domain metadata. Every body-eligible panel gets an entry
     * under its domain submenu, with the title coming from the panel's own {@link Panel#title()}. Renaming a panel's
     * title automatically updates the menu label since both paths read the same string.
     */
    public DropdownMenu buildWindowMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        var domains = new ArrayList<>(PanelRegistry.orderedDomains());
        domains.sort(Comparator.comparing(PanelRegistry.Domain::label, String.CASE_INSENSITIVE_ORDER));
        for (var domain : domains) {
            var children = buildWindowDomainItems(domain);
            if (!children.isEmpty()) {
                items.add(new DropdownMenu.Item(domain.label(), () -> {}, children));
            }
        }
        return new DropdownMenu(anchorX, anchorY, items);
    }

    private ArrayList<DropdownMenu.Item> buildWindowDomainItems(PanelRegistry.Domain domain) {
        var items = new ArrayList<DropdownMenu.Item>();
        for (var id : PanelRegistry.orderedIds(domain)) {
            var sample = PanelRegistry.create(id, actions.panelCtx());
            if (sample == null) {
                continue;
            }
            var displayName = sample.title();
            var panelClass = sample.getClass();
            items.add(
                new DropdownMenu.Item(
                    displayName,
                    () -> actions.reopenPanel(panelClass, () -> PanelRegistry.create(id, actions.panelCtx()))
                )
            );
        }
        items.sort(Comparator.comparing(DropdownMenu.Item::label, String.CASE_INSENSITIVE_ORDER));
        return items;
    }

    public DropdownMenu buildLayoutMenu(int anchorX, int anchorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        var templateItems = new ArrayList<DropdownMenu.Item>();
        var activeId = actions.activeLayoutId();
        var layoutDocs = new ArrayList<>(LayoutCatalog.listAll());
        layoutDocs.sort(Comparator.comparing(LayoutDoc::displayName, String.CASE_INSENSITIVE_ORDER));
        for (var doc : layoutDocs) {
            var prefix = doc.id().equals(activeId) ? "• " : "  ";
            var target = LayoutCatalog.isTemplateId(doc.id()) ? templateItems : items;
            target.add(new DropdownMenu.Item(prefix + doc.displayName(), () -> actions.switchLayout(doc.id())));
        }
        if (!templateItems.isEmpty()) {
            items.add(new DropdownMenu.Item("Templates", () -> {}, templateItems));
        }
        items.add(DropdownMenu.Item.divider());
        items.add(new DropdownMenu.Item("Save As New…", actions::openSaveAsDialog));
        items.add(new DropdownMenu.Item("Rename…", actions::openRenameDialog));
        items.add(new DropdownMenu.Item("Duplicate…", actions::openDuplicateDialog));
        items.add(new DropdownMenu.Item("Delete…", actions::openDeleteConfirm));
        var current = LayoutCatalog.get(activeId);
        var canReset = LayoutCatalog.isTemplateId(activeId) || (current != null && current.templateBase() != null);
        if (canReset) {
            items.add(new DropdownMenu.Item("Reset to Template", actions::resetLayout));
        }
        items.add(DropdownMenu.Item.divider());
        items.add(new DropdownMenu.Item("New", () -> {}, buildLayoutNewItems()));
        items.add(DropdownMenu.Item.divider());
        items.add(new DropdownMenu.Item("Manage Layouts…", actions::openManageLayoutsDialog));
        items.add(new DropdownMenu.Item("Show Layouts Folder", actions::openLayoutsFolder));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    private ArrayList<DropdownMenu.Item> buildLayoutNewItems() {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("From Template", () -> {}, buildLayoutNewFromTemplateItems()));
        return items;
    }

    private ArrayList<DropdownMenu.Item> buildLayoutNewFromTemplateItems() {
        var items = new ArrayList<DropdownMenu.Item>();
        for (var template : LayoutTemplate.all()) {
            items.add(new DropdownMenu.Item(template.displayName() + "…", () -> actions.openNewFromTemplateDialog(template)));
        }
        return items;
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
