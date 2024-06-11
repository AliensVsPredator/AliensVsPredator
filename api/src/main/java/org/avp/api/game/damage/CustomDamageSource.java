package org.avp.api.game.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CustomDamageSource(
    ResourceKey<DamageType> damageTypeResourceKey
) {

    public void hurt(Entity targetEntity, float damage) {
        var damageSource = simpleDamageSource(targetEntity.level());
        targetEntity.hurt(damageSource, damage);
    }

    public void hurtCovertlyAndIndirectly(Entity targetEntity, float damage) {
        var damageSource = covertIndirectDamageSource(targetEntity.level(), targetEntity);
        targetEntity.hurt(damageSource, damage);
    }

    @NotNull
    public DamageSource covertIndirectDamageSource(Level level, Entity targetEntity) {
        return new DamageSource(getHolderOrThrow(level), null, targetEntity);
    }

    @NotNull
    public DamageSource simpleDamageSource(Level level) {
        return new DamageSource(getHolderOrThrow(level));
    }

    private Holder.@NotNull Reference<DamageType> getHolderOrThrow(Level level) {
        return level.registryAccess()
            .registry(Registries.DAMAGE_TYPE)
            .orElseThrow()
            .getHolderOrThrow(damageTypeResourceKey);
    }
}
