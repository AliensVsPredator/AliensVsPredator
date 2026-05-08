package com.blib.internal.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.engine.EngineMode;

/**
 * When engine mode is active, override the camera position/rotation that vanilla {@code Camera.setup} just computed
 * from the player so the freecam transform takes effect, lerped by partial-tick for smooth motion at frame rates above
 * the tick rate. Also forces the camera into "detached" state so the player body renders (vanilla skips first-person
 * self-render based on {@code Camera.isDetached}). We inject at RETURN — after vanilla has finalized its own math — and
 * unconditionally overwrite, so all downstream consumers see our values.
 */
@Mixin(Camera.class)
public abstract class MixinCamera_EngineMode {

    @Invoker("setPosition")
    public abstract void blib$invokeSetPosition(double x, double y, double z);

    @Invoker("setRotation")
    public abstract void blib$invokeSetRotation(float yRot, float xRot);

    @Accessor("detached")
    public abstract void blib$setDetached(boolean detached);

    @Inject(method = "setup", at = @At("RETURN"))
    private void blib$applyEngineCameraOverride(
        BlockGetter level,
        Entity entity,
        boolean detached,
        boolean thirdPersonReverse,
        float partialTick,
        CallbackInfo ci
    ) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        blib$invokeSetRotation(session.interpolatedYaw(partialTick), session.interpolatedPitch(partialTick));
        var pos = session.interpolatedPosition(partialTick);
        blib$invokeSetPosition(pos.x, pos.y, pos.z);
        // Mark detached so first-person-only rendering paths (player body, hand) treat us as third-person.
        blib$setDetached(true);
    }
}
