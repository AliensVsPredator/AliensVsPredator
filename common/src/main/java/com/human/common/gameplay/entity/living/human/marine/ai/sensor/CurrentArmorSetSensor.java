package com.human.common.gameplay.entity.living.human.marine.ai.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.ArmorSet;
import com.just.core.functional.option.Option;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CurrentArmorSetSensor {

    public static @NotNull ArmorSet sense(Marine marine) {
        var headStack = marine.getItemBySlot(EquipmentSlot.HEAD);
        var chestStack = marine.getItemBySlot(EquipmentSlot.CHEST);
        var legsStack = marine.getItemBySlot(EquipmentSlot.LEGS);
        var feetStack = marine.getItemBySlot(EquipmentSlot.FEET);

        var head = headStack.getItem() instanceof ArmorItem
            ? Option.ofNullable(headStack)
            : Option.<ItemStack>none();
        var chest = chestStack.getItem() instanceof ArmorItem
            ? Option.ofNullable(chestStack)
            : Option.<ItemStack>none();
        var legs = legsStack.getItem() instanceof ArmorItem
            ? Option.ofNullable(legsStack)
            : Option.<ItemStack>none();
        var feet = feetStack.getItem() instanceof ArmorItem
            ? Option.ofNullable(feetStack)
            : Option.<ItemStack>none();

        return new ArmorSet(head, chest, legs, feet);
    }
}
