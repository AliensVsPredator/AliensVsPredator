package org.avp.api.block.model.provider;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;

public interface ItemModelDelegator {

    ItemDelegateData getItemModelDelegateData();

    record ItemDelegateData(ModelTemplate modelTemplate, TextureMapping textureMapping) {}
}
