package com.avp.common.manager;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.marine.MarineMob;
import net.minecraft.resources.ResourceLocation;

public class MarineOutfitManager extends OutfitManager {

    public MarineOutfitManager(MarineMob entity) {
        super(entity, 0, 0);
    }

    public ResourceLocation getMaleOutfitTexture(String humanType) {
        if (cachedMaleOutfitTexture == null) {
            cachedMaleOutfitTexture = AVPResources.entityTextureLocation(humanType + "_male_outfit");
        }
        return cachedMaleOutfitTexture;
    }

    public ResourceLocation getFemaleOutfitTexture(String humanType) {
        if (cachedFemaleOutfitTexture == null) {
            cachedFemaleOutfitTexture = AVPResources.entityTextureLocation(humanType + "_female_outfit");
        }
        return cachedFemaleOutfitTexture;
    }

}
