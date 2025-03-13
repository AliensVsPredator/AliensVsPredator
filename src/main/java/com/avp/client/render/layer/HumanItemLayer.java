package com.avp.client.render.layer;

import com.avp.common.entity.living.human.AbstractHumanMob;
import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzBlockAndItemLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HumanItemLayer<T extends AbstractHumanMob> extends AzBlockAndItemLayer<T> {

    @Override
    public ItemStack itemStackForBone(AzBone bone, T animatable) {
        return switch (bone.getName()) {
            case "rightHand_Item" -> animatable.getItemBySlot(EquipmentSlot.MAINHAND);
            case "leftHand_Item" -> animatable.getItemBySlot(EquipmentSlot.OFFHAND);
            default -> null;
        };
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(AzBone bone, ItemStack stack, T animatable) {
        return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    @Override
    protected void renderItemForBone(AzRendererPipelineContext<T> context, AzBone bone, ItemStack itemStack, T animatable) {
        context.poseStack().mulPose(Axis.XP.rotationDegrees(270));
        context.poseStack().mulPose(Axis.YP.rotationDegrees(0));
        context.poseStack().mulPose(Axis.ZP.rotationDegrees(0f));
        context.poseStack().translate(0.0D, 0.1D, -0.1D);
        super.renderItemForBone(context, bone, itemStack, animatable);
    }
}
