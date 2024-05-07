package org.avp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.avp.common.item.AVPItems;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntityLithiumExplode {

    @Inject(at = @At("TAIL"), method = "tick", cancellable = true)
    void tick(CallbackInfo callbackInfo) {
        var self = MixinUtils.<ItemEntity>self(this);
        var level = self.level();

        if (!self.getItem().is(AVPItems.INSTANCE.dustLithium.get()))
            return;
        if (level.isClientSide || level.getGameTime() % 10 != 0)
            return;

        if (self.isInWater()) {
            this.explode(level, self);
            self.kill();
            return;
        }

        if (self.onGround() && this.canExplodeAtEntityPosition(level, self)) {
            this.explode(level, self);
            self.kill();
        }
    }

    private boolean canExplodeAtEntityPosition(Level level, Entity entity) {
        if (level.random.nextInt(25) != 0)
            return false;

        return level.isRainingAt(entity.blockPosition()) && level.canSeeSky(entity.blockPosition());
    }

    private void explode(Level level, Entity entity) {
        level.explode(entity, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 1F, Level.ExplosionInteraction.NONE);
    }
}
