package com.blib.internal.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(PlayerAdvancements.class)
public abstract class MixinPlayerAdvancements_Events {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "award", at = @At("RETURN"))
    private void blib$onAward(
        AdvancementHolder advancementHolder,
        String criterionKey,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        var listeners = BLibGlobalEvents.PLAYER_ADVANCEMENT_AWARD.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        for (var listener : listeners) {
            listener.invoke(player, advancementHolder, criterionKey);
        }
    }
}
