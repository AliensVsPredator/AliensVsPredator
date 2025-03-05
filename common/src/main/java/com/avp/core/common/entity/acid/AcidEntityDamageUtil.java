package com.avp.core.common.entity.acid;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.damage.AVPDamageTypes;
import com.avp.core.common.entity.AVPEntityTypeTags;
import com.avp.core.common.item.AVPItemTags;
import com.avp.core.common.util.AVPPredicates;

public class AcidEntityDamageUtil {

    public static void damageEntities(Acid acid) {
        var level = acid.level();

        // Entity damage should only be done server-side.
        if (level.isClientSide || acid.isInWater() || acid.tickCount % 10 != 0) {
            return;
        }

        level.getEntities(
            acid,
            acid.getBoundingBox(),
            entity -> AVPPredicates.isLiving(entity) || entity instanceof Acid
        )
            .stream()
            .filter(entity -> {
                if (entity instanceof Acid otherAcid) {
                    if (otherAcid.isAlive() && Objects.equals(acid.isNetherAfflicted(), otherAcid.isNetherAfflicted())) {
                        otherAcid.remove(Entity.RemovalReason.DISCARDED);
                        acid.setMultiplier(acid.getMultiplier() + otherAcid.getMultiplier());
                    }

                    return false;
                }

                var isImmortalPlayer = entity instanceof Player player && AVPPredicates.IS_IMMORTAL.test(player);

                return entity.isAlive() && !isImmortalPlayer;
            })
            .forEach(entity -> damageEntity(acid, entity));
    }

    private static void damageEntity(Acid acid, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            var itemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

            if (itemStack.is(AVPItemTags.ACID_IMMUNE)) {
                return;
            }

            if (!itemStack.isEmpty()) {
                damageFootwear(acid, itemStack);
                return;
            }
        }

        if (entity.getType().is(AVPEntityTypeTags.ACID_IMMUNE)) {
            return;
        }

        var damage = AVP.STATS_CONFIG.properties().getOrDefault(ConfigProperties.ACID_ATTACK_DAMAGE, 1F);
        var registry = acid.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.ACID));

        if (acid.isNetherAfflicted()) {
            entity.igniteForTicks(5 * 20);
        }

        entity.hurt(damageSource, damage * acid.getMultiplier());
    }

    private static void damageFootwear(Acid acid, ItemStack itemStack) {
        var level = (ServerLevel) acid.level();
        var damage = (acid.getRandom().nextInt(3) + 3) * acid.getMultiplier();

        itemStack.hurtAndBreak(damage, level, null, item -> {});
    }
}
