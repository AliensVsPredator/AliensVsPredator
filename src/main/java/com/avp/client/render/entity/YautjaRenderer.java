package com.avp.client.render.entity;

import com.mojang.math.Axis;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzBlockAndItemLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.avp.AVPResources;
import com.avp.client.animation.YautjaAnimator;
import com.avp.common.entity.living.yautja.Yautja;

public class YautjaRenderer extends AzEntityRenderer<Yautja> {

    private static final String NAME = "yautja";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    public YautjaRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Yautja>builder(MODEL, TEXTURE)
                .setAnimatorProvider(YautjaAnimator::new)
                .addRenderLayer(new AzBlockAndItemLayer<>() {

                    private static final String RIGHT_HAND = "rightHand_Item";

                    @Override
                    public ItemStack itemStackForBone(AzBone bone, Yautja animatable) {
                        return switch (bone.getName()) {
                            case RIGHT_HAND -> animatable.getItemBySlot(EquipmentSlot.MAINHAND);
                            default -> null;
                        };
                    }

                    @Override
                    protected ItemDisplayContext getTransformTypeForStack(AzBone bone, ItemStack stack, Yautja animatable) {
                        return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    }

                    @Override
                    protected void renderItemForBone(
                        AzRendererPipelineContext<Yautja> context,
                        AzBone bone,
                        ItemStack itemStack,
                        Yautja animatable
                    ) {
                        context.poseStack().mulPose(Axis.XP.rotationDegrees(270));
                        context.poseStack().mulPose(Axis.YP.rotationDegrees(0));
                        context.poseStack().mulPose(Axis.ZP.rotationDegrees(0f));
                        context.poseStack().translate(0.0D, 0.1D, -0.1D);
                        super.renderItemForBone(context, bone, itemStack, animatable);
                    }
                })
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }
}
