package com.human.common.gameplay.entity.living.human;

import com.just.core.functional.function.memo.Memo;
import com.just.core.functional.function.memo.Memo2;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class HumanFeatureManager {

    private static final ResourceLocation EYES_TEXTURE_LOCATION = AVPResources.entityTextureLocation("human/generic/eyes/eyes");

    // beard variant index -> beard texture location
    private final Memo<Integer, ResourceLocation> cachedBeardTexture = new Memo<>(
        beardVariantIndex -> AVPResources.entityTextureLocation("human/male/beard/beard_" + beardVariantIndex)
    );

    // isMale, hair variant index -> hair texture location
    private final Memo2<Boolean, Integer, ResourceLocation> cachedHairTexture = new Memo2<>(
        (isMale, hairVariantIndex) -> AVPResources.entityTextureLocation(
            "human/" + (isMale ? "male" : "female") + "/hair/hair_" + hairVariantIndex
        )
    );

    // isMale -> outfit texture location
    private final Memo<Boolean, ResourceLocation> cachedOutfitTexture = new Memo<>(
        isMale -> AVPResources.entityTextureLocation("human/" + (isMale ? "male" : "female") + "/outfit/camo")
    );

    // isMale -> skin texture location
    private final Memo<Boolean, ResourceLocation> cachedSkinTexture = new Memo<>(
        isMale -> AVPResources.entityTextureLocation("human/" + (isMale ? "male" : "female") + "/skin/skin")
    );

    private final AbstractHuman entity;

    public HumanFeatureManager(AbstractHuman entity) {
        this.entity = entity;
    }

    public ResourceLocation getEyesTexture() {
        return EYES_TEXTURE_LOCATION;
    }

    public ResourceLocation getBeardTextureOrNull() {
        return entity.getBeardVariantOrNull() == null
            ? null
            : cachedBeardTexture.apply(entity.getBeardVariantOrNull());
    }

    public ResourceLocation getHairTexture() {
        return cachedHairTexture.apply(entity.isMale.get(), entity.hairVariant.get());
    }

    public ResourceLocation getOutfitTexture() {
        return cachedOutfitTexture.apply(entity.isMale.get());
    }

    public ResourceLocation getSkinTexture() {
        return cachedSkinTexture.apply(entity.isMale.get());
    }
}
