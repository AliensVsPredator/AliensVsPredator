package com.avp.fabric.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.yautja.Yautja;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.TempAVPPredicates;
import com.avp.fabric.common.entity.living.Host;
import com.avp.fabric.common.entity.living.alien.Alien;

// TODO: Merge this with TempAVPPredicates in common.
public class AVPPredicates {

    public static boolean hasEmbryo(Entity target) {
        return target instanceof Host host && host.parasiteType() != null;
    }

    public static boolean isFreeHost(Alien parasite, Entity hostTarget) {
        return TempAVPPredicates.isLiving(hostTarget) &&
            isHost(hostTarget) &&
            !hasEmbryo(hostTarget) &&
            !isSelfOrOtherParasiteAttached(parasite, hostTarget)
            && !HAS_MASK.test((LivingEntity) hostTarget)
            && !HAS_FACE_MASK.test((LivingEntity) hostTarget);
    }

    public static boolean isHost(Entity target) {
        return target.getType().is(AVPEntityTypeTags.HOSTS) &&
            TempAVPPredicates.isLiving(target) &&
            !TempAVPPredicates.isBaby(target) &&
            !TempAVPPredicates.IS_IMMORTAL.test((LivingEntity) target);
    }

    public static boolean isParasiteAttached(Entity target) {
        return target.hasPassenger(passenger -> passenger.getType().is(AVPEntityTypeTags.PARASITES));
    }

    public static boolean isSelfOrOtherParasiteAttached(Alien parasite, Entity target) {
        return target.hasPassenger(
            passenger -> passenger.equals(parasite) || passenger.getType().is(AVPEntityTypeTags.PARASITES)
        );
    }

    public static final Predicate<LivingEntity> HAS_FACE_MASK = livingEntity -> livingEntity.getItemBySlot(
        EquipmentSlot.HEAD
    ).is(AVPItemTags.FACEHUGGER_PROTECTION_HELMET);

    public static final Predicate<LivingEntity> HAS_MASK = livingEntity -> livingEntity instanceof Yautja yautja && yautja.yautjaMaskManager
        .hasMask();
}
