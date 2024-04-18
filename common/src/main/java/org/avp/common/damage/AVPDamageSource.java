package org.avp.common.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AVPDamageSource(
    ResourceKey<DamageType> damageTypeResourceKey
) {

    public void hurt(Entity entity, float damage) {
        var damageSource = asDamageSource(entity.level());
        entity.hurt(damageSource, damage);
    }

    @NotNull
    private DamageSource asDamageSource(Level level) {
        return new DamageSource(
            level.registryAccess()
                .registry(Registries.DAMAGE_TYPE)
                .orElseThrow()
                .getHolderOrThrow(damageTypeResourceKey)
        );
    }
}
