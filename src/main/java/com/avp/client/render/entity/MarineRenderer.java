package com.avp.client.render.entity;

import com.avp.AVPResources;
import com.avp.client.animation.MarineAnimator;
import com.avp.client.render.layer.HumanEyes;
import com.avp.client.render.layer.HumanHair;
import com.avp.client.render.layer.MarineOutfit;
import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;
import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzArmorLayer;
import mod.azure.azurelib.rewrite.render.layer.AzBlockAndItemLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MarineRenderer extends AzEntityRenderer<MarineMob> {
    private static final String NAME = "marine";

    private static final ResourceLocation MALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_male");

    private static final ResourceLocation FEMALE_MODEL = AVPResources.entityGeoModelLocation(NAME + "_female");

    private static final ResourceLocation MALE_TEXTURE = AVPResources.entityTextureLocation(NAME + "_male");

    private static final ResourceLocation FEMALE_TEXTURE = AVPResources.entityTextureLocation(NAME + "_female");

    /**
     * TODO: Change texture to choose a random one when all are completed.
     */
    public MarineRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<MarineMob>builder(
                marineMob -> Boolean.TRUE.equals(marineMob.getEntityData().get(AbstractHumanMob.SET_GENDER)) ? MALE_MODEL : FEMALE_MODEL,
                marineMob -> Boolean.TRUE.equals(marineMob.getEntityData().get(AbstractHumanMob.SET_GENDER)) ? MALE_TEXTURE : FEMALE_TEXTURE
                ).setAnimatorProvider(MarineAnimator::new)
                .addRenderLayer(new HumanHair())
                .addRenderLayer(new HumanEyes())
                .addRenderLayer(new MarineOutfit())
                .addRenderLayer(new AzArmorLayer<>() {
                    /**
                     * TODO: Add armor bones and test.
                     */
                    @Override
                    protected ItemStack getArmorItemForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone) {
                        return switch (bone.getName()) {
//                            case "gHead" -> context.animatable().getItemBySlot(EquipmentSlot.HEAD);
//                            case "gBody" -> context.animatable().getItemBySlot(EquipmentSlot.CHEST);
//                            case "gLegs" -> context.animatable().getItemBySlot(EquipmentSlot.LEGS);
//                            case "bootsBone" -> context.animatable().getItemBySlot(EquipmentSlot.FEET);
                            default -> null;
                        };
                    }
                })
                .addRenderLayer(new AzBlockAndItemLayer<>() {
                    @Override
                    public ItemStack itemStackForBone(AzBone bone, MarineMob animatable) {
                        return switch (bone.getName()) {
                            case "rightHand_Item" -> animatable.getItemBySlot(EquipmentSlot.MAINHAND);
                            case "leftHand_Item" -> animatable.getItemBySlot(EquipmentSlot.OFFHAND);
                            default -> null;
                        };
                    }

                    @Override
                    protected ItemDisplayContext getTransformTypeForStack(AzBone bone, ItemStack stack, MarineMob animatable) {
                        return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    }

                    @Override
                    protected void renderItemForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone, ItemStack itemStack, MarineMob animatable) {
                        context.poseStack().mulPose(Axis.XP.rotationDegrees(270));
                        context.poseStack().mulPose(Axis.YP.rotationDegrees(0));
                        context.poseStack().mulPose(Axis.ZP.rotationDegrees(0f));
                        context.poseStack().translate(0.0D, 0.1D, -0.1D);
                        super.renderItemForBone(context, bone, itemStack, animatable);
                    }
                }).build(), context);
    }
}
