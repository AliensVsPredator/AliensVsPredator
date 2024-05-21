package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.api.entity.HiveMember;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_HiveMember extends Entity implements HiveMember {

    private static final String HIVEMIND_SIGNATURE = "HivemindSignature";

    private UUID hivemindSignature;

    protected MixinLivingEntity_HiveMember(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    void addAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        if (hivemindSignature != null) {
            nbt.putUUID(HIVEMIND_SIGNATURE, hivemindSignature);
        }
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    void readAdditionalSaveData(CompoundTag nbt, CallbackInfo callbackInfo) {
        if (nbt.contains(HIVEMIND_SIGNATURE)) {
            this.hivemindSignature = nbt.getUUID(HIVEMIND_SIGNATURE);
        }
    }

    @Override
    public boolean hasHivemind() {
        return hivemindSignature != null;
    }

    @Override
    public UUID getHivemindSignature() {
        return hivemindSignature;
    }

    @Override
    public void setHivemindSignature(UUID hivemindSignature) {
        this.hivemindSignature = hivemindSignature;
    }
}
