package com.avp.client.render.layer;

import com.avp.common.entity.living.human.AbstractHumanMob;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzArmorLayer;
import net.minecraft.world.item.ItemStack;

public class HumanArmorLayer<T extends AbstractHumanMob> extends AzArmorLayer<T> {
    /**
     * TODO: Add armor bones and test.
     */
    @Override
    protected ItemStack getArmorItemForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        return switch (bone.getName()) {
//                            case "gHead" -> context.animatable().getItemBySlot(EquipmentSlot.HEAD);
//                            case "gBody" -> context.animatable().getItemBySlot(EquipmentSlot.CHEST);
//                            case "gLegs" -> context.animatable().getItemBySlot(EquipmentSlot.LEGS);
//                            case "bootsBone" -> context.animatable().getItemBySlot(EquipmentSlot.FEET);
            default -> null;
        };
    }
}
