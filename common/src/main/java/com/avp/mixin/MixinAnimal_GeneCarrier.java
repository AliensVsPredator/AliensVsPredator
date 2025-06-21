package com.avp.mixin;

import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.util.EmbryoUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class MixinAnimal_GeneCarrier extends AgeableMob implements GeneCarrier {

    public MixinAnimal_GeneCarrier(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void finalizeSpawnChildFromBreeding(ServerLevel level, Animal animal, @Nullable AgeableMob baby);

    @Inject(at = @At("HEAD"), method = "spawnChildFromBreeding")
    public void avp$spawnChildFromBreeding(ServerLevel level, Animal mate, CallbackInfo ci) {
        EmbryoUtil.birthEmbryos(this, parent -> avp$birthChild(level, mate), 0);
    }

    @Inject(at = @At("HEAD"), method = "finalizeSpawnChildFromBreeding")
    public void avp$finalizeSpawnChildFromBreeding(ServerLevel level, Animal animal, AgeableMob baby, CallbackInfo ci) {
        EmbryoUtil.applyGenesToEmbryo(this, (GeneCarrier) baby);
        EmbryoUtil.applyGenesToEmbryo(animal, (GeneCarrier) baby);
    }

    @Unique
    public @Nullable AgeableMob avp$birthChild(ServerLevel level, Animal mate) {
        var ageablemob = getBreedOffspring(level, mate);

        if (ageablemob != null) {
            ageablemob.setBaby(true);
            ageablemob.moveTo(getX(), getY(), getZ(), 0.0F, 0.0F);
            finalizeSpawnChildFromBreeding(level, mate, ageablemob);
            level.addFreshEntityWithPassengers(ageablemob);
        }

        return ageablemob;
    }
}
