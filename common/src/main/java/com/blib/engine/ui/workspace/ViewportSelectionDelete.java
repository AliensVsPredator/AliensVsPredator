package com.blib.engine.ui.workspace;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.engine.command.api.Command;
import com.blib.engine.command.api.CommandBus;
import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelectionOps;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;

/**
 * Single deletion route for world-viewport selections. Context menus, the Delete key, and future viewport-selection
 * affordances should call here so every selectable type gets the same label, player safety guard, and command routing.
 */
@ApiStatus.Internal
public final class ViewportSelectionDelete {

    public static final String LABEL = "Delete";

    private ViewportSelectionDelete() {}

    public static boolean canDeleteCurrentSelection() {
        var selection = SelectionManager.current();
        if (selection.isEmpty()) {
            return false;
        }
        return selection.items().stream().anyMatch(ViewportSelectionDelete::canDelete);
    }

    public static boolean canDelete(@Nullable Selectable selectable) {
        if (selectable instanceof EntitySelectable es) {
            var entity = es.entity();
            return entity != null && canDeleteEntity(entity);
        }
        if (selectable instanceof BlockSelectable) {
            return canDeleteBlock();
        }
        if (selectable instanceof BlockVolumeSelectable) {
            return BlockSelectionOps.canDelete();
        }
        if (selectable instanceof PlacedJigsawPieceSelectable pjs) {
            return ClientPlacedPieceRegistry.get(pjs.id()) != null;
        }
        return false;
    }

    public static boolean deleteCurrentSelection(CommandBus commands) {
        var selection = SelectionManager.current();
        if (selection.isEmpty()) {
            return false;
        }
        var deleted = false;
        for (var selectable : selection.items()) {
            deleted |= delete(selectable, commands);
        }
        return deleted;
    }

    public static boolean delete(@Nullable Selectable selectable, CommandBus commands) {
        if (selectable instanceof EntitySelectable es) {
            var entity = es.entity();
            return entity != null && deleteEntity(entity, commands);
        }
        if (selectable instanceof BlockSelectable bs) {
            return deleteBlock(bs.pos(), commands);
        }
        if (selectable instanceof BlockVolumeSelectable) {
            return deleteBlockVolume();
        }
        if (selectable instanceof PlacedJigsawPieceSelectable pjs) {
            return deletePiece(pjs.id(), commands);
        }
        return false;
    }

    public static boolean deleteEntity(LivingEntity entity, CommandBus commands) {
        if (!canDeleteEntity(entity)) {
            return false;
        }
        commands.dispatch(new Command.RemoveEntity(entity.getId()));
        return true;
    }

    public static boolean deleteBlock(BlockPos pos, CommandBus commands) {
        if (!canDeleteBlock()) {
            return false;
        }
        var mc = Minecraft.getInstance();
        var dim = mc.player.level().dimension().location();
        commands.dispatch(new Command.DeleteBlockVolume(pos, pos, dim));
        return true;
    }

    public static boolean deleteBlockVolume() {
        if (!BlockSelectionOps.canDelete()) {
            return false;
        }
        BlockSelectionOps.delete();
        return true;
    }

    public static boolean deletePiece(UUID pieceId, CommandBus commands) {
        if (ClientPlacedPieceRegistry.get(pieceId) == null) {
            return false;
        }
        commands.dispatch(new Command.DeletePlacedPiece(pieceId));
        return true;
    }

    public static boolean canDeleteEntity(LivingEntity entity) {
        return !(entity instanceof Player);
    }

    private static boolean canDeleteBlock() {
        var mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null;
    }
}
