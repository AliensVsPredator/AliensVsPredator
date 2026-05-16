package com.blib.internal.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.api.client.render.v1.item.BLibItemTransformOverrides;

/**
 * Routes "is the local player using their main-hand item?" queries to the {@code force-blocking} debug toggle. When the
 * toggle is on, the local client's {@link LivingEntity#isUsingItem()}, {@link LivingEntity#getUseItem()},
 * {@link LivingEntity#getUsedItemHand()}, and {@link LivingEntity#getUseItemRemainingTicks()} all report as if the
 * player were actively raising the item — so vanilla's {@code PlayerRenderer.getArmPose} sees {@code BLOCK} and the
 * third-person arm goes up, the held-item rendering switches to the blocking pose, etc.
 * <p>
 * Crucially this is restricted to the {@link Minecraft#player} instance — the server-side {@code ServerPlayer} (which
 * has a different identity even in single-player's integrated server) is not affected, so game state stays consistent:
 * the user can still walk and attack normally while their visual avatar appears to be blocking.
 * <p>
 * Mixin lives in the client list of {@code blib.mixins.json} so it doesn't load on dedicated servers.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_ForceBlocking {

    @Inject(method = "isUsingItem", at = @At("HEAD"), cancellable = true)
    private void blib$forceUsingItem(CallbackInfoReturnable<Boolean> cir) {
        if (blib$shouldForce()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getUseItem", at = @At("HEAD"), cancellable = true)
    private void blib$forceUseItem(CallbackInfoReturnable<ItemStack> cir) {
        if (blib$shouldForce()) {
            var self = (LivingEntity) (Object) this;
            cir.setReturnValue(self.getMainHandItem());
        }
    }

    @Inject(method = "getUsedItemHand", at = @At("HEAD"), cancellable = true)
    private void blib$forceUsedItemHand(CallbackInfoReturnable<InteractionHand> cir) {
        if (blib$shouldForce()) {
            cir.setReturnValue(InteractionHand.MAIN_HAND);
        }
    }

    @Inject(method = "getUseItemRemainingTicks", at = @At("HEAD"), cancellable = true)
    private void blib$forceUseItemRemainingTicks(CallbackInfoReturnable<Integer> cir) {
        if (blib$shouldForce()) {
            // Any positive value satisfies vanilla's "is currently using" check in PlayerRenderer.getArmPose.
            // 100 is a comfortable buffer that won't tick down to zero from anything since the override
            // re-asserts every frame.
            cir.setReturnValue(100);
        }
    }

    @Unique
    private boolean blib$shouldForce() {
        if (!BLibItemTransformOverrides.isForceBlockingEnabled()) {
            return false;
        }

        var self = (LivingEntity) (Object) this;
        var mc = Minecraft.getInstance();

        // Restrict to the local client player. The integrated-server ServerPlayer is a different instance,
        // so this check is false there — game logic on the server side runs unmodified, only the client's
        // visual representation of the local player gets the spoofed use state.
        return mc != null && self == mc.player;
    }
}
