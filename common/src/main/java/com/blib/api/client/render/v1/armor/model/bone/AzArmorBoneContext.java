package com.blib.api.client.render.v1.armor.model.bone;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.render.util.RenderUtil;

public class AzArmorBoneContext {

    private AzBakedModel lastModel;

    public AzBone head;

    public AzBone body;

    public AzBone rightArm;

    public AzBone leftArm;

    public AzBone rightLeg;

    public AzBone leftLeg;

    public AzBone rightBoot;

    public AzBone leftBoot;

    public AzBone waist;

    public AzArmorBoneContext() {
        this.head = null;
        this.body = null;
        this.rightArm = null;
        this.leftArm = null;
        this.rightLeg = null;
        this.leftLeg = null;
        this.rightBoot = null;
        this.leftBoot = null;
        this.waist = null;
    }

    public void setAllVisible(boolean pVisible) {
        setBoneVisible(this.head, pVisible);
        setBoneVisible(this.body, pVisible);
        setBoneVisible(this.rightArm, pVisible);
        setBoneVisible(this.leftArm, pVisible);
        setBoneVisible(this.rightLeg, pVisible);
        setBoneVisible(this.leftLeg, pVisible);
        setBoneVisible(this.rightBoot, pVisible);
        setBoneVisible(this.leftBoot, pVisible);
        setBoneVisible(this.waist, pVisible);
    }

    public void grabRelevantBones(AzBakedModel model, AzArmorBoneProvider boneProvider) {
        if (this.lastModel == model) {
            return;
        }

        this.lastModel = model;
        this.head = boneProvider.getHeadBone(model);
        this.body = boneProvider.getBodyBone(model);
        this.rightArm = boneProvider.getRightArmBone(model);
        this.leftArm = boneProvider.getLeftArmBone(model);
        this.rightLeg = boneProvider.getRightLegBone(model);
        this.leftLeg = boneProvider.getLeftLegBone(model);
        this.rightBoot = boneProvider.getRightBootBone(model);
        this.leftBoot = boneProvider.getLeftBootBone(model);
        this.waist = boneProvider.getWaistBone(model);
    }

    public void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;

            RenderUtil.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        }

        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;

            RenderUtil.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
        }

        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;

            RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
        }

        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;

            RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
        }

        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;

            RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

            if (this.rightBoot != null) {
                RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
            }
            if (this.waist != null) {
                RenderUtil.matchModelPartRot(baseModel.body, this.waist);
                this.waist.updatePosition(baseModel.body.x, -(baseModel.body.y), baseModel.body.z);
            }
        }

        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;

            RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

            if (this.leftBoot != null) {
                RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
            }
            if (this.waist != null) {
                RenderUtil.matchModelPartRot(baseModel.body, this.waist);
                this.waist.updatePosition(baseModel.body.x, -(baseModel.body.y), baseModel.body.z);
            }
        }
    }

    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
        setAllVisible(false);

        currentPart.visible = true;
        AzBone bone = null;

        if (currentPart == model.hat || currentPart == model.head) {
            bone = this.head;
        } else if (currentPart == model.body) {
            bone = this.body;
        } else if (currentPart == model.leftArm) {
            bone = this.leftArm;
        } else if (currentPart == model.rightArm) {
            bone = this.rightArm;
        } else if (currentPart == model.leftLeg) {
            bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
        } else if (currentPart == model.rightLeg) {
            bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
        }

        if (bone != null) {
            bone.setHidden(false);
        }

        if (
            currentSlot == EquipmentSlot.LEGS &&
                (currentPart == model.leftLeg || currentPart == model.rightLeg) &&
                this.waist != null
        ) {
            this.waist.setHidden(false);
        }
    }

    public void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        switch (currentSlot) {
            case HEAD -> setBoneVisible(this.head, true);
            case CHEST -> {
                setBoneVisible(this.body, true);
                setBoneVisible(this.rightArm, true);
                setBoneVisible(this.leftArm, true);
                setBoneVisible(this.waist, false);
            }
            case LEGS -> {
                setBoneVisible(this.rightLeg, true);
                setBoneVisible(this.leftLeg, true);
                setBoneVisible(this.waist, true);
            }
            case FEET -> {
                setBoneVisible(this.rightBoot, true);
                setBoneVisible(this.leftBoot, true);
            }
            case MAINHAND, OFFHAND -> { /* NO-OP */ }
        }
    }

    protected void setBoneVisible(@Nullable AzBone bone, boolean visible) {
        if (bone == null)
            return;

        bone.setHidden(!visible);
    }
}
