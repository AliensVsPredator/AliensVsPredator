package com.blib.engine.ui.workspace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbDismemberer;
import com.blib.engine.command.api.Command;
import com.blib.engine.command.api.CommandBus;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionOps;
import com.blib.engine.ui.panel.viewport.ViewportPanel;
import com.blib.engine.ui.popup.EntityContextMenuHandler;
import com.blib.engine.ui.popup.FactionManagePopup;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Builds the right-click context menus shown over the engine's viewport. Entity, block, block-volume, and
 * placed-jigsaw-piece triggers each open a different menu but share the same overlay surface (the workspace's open-menu
 * slot), close-on-outside-click absorber, and deletion route.
 * <p>
 * Implements both {@link ViewportPanel.RightClickHandler} (used by the viewport panel) and
 * {@link EntityContextMenuHandler} (used by panels like the outliner that need the same entity menu without holding a
 * viewport reference). The two interfaces converge here so the menu surface stays in lock-step.
 */
@ApiStatus.Internal
public final class ViewportContextMenuHandler implements ViewportPanel.RightClickHandler, EntityContextMenuHandler {

    /** Workspace-side operations the menus invoke. Implemented by {@code EngineWorkspaceScreen}. */
    public interface Host {

        void openMenu(DropdownMenu menu);

        void closeMenu();

        void openCaptureDialog();
    }

    private final CommandBus commands;

    private final Host host;

    public ViewportContextMenuHandler(CommandBus commands, Host host) {
        this.commands = commands;
        this.host = host;
    }

    @Override
    public void onRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY) {
        if (entity == null) {
            host.closeMenu();
            return;
        }
        openEntityMenu(entity, cursorX, cursorY);
    }

    @Override
    public void onEntityRightClick(LivingEntity entity, double mouseX, double mouseY) {
        openEntityMenu(entity, mouseX, mouseY);
    }

    private void openEntityMenu(LivingEntity entity, double cursorX, double cursorY) {
        var entityId = entity.getId();
        var entityUuid = entity.getUUID();
        var entityDisplayName = entity.getName().getString();
        var menuX = (int) cursorX;
        var menuY = (int) cursorY;
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("Manage Factions", () -> {
                // Anchor the popup at the original right-click point — by the time the menu item fires, the menu
                // itself has been dismissed, but the user expects the popup to land where their click was.
                FactionManagePopup.openAt(menuX, menuY, entityUuid, entityDisplayName);
            })
        );
        if (!(entity instanceof Player)) {
            if (entity instanceof Dismemberable) {
                var remaining = LimbDismemberer.getRemainingDefinitions(entity);
                if (!remaining.isEmpty()) {
                    var limbItems = new ArrayList<DropdownMenu.Item>();
                    limbItems.add(
                        new DropdownMenu.Item("All", () -> commands.dispatch(new Command.DismemberAllLimbs(entityId)))
                    );
                    for (var def : remaining) {
                        var label = prettifyLimbName(def.id().getPath());
                        var limbId = def.id();
                        limbItems.add(
                            new DropdownMenu.Item(label, () -> commands.dispatch(new Command.DismemberLimb(entityId, limbId)))
                        );
                    }
                    items.add(new DropdownMenu.Item("Dismember", () -> {}, limbItems));
                }
            }
            items.add(new DropdownMenu.Item(ViewportSelectionDelete.LABEL, () -> ViewportSelectionDelete.deleteEntity(entity, commands)));
        }

        host.openMenu(new DropdownMenu(menuX, menuY, items));
    }

    @Override
    public void onRightClickBlock(BlockPos pos, double cursorX, double cursorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item(ViewportSelectionDelete.LABEL, () -> ViewportSelectionDelete.deleteBlock(pos, commands)));
        host.openMenu(new DropdownMenu((int) cursorX, (int) cursorY, items));
    }

    /**
     * Right-click on the block-volume selection. Opens the same Capture/Cut/Copy/Paste/Delete menu the old inline
     * Selection panel surfaced. Ops self-gate when their preconditions aren't met, so disabled rendering isn't strictly
     * necessary — a no-op item just does nothing on click.
     */
    @Override
    public void onRightClickVolume(double cursorX, double cursorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("Capture…", host::openCaptureDialog));
        items.add(new DropdownMenu.Item("Cut", () -> BlockSelectionOps.copy(true)));
        items.add(new DropdownMenu.Item("Copy", () -> BlockSelectionOps.copy(false)));
        items.add(new DropdownMenu.Item("Paste", BlockSelectionOps::paste));
        items.add(new DropdownMenu.Item(ViewportSelectionDelete.LABEL, ViewportSelectionDelete::deleteBlockVolume));
        host.openMenu(new DropdownMenu((int) cursorX, (int) cursorY, items));
    }

    /**
     * Right-click on a placed jigsaw piece. Mirrors the volume menu so users get the same affordances plus Open
     * Inspector. Cut on a piece is Copy-then-DeletePiece so the registry entry is removed alongside the blocks — the
     * existing volume Cut would orphan the piece record (ghost outline over empty air).
     */
    @Override
    public void onRightClickPiece(UUID pieceId, double cursorX, double cursorY) {
        var items = new ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("Open Inspector", () -> {
                // Mutual exclusion: the volume wireframe must not coexist with a piece selection. performSelectionAt
                // clears the volume on every single-thing LMB pick; the context-menu path needs the same call.
                BlockSelection.clearVolume();
                SelectionManager.selectSingle(new PlacedJigsawPieceSelectable(pieceId));
            })
        );
        items.add(
            new DropdownMenu.Item("Capture…", () -> {
                PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                host.openCaptureDialog();
            })
        );
        items.add(
            new DropdownMenu.Item("Cut", () -> {
                PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                BlockSelectionOps.copy(false);
                ViewportSelectionDelete.deletePiece(pieceId, commands);
            })
        );
        items.add(
            new DropdownMenu.Item("Copy", () -> {
                PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                BlockSelectionOps.copy(false);
            })
        );
        items.add(new DropdownMenu.Item(ViewportSelectionDelete.LABEL, () -> ViewportSelectionDelete.deletePiece(pieceId, commands)));
        host.openMenu(new DropdownMenu((int) cursorX, (int) cursorY, items));
    }

    /**
     * Turn a limb id's path component ("left_arm", "head") into a display label ("Left Arm", "Head") for the Dismember
     * submenu. Splits on underscores and title-cases each segment; preserves any other characters in case authors used
     * mixed-case ids.
     */
    private static String prettifyLimbName(String path) {
        var parts = path.split("_");
        var sb = new StringBuilder();
        for (var i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            var part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
    }
}
