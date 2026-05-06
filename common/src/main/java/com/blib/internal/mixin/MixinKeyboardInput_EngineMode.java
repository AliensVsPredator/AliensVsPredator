package com.blib.internal.mixin;

import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.engine.EngineMode;

/**
 * While engine mode is active, neutralize player movement input so the body stays put while the freecam wanders. We
 * read the same key state ourselves in {@link com.blib.internal.client.engine.EngineNavigation} (since the keys
 * are shared), but the player's {@link KeyboardInput} should report no impulse.
 */
@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput_EngineMode {

    @Inject(method = "tick", at = @At("RETURN"))
    private void blib$zeroPlayerImpulseInEngineMode(boolean isSneaking, float sneakingSpeedMultiplier, CallbackInfo ci) {
        if (!EngineMode.get().isActive()) {
            return;
        }

        var self = (KeyboardInput) (Object) this;
        self.up = false;
        self.down = false;
        self.left = false;
        self.right = false;
        self.jumping = false;
        self.shiftKeyDown = false;
        self.forwardImpulse = 0;
        self.leftImpulse = 0;
    }
}
