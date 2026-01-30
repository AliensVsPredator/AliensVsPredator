package com.blib.api.client.render.v1.armor.model.bone;

import org.jetbrains.annotations.Nullable;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.internal.client.model.AzBone;

public interface AzArmorBoneProvider {

    String BONE_ARMOR_BODY_NAME = "armorBody";

    String BONE_ARMOR_HEAD_NAME = "armorHead";

    String BONE_ARMOR_LEFT_ARM_NAME = "armorLeftArm";

    String BONE_ARMOR_RIGHT_ARM_NAME = "armorRightArm";

    String BONE_ARMOR_LEFT_BOOT_NAME = "armorLeftBoot";

    String BONE_ARMOR_RIGHT_BOOT_NAME = "armorRightBoot";

    String BONE_ARMOR_LEFT_LEG_NAME = "armorLeftLeg";

    String BONE_ARMOR_RIGHT_LEG_NAME = "armorRightLeg";

    String BONE_ARMOR_WAIST_NAME = "armorWaist";

    @Nullable
    AzBone getHeadBone(AzBakedModel model);

    @Nullable
    AzBone getBodyBone(AzBakedModel model);

    @Nullable
    AzBone getRightArmBone(AzBakedModel model);

    @Nullable
    AzBone getLeftArmBone(AzBakedModel model);

    @Nullable
    AzBone getRightLegBone(AzBakedModel model);

    @Nullable
    AzBone getLeftLegBone(AzBakedModel model);

    @Nullable
    AzBone getRightBootBone(AzBakedModel model);

    @Nullable
    AzBone getLeftBootBone(AzBakedModel model);

    @Nullable
    AzBone getWaistBone(AzBakedModel model);
}
