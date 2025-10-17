package com.human.common.gameplay.entity.living.human.marine.ai.equip_best_armor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.AVPExpressions;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorSetTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import net.minecraft.world.entity.EquipmentSlot;

import com.avp.common.model.inventory.AVPInventory;

public class EquipBestArmorActions {

    public static final Action<Marine> PICK_UP_BEST_ARMOR_PIECES = Action.<Marine>builder("PickUpBestArmorPiecesAction")
        .addPrecondition(EquipBestArmorSensors.IS_ANY_BEST_ARMOR_SET_PIECE_IN_WORLD.key(), Expressions.Boolean.isTrue())
        .addEffect(EquipBestArmorSensors.IS_ANY_BEST_ARMOR_SET_PIECE_IN_WORLD.key().asDerived(), false)
        .addEffect(EquipBestArmorSensors.IS_ANY_BEST_ARMOR_SET_PIECE_IN_INVENTORY.key().asDerived(), true)
        .withPerformCallback((a, b, c) -> {
            var bestArmorTargetOption = b.getOrDefault(EquipBestArmorSensors.BEST_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);

            if (bestArmorTargetOption.isEmpty()) {
                return Action.Signal.ABORT;
            }

            // TODO: Take off current armor pieces and put them back into the inventory.
            // TODO: Equip the parts of the "BETTER_ARMOR" state that are present.
            return Action.Signal.CONTINUE;
        })
        .build();

    public static final Action<Marine> EQUIP_BEST_ARMOR_PIECES = Action.<Marine>builder("EquipBestArmorPiecesAction")
        .addPrecondition(EquipBestArmorSensors.IS_ANY_BEST_ARMOR_SET_PIECE_IN_INVENTORY.key(), Expressions.Boolean.isTrue())
        .addPrecondition(EquipBestArmorSensors.BEST_ARMOR_SET_TARGET.key(), AVPExpressions.ArmorSetTarget.isNotEmpty())
        .addEffect(EquipBestArmorSensors.ARE_ALL_BEST_ARMOR_SET_PIECES_EQUIPPED.key().asDerived(), true)
        .withPerformCallback((marine, b, c) -> {
            var bestArmorTarget = b.getOrDefault(EquipBestArmorSensors.BEST_ARMOR_SET_TARGET.key(), ArmorSetTarget.EMPTY);

            if (bestArmorTarget.isEmpty()) {
                return Action.Signal.ABORT;
            }

            var signal = Action.Signal.CONTINUE;

            if (bestArmorTarget.helmet() instanceof ItemTarget.Inventory(AVPInventory.Entry entry)) {
                signal = EquipItemAction.perform(marine, entry, EquipmentSlot.HEAD);
            }

            if (bestArmorTarget.chestplate() instanceof ItemTarget.Inventory(AVPInventory.Entry entry)) {
                signal = EquipItemAction.perform(marine, entry, EquipmentSlot.CHEST);
            }

            if (bestArmorTarget.leggings() instanceof ItemTarget.Inventory(AVPInventory.Entry entry)) {
                signal = EquipItemAction.perform(marine, entry, EquipmentSlot.LEGS);
            }

            if (bestArmorTarget.boots() instanceof ItemTarget.Inventory(AVPInventory.Entry entry)) {
                signal = EquipItemAction.perform(marine, entry, EquipmentSlot.FEET);
            }

            return signal;
        })
        .build();

    private EquipBestArmorActions() {
        throw new UnsupportedOperationException();
    }
}
