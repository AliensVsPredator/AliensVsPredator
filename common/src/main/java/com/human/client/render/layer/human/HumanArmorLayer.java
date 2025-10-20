package com.human.client.render.layer.human;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import mod.azure.azurelib.common.model.AzBone;
import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import mod.azure.azurelib.common.render.layer.AzArmorLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HumanArmorLayer<T extends AbstractHuman> extends AzArmorLayer<T> {

    private static final String LEFT_BOOT = "armorBipedLeftFoot";

    private static final String RIGHT_BOOT = "armorBipedRightFoot";

    private static final String LEFT_ARMOR_LEG = "armorBipedLeftLeg";

    private static final String RIGHT_ARMOR_LEG = "armorBipedRightLeg";

    private static final String CHESTPLATE = "armorBipedBody";

    private static final String RIGHT_SLEEVE = "armorBipedRightArm";

    private static final String LEFT_SLEEVE = "armorBipedLeftArm";

    private static final String HELMET = "armorBipedHead";

    @Override
    protected ItemStack getArmorItemForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone) {
        return switch (bone.getName()) {
            case LEFT_BOOT, RIGHT_BOOT -> this.bootsStack;
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> this.leggingsStack;
            case CHESTPLATE, RIGHT_SLEEVE, LEFT_SLEEVE -> this.chestplateStack;
            case HELMET -> this.helmetStack;
            default -> null;
        };
    }

    @Override
    protected @NotNull EquipmentSlot getEquipmentSlotForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone, ItemStack stack) {
        var animatable = context.animatable();
        return switch (bone.getName()) {
            case LEFT_BOOT, RIGHT_BOOT -> EquipmentSlot.FEET;
            case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG -> EquipmentSlot.LEGS;
            case RIGHT_SLEEVE -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case LEFT_SLEEVE -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case HELMET -> EquipmentSlot.HEAD;
            default -> super.getEquipmentSlotForBone(context, bone, stack);
        };
    }

    @Override
    protected @NotNull ModelPart getModelPartForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone, HumanoidModel<?> baseModel) {
        return switch (bone.getName()) {
            case HELMET -> baseModel.head;
            case CHESTPLATE -> baseModel.body;
            case LEFT_BOOT, LEFT_ARMOR_LEG -> baseModel.leftLeg;
            case RIGHT_BOOT, RIGHT_ARMOR_LEG -> baseModel.rightLeg;
            case RIGHT_SLEEVE -> baseModel.rightArm;
            case LEFT_SLEEVE -> baseModel.leftArm;
            default -> super.getModelPartForBone(context, bone, baseModel);
        };
    }
}
