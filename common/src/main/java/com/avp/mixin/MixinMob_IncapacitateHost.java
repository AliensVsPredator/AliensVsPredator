package com.avp.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.FreeMob;

@Mixin(Mob.class)
public abstract class MixinMob_IncapacitateHost extends LivingEntity implements FreeMob {

    protected MixinMob_IncapacitateHost(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected boolean isImmobile() {
        if (this.getPassengers().stream().anyMatch(entity -> entity.getType().is(AVPEntityTypeTags.PARASITES))) {
            return true;
        }

        return super.isImmobile();
    }

    @Override
    public boolean isUsingItem() {
        if (this.getPassengers().stream().anyMatch(entity -> entity.getType().is(AVPEntityTypeTags.PARASITES))) {
            return false;
        }

        return super.isUsingItem();
    }

    @Override
    public void removeFreedom() {
        var self = Mob.class.cast(this);
        self.xxa = 0;
        self.zza = 0;
        self.yya = 0;
        self.yBodyRot = 0;
        self.setSpeed(0.0f);
        self.setAggressive(false);
        self.setSilent(true);
    }

    @Override
    public void restoreFreedom() {
        var self = Mob.class.cast(this);
        self.setAggressive(true);
        self.setSilent(false);
    }
}
