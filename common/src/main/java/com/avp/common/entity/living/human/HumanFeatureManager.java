package com.avp.common.entity.living.human;

import com.bvanseg.just.functional.function.memo.BiMemo;
import com.bvanseg.just.functional.function.memo.Memo;
import com.bvanseg.just.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

public class HumanFeatureManager {

    private static final String BEARD_VARIANT_KEY = "beardVariant";

    private static final String EYE_COLOR_KEY = "eyeColor";

    private static final String HAIR_COLOR_KEY = "hairColor";

    private static final String HAIR_VARIANT_KEY = "hairVariant";

    private static final String IS_MALE_KEY = "isMale";

    private static final String SKIN_COLOR_KEY = "skinColor";

    private static final ResourceLocation EYES_TEXTURE_LOCATION = AVPResources.entityTextureLocation("human/generic/eyes/eyes");

    // beard variant index -> beard texture location
    private final Memo<Integer, ResourceLocation> cachedBeardTexture = new Memo<>(
        beardVariantIndex -> AVPResources.entityTextureLocation("human/male/beard/beard_" + beardVariantIndex)
    );

    // isMale, hair variant index -> hair texture location
    private final BiMemo<Boolean, Integer, ResourceLocation> cachedHairTexture = new BiMemo<>(
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

    // TODO: Use getBeardTextureOrNull here since this method is accessed on a hot path (rendering).
    public Option<ResourceLocation> getBeardTexture() {
        return entity.getBeardVariant().map(cachedBeardTexture);
    }

    public ResourceLocation getHairTexture() {
        return cachedHairTexture.apply(entity.isMale(), entity.getHairVariant());
    }

    public ResourceLocation getOutfitTexture() {
        return cachedOutfitTexture.apply(entity.isMale());
    }

    public ResourceLocation getSkinTexture() {
        return cachedSkinTexture.apply(entity.isMale());
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(BEARD_VARIANT_KEY)) {
            entity.setBeardVariant(compoundTag.getInt(BEARD_VARIANT_KEY));
        }

        if (compoundTag.contains(EYE_COLOR_KEY)) {
            entity.setEyeColor(compoundTag.getInt(EYE_COLOR_KEY));
        }

        if (compoundTag.contains(HAIR_COLOR_KEY)) {
            entity.setHairColor(compoundTag.getInt(HAIR_COLOR_KEY));
        }

        if (compoundTag.contains(HAIR_VARIANT_KEY)) {
            entity.setHairVariant(compoundTag.getInt(HAIR_VARIANT_KEY));
        }

        if (compoundTag.contains(IS_MALE_KEY)) {
            entity.setMale(compoundTag.getBoolean(IS_MALE_KEY));
        }

        if (compoundTag.contains(SKIN_COLOR_KEY)) {
            entity.setSkinColor(compoundTag.getInt(SKIN_COLOR_KEY));
        }
    }

    public void save(CompoundTag compoundTag) {
        entity.getBeardVariant()
            .ifSome(beardVariant -> compoundTag.putInt(BEARD_VARIANT_KEY, beardVariant));
        compoundTag.putInt(EYE_COLOR_KEY, entity.getEyeColor());
        compoundTag.putInt(HAIR_COLOR_KEY, entity.getHairColor());
        compoundTag.putInt(HAIR_VARIANT_KEY, entity.getHairVariant());
        compoundTag.putBoolean(IS_MALE_KEY, entity.isMale());
        compoundTag.putInt(SKIN_COLOR_KEY, entity.getSkinColor());
    }
}
