package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.gene.GeneKey;
import com.lib.common.gameplay.gene.GeneKeys;
import com.lib.common.gameplay.gene.decoder.GeneDecoder;
import com.lib.common.gameplay.gene.decoder.GeneDecoders;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.function.BiFunction;

public class AlienHurtUtil {

    public static boolean isHurt(
        Alien alien,
        DamageSource damageSource,
        float damage,
        BiFunction<DamageSource, Float, Boolean> superCall
    ) {
        if (isNonDamagingSource(damageSource)) {
            return false;
        }

        var geneManager = alien.geneManager();

        if (isFireDamageSource(damageSource)) {
            return hurtWithResistance(
                geneManager,
                GeneKeys.FIRE_RESISTANCE,
                GeneDecoders.FIRE_RESISTANCE,
                damageSource,
                damage,
                superCall
            );
        }

        if (damageSource.is(DamageTypes.FREEZE)) {
            return hurtWithResistance(
                geneManager,
                GeneKeys.COLD_RESISTANCE,
                GeneDecoders.COLD_RESISTANCE,
                damageSource,
                damage,
                superCall
            );
        }

        return superCall.apply(damageSource, damage);
    }

    private static boolean hurtWithResistance(
        GeneManager geneManager,
        GeneKey geneKey,
        GeneDecoder<Float> geneDecoder,
        DamageSource damageSource,
        float damage,
        BiFunction<DamageSource, Float, Boolean> superCall
    ) {
        var resistance = geneManager.get(geneKey, geneDecoder);
        var modifiedDamage = damage - (resistance * damage);
        return modifiedDamage > 0 && superCall.apply(damageSource, modifiedDamage);
    }

    private static boolean isFireDamageSource(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_FIRE) ||
            damageSource.is(DamageTypes.ON_FIRE) ||
            damageSource.is(DamageTypes.CAMPFIRE) ||
            damageSource.is(DamageTypes.HOT_FLOOR) ||
            damageSource.is(DamageTypes.LAVA);
    }

    private static boolean isNonDamagingSource(DamageSource damageSource) {
        // Xenomorphs should not drown.
        return damageSource.is(DamageTypes.DROWN) ||
        // Xenomorphs should not freeze.
            damageSource.is(DamageTypes.FREEZE) ||
            // Xenomorphs should not suffocate in walls.
            damageSource.is(DamageTypes.IN_WALL);
    }
}
