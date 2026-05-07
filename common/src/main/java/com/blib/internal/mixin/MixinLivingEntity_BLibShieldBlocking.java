package com.blib.internal.mixin;

import com.blib.api.common.shield.v1.BLibShieldItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Drives BLib's shield blocking pipeline for items implementing {@link BLibShieldItem}. Two injections:
 * <ol>
 *   <li>{@code isDamageSourceBlocked} returns {@code false} when the user is using a {@code BLibShieldItem},
 *       so vanilla's shield damage path (durability damage, {@code blockUsingShield} knockback,
 *       {@code hurtCurrentlyUsedShield}) does not fire — BLib handles everything itself.</li>
 *   <li>{@code hurt} runs at HEAD with cancellable=true. After cone check + {@link BLibShieldItem#onBlocked}:
 *       <ul>
 *         <li>Full block (reduction == 1) cancels {@code hurt} entirely with a {@code false} return so vanilla
 *             skips its damage broadcast — that's the broadcast that drives the red-flash and screen-shake
 *             feedback. Cancelling avoids both, matching vanilla shield behavior.</li>
 *         <li>Partial reduction recursively re-enters {@code hurt} with the reduced amount, guarded by a
 *             reentrancy flag so we don't loop. Vanilla flow then handles the leftover damage as a normal
 *             hit (red flash, knockback, etc.) — the player still took some damage, so seeing some feedback
 *             is correct.</li>
 *       </ul>
 *   </li>
 * </ol>
 * <p>
 * On disable, the mixin both calls {@code Player.getCooldowns().addCooldown} (which {@code ServerItemCooldowns}
 * propagates to the client to drive the cooldown overlay sweep) and broadcasts entity event 30, the same cue
 * vanilla {@code Player.disableShield} fires for the disabled-shield sound effect.
 * <p>
 * Server-side only — guard at top of the inject mirrors the {@code isClientSide} guard early in vanilla
 * {@code hurt}.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_BLibShieldBlocking {

    /**
     * Re-entrancy guard. Server-thread single-threaded, so a static flag is safe — no per-instance state needed.
     * When the partial-reduction branch recursively calls {@code self.hurt(source, reducedAmount)}, the inject
     * fires again on the recursive entry; this flag short-circuits it so vanilla flow proceeds with the reduced
     * amount instead of looping.
     */
    private static boolean blib$reentrant = false;

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void blib$bypassVanillaForBLibShield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        var self = (LivingEntity) (Object) this;

        if (self.getUseItem().getItem() instanceof BLibShieldItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void blib$applyShieldBlocking(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (blib$reentrant) return;

        var self = (LivingEntity) (Object) this;

        if (self.level().isClientSide()) return;
        if (amount <= 0f) return;
        if (source.is(DamageTypeTags.BYPASSES_SHIELD)) return;
        if (!self.isBlocking()) return;

        var useItem = self.getUseItem();
        if (!(useItem.getItem() instanceof BLibShieldItem shield)) return;

        var config = shield.getShieldConfig();
        if (!blib$isInBlockCone(self, source, config.blockAngleDegrees())) return;

        var outcome = shield.onBlocked(self, useItem, source, amount);

        if (config.blockSound() != null) {
            self.level().playSound(
                null,
                self.getX(), self.getY(), self.getZ(),
                config.blockSound(),
                self.getSoundSource(),
                1.0F,
                0.8F + self.getRandom().nextFloat() * 0.4F
            );
        }

        if (outcome.disable()) {
            if (self instanceof Player player) {
                player.getCooldowns().addCooldown(useItem.getItem(), outcome.disableDurationTicks());
            }
            self.stopUsingItem();
            // Vanilla parity: Player.disableShield broadcasts entity event 30, which drives the client-side
            // shield-disabled sound. Without it the cooldown packet still arrives (so the icon overlay sweeps)
            // but the audible cue is missing.
            self.level().broadcastEntityEvent(self, (byte) 30);
        }

        if (outcome.damageReduction() >= 1f) {
            // Fully blocked. Cancel hurt entirely so vanilla skips its damage broadcast (red flash + shake).
            cir.setReturnValue(false);
            return;
        }

        // Partial reduction. Re-enter hurt with the reduced amount; vanilla flow handles the residual hit
        // including its damage broadcast. The reentrancy flag prevents this inject from looping.
        var reducedAmount = amount * (1f - outcome.damageReduction());
        blib$reentrant = true;
        try {
            cir.setReturnValue(self.hurt(source, reducedAmount));
        } finally {
            blib$reentrant = false;
        }
    }

    private static boolean blib$isInBlockCone(LivingEntity self, DamageSource source, float blockAngleDegrees) {
        if (blockAngleDegrees >= 360f) return true;

        Vec3 sourcePos = source.getSourcePosition();
        if (sourcePos == null) return false;

        Vec3 viewVec = self.calculateViewVector(0.0F, self.getYHeadRot());
        Vec3 toSourceXZ = sourcePos.subtract(self.position());
        toSourceXZ = new Vec3(toSourceXZ.x, 0.0, toSourceXZ.z).normalize();
        Vec3 viewVecXZ = new Vec3(viewVec.x, 0.0, viewVec.z).normalize();

        double dot = toSourceXZ.dot(viewVecXZ);
        double cosHalfAngle = Math.cos(Math.toRadians(blockAngleDegrees / 2.0));
        return dot >= cosHalfAngle;
    }
}
