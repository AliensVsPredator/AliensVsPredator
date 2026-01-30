package com.blib.api.client.render.v1.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.AbstractSkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.api.client.registry.v1.AzArmorRendererRegistry;
import com.blib.api.client.render.v1.armor.AzArmorRenderer;
import com.blib.internal.client.model.AzBone;
import com.blib.internal.client.render.AzRendererPipelineContext;
import com.blib.internal.client.render.util.RenderUtil;
import com.blib.internal.common.model.Color;

public class AzArmorLayer<T extends LivingEntity> implements AzRenderLayer<UUID, T> {

    protected static final HumanoidModel<LivingEntity> INNER_ARMOR_MODEL = new HumanoidModel<>(
        Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)
    );

    protected static final HumanoidModel<LivingEntity> OUTER_ARMOR_MODEL = new HumanoidModel<>(
        Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)
    );

    @Nullable
    protected ItemStack mainHandStack;

    @Nullable
    protected ItemStack offhandStack;

    @Nullable
    protected ItemStack helmetStack;

    @Nullable
    protected ItemStack chestplateStack;

    @Nullable
    protected ItemStack leggingsStack;

    @Nullable
    protected ItemStack bootsStack;

    @Override
    public void preRender(AzRendererPipelineContext<UUID, T> context) {
        var animatable = context.animatable();

        this.mainHandStack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
        this.offhandStack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
        this.helmetStack = animatable.getItemBySlot(EquipmentSlot.HEAD);
        this.chestplateStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
        this.leggingsStack = animatable.getItemBySlot(EquipmentSlot.LEGS);
        this.bootsStack = animatable.getItemBySlot(EquipmentSlot.FEET);
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context) {}

    @Override
    public void renderForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone) {
        var armorStack = getArmorItemForBone(context, bone);

        if (armorStack == null) {
            return;
        }

        context.poseStack().pushPose();
        if (
            armorStack.getItem() instanceof BlockItem blockItem && blockItem
                .getBlock() instanceof AbstractSkullBlock skullBlock
        ) {
            renderSkullAsArmor(context, bone, armorStack, skullBlock);
        } else {
            renderArmor(context, bone, armorStack);
        }

        context.setVertexConsumer(context.multiBufferSource().getBuffer(context.renderType()));

        context.poseStack().popPose();
    }

    public void renderArmor(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        ItemStack armorStack
    ) {
        var slot = getEquipmentSlotForBone(context, bone, armorStack);
        var model = getModelForItem(slot);
        var modelPart = getModelPartForBone(context, bone, model);
        var renderer = AzArmorRendererRegistry.getOrNull(armorStack);

        if (!modelPart.cubes.isEmpty()) {
            context.poseStack().pushPose();
            context.poseStack().scale(-1, -1, 1);

            if (armorStack.getItem() instanceof ArmorItem) {
                prepModelPartForRender(context, bone, modelPart);
                if (renderer != null) {
                    renderAzArmorPiece(renderer, context, bone, slot, armorStack, modelPart, model);
                } else {
                    renderArmorPiece(context, bone, slot, armorStack, modelPart);
                }
            }

            context.poseStack().popPose();
        }
    }

    protected @NotNull EquipmentSlot getEquipmentSlotForBone(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        ItemStack stack
    ) {
        var animatable = context.animatable();

        for (var slot : EquipmentSlot.values()) {
            var isHumanoidArmorSlotType = slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;

            if (isHumanoidArmorSlotType && stack == animatable.getItemBySlot(slot)) {
                return slot;
            }
        }

        return EquipmentSlot.CHEST;
    }

    @NotNull
    protected ModelPart getModelPartForBone(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        HumanoidModel<?> baseModel
    ) {
        return baseModel.body;
    }

    @Nullable
    protected ItemStack getArmorItemForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone) {
        return null;
    }

    protected <I extends Item> void renderAzArmorPiece(
        AzArmorRenderer renderer,
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        EquipmentSlot slot,
        ItemStack armorStack,
        ModelPart modelPart,
        HumanoidModel<T> baseModel
    ) {
        var armorModel = renderer.rendererPipeline().armorModel();
        var boneContext = renderer.rendererPipeline().context().boneContext();
        var color = armorStack.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(armorStack, -6265536) : -1;

        renderer.prepForRender(context.animatable(), armorStack, slot, baseModel);
        boneContext.applyBoneVisibilityByPart(slot, modelPart, baseModel);
        armorModel.renderToBuffer(
            context.poseStack(),
            null,
            context.packedLight(),
            OverlayTexture.NO_OVERLAY,
            color
        );
    }

    protected <I extends Item> void renderArmorPiece(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        EquipmentSlot slot,
        ItemStack armorStack,
        ModelPart modelPart
    ) {
        var color = armorStack.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(armorStack, -6265536) : -1;

        // Vanilla armor rendering
        var material = ((ArmorItem) armorStack.getItem()).getMaterial();

        for (var layer : material.value().layers()) {
            var buffer = getVanillaArmorBuffer(context, armorStack, slot, bone, layer, false);

            modelPart.render(context.poseStack(), buffer, context.packedLight(), context.packedOverlay(), color);
        }

        var trim = armorStack.get(DataComponents.TRIM);

        if (trim != null) {
            var sprite = Minecraft.getInstance()
                .getModelManager()
                .getAtlas(Sheets.ARMOR_TRIMS_SHEET)
                .getSprite(slot == EquipmentSlot.LEGS ? trim.innerTexture(material) : trim.outerTexture(material));
            var buffer = sprite.wrap(
                context.multiBufferSource().getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal()))
            );

            modelPart.render(context.poseStack(), buffer, context.packedLight(), context.packedOverlay());
        }

        if (armorStack.hasFoil())
            modelPart.render(
                context.poseStack(),
                getVanillaArmorBuffer(context, armorStack, slot, bone, null, true),
                context.packedLight(),
                context.packedOverlay(),
                Color.WHITE.argbInt()
            );
    }

    protected VertexConsumer getVanillaArmorBuffer(
        AzRendererPipelineContext<UUID, T> context,
        ItemStack stack,
        EquipmentSlot slot,
        AzBone bone,
        @Nullable ArmorMaterial.Layer layer,
        boolean forGlint
    ) {
        if (forGlint) {
            return context.multiBufferSource().getBuffer(RenderType.armorEntityGlint());
        }

        return context.multiBufferSource()
            .getBuffer(RenderType.armorCutoutNoCull(layer.texture(slot == EquipmentSlot.LEGS)));
    }

    protected @Nullable AzArmorRenderer getRendererForItem(ItemStack stack) {
        return AzArmorRendererRegistry.getOrNull(stack);
    }

    protected HumanoidModel<T> getModelForItem(EquipmentSlot slot) {
        return (HumanoidModel<T>) (slot == EquipmentSlot.LEGS ? INNER_ARMOR_MODEL : OUTER_ARMOR_MODEL);
    }

    protected void renderSkullAsArmor(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        ItemStack stack,
        AbstractSkullBlock skullBlock
    ) {
        var type = skullBlock.getType();
        var model = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels())
            .get(type);
        var renderType = SkullBlockRenderer.getRenderType(type, stack.get(DataComponents.PROFILE));

        context.poseStack().pushPose();
        RenderUtil.translateAndRotateMatrixForBone(context.poseStack(), bone);
        context.poseStack().scale(1.1875f, 1.1875f, 1.1875f);
        context.poseStack().translate(-0.5f, 0, -0.5f);
        SkullBlockRenderer.renderSkull(
            null,
            0,
            0,
            context.poseStack(),
            context.multiBufferSource(),
            context.packedLight(),
            model,
            renderType
        );
        context.poseStack().popPose();
    }

    protected void prepModelPartForRender(
        AzRendererPipelineContext<UUID, T> context,
        AzBone bone,
        ModelPart sourcePart
    ) {
        var firstCube = bone.getCubes().getFirst();
        var armorCube = sourcePart.cubes.getFirst();
        var armorBoneSizeX = firstCube.size().x();
        var armorBoneSizeY = firstCube.size().y();
        var armorBoneSizeZ = firstCube.size().z();
        var actualArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
        var actualArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
        var actualArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
        var scaleX = (float) (armorBoneSizeX / actualArmorSizeX);
        var scaleY = (float) (armorBoneSizeY / actualArmorSizeY);
        var scaleZ = (float) (armorBoneSizeZ / actualArmorSizeZ);

        sourcePart.setPos(
            -(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
            -(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
            (bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ)
        );

        sourcePart.xRot = -bone.getRotX();
        sourcePart.yRot = -bone.getRotY();
        sourcePart.zRot = bone.getRotZ();

        context.poseStack().scale(scaleX, scaleY, scaleZ);
    }
}
