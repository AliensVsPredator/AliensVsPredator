package com.avp.common.entity.projectile;

import com.avp.common.effect.AVPEffects;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThrownGrenade extends BouncingItemProjectile {

    private static final String IS_INCENDIARY_KEY = "IsIncendiary";

    private static final String IS_IRRADIATED_KEY = "IsIrradiated";

    private boolean isIncendiary;

    private boolean isIrradiated;

    public ThrownGrenade(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
        this.shouldBounce = true;
        this.maxLife = 5 * 20;
    }

    public ThrownGrenade(Level level, LivingEntity livingEntity) {
        super(AVPEntityTypes.GRENADE_THROWN, livingEntity, level);
        this.shouldBounce = true;
        this.maxLife = 5 * 20;
    }


    @Override
    public void tick() {
        super.tick();

        var level = level();
        var pos = position();
        var posX = pos.x;
        var posY = pos.y;
        var posZ = pos.z;

        if (this.firstTick) {
            playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
        }

        if (level.isClientSide) {
            level.addParticle(ParticleTypes.SMOKE, posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        if (isIrradiated) {
            return AVPItems.GRENADE_IRRADIATED;
        }

        if (isIncendiary) {
            return AVPItems.GRENADE_INCENDIARY;
        }

        return AVPItems.GRENADE;
    }

    @Override
    protected void onDeath() {
        level().explode(this, getX(), getY(), getZ(),  isIrradiated ? 9F : 3F, isIncendiary, Level.ExplosionInteraction.BLOCK);
        if (isIrradiated && this.level() instanceof Level serverLevel) {
            var areaEffectCloudEntity = new AreaEffectCloud(serverLevel, this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ());
            areaEffectCloudEntity.setRadius(10.0F);
            areaEffectCloudEntity.setDuration(100);
            areaEffectCloudEntity.setRadiusPerTick(
                    -areaEffectCloudEntity.getRadius() / areaEffectCloudEntity.getDuration()
            );
            areaEffectCloudEntity.setParticle(ParticleTypes.ASH);
            areaEffectCloudEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0));
            serverLevel.addFreshEntity(areaEffectCloudEntity);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        isIncendiary = compoundTag.getBoolean(IS_INCENDIARY_KEY);
        isIrradiated = compoundTag.getBoolean(IS_IRRADIATED_KEY);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean(IS_INCENDIARY_KEY, isIncendiary);
        compoundTag.putBoolean(IS_IRRADIATED_KEY, isIncendiary);
    }

    public void setIncendiary(boolean incendiary) {
        this.isIncendiary = incendiary;
    }

    public void setIrradiated(boolean isIrradiated) {
        this.isIrradiated = isIrradiated;
    }
}
