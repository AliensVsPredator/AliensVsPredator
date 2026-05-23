package com.blib.neoforge.internal.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.common.territory.BLibTerritoryManager;
import com.blib.mod.BLib;

@ApiStatus.Internal
public final class BLibNeoForgeTerritoryEvents {

    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!BLib.checkBlockInteractProtection(event.getLevel(), event.getEntity(), event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        var target = event.getTarget();

        if (!BLib.checkEntityInteractProtection(event.getLevel(), event.getEntity(), target.blockPosition())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    public static void onEntityAttack(AttackEntityEvent event) {
        var target = event.getTarget();

        if (target instanceof Player) {
            if (!BLib.checkPvpProtection(target.level(), target.blockPosition())) {
                event.setCanceled(true);
            }
        } else if (!(target instanceof LivingEntity)) {
            if (!BLib.checkNonLivingEntityAttackProtection(target.level(), event.getEntity(), target.blockPosition())) {
                event.setCanceled(true);
            }
        }
    }

    public static void onMobGriefing(EntityMobGriefingEvent event) {
        var entity = event.getEntity();

        if (entity.level().isClientSide || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        var chunkPos = new ChunkPos(entity.blockPosition());

        if (!BLibTerritoryManager.INSTANCE.allowMobGriefingAt(serverLevel, chunkPos)) {
            event.setCanGrief(false);
        }
    }

    private BLibNeoForgeTerritoryEvents() {
        throw new UnsupportedOperationException();
    }
}
